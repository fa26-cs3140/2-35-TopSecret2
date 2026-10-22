public class TopSecret {
    public static void main(String[] args) {

        DataStoreInterface store = new SQLiteDataStore();
        UserManagerInterface users = null; //to be completed
        AccessControlInterface access = null;
        AuditLogInterface audit = null;
        UserInterface ui = new UserInterface(users, access, store, audit);
        System.exit(ui.run());
        }
    }

