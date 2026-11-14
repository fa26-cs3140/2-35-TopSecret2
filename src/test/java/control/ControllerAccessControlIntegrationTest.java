package control;

import java.util.List;
import java.util.Set;
import model.AccessControl;
import model.AuditLogInterface;
import model.DataStoreInterface;
import model.UserManagerInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// Integration tests: a real AccessControl with in-memory roles
// (admin "boss", standard "alice" and "bob") behind the full
// Controller. Users, store, audit, and messages stay mocked:
// this file tests the controller-to-roles wiring only, that is,
// which menu options and actions each role can actually use.
// Run: .\gradlew test --tests "control.ControllerAccessControlIntegrationTest" --console=plain
public class ControllerAccessControlIntegrationTest {
    private AccessControl access;
    private UserManagerInterface users;
    private DataStoreInterface store;
    private Controller controller;

    @BeforeEach
    void setUp() {
        access = new AccessControl(Set.of("boss"), Set.of("alice", "bob"));
        users = mock(UserManagerInterface.class);
        store = mock(DataStoreInterface.class);
        controller = new Controller(users, access, store,
                mock(AuditLogInterface.class), mock(MessageManager.class), mock(SessionManager.class));
    }

    @Test
    void adminSeesAdminOptions() {
        String menu = controller.showMainMenu("boss");
        assertTrue(menu.contains("6. Create a new user"));
        assertTrue(menu.contains("7. Promote or Demote users"));
        assertTrue(menu.contains("8. Review audit log"));
        assertTrue(menu.contains("10. Assign agent to mission"));
    }

    @Test
    void standardUserSeesNoAdminOptions() {
        String menu = controller.showMainMenu("alice");
        assertFalse(menu.contains("6. Create a new user"));
        assertFalse(menu.contains("7. Promote or Demote users"));
        assertFalse(menu.contains("8. Review audit log"));
        assertFalse(menu.contains("10. Assign agent to mission"));
        assertTrue(menu.contains("9. Send or view messages"));
    }

    @Test
    void addEntryRejectsStandardUser() {
        assertEquals("Not allowed",
                controller.addEntry("alice", "briefs",
                        List.of("1", "T", "2026-01-01", "C")));
        verifyNoInteractions(store);
    }

    @Test
    void manageAccessPromotesWithRealRoles() {
        assertEquals("alice was promoted",
                controller.manageAccess("boss", "alice", "Promote"));
        assertTrue(controller.isAdmin("alice"));
        assertTrue(controller.showMainMenu("alice").contains("6. Create a new user"));
    }

    @Test
    void manageAccessDemotesWithRealRoles() {
        controller.manageAccess("boss", "alice", "Promote");
        assertEquals("alice was demoted",
                controller.manageAccess("boss", "alice", "Demote"));
        assertFalse(controller.isAdmin("alice"));
    }

    @Test
    void manageAccessRejectsNonAdminCaller() {
        assertEquals("Not allowed",
                controller.manageAccess("alice", "bob", "Promote"));
        assertFalse(controller.isAdmin("bob"));
    }

    @Test
    void manageAccessRejectsSelfDemotion() {
        assertEquals("Not allowed",
                controller.manageAccess("boss", "boss", "Demote"));
        assertTrue(controller.isAdmin("boss"));
    }

    @Test
    void createUserRejectsStandardUser() {
        assertEquals("Not allowed", controller.createUser("alice", "carol"));
        verifyNoInteractions(users);
    }

    @Test
    void reviewAuditLogRejectsStandardUser() {
        assertEquals("Not allowed", controller.reviewAuditLog("alice"));
    }

    @Test
    void browseMainMenuHidesAddEditForStandardUser() {
        String standard = controller.browseMainMenu("alice", "briefs");
        assertFalse(standard.contains("3. Add briefs"));
        assertFalse(standard.contains("4. Edit briefs"));
        String admin = controller.browseMainMenu("boss", "briefs");
        assertTrue(admin.contains("3. Add briefs"));
        assertTrue(admin.contains("4. Edit briefs"));
    }

    @Test
    void listUsernamesExcludesCaller() {
        when(users.listUsernames()).thenReturn(List.of("boss", "alice", "bob"));
        assertEquals("alice\nbob", controller.listUsernames("boss"));
        assertEquals("Not allowed", controller.listUsernames("alice"));
    }
}

