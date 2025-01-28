package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookingSystemTest {

    private TimeProvider timeProvider;
    private RoomRepository roomRepository;
    private NotificationService notificationService;
    private BookingSystem bookingSystem;

    @BeforeEach
    // innan varje test, skapa mocks för dependencies
    void setUp() {
        timeProvider = mock(TimeProvider.class);
        roomRepository = mock(RoomRepository.class);
        notificationService = mock(NotificationService.class);
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
    }

    private static Stream<Arguments> provideBookRoomTestCases() {
        LocalDateTime now = LocalDateTime.of(2025, 1, 28, 10, 0);

        return Stream.of(
                // Normalt fall: giltiga tider och ledigt rum
                Arguments.of("room1", now.plusHours(1), now.plusHours(2), true, null, null),

                // Fel: starttid före nuvarande tid
                Arguments.of("room1", now.minusHours(1), now.plusHours(1), false, IllegalArgumentException.class, "Kan inte boka tid i dåtid"),

                // Fel: sluttid före starttid
                Arguments.of("room1", now.plusHours(2), now.plusHours(1), false, IllegalArgumentException.class, "Sluttid måste vara efter starttid"),

                // Fel: rummet existerar inte
                Arguments.of("nonexistent", now.plusHours(1), now.plusHours(2), false, IllegalArgumentException.class, "Rummet existerar inte"),

                // Fel: dubbelbokning
                Arguments.of("room1", now.plusHours(1), now.plusHours(2), false, null, "Room already booked")
        );
    }

    @ParameterizedTest
    @MethodSource("provideBookRoomTestCases")
    void bookRoom_shouldHandleVariousScenarios(String roomId, LocalDateTime startTime, LocalDateTime endTime,
                                               boolean expectedResult, Class<Exception> expectedException, String errorMessage) throws NotificationException {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2025, 1, 28, 10, 0);
        when(timeProvider.getCurrentTime()).thenReturn(now);

        // Simulera att rummet finns eller inte
        Room room = new Room(roomId, "room");
        if (!"nonexistent".equals(roomId)) {
            when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        } else {
            when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        }

        // Simulera dubbelbokning om det är relevant
        if ("Room already booked".equals(errorMessage)) {
            Booking existingBooking = new Booking("existing", roomId, now.plusHours(1), now.plusHours(2));
            room.addBooking(existingBooking);
        }

        // Act & Assert
        if (expectedException != null) {
            assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                    .isInstanceOf(expectedException)
                    .hasMessage(errorMessage);
        } else {
            boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);
            assertThat(result).isEqualTo(expectedResult);

            if (expectedResult) {
                verify(roomRepository).save(room);
                verify(notificationService).sendBookingConfirmation(any(Booking.class));
            } else {
                verify(roomRepository, never()).save(any());
            }
        }
    }
}

