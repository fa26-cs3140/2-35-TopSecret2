import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class UserInterfaceAuditLogIntegrationTest {
    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        store = mock(DataStoreInterface.class);
        audit = new AuditLog();
        origOut = System.out;
        origIn = System.in;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
        when(users.hasUser()).thenReturn(true);
        when(users.verifyLogin("tester", "Pass123")).thenReturn(true);
        when(users.getUserByUsername("tester")).thenReturn(new User(1, "tester"));
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

    // Reading through the UI leaves a READ LoggedAction in the log.
    @Test
    void readEntryLogsReadAction() {
        when(store.getBrief(1))
                .thenReturn(new Brief(1, 1, "Alpha", "2024-01-01", "Alpha content"));
        UserInterface ui = ui("tester\nPass123\n1\n");
        assertTrue(ui.loginStep());
        ui.readEntry("briefs");
        List<LoggedAction> log = audit.getLog();
        LoggedAction a = log.get(log.size() - 1);
        assertEquals(LoggedAction.READ, a.getAction());
        assertEquals("briefs 1", a.getRecord());
        assertEquals("tester", a.getUser().getUsername());
    }

    // Adding through the UI leaves a CREATE LoggedAction with the new id.
    @Test
    void addEntryLogsCreateAction() {
        when(store.addBrief(any(Brief.class)))
                .thenReturn(new Brief(5, 1, "T", "2024-01-01", "text"));
        UserInterface ui = ui("tester\nPass123\n1\nT\n2024-01-01\ntext\n");
        assertTrue(ui.loginStep());
        ui.addEntry("briefs");
        List<LoggedAction> log = audit.getLog();
        LoggedAction a = log.get(log.size() - 1);
        assertEquals(LoggedAction.CREATE, a.getAction());
        assertEquals("briefs 5", a.getRecord());
    }

    // reviewAuditLog prints what earlier UI actions logged.
    @Test
    void reviewAuditLogShowsLoggedActions() {
        when(store.getBrief(1))
                .thenReturn(new Brief(1, 1, "Alpha", "2024-01-01", "Alpha content"));
        when(store.addBrief(any(Brief.class)))
                .thenReturn(new Brief(5, 1, "T", "2024-01-01", "text"));
        UserInterface ui = ui("tester\nPass123\n1\n");
        assertTrue(ui.loginStep());
        ui.readEntry("briefs");
        // fresh UI (fresh Scanner) for the add; same collaborators underneath
        UserInterface ui2 = ui("tester\nPass123\n1\nT\n2024-01-01\ntext\n");
        assertTrue(ui2.loginStep());
        ui2.addEntry("briefs");
        ui2.reviewAuditLog();
        assertTrue(out().contains("briefs 1"));
        assertTrue(out().contains("briefs 5"));
        assertTrue(out().contains("tester"));
        assertTrue(out().contains("read"));
        assertTrue(out().contains("create"));
    }

}
