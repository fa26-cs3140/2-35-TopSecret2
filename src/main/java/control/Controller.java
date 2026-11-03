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
        List<String> lines = new ArrayList<>();
        switch (itemType) {
            case "briefs" -> {
                for (Brief b : store.listBriefs()) {
                    lines.add(b.getBriefID() + " : " + b.getTitle());
                }
            }
            case "agents" -> {
                for (Agent a : store.listAgents()) {
                    lines.add(a.getAgentID() + " : " + a.getName());
                }
            }
            case "facilities" -> {
                for (Facility f : store.listFacilities()) {
                    lines.add(f.getFacilityID() + " : " + f.getName());
                }
            }
            case "missions" -> {
                for (Mission m : store.listMissions()) {
                    lines.add(m.getMissionID() + " : " + m.getTitle());
                }
            }
            default -> {
                return "Invalid option";
            }
        }
        return String.join("\n", lines);
    }

    // Week 2: one full record from the store, plus a READ audit.
    @Override
    public String readEntry(String username, String itemType,int id, List<String> fields) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        try {
            switch (itemType) {
                case "briefs" -> {
                    Brief added = store.addBrief(new Brief(Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3)));
                    User userObj = users.getUserByUsername(username);
                    if (userObj != null) {
                        audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "briefs " + added.getBriefID()));
                    }
                }
                case "agents" -> {
                    Agent added = store.addAgent(new Agent(null, Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
                    User userObj = users.getUserByUsername(username);
                    if (userObj != null) {
                        audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "agents " + added.getAgentID()));
                    }
                }
                case "facilities" -> {
                    Facility added = store.addFacility(new Facility(fields.get(0), fields.get(1)));
                    User userObj = users.getUserByUsername(username);
                    if (userObj != null) {
                        audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "facilities " + added.getFacilityID()));
                    }
                }
                case "missions" -> {
                    Mission added = store.addMission(new Mission(fields.get(0), Integer.parseInt(fields.get(1))));
                    User userObj = users.getUserByUsername(username);
                    if (userObj != null) {
                        audit.log(new LoggedAction(LoggedAction.CREATE, userObj, LocalDateTime.now().toString(), "missions " + added.getMissionID()));
                    }
                }
                default -> {
                    return "Invalid option";
                }
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return listEntries(username, itemType);
    }

    // Week 2: admin-gated store add, plus a CREATE audit and the
    // updated list text.
    @Override
    public String addEntry(String username, String itemType, int id, List<String> fields) {
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
                store.updateAgent(new Agent(id, existing.getMissionID(), Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
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

    // Stays a stub until C adds store update methods.
    @Override
    public String editEntry(String username, String itemType, int id, List<String> fields) {
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
                Agent added = store.addAgent(new Agent(null, Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
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

    // Week 2: admin-gated audit.getLog() formatted as
    // time : username : read/create : record.
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

    // Stays a stub until C implements the store methods and the
    // mission-agent join table (they currently throw
    // UnsupportedOperationException).
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

    // Week 2: pass the message to the message store. Any
    // logged-in user may send one.
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
    };

    // Week 2: only the caller's own messages, matched
    // case-insensitively. No admin permission needed.
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
