import control.Controller;
import control.ControllerInterface;
import control.MessageManager;
import model.*;
import org.junit.jupiter.api.*;
import view.TerminalView;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TerminalViewControllerTest {

    private UserManagerInterface users;
    private AccessControlInterface access;
    private DataStoreInterface store;
    private AuditLogInterface audit;
    private MessageManager messages;

    private ControllerInterface controller;
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    public void setUp() {
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        store = mock(DataStoreInterface.class);
        audit = mock(AuditLogInterface.class);
        messages = mock(MessageManager.class);

        controller = new Controller(users, access, store, audit, messages);

        originalIn = System.in;
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void userLoginAndBrowseMissionsIntegrationTest() {
        when(users.hasUser()).thenReturn(true);
        when(users.needsPasswordSetup("alice")).thenReturn(false);
        when(users.verifyLogin("alice", "password")).thenReturn(true);
        when(access.isAdmin("alice")).thenReturn(false);

        Mission mission = mock(Mission.class);
        when(mission.getMissionID()).thenReturn(1);
        when(mission.getTitle()).thenReturn("Operation Eclipse");
        when(store.listMissions()).thenReturn(List.of(mission));

        System.setIn(new ByteArrayInputStream(
                "alice\npassword\n4\n1\n5\n11\n".getBytes()
        ));

        TerminalView view = new TerminalView(controller);

        assertEquals(0, view.run());

        String actual = output.toString();

        assertTrue(actual.contains("Login successful."));
        assertTrue(actual.contains("Browse missions"));
        assertTrue(actual.contains("Operation Eclipse"));
    }

    @Test
    public void adminCreatesFacilityIntegrationTest() {
        when(users.hasUser()).thenReturn(true);
        when(users.needsPasswordSetup("admin")).thenReturn(false);
        when(users.verifyLogin("admin", "password")).thenReturn(true);
        when(access.isAdmin("admin")).thenReturn(true);

        Facility facility = mock(Facility.class);
        when(facility.getFacilityID()).thenReturn(1);
        when(facility.getName()).thenReturn("Headquarters");

        when(store.addFacility(any(Facility.class))).thenReturn(facility);
        when(store.listFacilities()).thenReturn(List.of(facility));

        System.setIn(new ByteArrayInputStream(
                "admin\npassword\n3\n3\nHeadquarters\nHQ\n5\n11\n".getBytes()
        ));

        TerminalView view = new TerminalView(controller);

        assertEquals(0, view.run());

        String actual = output.toString();

        assertTrue(actual.contains("Login successful."));
        assertTrue(actual.contains("Facility name:"));
        assertTrue(actual.contains("Facility abbreviation:"));
        assertTrue(actual.contains("Headquarters"));

        verify(store).addFacility(any(Facility.class));
    }

    @Test
    public void userChangesPasswordIntegrationTest() {
        when(users.hasUser()).thenReturn(true);
        when(users.needsPasswordSetup("alice")).thenReturn(false);
        when(users.verifyLogin("alice", "oldPassword")).thenReturn(true);
        when(access.isAdmin("alice")).thenReturn(false);

        System.setIn(new ByteArrayInputStream(
                "alice\noldPassword\n5\nnewPassword\nnewPassword\n11\n".getBytes()
        ));

        TerminalView view = new TerminalView(controller);

        assertEquals(0, view.run());

        String actual = output.toString();

        assertTrue(actual.contains("Login successful."));
        assertTrue(actual.contains("New password:"));
        assertTrue(actual.contains("Password changed"));

        try {
            verify(users).changePassword(
                    "alice", "newPassword", "newPassword"
            );
        } catch (TopSecretException e) {
            fail();
        }
    }
}