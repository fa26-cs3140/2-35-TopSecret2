import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// Member B (Sebastian) -- HW5 integration tests: UI + user accounts.
// Uses D's REAL UserManager over C's REAL SQLiteDataStore on a throwaway
// test database (@TempDir). Passwords go to the real credentials.cip in
// the working directory (the UserManager design), deleted before and
// after each test so no stray file leaks between tests.
// AccessControl and the audit log are Mockito mocks here; their UI
// integrations live in their own test files.
public class UserInterfaceUserManagerIntegrationTest {

    private UserManager users;
    private SQLiteDataStore store;
    private AccessControlInterface access;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("credentials.cip"));
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        store = new SQLiteDataStore(databaseUrl);
        users = new UserManager(store);
        access = mock(AccessControlInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(access.isAdmin(anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() throws Exception {
        store.close();
        Files.deleteIfExists(Paths.get("credentials.cip"));
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

    // First run: create account -> exit cleanly.
    @Test
    void firstRunCreatesUserAndExits() {
        assertEquals(0, ui("boss\nPass123\nPass123\n8\n").run());
        assertTrue(users.hasUser());
        assertTrue(users.verifyLogin("boss", "Pass123"));
    }

    // Admin creates a user (username only); new user sets password at first login.
    @Test
    void adminCreatesUserThenFirstLoginSetsPassword() {
        assertEquals(0, ui("boss\nPass123\nPass123\n8\n").run());
        assertEquals(0, ui("boss\nPass123\n5\nworker\n8\n").run());
        assertTrue(users.needsPasswordSetup("worker"));
        assertEquals(0, ui("worker\nWorker1a\nWorker1a\n8\n").run());
        assertTrue(users.verifyLogin("worker", "Worker1a"));
    }

    // Change password through the UI, then log in with the new one.
    @Test
    void changePasswordThenLoginWithNewPassword() {
        assertEquals(0, ui("boss\nPass123\nPass123\n8\n").run());
        assertEquals(0, ui("boss\nPass123\n4\nNewPass9\nNewPass9\n8\n").run());
        assertTrue(users.verifyLogin("boss", "NewPass9"));
        assertEquals(0, ui("boss\nNewPass9\n8\n").run());
    }

    // Duplicate username: D's TopSecretException is caught, UI keeps running.
    @Test
    void createUserRejectsDuplicateName() {
        assertEquals(0, ui("boss\nPass123\nPass123\n8\n").run());
        assertEquals(0, ui("boss\nPass123\n5\nboss\n8\n").run());
    }
}
