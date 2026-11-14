package control;

import java.nio.file.Path;
import java.util.List;
import model.AccessControlInterface;
import model.AuditLog;
import model.AuditLogInterface;
import model.LoggedAction;
import model.SQLiteDataStore;
import model.User;
import model.UserManagerInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Integration tests: a real AuditLog over a real SQLiteDataStore
// on a test database, behind the full Controller. Users, access,
// and messages stay mocked: this file tests the
// controller-to-audit-log wiring only, that is, reads and adds
// that reach the log as real rows. The acting user is seeded
// into the store first, since log rows reference their user.
// Run: .\gradlew test --tests "control.ControllerAuditLogIntegrationTest" --console=plain
public class ControllerAuditLogIntegrationTest {
    @TempDir
    Path tempDir;

    private SQLiteDataStore store;
    private AuditLogInterface audit;
    private UserManagerInterface users;
    private Controller controller;

    @BeforeEach
    void setUp() throws Exception {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        store = new SQLiteDataStore(databaseUrl);
        audit = new AuditLog(store);
        users = mock(UserManagerInterface.class);
        AccessControlInterface access = mock(AccessControlInterface.class);
        when(access.isAdmin("boss")).thenReturn(true);
        User boss = store.addUser(new User(-1, "boss"));
        when(users.getUserByUsername("boss")).thenReturn(boss);
        controller = new Controller(users, access, store, audit,
                mock(MessageManager.class), mock(SessionManager.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        store.close();
    }

    @Test
    void readEntryLogsRealReadAction() {
        controller.readEntry("boss", "briefs", 1);
        List<LoggedAction> log = audit.getLog();
        assertEquals(1, log.size());
        assertEquals(LoggedAction.READ, log.get(0).getAction());
        assertEquals("briefs 1", log.get(0).getRecord());
        assertEquals("boss", log.get(0).getUser().getUsername());
    }

    @Test
    void addEntryLogsRealCreateAction() {
        controller.addEntry("boss", "facilities", List.of("New Facility", "NF"));
        List<LoggedAction> log = audit.getLog();
        assertEquals(1, log.size());
        assertEquals(LoggedAction.CREATE, log.get(0).getAction());
        assertEquals("facilities 7", log.get(0).getRecord());
    }

    @Test
    void reviewAuditLogShowsRealEntries() {
        controller.readEntry("boss", "briefs", 1);
        controller.addEntry("boss", "facilities", List.of("New Facility", "NF"));
        String review = controller.reviewAuditLog("boss");
        assertTrue(review.contains("briefs 1"));
        assertTrue(review.contains("facilities 7"));
        assertTrue(review.contains("read"));
        assertTrue(review.contains("create"));
    }

    @Test
    void readEntrySkipsAuditWhenUserUnknown() {
        String brief = controller.readEntry("ghost", "briefs", 1);
        assertTrue(brief.contains("Title: Operation Sandtrap"));
        assertTrue(audit.getLog().isEmpty());
    }

    @Test
    void reviewAuditLogRejectsStandardUser() {
        assertEquals("Not allowed", controller.reviewAuditLog("alice"));
    }

    @Test
    void readEntryNotFoundLogsNothing() {
        assertEquals("No brief found with this id",
                controller.readEntry("boss", "briefs", 999));
        assertTrue(audit.getLog().isEmpty());
    }
}
