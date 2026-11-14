package control;

import java.time.Clock;
import java.time.Instant;
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
// so no test has to sleep. Week 1 only: sessions are
// stored, nothing expires yet. The expiry tests land
// in week 2, when validate expires a session after
// 30 minutes inactive.
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
}
