import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessControlTest {

    @Test
    public void testIsAdmin() {
        Set<String> admins = new HashSet<>(Set.of("admin"));
        Set<String> users = new HashSet<>(Set.of("admin", "agent"));

        AccessControl accessControl = new AccessControl(admins, users);

        assertTrue(accessControl.isAdmin("admin"));
        assertFalse(accessControl.isAdmin("agent"));
    }

    @Test
    public void testPromoteUser() {
        Set<String> admins = new HashSet<>(Set.of("admin"));
        Set<String> users = new HashSet<>(Set.of("admin", "agent"));

        AccessControl accessControl = new AccessControl(admins, users);

        assertTrue(accessControl.promoteUser("admin", "agent"));
        assertTrue(accessControl.isAdmin("agent"));
    }

    @Test
    public void testDemoteUser() {
        Set<String> admins = new HashSet<>(Set.of("admin", "admin2"));
        Set<String> users = new HashSet<>(Set.of("admin", "admin2", "agent"));

        AccessControl accessControl = new AccessControl(admins, users);

        assertTrue(accessControl.demoteUser("admin", "admin2"));
        assertFalse(accessControl.isAdmin("admin2"));
    }

    @Test
    public void testCannotDemoteSelf() {
        Set<String> admins = new HashSet<>(Set.of("admin"));
        Set<String> users = new HashSet<>(Set.of("admin", "agent"));

        AccessControl accessControl = new AccessControl(admins, users);

        assertFalse(accessControl.demoteUser("admin", "admin"));
        assertTrue(accessControl.isAdmin("admin"));
    }

    @Test
    public void testHasPermission() {
        Set<String> admins = new HashSet<>(Set.of("admin"));
        Set<String> users = new HashSet<>(Set.of("admin", "agent"));

        AccessControl accessControl = new AccessControl(admins, users);

        assertTrue(accessControl.hasPermission("admin", "read"));
        assertTrue(accessControl.hasPermission("agent", "read"));
        assertTrue(accessControl.hasPermission("admin", "create"));
        assertFalse(accessControl.hasPermission("agent", "create"));
        assertTrue(accessControl.hasPermission("admin", "audit_log"));
        assertFalse(accessControl.hasPermission("agent", "audit_log"));
    }
}
