import java.util.HashSet;
import java.util.Set;
import java.nio.file.Paths;
public class TopSecret {
    public static void main(String[] args) {

        DataStoreInterface store = new SQLiteDataStore();
        UserManagerInterface users = new UserManager(store);
        Set<String> userSet = new HashSet<>();
        Set<String> adminSet = new HashSet<>();
        for (User u : store.listUsers()) {
            userSet.add(u.getUsername());
            if (adminSet.isEmpty()) {
                adminSet.add(u.getUsername());
            }
        }
        AccessControlInterface access =
                new AccessControl(adminSet, userSet, Paths.get("roles.txt"));
        AuditLogInterface audit = new AuditLog(store);
        UserInterface ui = new UserInterface(users, access, store, audit);
        System.exit(ui.run());
    }
}

