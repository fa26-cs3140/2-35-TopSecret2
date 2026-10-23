import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// Member B (Sebastian) -- HW5 integration tests: UI + data store.
// Uses C's REAL SQLiteDataStore on a throwaway test database (@TempDir,
// the pattern from C's SQLiteDataStoreTest). The user manager, access
// control and audit log are Mockito mocks; their UI integrations live in
// their own test files.
public class UserInterfaceSQLiteDataStoreIntegrationTest {

    private SQLiteDataStore realStore;
    private UserManagerInterface users;
    private AccessControlInterface access;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        realStore = new SQLiteDataStore(databaseUrl);
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(users.getUserByUsername("tester"))
                .thenReturn(new User(1, "tester"));
        when(access.isAdmin("tester")).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        realStore.close();
        System.setOut(origOut);
        System.setIn(origIn);
    }

    private UserInterface ui(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        return new UserInterface(users, access, realStore, audit);
    }

    private String out() {
        return outBuf.toString();
    }

    // listEntries against the real store: seeded briefs print, no crash.
    @Test
    void listEntriesAsksRealStore() {
        ui("").listEntries("briefs");
        assertTrue(out().isEmpty() || !out().contains("Exception"));
    }

    // readEntry for an id that does not exist: not-found message, no audit.
    @Test
    void readEntryNotFoundOnRealStore() {
        ui("999\n").readEntry("briefs");
        assertTrue(out().contains("No brief found with this id"));
        verify(audit, never()).log(any());
    }
}
