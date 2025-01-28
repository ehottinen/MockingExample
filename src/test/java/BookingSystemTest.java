import com.example.BookingSystem;
import com.example.NotificationService;
import com.example.RoomRepository;
import com.example.TimeProvider;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public class BookingSystemTest {

    private TimeProvider timeProvider;
    private RoomRepository roomRepository;
    private NotificationService notificationService;
    private BookingSystem bookingSystem;

    @BeforeEach
        // innan varje test, skapa mock för class dependencies
    void setUp() {
        timeProvider = mock(TimeProvider.class);
        roomRepository = mock(RoomRepository.class);
        notificationService = mock(NotificationService.class);
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);
    }

}
