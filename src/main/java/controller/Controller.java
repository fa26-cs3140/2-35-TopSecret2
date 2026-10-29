package controller;

import java.util.List;
import model.AccessControlInterface;
import model.AuditLogInterface;
import model.DataStoreInterface;
import model.MessageInterface;
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
    private final MessageInterface messages;

    public Controller(UserManagerInterface users, AccessControlInterface access,
                      DataStoreInterface store, AuditLogInterface audit,
                      MessageInterface messages) {
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
        return "";
    }

    // Week 2: users.changePassword, returning its message on error.
    @Override
    public String changePassword(String username, String password1, String passwordConfirm) {
        return "";
    }

    // Week 2: admin-gated users.createUser.
    @Override
    public String createUser(String adminUsername, String newUsername) {
        return "";
    }

    // Week 2: admin-gated promote/demote, refusing self-action,
    // with the HW5 wording ("<name> was promoted" / "Not allowed").
    @Override
    public String manageAccess(String adminUsername, String targetUsername, String action) {
        return "";
    }

    // Week 2: the 1-8 menu text, admin options only for admins.
    @Override
    public String showMainMenu(String username) {
        return "";
    }

    // Week 2: the list/read/add/back text for one record type.
    @Override
    public String browseMainMenu(String username, String itemType) {
        return "";
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
