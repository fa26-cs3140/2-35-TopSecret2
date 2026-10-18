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
        return false;
    }

    @Override
    public boolean promoteUser(String adminUsername, String targetUsername) {
        return false;
    }

    @Override
    public boolean demoteUser(String adminUsername, String targetUsername) {
        return false;
    }

    @Override
    public boolean hasPermission(String username, String action) {
        return false;
    }
}
