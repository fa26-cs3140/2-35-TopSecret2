package control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.AccessControlInterface;
import model.Agent;
import model.AuditLogInterface;
import model.Brief;
import model.Country;
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
// menus (main menu 1-12, browse menu gains Edit at 4, Back at 5);
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
    private final SessionManager sessions;

    public Controller(UserManagerInterface users, AccessControlInterface access,
                      DataStoreInterface store, AuditLogInterface audit,
                      MessageManager messages, SessionManager sessions) {
        this.users = users;
        this.access = access;
        this.store = store;
        this.audit = audit;
        this.messages = messages;
        this.sessions = sessions;
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
    // users see 1-6, 10 and 12, like the HW5 menu showed 1-4 and 8.
    @Override
    public String showMainMenu(String username) {
        boolean admin = access.isAdmin(username);
        StringBuilder menu = new StringBuilder();
        menu.append("1. Browse briefs\n");
        menu.append("2. Browse agents\n");
        menu.append("3. Browse facilities\n");
        menu.append("4. Browse missions\n");
        menu.append("5. Browse countries\n");
        menu.append("6. Change Password\n");
        if (admin) {
            menu.append("7. Create a new user\n");
            menu.append("8. Promote or Demote users\n");
            menu.append("9. Review audit log\n");
        }
        menu.append("10. Send or view messages\n");
        if (admin) {
            menu.append("11. Assign agent to mission\n");
        }
        menu.append("12. Exit/logout\n");
        menu.append("Enter an option number:");
        return menu.toString();
    }

    // Browse menu text for one record type. Add and Edit are
    // admin only; Back is always last.
    @Override
    public String browseMainMenu(String username, String itemType) {
        switch (itemType) {
            case "briefs":
            case "agents":
            case "facilities":
            case "missions":
            case "countries":
                break;
            default:
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
        switch (itemType) {
            case "briefs":
                for (Brief b : store.listBriefs()) {
                    lines.add(b.getBriefID() + " : " + b.getTitle());
                }
                break;
            case "agents":
                for (Agent a : store.listAgents()) {
                    lines.add(a.getAgentID() + " : " + a.getName());
                }
                break;
            case "facilities":
                for (Facility f : store.listFacilities()) {
                    lines.add(f.getFacilityID() + " : " + f.getName());
                }
                break;
            case "missions":
                for (Mission m : store.listMissions()) {
                    lines.add(m.getMissionID() + " : " + m.getTitle());
                }
                break;
            case "countries":
                for (Country c : store.listCountries()) {
                    lines.add(c.getCountryID() + " : " + c.getName());
                }
                break;
            default:
                return "Invalid option";
        }
        return String.join("\n", lines);
    }

    @Override
    public String readEntry(String username, String itemType, int id) {
        switch (itemType) {
            case "briefs": {
                Brief requested = store.getBrief(id);
                if (requested == null) {
                    return "No brief found with this id";
                }
                audit(username, LoggedAction.READ, itemType + " " + id);
                return "Title: " + requested.getTitle() + "\nDate: " + requested.getDate() + "\nContent: " + requested.getContent();
            }
            case "agents": {
                Agent requested = store.getAgentByID(id);
                if (requested == null) {
                    return "No agent found with this id";
                }
                audit(username, LoggedAction.READ, itemType + " " + id);
                return "Name: " + requested.getName() + "\nDoB: " + requested.getDate_of_birth() + "\nDoDeath: " + requested.getDate_of_death() + "\nNotes: " + requested.getNotes();
            }
            case "facilities": {
                Facility requested = store.getFacility(id);
                if (requested == null) {
                    return "No facility found with this id";
                }
                audit(username, LoggedAction.READ, itemType + " " + id);
                Country country = store.getCountry(requested.getCountryID());
                return "Name: " + requested.getName() + "\nAbbreviation: " + requested.getAbbreviation()
                        + "\nCountry: " + (country == null ? "None" : country.getName());
            }
            case "missions": {
                Mission requested = store.getMission(id);
                if (requested == null) {
                    return "No mission found with this id";
                }
                audit(username, LoggedAction.READ, itemType + " " + id);
                return "Title: " + requested.getTitle() + "\nFacility ID: " + requested.getFacilityID();
            }
            case "countries": {
                Country requested = store.getCountry(id);
                if (requested == null) {
                    return "No country found with this id";
                }
                audit(username, LoggedAction.READ, itemType + " " + id);
                return "Name: " + requested.getName();
            }
            default:
                return "Invalid option";
        }
    }

    @Override
    public String addEntry(String username, String itemType, List<String> fields) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        try {
            switch (itemType) {
                case "briefs": {
                    if (fields.size() < 4) {
                        return "Not enough fields";
                    }
                    Brief added = store.addBrief(new Brief(Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3)));
                    audit(username, LoggedAction.CREATE, "briefs " + added.getBriefID());
                    break;
                }
                case "agents": {
                    if (fields.size() < 5) {
                        return "Not enough fields";
                    }
                    Agent added = store.addAgent(new Agent(Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
                    audit(username, LoggedAction.CREATE, "agents " + added.getAgentID());
                    break;
                }
                case "facilities": {
                    if (fields.size() < 3) {
                        return "Not enough fields";
                    }
                    // Parse the country ID so the facility is assigned to a country.
                    int countryId = Integer.parseInt(fields.get(2));
                    Facility added = store.addFacility(new Facility(fields.get(0), fields.get(1), countryId));
                    audit(username, LoggedAction.CREATE, "facilities " + added.getFacilityID());
                    break;
                }
                case "missions": {
                    if (fields.size() < 2) {
                        return "Not enough fields";
                    }
                    Mission added = store.addMission(new Mission(fields.get(0), Integer.parseInt(fields.get(1))));
                    audit(username, LoggedAction.CREATE, "missions " + added.getMissionID());
                    break;
                }
                case "countries": {
                    if (fields.size() < 1) {
                        return "Not enough fields";
                    }
                    Country added = store.addCountry(new Country(fields.get(0)));
                    audit(username, LoggedAction.CREATE, "countries " + added.getCountryID());
                    break;
                }
                default:
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
            switch (itemType) {
                case "briefs": {
                    if (fields.size() < 4) {
                        return "Not enough fields";
                    }
                    if (store.getBrief(id) == null) {
                        return "No brief found with this id";
                    }
                    store.updateBrief(new Brief(id, Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3)));
                    break;
                }
                case "agents": {
                    if (fields.size() < 5) {
                        return "Not enough fields";
                    }
                    Agent existing = store.getAgentByID(id);
                    if (existing == null) {
                        return "No agent found with this id";
                    }
                    store.updateAgent(new Agent(id, Integer.parseInt(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), fields.get(4)));
                    break;
                }
                case "facilities": {
                    if (fields.size() < 3) {
                        return "Not enough fields";
                    }
                    if (store.getFacility(id) == null) {
                        return "No facility found with this id";
                    }
                    // Update the facility's name, abbreviation, and assigned country.
                    store.updateFacility(new Facility(id, fields.get(0), fields.get(1), Integer.parseInt(fields.get(2))));
                    break;
                }
                case "missions": {
                    if (fields.size() < 2) {
                        return "Not enough fields";
                    }
                    if (store.getMission(id) == null) {
                        return "No mission found with this id";
                    }
                    store.updateMission(new Mission(id, fields.get(0), Integer.parseInt(fields.get(1))));
                    break;
                }
                case "countries": {
                    if (fields.size() < 1) {
                        return "Not enough fields";
                    }
                    if (store.getCountry(id) == null) {
                        return "No country found with this id";
                    }
                    store.updateCountry(new Country(id, fields.get(0)));
                    break;
                }
                default:
                    return "Invalid option";
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        audit(username, LoggedAction.EDIT, itemType + " " + id);
        return listEntries(username, itemType);
    }

    // One place for the audit line: the user, a null check, and the log call.
    private void audit(String username, int action, String record) {
        User userObj = users.getUserByUsername(username);
        if (userObj != null) {
            audit.log(new LoggedAction(action, userObj, LocalDateTime.now().toString(), record));
        }
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

    // The agent with this name, case-insensitively, or null.
    private Agent findAgentByName(String name) {
        for (Agent a : store.listAgents()) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    // Any logged-in user may send one. The agent is found by
    // name, case-insensitively, first match wins.
    @Override
    public String sendMessage(String username, String agentName, String message) {
        Agent recipient = findAgentByName(agentName);
        if (recipient == null) {
            return "No agent found with this name";
        }
        try {
            User sender = users.getUserByUsername(username);
            messages.createMessage(sender, recipient, message);
            return "Message sent to " + recipient.getName();
        } catch (TopSecretException e){
            return e.getMessage();
        }
    }

    // Only the caller's own messages: the agent whose name
    // matches the logged-in username, case-insensitively.
    // No admin permission needed.
    @Override
    public String viewMessages(String username) {
        Agent self = findAgentByName(username);
        if (self == null) {
            return "No messages";
        }
        List<Message> found;
        try {
            found = messages.retrieveMessages(self);
        } catch (TopSecretException e) {
            return e.getMessage();
        }
        if (found.isEmpty()) {
            return "No messages";
        }
        List<String> lines = new ArrayList<>();
        for (Message m : found) {
            lines.add(m.getMessageContent());
        }
        return String.join("\n", lines);
    }

    @Override
    public List<FormField> getFormFields(String username, String itemType, int id) {
        if (!access.isAdmin(username) || id < 0) {
            return List.of();
        }
        switch (itemType) {
            case "briefs": {
                Brief brief = id == 0 ? null : store.getBrief(id);
                if (id != 0 && brief == null) {
                    return List.of();
                }
                List<String> missionOptions = new ArrayList<>();
                for (Mission m : store.listMissions()) {
                    missionOptions.add(m.getMissionID() + " : " + m.getTitle());
                }
                return List.of(
                        new FormField("missionID", "Mission ID", brief == null ? "" : String.valueOf(brief.getMissionID()), "number", missionOptions),
                        new FormField("title", "Brief title", brief == null ? "" : brief.getTitle(), "text", List.of()),
                        new FormField("date", "Date", brief == null ? "" : brief.getDate(), "date", List.of()),
                        new FormField("content", "Brief content", brief == null ? "" : brief.getContent(), "text", List.of()));
            }
            case "agents": {
                Agent agent = id == 0 ? null : store.getAgentByID(id);
                if (id != 0 && agent == null) {
                    return List.of();
                }
                List<String> facilityOptions = new ArrayList<>();
                for (Facility f : store.listFacilities()) {
                    facilityOptions.add(f.getFacilityID() + " : " + f.getName());
                }
                return List.of(
                        new FormField("facilityID", "Facility ID", agent == null ? "" : String.valueOf(agent.getFacilityID()), "number", facilityOptions),
                        new FormField("name", "Agent name", agent == null ? "" : agent.getName(), "text", List.of()),
                        new FormField("date_of_birth", "Date of birth", agent == null ? "" : agent.getDate_of_birth(), "date", List.of()),
                        new FormField("date_of_death", "Date of death", agent == null ? "" : agent.getDate_of_death(), "date", List.of()),
                        new FormField("notes", "Notes", agent == null ? "" : agent.getNotes(), "text", List.of()));
            }
            case "facilities": {
                Facility facility = id == 0 ? null : store.getFacility(id);
                if (id != 0 && facility == null) {
                    return List.of();
                }
                List<String> countryOptions = new ArrayList<>();
                for (Country c : store.listCountries()) {
                    countryOptions.add(c.getCountryID() + " : " + c.getName());
                }
                return List.of(
                        new FormField("name", "Facility name", facility == null ? "" : facility.getName(), "text", List.of()),
                        new FormField("abbreviation", "Facility abbreviation", facility == null ? "" : facility.getAbbreviation(), "text", List.of()),
                        new FormField("countryID", "Country", facility == null ? "" : String.valueOf(facility.getCountryID()), "number", countryOptions));
            }
            case "missions": {
                Mission mission = id == 0 ? null : store.getMission(id);
                if (id != 0 && mission == null) {
                    return List.of();
                }
                List<String> facilityOptions = new ArrayList<>();
                for (Facility f : store.listFacilities()) {
                    facilityOptions.add(f.getFacilityID() + " : " + f.getName());
                }
                return List.of(
                        new FormField("title", "Mission title", mission == null ? "" : mission.getTitle(), "text", List.of()),
                        new FormField("facilityID", "Facility ID", mission == null ? "" : String.valueOf(mission.getFacilityID()), "number", facilityOptions));
            }
            case "countries": {
                Country country = id == 0 ? null : store.getCountry(id);
                if (id != 0 && country == null) {
                    return List.of();
                }
                return List.of(
                        new FormField("name", "Country name", country == null ? "" : country.getName(), "text", List.of()));
            }
            default:
                return List.of();
        }
    }

    // True when the text holds the query, case-insensitively.
    private static boolean matches(String text, String needle) {
        return text != null && text.toLowerCase().contains(needle);
    }

    @Override
    public String searchEntries(String username, String query) {
        if (query == null || query.isBlank()) {
            return "No matches";
        }
        String needle = query.toLowerCase();
        List<String> lines = new ArrayList<>();
        for (Brief b : store.listBriefs()) {
            if (matches(b.getTitle(), needle) || matches(b.getContent(), needle)) {
                lines.add("briefs " + b.getBriefID() + " : " + b.getTitle());
            }
        }
        for (Agent a : store.listAgents()) {
            if (matches(a.getName(), needle) || matches(a.getNotes(), needle)) {
                lines.add("agents " + a.getAgentID() + " : " + a.getName());
            }
        }
        for (Facility f : store.listFacilities()) {
            if (matches(f.getName(), needle) || matches(f.getAbbreviation(), needle)) {
                lines.add("facilities " + f.getFacilityID() + " : " + f.getName());
            }
        }
        for (Mission m : store.listMissions()) {
            if (matches(m.getTitle(), needle)) {
                lines.add("missions " + m.getMissionID() + " : " + m.getTitle());
            }
        }
        for (Country c : store.listCountries()) {
            if (matches(c.getName(), needle)) {
                lines.add("countries " + c.getCountryID() + " : " + c.getName());
            }
        }
        if (lines.isEmpty()) {
            return "No matches";
        }
        return String.join("\n", lines);
    }

    @Override
    public String listMessages(String username) {
        Agent self = findAgentByName(username);
        if (self == null) {
            return "No messages";
        }
        List<Message> found;
        try {
            found = messages.retrieveMessages(self);
        } catch (TopSecretException e) {
            return e.getMessage();
        }
        if (found.isEmpty()) {
            return "No messages";
        }
        List<String> lines = new ArrayList<>();
        for (Message m : found) {
            lines.add(m.getMessageID() + " : " + m.getMessageContent() + (m.isRead() ? "" : " (unread)"));
        }
        return String.join("\n", lines);
    }

    @Override
    public String readMessage(String username, int messageId) {
        Agent self = findAgentByName(username);
        if (self == null) {
            return "No message found with this id";
        }
        List<Message> found;
        try {
            found = messages.retrieveMessages(self);
        } catch (TopSecretException e) {
            return e.getMessage();
        }
        for (Message m : found) {
            if (m.getMessageID() == messageId) {
                try {
                    messages.readMessage(m);
                } catch (TopSecretException e) {
                    return e.getMessage();
                }
                return m.getMessageID() + " : " + m.getMessageContent();
            }
        }
        return "No message found with this id";
    }

    @Override
    public String listWebSessions(String username) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        List<String> lines = new ArrayList<>();
        for (SessionManager.Session s : sessions.listSessions()) {
            lines.add(s.token() + " : " + s.username() + " : " + s.lastActivity());
        }
        return String.join("\n", lines);
    }

    @Override
    public String deleteWebSession(String username, String token) {
        if (!access.isAdmin(username)) {
            return "Not allowed";
        }
        if (!sessions.revokeSession(token)) {
            return "No session found with this token";
        }
        return listWebSessions(username);
    }
}
