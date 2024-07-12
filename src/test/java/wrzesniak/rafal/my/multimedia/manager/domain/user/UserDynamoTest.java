package wrzesniak.rafal.my.multimedia.manager.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserDynamoTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.parse("2024-02-12T11:00:00");
    private static final Clock FIXED_CLOCK = Clock.fixed(TIMESTAMP.toInstant(UTC), ZoneId.of("UTC"));

    @Test
    public void shouldMarkFirstLoggedIn() {
        // given
        UserDynamo user = new UserDynamo("TestUserId", "TestUserName", "test@test.com");

        // when
        try (MockedStatic<Clock> mocked = Mockito.mockStatic(Clock.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(Clock::systemDefaultZone).thenReturn(FIXED_CLOCK);
            assertTrue(user.getLoggedInTimestamps().isEmpty());
            user.markedLoggedIn();
        }

        // then
        assertEquals(1, user.getLoggedInTimestamps().size());
        assertEquals(TIMESTAMP, user.getLoggedInTimestamps().getFirst());
    }

    @Test
    public void shouldMarkAnotherLoggedInAsFirstInList() {
        // given
        UserDynamo user = new UserDynamo("TestUserId", "TestUserName", "test@test.com");
        user.setLoggedInTimestamps(new ArrayList<>(List.of(LocalDateTime.parse("2024-02-10T11:00:00"), LocalDateTime.parse("2024-02-01T11:00:00"), LocalDateTime.parse("2024-01-11T11:00:00"))));

        // when
        try (MockedStatic<Clock> mocked = Mockito.mockStatic(Clock.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(Clock::systemDefaultZone).thenReturn(FIXED_CLOCK);
            assertEquals(3, user.getLoggedInTimestamps().size());
            user.markedLoggedIn();
        }

        // then
        assertEquals(4, user.getLoggedInTimestamps().size());
        assertEquals(TIMESTAMP, user.getLoggedInTimestamps().getFirst());

    }
    @Test
    public void shouldMarkAnotherLoggedInAndRemoveLastWhenRichTheLimit() {
        // given
        UserDynamo user = new UserDynamo("TestUserId", "TestUserName", "test@test.com");
        user.setLoggedInTimestamps(prepareListOfTimeStamps(30));

        // when
        try (MockedStatic<Clock> mocked = Mockito.mockStatic(Clock.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(Clock::systemDefaultZone).thenReturn(FIXED_CLOCK);
            assertEquals(30, user.getLoggedInTimestamps().size());
            user.markedLoggedIn();
        }

        // then
        assertEquals(30, user.getLoggedInTimestamps().size());
        assertEquals(TIMESTAMP, user.getLoggedInTimestamps().getFirst());
    }

    private List<LocalDateTime> prepareListOfTimeStamps(int count) {
        List<LocalDateTime> timestamps = new ArrayList<>();
        LocalDateTime dateTime = TIMESTAMP.minusDays(1);
        for (int i = 0; i < count; i++) {
            timestamps.add(dateTime.minusDays(i));
        }
        return timestamps;
    }
}