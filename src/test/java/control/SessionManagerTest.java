package control;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Unit tests for SessionManager. A fixed clock is used
// so no test has to sleep. The expiry tests use a clock
// whose time the test moves forward, so they can prove
// the 30-minute sliding timeout without waiting.
// Run: .\gradlew test --tests "control.SessionManagerTest" --console=plain
public class SessionManagerTest {
    private SessionManager sessions;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-13T00:00:00Z"), ZoneOffset.UTC);
        sessions = new SessionManager(clock);
    }

    @Test
    void createSessionReturnsToken() {
        String token = sessions.createSession("alice");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateReturnsUsername() {
        String token = sessions.createSession("alice");
        assertEquals("alice", sessions.validate(token));
    }

    @Test
    void validateUnknownTokenReturnsNull() {
        assertNull(sessions.validate("nope"));
    }

    @Test
    void createSessionGivesDifferentTokens() {
        String first = sessions.createSession("alice");
        String second = sessions.createSession("alice");
        assertNotEquals(first, second);
    }

    @Test
    void listSessionsEmptyAtStart() {
        assertTrue(sessions.listSessions().isEmpty());
    }

    @Test
    void listSessionsShowsCreatedSessions() {
        sessions.createSession("alice");
        sessions.createSession("boss");
        List<SessionManager.Session> listed = sessions.listSessions();
        assertEquals(2, listed.size());
        assertTrue(listed.stream().anyMatch(s -> s.username().equals("alice")));
        assertTrue(listed.stream().anyMatch(s -> s.username().equals("boss")));
    }

    @Test
    void revokeSessionRemovesSession() {
        String token = sessions.createSession("alice");
        assertTrue(sessions.revokeSession(token));
        assertNull(sessions.validate(token));
    }

    @Test
    void revokeUnknownTokenReturnsFalse() {
        assertFalse(sessions.revokeSession("nope"));
    }

    @Test
    void listSessionsAfterRevoke() {
        String token = sessions.createSession("alice");
        sessions.createSession("boss");
        sessions.revokeSession(token);
        List<SessionManager.Session> listed = sessions.listSessions();
        assertEquals(1, listed.size());
        assertEquals("boss", listed.get(0).username());
    }

    // A clock whose time the test moves forward.
    static class MutableClock extends Clock {
        private Instant instant;

        MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    @Test
    void validateExpiresAfterThirtyMinutes() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-13T00:00:00Z"));
        SessionManager manager = new SessionManager(clock);
        String token = manager.createSession("alice");
        clock.advance(Duration.ofMinutes(31));
        assertNull(manager.validate(token));
        assertTrue(manager.listSessions().isEmpty());
    }

    @Test
    void validateWithinThirtyMinutes() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-13T00:00:00Z"));
        SessionManager manager = new SessionManager(clock);
        String token = manager.createSession("alice");
        clock.advance(Duration.ofMinutes(29));
        assertEquals("alice", manager.validate(token));
    }

    @Test
    void validateAtExactlyThirtyMinutes() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-13T00:00:00Z"));
        SessionManager manager = new SessionManager(clock);
        String token = manager.createSession("alice");
        clock.advance(Duration.ofMinutes(30));
        assertEquals("alice", manager.validate(token));
    }

    @Test
    void validateSlidesOnUse() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-13T00:00:00Z"));
        SessionManager manager = new SessionManager(clock);
        String token = manager.createSession("alice");
        clock.advance(Duration.ofMinutes(29));
        assertEquals("alice", manager.validate(token));
        clock.advance(Duration.ofMinutes(29));
        assertEquals("alice", manager.validate(token));
        clock.advance(Duration.ofMinutes(31));
        assertNull(manager.validate(token));
    }

    @Test
    void listSessionsDropsExpired() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-13T00:00:00Z"));
        SessionManager manager = new SessionManager(clock);
        manager.createSession("alice");
        clock.advance(Duration.ofMinutes(31));
        manager.createSession("boss");
        List<SessionManager.Session> listed = manager.listSessions();
        assertEquals(1, listed.size());
        assertEquals("boss", listed.get(0).username());
    }
}
