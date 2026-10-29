import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// Member B (Sebastian) -- HW5 integration tests: UI + audit log.
// Uses D's REAL model.AuditLog over C's REAL model.SQLiteDataStore on a throwaway
// test database (@TempDir), so logged actions are read back from the
// database itself. The user manager and access control are Mockito
// mocks; their UI integrations live in their own test files.
public class UserInterfaceAuditLogIntegrationTest {

    private UserManagerInterface users;
    private AccessControlInterface access;
    private SQLiteDataStore store;
    private AuditLog audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        store = new SQLiteDataStore(databaseUrl);
        audit = new AuditLog(store);
        // The user the UI logs actions for must exist in the store:
        // log rows reference users, and getLog() resolves them back.
        // First user added to a fresh database gets ID 1.
        store.addUser(new User(-1, "tester"));
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(users.hasUser()).thenReturn(true);
        when(users.verifyLogin("tester", "Pass123")).thenReturn(true);
        when(users.getUserByUsername("tester"))
                .thenReturn(new User(1, "tester"));
    }

    @AfterEach
    void tearDown() {
        store.close();
        System.setOut(origOut);
        System.setIn(origIn);
    }

    private UserInterface ui(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new UserInterface(users, access, store, audit);
    }

    private String out() {
        return outBuf.toString();
    }

    // Reading through the UI leaves a READ model.LoggedAction in the database.
    @Test
    void readEntryLogsReadAction() {
        UserInterface ui = ui("tester\nPass123\n1\n");
        assertTrue(ui.loginStep());
        ui.readEntry("briefs");
        List<LoggedAction> log = audit.getLog();
        LoggedAction a = log.get(log.size() - 1);
        assertEquals(LoggedAction.READ, a.getAction());
        assertEquals("briefs 1", a.getRecord());
        assertEquals("tester", a.getUser().getUsername());
    }

    // Adding through the UI leaves a CREATE model.LoggedAction with the new id.
    @Test
    void addEntryLogsCreateAction() {
        UserInterface ui = ui("tester\nPass123\n1\nT\n2024-01-01\ntext\n");
        assertTrue(ui.loginStep());
        ui.addEntry("briefs");
        List<LoggedAction> log = audit.getLog();
        LoggedAction a = log.get(log.size() - 1);
        assertEquals(LoggedAction.CREATE, a.getAction());
        assertTrue(a.getRecord().startsWith("briefs "));
        int newId = Integer.parseInt(
                a.getRecord().substring("briefs ".length()));
        assertNotNull(store.getBrief(newId));
    }

    // reviewAuditLog prints what earlier UI actions logged.
    @Test
    void reviewAuditLogShowsLoggedActions() {
        UserInterface ui = ui("tester\nPass123\n1\n");
        assertTrue(ui.loginStep());
        ui.readEntry("briefs");
        // fresh UI (fresh Scanner) for the add; same collaborators underneath
        UserInterface ui2 = ui("tester\nPass123\n1\nT\n2024-01-01\ntext\n");
        assertTrue(ui2.loginStep());
        ui2.addEntry("briefs");
        ui2.reviewAuditLog();
        List<LoggedAction> log = audit.getLog();
        assertTrue(out().contains("briefs 1"));
        assertTrue(out().contains(log.get(log.size() - 1).getRecord()));
        assertTrue(out().contains("tester"));
        assertTrue(out().contains("read"));
        assertTrue(out().contains("create"));
    }
}
