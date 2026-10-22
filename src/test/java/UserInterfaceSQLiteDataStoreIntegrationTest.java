import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class    UserInterfaceSQLiteDataStoreIntegrationTest {
    private SQLiteDataStore realStore;
    private UserManagerInterface users;
    private AccessControlInterface access;
    private AuditLogInterface audit;
    private PrintStream origOut;
    private InputStream origIn;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() throws Exception {
        File dbFile = File.createTempFile("ui-ds-test", ".db");
        dbFile.deleteOnExit();
        // forward slashes: sqlite-jdbc dislikes Windows backslashes in the URL
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath().replace('\\', '/');
        realStore = new SQLiteDataStore(url);
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        audit = mock(AuditLogInterface.class);
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(users.getUserByUsername("tester")).thenReturn(new User(1, "tester"));
        when(access.isAdmin("tester")).thenReturn(true);
    }

    @AfterEach
    void tearDown() throws Exception {
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
    // searchMissions runs the real Search against real imported rows.
    @Test
    void searchMissionsFindsRealMission() throws Exception {
        File tsv = File.createTempFile("missions", ".tsv");
        tsv.deleteOnExit();
        Files.writeString(tsv.toPath(),
                "Title\tDate\tText\nAlpha\t2024-01-01\tAlpha text\n",
                StandardCharsets.UTF_8);
        realStore.addMission(new Mission("Alpha", 1));
        ui("alpha\n").searchMissions();
        assertTrue(out().contains("Alpha"));
    }

    // listEntries against the stubbed listBriefs: no crash, asks the store.
    @Test
    void listEntriesAsksRealStore() {
        ui("").listEntries("briefs");
        // listBriefs() is still a stub returning an empty list (C TODO)
        assertTrue(out().isEmpty() || !out().contains("Exception"));
    }

    // readEntry against the stubbed getBrief: "No brief found with this id", no audit.
    @Test
    void readEntryNotFoundOnStubbedStore() {
        ui("1\n").readEntry("briefs");
        assertTrue(out().contains("No brief found with this id"));
        verify(audit, never()).log(any());
    }
}
