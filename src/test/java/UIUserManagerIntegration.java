import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
public class UIUserManagerIntegration {
    @Nested
    class UserInterfaceUserManagerIntegrationTest {

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
        //First run creates an account and exits
        @Test
        void firstRunCreatesUserAndExits(){
        assertEquals(0, ui("boss\\nPass123\\nPass123\\n9\\n").run());
        assertTrue(users.hasUser());
        assertTrue(users.verifyLogin("boss", "Pass123"));
        }
        //Admin created user sets password at first login
        @Test
        void adminCreatedUserSetsPasswordFirstLogin(){
            assertEquals(0, ui("boss\\nPass123\\nPass123\\n9\\n").run());
            assertEquals(0, ui("boss\\nPass123\\nPass123\\n9\\n").run());
            assertTrue(users.needPasswordSetup("worker"));
            assertEquals(0, ui("worker\nWorker1a\nWorker1a\n9\n").run());
            assertTrue(users.verifyLogin("worker", "Worker1a"));
        }
        //Changing password updates the whole system
        @Test
        void changePasswordThenLoginNewPass(){
            assertEquals(0, ui("boss\\nPass123\\nPass123\\n9\\n").run());
            assertEquals(0, ui("boss\nPass123\nPass123\n9\n").run());
            assertTrue(users.verifyLogin("boss", "NewPass9"));
            assertEquals(0, ui("boss\nNewPass9\n9\n").run());
        }
        //Duplicate usernames can't exist, but don't crash the program:
        @Test
        void createUserRejectsDuplicateUsername() {
            assertEquals(0, ui("boss\\nPass123\\nPass123\\n9\\n").run());
            assertEquals(0, ui("boss\nPass123\n6\nboss\n9\n").run());
        }

    }
}
