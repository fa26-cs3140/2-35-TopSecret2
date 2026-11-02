package control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.AccessControlInterface;
import model.Agent;
import model.AuditLogInterface;
import model.Brief;
import model.DataStoreInterface;
import model.Facility;
import model.LoggedAction;
import model.Message;
import model.Mission;
import model.TopSecretException;
import model.User;
import model.UserManagerInterface;

// Week-1 stub of the controller. The four one-line delegations
// below are real; every other method returns an empty String
// until week 2 moves the HW5 logic in (see ControllerUML).
// Note: these imports assume the model move. If you compile
// before your teammates move their files into model, delete
// the import lines above.
public class Controller implements ControllerInterface {
    private final UserManagerInterface users;
    private final AccessControlInterface access;
    private final DataStoreInterface store;
    private final AuditLogInterface audit;
    private final MessageManager messages;

    public Controller(UserManagerInterface users, AccessControlInterface access,
                      DataStoreInterface store, AuditLogInterface audit,
                      MessageManager messages) {
        this.users = users;
        this.access = access;
        this.store = store;
        this.audit = audit;
        this.messages = messages;
    }

    @Override
    public boolean hasUsers() {
        return users.hasUser();
    }

    @Override
    public boolean verifyLogin(String username, String password) {
        return users.verifyLogin(username, password);
    }

    @Override
    public boolean needsPasswordSetup(String username) {
        return users.needsPasswordSetup(username);
    }

    @Override
    public boolean isAdmin(String username) {
        return access.isAdmin(username);
    }

    // Week 2: users.createUser + users.changePassword, with the
    // first account admin by the bootstrap rule in main.
    @Override
    public String createFirstAccount(String username, String password1, String passwordConfirm) {
        try{
            users.createUser(username);
            users.changePassword(username, password1, passwordConfirm);
        } catch (TopSecretException e){
            return e.getMessage();
        }
        return "Account is created!";
    }

    // Week 2: users.changePassword, returning its message on error.
    @Override
    public String changePassword(String username, String password1, String passwordConfirm) {
        try{
            users.changePassword(username, password1, passwordConfirm);
        } catch (TopSecretException e){
            return e.getMessage();
        }
        return "Password changed";
    }

    // Week 2: admin-gated users.createUser.
    @Override
    public String createUser(String adminUsername, String newUsername) {
        if (!access.isAdmin(adminUsername)) {
            return "Not allowed";
        };
        try {
            users.createUser(newUsername);
        } catch (TopSecretException e){
            return e.getMessage();
        }
        return "User created!";
    }

    // Week 2: admin-gated promote/demote, refusing self-action,
    // with the HW5 wording ("<name> was promoted" / "Not allowed").
    @Override
    public String manageAccess(String adminUsername, String targetUsername, String action) {
        if (!access.isAdmin(adminUsername)) {
            return "Not allowed";
        };
        boolean worked;
        if (action.equalsIgnoreCase("promote")) {
            worked = access.promoteUser(adminUsername, targetUsername);
        }else if (action.equalsIgnoreCase("demote")) {
            worked = access.demoteUser(adminUsername, targetUsername);
        }
        else {
            return "Invalid Option";
        }
        return worked ? targetUsername + " was " + action + "d." : "Not Allowed";
    }

    // Week 2: the 1-8 menu text, admin options only for admins.
    @Override
    public String showMainMenu(String username) {
        StringBuilder menu = new StringBuilder();
        menu.append("1. Browse briefs\n");
        menu.append("2. Browse agents\n");
        menu.append("3. Browse facilities\n");
        menu.append("4. Browse missions\n");
        menu.append("5. Change Password\n");
        if (access.isAdmin(username)) {
            menu.append("6. Create a new user\n");
            menu.append("7. Promote or Demote users\n");
            menu.append("8. Review audit log\n");
        }
        menu.append("9. Exit/logout\n");
        menu.append("Enter an option number:");
        return menu.toString();
    }

    // Week 2: the list/read/add/back text for one record type.
    @Override
    public String browseMainMenu(String username, String itemType) {
        if (!itemType.equals("briefs") && !itemType.equals("agents")
                && !itemType.equals("facilities") && !itemType.equals("missions")) {
            return "Invalid option";
        }
        StringBuilder menu = new StringBuilder();
        menu.append("1. List ").append(itemType).append("\n");
        menu.append("2. Read ").append(itemType).append("\n");
        if (access.isAdmin(username)) {
            menu.append("3. Add ").append(itemType).append("\n");
            menu.append("4. Edit ").append(itemType).append("\n");
        }
        menu.append("5. Go back to main menu\n");
        menu.append("Enter an option number:");
        return menu.toString();
    }

    // Week 2: one "id : name/title" line per record from the store.
    @Override
    public String listEntries(String username, String itemType) {
        return "";
    }

    // Week 2: one full record from the store, plus a READ audit.
    @Override
    public String readEntry(String username, String itemType, int id) {
        return "";
    }

    // Week 2: admin-gated store add, plus a CREATE audit and the
    // updated list text.
    @Override
    public String addEntry(String username, String itemType, List<String> fields) {
        return "";
    }

    // Stays a stub until C adds store update methods.
    @Override
    public String editEntry(String username, String itemType, int id, List<String> fields) {
        return "";
    }

    // Week 2: admin-gated audit.getLog() formatted as
    // time : username : read/create : record.
    @Override
    public String reviewAuditLog(String username) {
        return "";
    }

    // Stays a stub until C implements the store methods and the
    // mission-agent join table (they currently throw
    // UnsupportedOperationException).
    @Override
    public String assignAgentToMission(String username, int agentId, int missionId) {
        return "";
    }

    // Week 2: pass the message to the message store. Any
    // logged-in user may send one.
    @Override
    public String sendMessage(String username, String agentName, String message) {
        return "";
    }

    // Week 2: only the caller's own messages, matched
    // case-insensitively. No admin permission needed.
    @Override
    public String viewMessages(String username) {
        return "";
    }
}
