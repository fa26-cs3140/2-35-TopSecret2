import java.util.Set;

public class AccessControl implements AccessControlInterface {

    private final Set<String> admins;
    private final Set<String> users;

    public AccessControl(Set<String> admins, Set<String> users) {
        this.admins = admins;
        this.users = users;
    }

    @Override
    public boolean isAdmin(String username) {
        return username != null && admins.contains(username);
    }

    @Override
    public boolean promoteUser(String adminUsername, String targetUsername) {
        if (!isAdmin(adminUsername)
                || targetUsername == null
                || !users.contains(targetUsername)
                || admins.contains(targetUsername)) {
            return false;
        }

        return admins.add(targetUsername);
    }

    @Override
    public boolean demoteUser(String adminUsername, String targetUsername) {
        if (!isAdmin(adminUsername)
                || targetUsername == null
                || adminUsername.equals(targetUsername)
                || !admins.contains(targetUsername)) {
            return false;
        }

        return admins.remove(targetUsername);
    }

    @Override
    public boolean hasPermission(String username, String action) {
        if (username == null || action == null || !users.contains(username)) {
            return false;
        }

        if (action.equalsIgnoreCase("read")) {
            return true;
        }

        if (isAdmin(username)) {
            return action.equalsIgnoreCase("create")
                    || action.equalsIgnoreCase("create_entry")
                    || action.equalsIgnoreCase("create_user")
                    || action.equalsIgnoreCase("audit_log");
        }

        return false;
    }
}
