package control;

import java.util.List;
import model.AccessControlInterface;
import model.AuditLogInterface;
import model.DataStoreInterface;
import model.MessageInterface;
import model.UserManagerInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Unit tests for the week-1 Controller stub. Collaborators are
// Mockito mocks. The four delegations are verified against the
// mocks; every other method is verified to return the stub
// default (empty String) until week 2 moves the HW5 logic in.
// Run: .\gradlew test --tests "controller.ControllerTest" --console=plain
public class ControllerTest {
    private UserManagerInterface users;
    private AccessControlInterface access;
    private Controller controller;

    @BeforeEach
    void setUp() {
        users = mock(UserManagerInterface.class);
        access = mock(AccessControlInterface.class);
        controller = new Controller(users, access,
                mock(DataStoreInterface.class),
                mock(AuditLogInterface.class),
                mock(MessageInterface.class));
    }

    @Test
    void hasUsersDelegatesToUsers() {
        when(users.hasUser()).thenReturn(true);
        assertTrue(controller.hasUsers());
        when(users.hasUser()).thenReturn(false);
        assertFalse(controller.hasUsers());
    }

    @Test
    void verifyLoginDelegatesToUsers() {
        when(users.verifyLogin("alice", "Pass123")).thenReturn(true);
        assertTrue(controller.verifyLogin("alice", "Pass123"));
        verify(users).verifyLogin("alice", "Pass123");
    }

    @Test
    void needsPasswordSetupDelegatesToUsers() {
        when(users.needsPasswordSetup("alice")).thenReturn(true);
        assertTrue(controller.needsPasswordSetup("alice"));
        verify(users).needsPasswordSetup("alice");
    }

    @Test
    void isAdminDelegatesToAccess() {
        when(access.isAdmin("boss")).thenReturn(true);
        assertTrue(controller.isAdmin("boss"));
        verify(access).isAdmin("boss");
    }

    @Test
    void createFirstAccountReturnsEmptyStub() {
        assertEquals("", controller.createFirstAccount("alice", "Pass1", "Pass1"));
    }

    @Test
    void changePasswordReturnsEmptyStub() {
        assertEquals("", controller.changePassword("alice", "Pass1", "Pass1"));
    }

    @Test
    void createUserReturnsEmptyStub() {
        assertEquals("", controller.createUser("boss", "alice"));
    }

    @Test
    void manageAccessReturnsEmptyStub() {
        assertEquals("", controller.manageAccess("boss", "alice", "promote"));
    }

    @Test
    void showMainMenuReturnsEmptyStub() {
        assertEquals("", controller.showMainMenu("alice"));
    }

    @Test
    void browseMainMenuReturnsEmptyStub() {
        assertEquals("", controller.browseMainMenu("alice", "briefs"));
    }

    @Test
    void listEntriesReturnsEmptyStub() {
        assertEquals("", controller.listEntries("alice", "briefs"));
    }

    @Test
    void readEntryReturnsEmptyStub() {
        assertEquals("", controller.readEntry("alice", "briefs", 1));
    }

    @Test
    void addEntryReturnsEmptyStub() {
        assertEquals("", controller.addEntry("boss", "briefs",
                List.of("1", "Title", "2026-01-01", "Content")));
    }

    @Test
    void editEntryReturnsEmptyStub() {
        assertEquals("", controller.editEntry("boss", "briefs", 1,
                List.of("1", "Title", "2026-01-01", "Content")));
    }

    @Test
    void reviewAuditLogReturnsEmptyStub() {
        assertEquals("", controller.reviewAuditLog("boss"));
    }

    @Test
    void assignAgentToMissionReturnsEmptyStub() {
        assertEquals("", controller.assignAgentToMission("boss", 1, 2));
    }

    @Test
    void sendMessageReturnsEmptyStub() {
        assertEquals("", controller.sendMessage("boss", "Ghost", "Hello"));
    }

    @Test
    void viewMessagesReturnsEmptyStub() {
        assertEquals("", controller.viewMessages("alice"));
    }
}