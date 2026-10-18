import java.util.Scanner;
import java.util.List;

// Command-line interface for TopSecret.
// Validates user input, asks ProgramControl for results, and displays them.
public class UserInterface implements UserInterfaceInterface {
    private final UserManagerInterface users;
    private final AccessControlInterface access;
    private final DataStoreInterface store;
    private final AuditLogInterface audit;
    private final Scanner scnr;
    private String currentUser;
    private User currentUserObj;

    public UserInterface(UserManagerInterface users,
                         AccessControlInterface access,
                         DataStoreInterface store,
                         AuditLogInterface audit) {
        this.users = users;
        this.access = access;
        this.store = store;
        this.audit = audit;
        this.scnr = new Scanner(System.in);
    }

    @Override
    public int run() {
        // TODO
        return 0;
    }

    @Override
    public boolean loginStep() {
        // TODO
        return false;
    }

    @Override
    public void showMainMenu() {
        // TODO
    }

    @Override
    public void browseMenu(String itemType) {
        // TODO
    }

    @Override
    public void listEntries(String itemType) {
        // TODO
    }

    @Override
    public void readEntry(String itemType) {
        // TODO
    }

    @Override
    public void addEntry(String itemType) {
        // TODO
    }

    @Override
    public void reviewAuditLog() {
        // TODO
    }

    @Override
    public void changePassword() {
        // TODO
    }

    @Override
    public void createUser() {
        // TODO
    }

    @Override
    public void manageAccess() {
        // TODO
    }

    @Override
    public void searchMissions() {
        // TODO
    }

}
