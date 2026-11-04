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

// Week-2 controller: the full HW5 UserInterface logic, moved
// not rewritten. Only two mechanical changes: println becomes
// a returned String, and Scanner reads become parameters, with
// the username passed explicitly instead of a currentUser field.
// Deliberate HW5-to-HW6 changes, all flagged: missions join the
// menus (main menu 1-9, browse menu gains Edit at 4, Back at 5);
// agent creation takes no missionID (missions are assigned
// later through assignAgentToMission); an invalid itemType in
// readEntry returns "Invalid option" instead of the old UI's
// impossible-case text, since the web can pass any type.
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

    // First account: create the user, then set the password.
    // The first account is admin by the bootstrap rule in main.
    @Override
    public String createFirstAccount(String username, String password1, String passwordConfirm) {
        try {
            users.createUser(username);
            users.changePassword(username, password1, passwordConfirm);
        } catch (TopSecretException e) {
            return e.getMessage();
        }
        return "Account is created!";
    }

    @Override
    public String changePassword(String username, String password1, String passwordConfirm) {
        try {
            users.changePassword(username, password1, passwordConfirm);
        } catch (TopSecretException e) {
            return e.getMessage();
        }
        return "Password changed";
    }

    // Admin-created users set their own password at first login,
    // so success prints nothing, exactly like the HW5 UI.
    @Override
    public String createUser(String adminUsername, String newUsername) {
        if (!access.isAdmin(adminUsername)) {
            return "Not allowed";
        }
        try {
            users.createUser(newUsername);
        } catch (TopSecretException e) {
            return e.getMessage();
        }
        return "";
    }

    @Override
    public String manageAccess(String adminUsername, String targetUsername, String action) {
        if (!access.isAdmin(adminUsername)) {
            return "Not allowed";
        }
        boolean ok;
        String word;
        if (action.equalsIgnoreCase("promote")) {
            ok = access.promoteUser(adminUsername, targetUsername);
            word = "promoted";
        } else if (action.equalsIgnoreCase("demote")) {
            ok = access.demoteUser(adminUsername, targetUsername);
            word = "demoted";
        } else {
            return "Invalid option";
        }
        return ok ? targetUsername + " was " + word : "Not allowed";
    }

    @Override
    public boolean isAdmin(String username) {
        return access.isAdmin(username);
    }
    // Manage-access screen: all usernames except the caller,
// one per line. Admin only; the caller is never listed.
    @Override
    public String listUsernames(String username) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        List<String> names = new ArrayList<>();
        for (String name : users.listUsernames()) {
            if (!name.equals(username)) {
                names.add(name);
            }
        }
        return String.join("\n", names);
    }


    // Main menu text. Admin options only for admins; standard
    // users see 1-5 and 9, like the HW5 menu showed 1-4 and 8.
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
        menu.append("9. Send or view messages\n");
        if (access.isAdmin(username)) {
            menu.append("10. Assign agent to mission\n");
        }
        menu.append("11. Exit/logout\n");
        menu.append("Enter an option number:");
        return menu.toString();
    }

    // Browse menu text for one record type. Add and Edit are
    // admin only; Back is always last.
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

    @Override
    public String listEntries(String username, String itemType) {
        List<String> lines = new ArrayList<>();
        if (itemType.equals("briefs")) {
            for (Brief b : store.listBriefs()) {
                lines.add(b.getBriefID() + " : " + b.getTitle());
            }
        } else if (itemType.equals("agents")) {
            for (Agent a : store.listAgents()) {
                lines.add(a.getAgentID() + " : " + a.getName());
            }
        } else if (itemType.equals("facilities")) {
            for (Facility f : store.listFacilities()) {
                lines.add(f.getFacilityID() + " : " + f.getName());
            }
        } else if (itemType.equals("missions")) {
            for (Mission m : store.listMissions()) {
                lines.add(m.getMissionID() + " : " + m.getTitle());
            }
        } else {
            return "Invalid option";
        }
        return String.join("\n", lines);
    }

    @Override
    public String readEntry(String username, String itemType, int id) {
        if (itemType.equals("briefs")) {
            Brief requested = store.getBrief(id);
            if (requested == null) {
                return "No brief found with this id";
            }
            User userObj = users.getUserByUsername(username);
            if (userObj != null) {
                audit.log(new LoggedAction(LoggedAction.READ, userObj, LocalDateTime.now().toString(), itemType + " " + id));
            }
            return "Title: " + requested.getTitle() + "\nDate: " + requested.getDate() + "\nContent: " + requested.getContent();
        } else if (itemType.equals("agents")) {
            Agent requested = store.getAgent(id);
            if (requested == null) {
                return "No agent found with this id";
            }
            User userObj = users.getUserByUsername(username);
            if (userObj != null) {
                audit.log(new LoggedAction(LoggedAction.READ, userObj, LocalDateTime.now().toString(), itemType + " " + id));
            }
            return "Name: " + requested.getName() + "\nDoB: " + requested.getDate_of_birth() + "\nDoDeath: " + requested.getDate_of_death() + "\nNotes: " + requested.getNotes();
        } else if (itemType.equals("facilities")) {
            Facility requested = store.getFacility(id);
            if (requested == null) {
                return "No facility found with this id";
            }
            User userObj = users.getUserByUsername(username);
            if (userObj != null) {
                audit.log(new LoggedAction(LoggedAction.READ, userObj, LocalDateTime.now().toString(), itemType + " " + id));
            }
            return "Name: " + requested.getName() + "\nAbbreviation: " + requested.getAbbreviation();
        } else if (itemType.equals("missions")) {
            Mission requested = store.getMission(id);
            if (requested == null) {
                return "No mission found with this id";
            }
            User userObj = users.getUserByUsername(username);
            if (userObj != null) {
                audit.log(new LoggedAction(LoggedAction.READ, userObj, LocalDateTime.now().toString(), itemType + " " + id));
            }
            return "Title: " + requested.getTitle() + "\nFacility ID: " + requested.getFacilityID();
        } else {
            return "Invalid option";
        }
    }

    @Override
    public String addEntry(String username, String itemType, List<String> fields) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        try {
            if (itemType.equals("briefs")) {
                Brief added = store.addBrief(new Brief(Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3)));
                User userObj = users.getUserByUsername(username);
                if (userObj != null) {
                    audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "briefs " + added.getBriefID()));
                }
            } else if (itemType.equals("agents")) {
                Agent added = store.addAgent(new Agent(Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
                User userObj = users.getUserByUsername(username);
                if (userObj != null) {
                    audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "agents " + added.getAgentID()));
                }
            } else if (itemType.equals("facilities")) {
                Facility added = store.addFacility(new Facility(fields.get(0), fields.get(1)));
                User userObj = users.getUserByUsername(username);
                if (userObj != null) {
                    audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "facilities " + added.getFacilityID()));
                }
            } else if (itemType.equals("missions")) {
                Mission added = store.addMission(new Mission(fields.get(0), Integer.parseInt(fields.get(1))));
                User userObj = users.getUserByUsername(username);
                if (userObj != null) {
                    audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "missions " + added.getMissionID()));
                }
            } else {
                return "Invalid option";
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return listEntries(username, itemType);
    }

    @Override
    public String editEntry(String username, String itemType, int id, List<String> fields) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        try {
            if (itemType.equals("briefs")) {
                if (store.getBrief(id) == null) {
                    return "No brief found with this id";
                }
                store.updateBrief(new Brief(id, Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3)));
            } else if (itemType.equals("agents")) {
                Agent existing = store.getAgent(id);
                if (existing == null) {
                    return "No agent found with this id";
                }
                store.updateAgent(new Agent(id, Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
            } else if (itemType.equals("facilities")) {
                if (store.getFacility(id) == null) {
                    return "No facility found with this id";
                }
                store.updateFacility(new Facility(id, fields.get(0), fields.get(1)));
            } else if (itemType.equals("missions")) {
                if (store.getMission(id) == null) {
                    return "No mission found with this id";
                }
                store.updateMission(new Mission(id, fields.get(0), Integer.parseInt(fields.get(1))));
            } else {
                return "Invalid option";
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        // No audit yet: LoggedAction has no EDIT value (D's call).
        return listEntries(username, itemType);
    }

    @Override
    public String reviewAuditLog(String username) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        List<String> lines = new ArrayList<>();
        for (LoggedAction log : audit.getLog()) {
            String type;
            if (log.getAction() == LoggedAction.READ) {
                type = "read";
            } else if (log.getAction() == LoggedAction.CREATE) {
                type = "create";
            } else {
                type = "edit";
            }
            lines.add(log.getDateTime() + " : " + log.getUser().getUsername() + " : " + type + " : " + log.getRecord());
        }
        return String.join("\n", lines);
    }

    // The store throws IllegalArgumentException once implemented
    // (missing agent/mission, already assigned); today it throws
    // UnsupportedOperationException, so RuntimeException covers
    // both and the page never crashes.
    @Override
    public String assignAgentToMission(String username, int agentId, int missionId) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        try {
            store.assignAgentToMission(agentId, missionId);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return "Agent " + agentId + " assigned to mission " + missionId;
    }

    // Any logged-in user may send one. The agent is found by
    // name, case-insensitively, first match wins.
    @Override
    public String sendMessage(String username, String agentName, String message) {
        Agent recipient = null;
        for (Agent a : store.listAgents()) {
            if (a.getName().equalsIgnoreCase(agentName)) {
                recipient = a;
                break;
            }
        }
        if (recipient == null) {
            return "No agent found with this name";
        }
        messages.createMessage(recipient, message);
        return "Message sent to " + recipient.getName();
    }

    // Only the caller's own messages: the agent whose name
    // matches the logged-in username, case-insensitively.
    // No admin permission needed.
    @Override
    public String viewMessages(String username) {
        Agent self = null;
        for (Agent a : store.listAgents()) {
            if (a.getName().equalsIgnoreCase(username)) {
                self = a;
                break;
            }
        }
        if (self == null) {
            return "No messages";
        }
        List<Message> found = messages.retrieveMessages(self);
        if (found.isEmpty()) {
            return "No messages";
        }
        List<String> lines = new ArrayList<>();
        for (Message m : found) {
            lines.add(m.getMessageContent());
        }
        return String.join("\n", lines);
    }
}
