import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Member B (Sebastian) -- HW5 integration tests: UI + access control.
// Uses A's REAL model.AccessControl class (fully implemented), constructed
// with the in-memory 2-arg constructor, so no roles file is touched.
// The user manager, store and audit log are Mockito mocks; their UI
// integrations live in their own test files.
public class UserInterfaceAccessControlIntegrationTest {

    private UserManagerInterface users;
    private AccessControl access;
    private DataStoreInterface store;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = new AccessControl(new HashSet<>(Set.of("boss")),
                new HashSet<>(Set.of("boss", "worker")));
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(users.hasUser()).thenReturn(true);
        when(users.needsPasswordSetup("boss")).thenReturn(false);
        when(users.needsPasswordSetup("worker")).thenReturn(false);
        when(users.verifyLogin("boss", "x")).thenReturn(true);
        when(users.verifyLogin("worker", "x")).thenReturn(true);
        when(users.getUserByUsername("boss")).thenReturn(new User(1, "boss"));
        when(users.getUserByUsername("worker")).thenReturn(new User(2, "worker"));
        when(users.listUsernames()).thenReturn(Arrays.asList("boss", "worker"));
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

    // Real isAdmin = true shows the admin options.
    @Test
    void adminSeesAdminOptions() {
        assertEquals(0, ui("boss\nx\n8\n").run());
        assertTrue(out().contains("Create a new user"));
    }

    // Real isAdmin = false doesn't show the admin options.
    @Test
    void nonAdminNoAdminOptions() {
        assertEquals(0, ui("worker\nx\n8\n").run());
        assertFalse(out().contains("Create a new user"));
    }

    // Promoting a worker switches their status.
    @Test
    void promoteCarriesThrough() {
        assertEquals(0, ui("boss\nx\n6\npromote\nworker\n8\n").run());
        assertTrue(access.isAdmin("worker"));
    }

    // Demoting an admin switches their status.
    @Test
    void demoteCarriesThrough() {
        access.promoteUser("boss", "worker"); // precondition: worker is admin
        assertEquals(0, ui("boss\nx\n6\ndemote\nworker\n8\n").run());
        assertFalse(access.isAdmin("worker"));
    }

    // Admin can't demote themselves.
    @Test
    void NoSelfDemote() {
        assertEquals(0, ui("boss\nx\n6\ndemote\nboss\n8\n").run());
        assertTrue(access.isAdmin("boss"));
        assertTrue(out().contains("Not allowed"));
    }
}
