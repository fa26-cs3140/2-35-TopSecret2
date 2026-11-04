
package view;

import control.ControllerInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TerminalViewTest {

    private ControllerInterface controller;
    private TerminalView view;

    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    public void setUp() {
        controller = mock(ControllerInterface.class);

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

    private void createView(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TerminalView(controller);
    }

    private String getOutput() {
        return output.toString();
    }

    private void mockSuccessfulLogin(boolean admin) {
        when(controller.hasUsers()).thenReturn(true);
        when(controller.needsPasswordSetup("alice")).thenReturn(false);
        when(controller.verifyLogin("alice", "password")).thenReturn(true);
        when(controller.isAdmin("alice")).thenReturn(admin);
        when(controller.showMainMenu("alice")).thenReturn("Main menu");
    }

    // ---------------- CONSTRUCTOR ----------------

    @Test
    public void constructorInitializesTerminalView() {
        createView("");

        assertNotNull(view);
    }

    // ---------------- FIRST ACCOUNT / SIGNUP ----------------

    @Test
    public void runCreatesFirstAccountAndEntersMainMenu() {
        createView("alice\npassword\npassword\n9\n");

        when(controller.hasUsers()).thenReturn(false);
        when(controller.createFirstAccount(
                "alice", "password", "password"
        )).thenReturn("Account is created!");
        when(controller.isAdmin("alice")).thenReturn(true);

        assertEquals(0, view.run());

        verify(controller).createFirstAccount(
                "alice", "password", "password"
        );
        verify(controller).showMainMenu("alice");
    }

    @Test
    public void runReturnsOneWhenFirstAccountCreationFails() {
        createView("alice\npassword\nwrong\n");

        when(controller.hasUsers()).thenReturn(false);
        when(controller.createFirstAccount(
                "alice", "password", "wrong"
        )).thenReturn("Passwords do not match.");

        assertEquals(1, view.run());

        verify(controller).createFirstAccount(
                "alice", "password", "wrong"
        );
        verify(controller, never()).showMainMenu(anyString());
    }

    @Test
    public void runReturnsOneWhenSignupInputEnds() {
        createView("");

        when(controller.hasUsers()).thenReturn(false);

        assertEquals(1, view.run());

        verify(controller, never()).createFirstAccount(
                anyString(), anyString(), anyString()
        );
    }

    // ---------------- LOGIN ----------------

    @Test
    public void runLogsInExistingUser() {
        createView("alice\npassword\n9\n");
        mockSuccessfulLogin(true);

        assertEquals(0, view.run());

        verify(controller).verifyLogin("alice", "password");
        verify(controller).showMainMenu("alice");
        assertTrue(getOutput().contains("Login successful."));
    }

    @Test
    public void runRetriesAfterIncorrectLogin() {
        createView("alice\nwrong\nalice\npassword\n9\n");

        when(controller.hasUsers()).thenReturn(true);
        when(controller.needsPasswordSetup("alice")).thenReturn(false);
        when(controller.verifyLogin("alice", "wrong")).thenReturn(false);
        when(controller.verifyLogin("alice", "password")).thenReturn(true);
        when(controller.isAdmin("alice")).thenReturn(true);
        when(controller.showMainMenu("alice")).thenReturn("Main menu");

        assertEquals(0, view.run());

        verify(controller).verifyLogin("alice", "wrong");
        verify(controller).verifyLogin("alice", "password");
        assertTrue(getOutput().contains(
                "Incorrect username or password."
        ));
    }

    @Test
    public void runSetsUpPasswordForNewUser() {
        createView("alice\nnewPassword\nnewPassword\n9\n");

        when(controller.hasUsers()).thenReturn(true);
        when(controller.needsPasswordSetup("alice")).thenReturn(true);
        when(controller.changePassword(
                "alice", "newPassword", "newPassword"
        )).thenReturn("Password changed");
        when(controller.isAdmin("alice")).thenReturn(false);

        assertEquals(0, view.run());

        verify(controller).changePassword(
                "alice", "newPassword", "newPassword"
        );
        verify(controller).showMainMenu("alice");
    }

    @Test
    public void runRetriesLoginWhenPasswordSetupFails() {
        createView(
                "alice\nfirst\nsecond\nalice\npassword\n9\n"
        );

        when(controller.hasUsers()).thenReturn(true);
        when(controller.needsPasswordSetup("alice"))
                .thenReturn(true, false);
        when(controller.changePassword(
                "alice", "first", "second"
        )).thenReturn("Passwords do not match.");
        when(controller.verifyLogin("alice", "password"))
                .thenReturn(true);
        when(controller.isAdmin("alice")).thenReturn(true);
        when(controller.showMainMenu("alice")).thenReturn("Main menu");

        assertEquals(0, view.run());

        verify(controller).changePassword(
                "alice", "first", "second"
        );
        verify(controller).verifyLogin("alice", "password");
    }

    // ---------------- MAIN MENU ----------------

    @Test
    public void adminCanExitMainMenu() {
        createView("alice\npassword\n9\n");
        mockSuccessfulLogin(true);

        assertEquals(0, view.run());

        verify(controller).showMainMenu("alice");
    }

    @Test
    public void regularUserCanExitMainMenu() {
        createView("alice\npassword\n6\n");
        mockSuccessfulLogin(false);

        assertEquals(0, view.run());

        verify(controller).showMainMenu("alice");
    }

    @Test
    public void adminCanChangePassword() {
        createView(
                "alice\npassword\n5\nnewPassword\nnewPassword\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.changePassword(
                "alice", "newPassword", "newPassword"
        )).thenReturn("Password changed");

        assertEquals(0, view.run());

        verify(controller).changePassword(
                "alice", "newPassword", "newPassword"
        );
    }

    @Test
    public void adminCanCreateUser() {
        createView("alice\npassword\n6\nbob\n9\n");
        mockSuccessfulLogin(true);

        when(controller.createUser("alice", "bob"))
                .thenReturn("User created.");

        assertEquals(0, view.run());

        verify(controller).createUser("alice", "bob");
    }

    @Test
    public void adminCanPromoteUser() {
        createView("alice\npassword\n7\nPromote bob\n9\n");
        mockSuccessfulLogin(true);

        when(controller.manageAccess("alice", "bob", "Promote"))
                .thenReturn("User promoted.");

        assertEquals(0, view.run());

        verify(controller).manageAccess("alice", "bob", "Promote");
    }

    @Test
    public void adminCanDemoteUser() {
        createView("alice\npassword\n7\nDemote bob\n9\n");
        mockSuccessfulLogin(true);

        when(controller.manageAccess("alice", "bob", "Demote"))
                .thenReturn("User demoted.");

        assertEquals(0, view.run());

        verify(controller).manageAccess("alice", "bob", "Demote");
    }

    @Test
    public void adminCanReviewAuditLog() {
        createView("alice\npassword\n8\n9\n");
        mockSuccessfulLogin(true);

        when(controller.reviewAuditLog("alice"))
                .thenReturn("Audit log");

        assertEquals(0, view.run());

        verify(controller).reviewAuditLog("alice");
    }

    @Test
    public void adminCanReturnFromInvalidManageAccessInput() {
        createView(
                "alice\npassword\n7\ninvalid input\nPromote bob\n9\n"
        );
        mockSuccessfulLogin(true);

        assertEquals(0, view.run());

        verify(controller).manageAccess(
                "alice", "bob", "Promote"
        );
        assertTrue(getOutput().contains("Invalid input."));
    }

    // ---------------- BROWSE MENUS ----------------

    @Test
    public void adminCanOpenBriefsMenu() {
        createView("alice\npassword\n1\n5\n9\n");
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "briefs"))
                .thenReturn("Browse briefs");

        assertEquals(0, view.run());

        verify(controller).browseMainMenu("alice", "briefs");
    }

    @Test
    public void adminCanOpenAgentsMenu() {
        createView("alice\npassword\n2\n5\n9\n");
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "agents"))
                .thenReturn("Browse agents");

        assertEquals(0, view.run());

        verify(controller).browseMainMenu("alice", "agents");
    }

    @Test
    public void adminCanOpenFacilitiesMenu() {
        createView("alice\npassword\n3\n5\n9\n");
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "facilities"))
                .thenReturn("Browse facilities");

        assertEquals(0, view.run());

        verify(controller).browseMainMenu("alice", "facilities");
    }

    @Test
    public void adminCanOpenMissionsMenu() {
        createView("alice\npassword\n4\n5\n9\n");
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "missions"))
                .thenReturn("Browse missions");

        assertEquals(0, view.run());

        verify(controller).browseMainMenu("alice", "missions");
    }

    // ---------------- LIST / READ RECORDS ----------------

    @Test
    public void adminCanListBriefs() {
        createView("alice\npassword\n1\n1\n5\n9\n");
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "briefs"))
                .thenReturn("Browse briefs");
        when(controller.listEntries("alice", "briefs"))
                .thenReturn("Brief list");

        assertEquals(0, view.run());

        verify(controller).listEntries("alice", "briefs");
    }

    @Test
    public void adminCanReadBrief() {
        createView("alice\npassword\n1\n2\n12\n5\n9\n");
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "briefs"))
                .thenReturn("Browse briefs");
        when(controller.readEntry("alice", "briefs", 12))
                .thenReturn("Brief details");

        assertEquals(0, view.run());

        verify(controller).readEntry("alice", "briefs", 12);
    }

    @Test
    public void regularUserCanListAndReadBriefs() {
        createView("alice\npassword\n1\n1\n2\n12\n3\n6\n");
        mockSuccessfulLogin(false);

        when(controller.browseMainMenu("alice", "briefs"))
                .thenReturn("Browse briefs");
        when(controller.listEntries("alice", "briefs"))
                .thenReturn("Brief list");
        when(controller.readEntry("alice", "briefs", 12))
                .thenReturn("Brief details");

        assertEquals(0, view.run());

        verify(controller).listEntries("alice", "briefs");
        verify(controller).readEntry("alice", "briefs", 12);
        verify(controller, never()).addEntry(
                anyString(), anyString(), anyList()
        );
    }

    // ---------------- ADD RECORDS ----------------

    @Test
    public void adminCanAddBrief() {
        createView(
                "alice\npassword\n1\n3\n10\nTitle\n2026-11-03\nContent\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "briefs"))
                .thenReturn("Browse briefs");
        when(controller.addEntry(eq("alice"), eq("briefs"), anyList()))
                .thenReturn("Brief added.");

        assertEquals(0, view.run());

        verify(controller).addEntry(
                "alice",
                "briefs",
                List.of("10", "Title", "2026-11-03", "Content")
        );
    }

    @Test
    public void adminCanAddAgent() {
        createView(
                "alice\npassword\n2\n3\n4\nAgent Name\n1990-01-01\n\nNotes\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "agents"))
                .thenReturn("Browse agents");
        when(controller.addEntry(eq("alice"), eq("agents"), anyList()))
                .thenReturn("Agent added.");

        assertEquals(0, view.run());

        verify(controller).addEntry(
                "alice",
                "agents",
                List.of("4", "Agent Name", "1990-01-01", "", "Notes")
        );
    }

    @Test
    public void adminCanAddFacility() {
        createView(
                "alice\npassword\n3\n3\nFacility Name\nFAC\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "facilities"))
                .thenReturn("Browse facilities");
        when(controller.addEntry(
                eq("alice"), eq("facilities"), anyList()
        )).thenReturn("Facility added.");

        assertEquals(0, view.run());

        verify(controller).addEntry(
                "alice",
                "facilities",
                List.of("Facility Name", "FAC")
        );
    }

    @Test
    public void adminCanAddMission() {
        createView(
                "alice\npassword\n4\n3\nMission Title\n7\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "missions"))
                .thenReturn("Browse missions");
        when(controller.addEntry(eq("alice"), eq("missions"), anyList()))
                .thenReturn("Mission added.");

        assertEquals(0, view.run());

        verify(controller).addEntry(
                "alice",
                "missions",
                List.of("Mission Title", "7")
        );
    }

    // ---------------- EDIT RECORDS ----------------

    @Test
    public void adminCanEditBrief() {
        createView(
                "alice\npassword\n1\n4\n12\n10\nNew title\n2026-11-03\nNew content\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "briefs"))
                .thenReturn("Browse briefs");
        when(controller.editEntry(
                eq("alice"), eq("briefs"), eq(12), anyList()
        )).thenReturn("Brief updated.");

        assertEquals(0, view.run());

        verify(controller).editEntry(
                "alice",
                "briefs",
                12,
                List.of("10", "New title", "2026-11-03", "New content")
        );
    }

    @Test
    public void adminCanEditAgent() {
        createView(
                "alice\npassword\n2\n4\n12\n4\nAgent Name\n1990-01-01\n\nNotes\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "agents"))
                .thenReturn("Browse agents");
        when(controller.editEntry(
                eq("alice"), eq("agents"), eq(12), anyList()
        )).thenReturn("Agent updated.");

        assertEquals(0, view.run());

        verify(controller).editEntry(
                "alice",
                "agents",
                12,
                List.of("4", "Agent Name", "1990-01-01", "", "Notes")
        );
    }

    @Test
    public void adminCanEditFacility() {
        createView(
                "alice\npassword\n3\n4\n12\nFacility Name\nFAC\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "facilities"))
                .thenReturn("Browse facilities");
        when(controller.editEntry(
                eq("alice"), eq("facilities"), eq(12), anyList()
        )).thenReturn("Facility updated.");

        assertEquals(0, view.run());

        verify(controller).editEntry(
                "alice",
                "facilities",
                12,
                List.of("Facility Name", "FAC")
        );
    }

    @Test
    public void adminCanEditMission() {
        createView(
                "alice\npassword\n4\n4\n12\nMission Title\n7\n5\n9\n"
        );
        mockSuccessfulLogin(true);

        when(controller.browseMainMenu("alice", "missions"))
                .thenReturn("Browse missions");
        when(controller.editEntry(
                eq("alice"), eq("missions"), eq(12), anyList()
        )).thenReturn("Mission updated.");

        assertEquals(0, view.run());

        verify(controller).editEntry(
                "alice",
                "missions",
                12,
                List.of("Mission Title", "7")
        );
    }
}
