package controller;

import java.util.List;

// Shared control contract for TopSecret. Both the terminal view
// and the web server call these methods, so control logic is
// written once. Methods returning String return the text to
// display, including error text. No TopSecretException escapes
// the controller; the implementation catches it and returns
// the message. itemType is "briefs", "agents", "facilities",
// or "missions". Field order for addEntry/editEntry matches
// the HW5 terminal prompts: briefs = missionID, title, date,
// content; agents = facilityID, name, date_of_birth,
// date_of_death, notes; facilities = name, abbreviation;
// missions = title, facilityID.
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
    // Proposed addition: the team's doc omits it, but C's
    // terminal editing needs a controller edit path once the
    // store adds update methods. The week-1 stub returns a
    // not-yet-implemented text.
    String editEntry(String username, String itemType, int id, List<String> fields);
    String reviewAuditLog(String username);

    // Joint operations (HW6). The store declares these methods
    // but its current implementation throws
    // UnsupportedOperationException until C builds the
    // mission-agent join table, so this stays a stub for now.
    String assignAgentToMission(String username, int agentId, int missionId);

    // Messages (HW6, D's feature, terminal only, not on the web)
    String sendMessage(String username, String agentName, String message);
    String viewMessages(String username);
}
