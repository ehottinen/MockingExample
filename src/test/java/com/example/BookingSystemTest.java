package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookingSystemTest {

    private static final String ERROR_PAST_BOOKING = "Kan inte boka tid i dåtid";
    private static final String ERROR_END_BEFORE_START = "Sluttid måste vara efter starttid";
    private static final String ERROR_ROOM_NOT_FOUND = "Rummet existerar inte";
    private static final String ERROR_BOOKING_NOT_FOUND = "Boknings-id kan inte vara null";
    private static final String ERROR_BOOKING_STARTED = "Kan inte avboka påbörjad eller avslutad bokning";

    private RoomRepository roomRepository;
    private NotificationService notificationService;
    private BookingSystem bookingSystem;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        TimeProvider timeProvider = mock(TimeProvider.class);
        roomRepository = mock(RoomRepository.class);
        notificationService = mock(NotificationService.class);
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);

        now = LocalDateTime.of(2025, 1, 28, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);
    }

    @Test
    void bookRoom_shouldSucceedWhenRoomIsAvailable() throws NotificationException {
        // Arrange
        Room room = new Room("room1", "Test Room");
        when(roomRepository.findById("room1")).thenReturn(Optional.of(room));

        // Act
        boolean result = bookingSystem.bookRoom("room1", now.plusHours(1), now.plusHours(2));

        // Assert
        assertThat(result).isTrue();
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void bookRoom_shouldFailWhenStartTimeIsInThePast() {
        // Arrange
        LocalDateTime startTime = now.minusHours(1); // Move method calls outside lambda
        LocalDateTime endTime = now.plusHours(1);

        // Act & Assert
        assertThatThrownBy(() -> bookingSystem.bookRoom("room1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ERROR_PAST_BOOKING);
    }

    @Test
    void bookRoom_shouldFailWhenEndTimeIsBeforeStartTime() {
        // Arrange
        LocalDateTime startTime = now.plusHours(2);
        LocalDateTime endTime = now.plusHours(1); // Move method calls outside lambda

        // Act & Assert
        assertThatThrownBy(() -> bookingSystem.bookRoom("room1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ERROR_END_BEFORE_START);
    }


    @Test
    void bookRoom_shouldFailWhenRoomDoesNotExist() {
        // Arrange
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        when(roomRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingSystem.bookRoom("nonexistent", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ERROR_ROOM_NOT_FOUND);
    }

    @Test
    void bookRoom_shouldFailWhenRoomIsAlreadyBooked() {
        // Arrange
        Room room = new Room("room1", "Test Room");
        room.addBooking(new Booking("existing", "room1", now.plusHours(1), now.plusHours(2)));
        when(roomRepository.findById("room1")).thenReturn(Optional.of(room));

        // Act
        boolean result = bookingSystem.bookRoom("room1", now.plusHours(1), now.plusHours(2));

        // Assert
        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
    }

    @Test
    void getAvailableRooms_shouldReturnAvailableRooms() {
        // Arrange
        Room room1 = new Room("room1", "Room A");
        Room room2 = new Room("room2", "Room B");

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        when(room1.isAvailable(now.plusHours(1), now.plusHours(2))).thenReturn(true);
        when(room2.isAvailable(now.plusHours(1), now.plusHours(2))).thenReturn(false);

        // Act
        var availableRooms = bookingSystem.getAvailableRooms(now.plusHours(1), now.plusHours(2));

        // Assert
        assertThat(availableRooms).hasSize(1).extracting(Room::getId).containsExactly("room1");
    }

    @Test
    void getAvailableRooms_shouldThrowExceptionWhenEndTimeBeforeStartTime() {
        // Arrange
        LocalDateTime startTime = now.plusHours(2);
        LocalDateTime endTime = now.plusHours(1); // Move method calls outside lambda

        // Act & Assert
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ERROR_END_BEFORE_START);
    }

    @Test
    void cancelBooking_shouldSucceedForValidBooking() throws NotificationException {
        // Arrange
        Room room = new Room("room1", "Test Room");
        Booking booking = new Booking("booking123", "room1", now.plusHours(1), now.plusHours(2));
        room.addBooking(booking);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        // Act
        boolean result = bookingSystem.cancelBooking("booking123");

        // Assert
        assertThat(result).isTrue();
        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(booking);
    }

    @Test
    void cancelBooking_shouldThrowExceptionWhenBookingIdIsNull() {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ERROR_BOOKING_NOT_FOUND);
    }

    @Test
    void cancelBooking_shouldFailWhenBookingDoesNotExist() {
        when(roomRepository.findAll()).thenReturn(List.of());

        boolean result = bookingSystem.cancelBooking("nonexistent");

        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
    }

    @Test
    void cancelBooking_shouldThrowExceptionIfBookingHasStarted() {
        // Arrange
        Room room = new Room("room1", "Test Room");
        Booking booking = new Booking("booking123", "room1", now.minusHours(1), now.plusMinutes(30));
        room.addBooking(booking);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        // Act & Assert
        assertThatThrownBy(() -> bookingSystem.cancelBooking("booking123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ERROR_BOOKING_STARTED);
    }
}
