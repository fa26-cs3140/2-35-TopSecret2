import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Member B (Sebastian) -- HW5 integration tests: UI + user accounts.
// Uses D's REAL UserManager implementation (assumes the natural name;
// rename if D calls it otherwise). Assumes a no-arg constructor and that a
// fresh instance starts with no users -- adjust construction if D's class
// differs. Does not compile until D delivers the class.
// AccessControl, the store and the audit log are Mockito mocks here; their
// UI integrations live in their own test files.
public class UserInterfaceUserManagerIntegrationTest {

    private UserManager users;
    private AccessControlInterface access;
    private DataStoreInterface store;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        users = new UserManager();
        access = mock(AccessControlInterface.class);
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(access.isAdmin(anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
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
        assertEquals(0, ui("boss\nPass123\nPass123\n9\n").run());
        assertTrue(users.hasUser());
        assertTrue(users.verifyLogin("boss", "Pass123"));
    }

    // Admin creates a user (username only); new user sets password at first login.
    @Test
    void adminCreatesUserThenFirstLoginSetsPassword() {
        assertEquals(0, ui("boss\nPass123\nPass123\n9\n").run());
        assertEquals(0, ui("boss\nPass123\n6\nworker\n9\n").run());
        assertTrue(users.needsPasswordSetup("worker"));
        assertEquals(0, ui("worker\nWorker1a\nWorker1a\n9\n").run());
        assertTrue(users.verifyLogin("worker", "Worker1a"));
    }

    // Change password through the UI, then log in with the new one.
    @Test
    void changePasswordThenLoginWithNewPassword() {
        assertEquals(0, ui("boss\nPass123\nPass123\n9\n").run());
        assertEquals(0, ui("boss\nPass123\n5\nNewPass9\nNewPass9\n9\n").run());
        assertTrue(users.verifyLogin("boss", "NewPass9"));
        assertEquals(0, ui("boss\nNewPass9\n9\n").run());
    }

    // Duplicate username: D's TopSecretException is caught, UI keeps running.
    @Test
    void createUserRejectsDuplicateName() {
        assertEquals(0, ui("boss\nPass123\nPass123\n9\n").run());
        assertEquals(0, ui("boss\nPass123\n6\nboss\n9\n").run());
    }
}