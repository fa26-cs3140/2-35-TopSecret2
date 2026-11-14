package control;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Stub, week 1: sessions are stored, nothing
// expires yet. Week 2 adds the 30-minute sliding
// expiry, using the Clock. The Clock is stored
// now so the constructor does not change in
// week 2.
public class SessionManager {
    public record Session(String token, String username, Instant lastActivity) {
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Clock clock;

    public SessionManager(Clock clock) {
        this.clock = clock;
    }

    public String createSession(String username) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(token, username, Instant.now(clock)));
        return token;
    }

    public String validate(String token) {
        Session session = sessions.get(token);
        return session == null ? null : session.username();
    }

    public List<Session> listSessions() {
        return new ArrayList<>(sessions.values());
    }

    public boolean revokeSession(String token) {
        return sessions.remove(token) != null;
    }
}
