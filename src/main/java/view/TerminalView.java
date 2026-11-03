package view;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TerminalView extends View {
    private final boolean admin;


    public TerminalView(boolean isAdmin) {
        super(isAdmin);
        this.admin = isAdmin;
    }


    // ---------------- GENERAL INPUT ----------------

    public void promptMessage(String message) {
        System.out.println(message);
    }


    public int getInputInt(
            Scanner scnr, int rangeMin, int rangeMax, String message) {

        while (true) {
            System.out.print(message);

            if (!scnr.hasNextLine()) {
                return -1;
            }

            String input = scnr.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value >= rangeMin && value <= rangeMax) {
                    return value;
                }

            } catch (NumberFormatException ignored) {
                // Prompt again if the input is not an integer.
            }

            System.out.println(
                    "Invalid input. Enter an integer between "
                            + rangeMin + " and " + rangeMax + "."
            );
        }
    }
    public int getInputInt(Scanner scnr, String message) {
        return getInputInt(
                scnr, Integer.MIN_VALUE, Integer.MAX_VALUE, message
        );
    }
    public String getInputStr(Scanner scnr, String regEx, String message) {

        while (true) {
            System.out.print(message);

            if (!scnr.hasNextLine()) {
                return "";
            }

            String input = scnr.nextLine().trim();

            if (input.matches(regEx)) {
                return input;
            }

            System.out.println("Invalid input. Please try again.");
        }
    }
    public String getInputStr(Scanner scnr, String message) {
        return getInputStr(scnr, ".+", message);
    }

    private String getOptionalInput(Scanner scnr, String message) {
        System.out.print(message);

        if (!scnr.hasNextLine()) {
            return "";
        }

        return scnr.nextLine().trim();
    }

    // ---------------- MISSION VIEWS ----------------

    @Override
    public String singleMissionView(Mission mission) {
        return utility.missionToStr(mission);
    }

    @Override
    public String listMissionsView(List<Mission> missions) {
        return String.join("\n", utility.missionsToStr(missions));
    }

    // ---------------- FACILITY VIEWS ----------------

    @Override
    public String singleFacilityView(Facility facility) {
        return utility.facilityToStr(facility);
    }

    @Override
    public String listFacilitiesView(List<Facility> facilities) {
        return String.join("\n", utility.facilitiesToStr(facilities));
    }

    // ---------------- BRIEF VIEWS ----------------

    @Override
    public String singleBriefView(Brief brief) {
        return utility.briefToStr(brief);
    }

    @Override
    public String listBriefsView(List<Brief> briefs) {
        return String.join("\n", utility.briefsToStr(briefs));
    }

    // ---------------- AGENT VIEWS ----------------

    @Override
    public String singleAgentView(Agent agent) {
        return utility.agentToStr(agent);
    }

    @Override
    public String listAgentsView(List<Agent> agents) {
        return String.join("\n", utility.agentsToStr(agents));
    }

    // ---------------- USER VIEWS ----------------

    @Override
    public String singleUserView(User user) {
        return utility.userToStr(user);
    }

    @Override
    public String listUsersView(List<User> users) {
        return String.join("\n", utility.usersToStr(users));
    }

    // ---------------- AUDIT LOG VIEWS ----------------

    @Override
    public String singleLogView(LoggedAction log) {
        return utility.logToStr(log);
    }

    @Override
    public String listLogsView(List<LoggedAction> logs) {
        return String.join("\n", utility.logsToStr(logs));
    }

    // ---------------- MENU VIEWS ----------------
    @Override
    public String mainMenuView() {
        if (admin) {
            return """
                ----------Main Menu----------

                1. Browse briefs
                2. Browse agents
                3. Browse facilities
                4. Browse missions
                5. Browse messages
                6. Change Password
                7. Create a new user
                8. Promote or Demote users
                9. Review audit log
                10. Exit/logout
                Enter an option number:
                """;
        }

        return """
            ----------Main Menu----------

            1. Browse briefs
            2. Browse agents
            3. Browse facilities
            4. Browse missions
            5. Browse messages
            6. Change Password
            7. Exit/logout
            Enter an option number:
            """;
    }

    @Override
    public String browseMissionsView() {
        return browseMenu("missions", "mission");
    }

    @Override
    public String browseFacilitiesView() {
        return browseMenu("facilities", "facility");
    }

    @Override
    public String browseBriefsView() {
        return browseMenu("briefs", "brief");
    }

    @Override
    public String browseAgentsView() {
        return browseMenu("agents", "agent");
    }

    public String browseMessagesView() {
        return """
            ----------Browse messages----------

            1. List your messages
            2. Leave a message to an agent
            Enter an option number:
            """;
    }

    private String browseMenu(String plural, String singular) {
        String title = plural.substring(0, 1).toUpperCase()
                + plural.substring(1);

        if (admin) {
            return String.format("""
                    ----------Browse %s----------

                    1. List %s
                    2. Read %s
                    3. Add %s
                    4. Go back to main menu
                    Enter an option number:
                    """,
                    plural,
                    title,
                    capitalize(singular),
                    capitalize(singular)
            );
        }
        return String.format("""
                ----------Browse %s----------

                1. List %s
                2. Read %s
                3. Go back to main menu
                Enter an option number:
                """,
                plural,
                title,
                capitalize(singular)
        );
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public String manageUsersView(List<User> users, User currentUser) {
        List<String> usernames = new ArrayList<>();

        for (User user : users) {
            if (user.getUserID() != currentUser.getUserID()) {
                usernames.add("-" + user.getUsername());
            }
        }

        String userList = String.join("\n", usernames);

        return """
                ----------Promote or Demote users----------

                Users:

                """ + userList + """

                
                Enter Promote or Demote followed by username like "Promote username1"
                """;
    }


    public String reviewAuditLogView(List<LoggedAction> logs) {
        List<String> entries = new ArrayList<>();

        for (LoggedAction log : logs) {
            String action = log.getAction() == 0 ? "read" : "create";

            entries.add(
                    log.getDateTime()
                            + " : " + log.getUser().getUsername()
                            + " : " + action
                            + " : " + log.getRecord()
            );
        }

        return """
                ----------Logs----------

                """ + String.join("\n", entries)+"\n";
    }

    public String listMessagesView(List<Message> messages) {
        return String.join("\n", utility.messagesToStr(messages));
    }


    // ---------------- LOGIN / ACCOUNT INPUT ----------------
    /**
     * prompts user for their username and password and returns them in String array
     *
     * @return {@code String[]} 1st element is username, 2nd element is password
     */
    public String[] promptLogin(Scanner scnr) {
        String username = getInputStr(scnr, "Username: ");
        String password = getInputStr(scnr, "Password: ");

        return new String[]{username, password};
    }


    /**
     * prompts user to create a username and password and returns them in String array
     * @return {@code String[]} 1st element is username, 2nd element is password, 3rd element is confirm password
     */
    public String[] promptSignup(Scanner scnr) {
        String username = getInputStr(scnr, "Choose a username: ");
        String password = getInputStr(scnr, "Choose a password: ");
        String confirmation = getInputStr(scnr, "Confirm password: ");

        return new String[]{username, password, confirmation};
    }

    /**
     * Prompts the user to change their password.
     * @return {@code String[]} 1st element is password, 2nd element is password confirmation
     */
    public String[] promptChangePassword(Scanner scnr) {
        String password = getInputStr(scnr, "New password: ");
        String confirmation = getInputStr(
                scnr, "Confirm new password: "
        );

        return new String[]{password, confirmation};
    }

    // ---------------- RECORD INPUT ----------------

    /**
     * @return String[] containing mission title and facility ID.
     */
    public String[] promptAddMission(Scanner scnr) {
        String title = getInputStr(scnr, "Mission title: ");
        String facilityID = getInputStr(
                scnr, "\\d+", "Facility ID: "
        );

        return new String[]{title, facilityID};
    }

    /**
     * @return String[] containing facility name and abbreviation.
     */
    public String[] promptAddFacility(Scanner scnr) {
        String name = getInputStr(scnr, "Facility name: ");
        String abbreviation = getInputStr(
                scnr, "Facility abbreviation: "
        );

        return new String[]{name, abbreviation};
    }

    /**
     * @param scnr
     * @return String[]{missionID, title, date, content}
     */
    public String[] promptAddBrief(Scanner scnr) {
        String missionID = getInputStr(
                scnr, "\\d+", "Mission ID: "
        );
        String title = getInputStr(scnr, "Brief title: ");
        String date = getInputStr(scnr, "Date: ");
        String content = getInputStr(scnr, "Brief content: ");

        return new String[]{missionID, title, date, content};
    }

    /**
     * @return String[] containing mission ID, facility ID,
     * name, DOB, DOD, and notes.
     */
    public String[] promptAddAgent(Scanner scnr) {
        String missionID = getOptionalInput(
                scnr, "Mission ID (leave blank if none): "
        );
        String facilityID = getInputStr(
                scnr, "\\d+", "Facility ID: "
        );
        String name = getInputStr(scnr, "Agent name: ");
        String dob = getInputStr(scnr, "Date of birth: ");
        String dod = getOptionalInput(
                scnr, "Date of death (leave blank if none): "
        );
        String notes = getOptionalInput(
                scnr, "Notes (leave blank if none): "
        );

        return new String[]{
                missionID, facilityID, name, dob, dod, notes
        };
    }

    /**
     * @return String containing the record ID.
     */
    public String promptRecordID(Scanner scnr, String itemType) {
        return getInputStr(
                scnr, "\\d+",
                capitalize(itemType) + " ID: "
        );
    }


    // ---------------- USER MANAGEMENT INPUT ----------------

    /**
     * @return String containing the new username.
     */
    public String promptCreateUser(Scanner scnr) {
        return getInputStr(scnr, "New username: ");
    }

    /**
     * @return String[] containing action (promote/demote)
     * and the username to modify.
     */
    public String[] promptManageAccess(Scanner scnr) {
        while (true) {
            String input = getInputStr(
                    scnr,
                    "Enter Promote or Demote followed by username: "
            );

            String[] parts = input.trim().split("\\s+", 2);

            if (parts.length == 2
                    && (parts[0].equalsIgnoreCase("promote")
                    || parts[0].equalsIgnoreCase("demote"))) {

                String action = parts[0].equalsIgnoreCase("promote")
                        ? "Promote" : "Demote";

                return new String[]{action, parts[1]};
            }

            System.out.println(
                    "Invalid input. Use Promote username or Demote username."
            );
        }
    }

    /**
     * @return String containing the username to modify.
     */
    public String promptSelectUser(Scanner scnr) {
        return getInputStr(scnr, "Enter username: ");
    }


    // ---------------- MESSAGE INPUT ----------------

    /**
     * @return String[] containing recipient agent ID
     * and message content.
     */
    public String[] promptCreateMessage(Scanner scnr) {
        String recipientID = getInputStr(
                scnr, "\\d+", "Recipient agent ID: "
        );
        String content = getInputStr(
                scnr, "Message content: "
        );

        return new String[]{recipientID, content};
    }


    // ---------------- GENERAL PROMPTS ----------------

    /**
     * displays main menu and prompts for input
     * @return Selected menu option.
     */
    public int promptMainMenuChoice(Scanner scnr) {
        int maxOption = admin ? 10 : 7;

        return getInputInt(
                scnr, 1, maxOption, mainMenuView()
        );
    }

    /**
     * @return Selected browse menu option.
     */
    public int promptBrowseMenuChoice(Scanner scnr, String itemType) {
        int maxOption;

        if (itemType.equalsIgnoreCase("messages")) {
            maxOption = 2;
        } else {
            maxOption = admin ? 4 : 3;
        }

        String menu;

        if (itemType.equalsIgnoreCase("missions")) {
            menu = browseMissionsView();
        } else if (itemType.equalsIgnoreCase("facilities")) {
            menu = browseFacilitiesView();
        } else if (itemType.equalsIgnoreCase("briefs")) {
            menu = browseBriefsView();
        } else if (itemType.equalsIgnoreCase("agents")) {
            menu = browseAgentsView();
        } else if (itemType.equalsIgnoreCase("messages")) {
            menu = browseMessagesView();
        } else {
            menu = "Enter an option number: ";
        }

        return getInputInt(scnr, 1, maxOption, menu);
    }

    /**
     * @return Selected yes/no response.
     */
    public boolean promptConfirmation(Scanner scnr, String message) {
        while (true) {
            String input = getInputStr(
                    scnr, message + " (yes/no): "
            );

            if (input.equalsIgnoreCase("yes")
                    || input.equalsIgnoreCase("y")) {
                return true;
            }

            if (input.equalsIgnoreCase("no")
                    || input.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Please enter yes or no.");
        }
    }

    /**
     * @return Selected option (e.g. promote/demote).
     */
    public String promptOption(Scanner scnr, String message) {
        return getInputStr(scnr, message);
    }


    // ---------------- ERROR / STATUS VIEWS ----------------

    public String formatError(String error) {
        return "Error: " + error;
    }

    public String formatSuccess(String message) {
        return "Success: " + message;
    }


}
