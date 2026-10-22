import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AuditLogTest {
    private DataStoreInterface mockDataStore;
    AuditLogInterface testedAuditLog;

    @BeforeEach
    void setUp() {
        mockDataStore = Mockito.mock(SQLiteDataStore.class);
        testedAuditLog = new AuditLog(mockDataStore);
    }

    @Test
    void logEntryTest1() {
        User sampleUser = new User(
                1,
                "smitty_werbenmanjensen");
        LoggedAction sampleAction = new LoggedAction(
                0,
                sampleUser,
                "February 22, 2002 8:15 PM",
                "Drinking Hat Manual");

        verify(mockDataStore).addLog(sampleAction);
        assertEquals(sampleAction, testedAuditLog.log(sampleAction));
    }

    @Test
    void logEntryTest2() {
        User sampleUser = new User(
                1,
                "smitty_werbenmanjensen");
        LoggedAction sampleAction = new LoggedAction(
                1,
                sampleUser,
                "February 22, 2002 8:15 PM",
                "Drinking Hat Manual"
        );

        verify(mockDataStore).addLog(sampleAction);
        assertEquals(sampleAction, testedAuditLog.log(sampleAction));
    }

    @Test
    void logEntryTest3() {
        User sampleUser = new User(
                1,
                "smitty_werbenmanjensen"
        );
        LoggedAction sampleAction = new LoggedAction(
                0,
                sampleUser,
                "March 21, 2026 5:36 PM",
                "Secret Recipes"
        );

        verify(mockDataStore).addLog(sampleAction);
        assertEquals(sampleAction, testedAuditLog.log(sampleAction));
    }

    @Test
    void logEntryTest4() {
        User sampleUser = new User(
                7,
                "james_bond"
        );
        LoggedAction sampleAction = new LoggedAction(
                0,
                sampleUser,
                "March 21, 2026 5:36 PM",
                "Secret Recipes"
        );

        verify(mockDataStore).addLog(sampleAction);
        assertEquals(sampleAction, testedAuditLog.log(sampleAction));
    }

    @Test
    void logReadTest() {
        User sampleUser1 = new User(
                1,
                "smitty_werbenmanjensen"
        );
        User sampleUser2 = new User(
                7,
                "james_bond"
        );

        LoggedAction sampleAction1 = new LoggedAction(
                1,
                sampleUser1,
                "February 22, 2002 8:15 PM",
                "Drinking Hat Manual"
        );

        LoggedAction sampleAction2 = new LoggedAction(
                0,
                sampleUser1,
                "February 22, 2002 8:36 PM",
                "Drinking Hat Manual"
        );

        LoggedAction sampleAction3 = new LoggedAction(
                1,
                sampleUser1,
                "March 21, 2026 5:33 PM",
                "Secret Recipes"
        );

        LoggedAction sampleAction4 = new LoggedAction(
                0,
                sampleUser2,
                "March 21, 2026 5:36 PM",
                "Secret Recipes"
        );


        ArrayList<LoggedAction> sampleList = new ArrayList<>(List.of(
                sampleAction1,
                sampleAction2,
                sampleAction3,
                sampleAction4
        ));

        when(mockDataStore.listLogs()).thenReturn(sampleList);
        assertEquals(sampleList, testedAuditLog.getLog());
    }
}