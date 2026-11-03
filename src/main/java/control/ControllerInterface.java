package control;

import java.util.List;

// Shared control contract for TopSecret. Both the terminal view
// and the web server call these methods, so control logic is
// written once. Methods returning String return the text to
// display, including error text. No TopSecretException escapes
// the controller; the implementation catches it and returns
// the message. itemType is "briefs", "agents", "facilities",
// or "missions". Multi-line results join lines with "\n" and
// have no trailing newline. Field order for addEntry/editEntry
// matches the terminal prompts: briefs = missionID, title, date,
// content; agents = facilityID, name, date_of_birth,
// date_of_death, notes (no missionID: agents are assigned to
// missions later through assignAgentToMission, so a new agent
// is created with a null missionID); facilities = name,
// abbreviation; missions = title, facilityID.
// Admin-gated methods return "Not allowed" for non-admins:
// createUser, manageAccess, addEntry, editEntry,
// reviewAuditLog, assignAgentToMission.
// Note the two shapes that caused confusion: readEntry takes
// no fields (it fetches the record itself), and addEntry
// takes no id (the database assigns a new record's id).
// Only editEntry takes both id and fields.
public interface ControllerInterface {
    // Login and accounts
    boolean hasUsers();
    boolean verifyLogin(String username, String password);
    boolean needsPasswordSetup(String username);
    String createFirstAccount(String username, String password1, String passwordConfirm);
    String changePassword(String username, String password1, String passwordConfirm);
    String createUser(String adminUsername, String newUsername);
    String manageAccess(String adminUsername, String targetUsername, String action);
    boolean isAdmin(String username);

    // Navigation and records, shared by terminal and web
    String showMainMenu(String username);
    String browseMainMenu(String username, String itemType);
    String listEntries(String username, String itemType);
    String readEntry(String username, String itemType, int id);
    String addEntry(String username, String itemType, List<String> fields);
    // Terminal editing. The store update methods back this;
    // edits are not audit-logged until LoggedAction gains an
    // EDIT value.
    String editEntry(String username, String itemType, int id, List<String> fields);
    String reviewAuditLog(String username);

    // Joint operations (HW6). The store declares these methods;
    // until C implements them they throw, and the controller
    // returns the message instead of crashing.
    String assignAgentToMission(String username, int agentId, int missionId);

    // Messages (HW6, D's feature, terminal only, not on the web)
    String sendMessage(String username, String agentName, String message);
    String viewMessages(String username);
}
