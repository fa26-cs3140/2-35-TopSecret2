package control;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Shared session store for the web server. One login = one
// random UUID token, mapped to the username that logged in.
// A session expires after 30 minutes inactive: validate
// drops it, and listSessions drops expired sessions. The
// timeout slides, because validate touches the last-activity
// time on every successful check. The clock is injected so
// tests can move time forward without sleeping.
public class SessionManager {
    public record Session(String token, String username, Instant lastActivity) {
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Clock clock;
    private static final long TIMEOUT_SECONDS = 1800;

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
        if (session == null || isExpired(session)) {
            if (session != null) {
                sessions.remove(token);
            }
            return null;
        }
        sessions.put(token, new Session(token, session.username(), Instant.now(clock)));
        return session.username();
    }

    // True when the session has been inactive for more than
    // 30 minutes. At exactly 30 minutes it is still valid.
    private boolean isExpired(Session session) {
        return Instant.now(clock).isAfter(session.lastActivity().plusSeconds(TIMEOUT_SECONDS));
    }

    public List<Session> listSessions() {
        sessions.entrySet().removeIf(entry -> isExpired(entry.getValue()));
        return new ArrayList<>(sessions.values());
    }

    public boolean revokeSession(String token) {
        return sessions.remove(token) != null;
    }
}
