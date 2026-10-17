public interface AccessControlInterface {

    boolean isAdmin(String username);

    boolean promoteUser(String adminUsername, String targetUsername);

    boolean demoteUser(String adminUsername, String targetUsername);

    boolean hasPermission(String username, String action);
}