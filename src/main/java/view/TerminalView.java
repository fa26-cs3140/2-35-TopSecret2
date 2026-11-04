
package view;

import control.ControllerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Terminal interface for TopSecret.
// All application logic is delegated to ControllerInterface.
public class TerminalView {

    private final ControllerInterface controller;
    private final Scanner scnr;
    private String currentUser;

    public TerminalView(ControllerInterface controller) {
        this.controller = controller;
        this.scnr = new Scanner(System.in);
    }

    // ---------------- GENERAL INPUT ----------------

    private void display(String message) {
        if (message != null && !message.isEmpty()) {
            System.out.println(message);
        }
    }

    private String getInput(String message) {
        System.out.print(message);

        if (!scnr.hasNextLine()) {
            return null;
        }

        return scnr.nextLine().trim();
    }

    private String getRequiredInput(String message) {
        while (true) {
            String input = getInput(message);

            if (input == null) {
                return null;
            }

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    private String getOptionalInput(String message) {
        return getInput(message);
    }

    private int getInputInt(int min, int max, String message) {
        while (true) {
            String input = getInput(message);

            if (input == null) {
                return -1;
            }

            try {
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Prompt again.
            }

            System.out.println(
                    "Invalid input. Enter an integer between "
                            + min + " and " + max + "."
            );
        }
    }

    private int getPositiveInt(String message) {
        while (true) {
            String input = getInput(message);

            if (input == null) {
                return -1;
            }

            try {
                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Prompt again.
            }

            System.out.println("Please enter a positive integer.");
        }
    }

    private boolean isAdmin() {
        return controller.isAdmin(currentUser);
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase()
                + value.substring(1);
    }

    // ---------------- APPLICATION ENTRY ----------------

    public int run() {
        if (!loginStep()) {
            return 1;
        }

        showMainMenu();
        return 0;
    }

    // ---------------- LOGIN / ACCOUNT MANAGEMENT ----------------

    private boolean loginStep() {

        // Create the first account if no accounts exist.
        if (!controller.hasUsers()) {
            System.out.println(
                    "No accounts exist. Create the first account."
            );

            String[] signup = promptSignup();

            if (signup == null) {
                return false;
            }

            String result = controller.createFirstAccount(
                    signup[0], signup[1], signup[2]
            );

            display(result);

            // Only continue if account creation succeeded.
            if (!result.equals("Account is created!")) {
                return false;
            }

            currentUser = signup[0];
            return true;
        }

        while (true) {
            String username = getRequiredInput("Username: ");

            if (username == null) {
                return false;
            }

            // Newly created users must set up their password.
            if (controller.needsPasswordSetup(username)) {
                System.out.println(
                        "You need to set up your password."
                );

                String[] passwords = promptChangePassword();

                if (passwords == null) {
                    return false;
                }

                String result = controller.changePassword(
                        username, passwords[0], passwords[1]
                );

                display(result);

                if (result.equals("Password changed")) {
                    currentUser = username;
                    return true;
                }

                continue;
            }

            String password = getRequiredInput("Password: ");

            if (password == null) {
                return false;
            }

            if (controller.verifyLogin(username, password)) {
                currentUser = username;
                System.out.println("Login successful.");
                return true;
            }

            System.out.println("Incorrect username or password.");
        }
    }

    private String[] promptSignup() {
        String username = getRequiredInput("Choose a username: ");

        if (username == null) {
            return null;
        }

        String password = getRequiredInput("Choose a password: ");

        if (password == null) {
            return null;
        }

        String confirmation = getRequiredInput(
                "Confirm password: "
        );

        if (confirmation == null) {
            return null;
        }

        return new String[]{username, password, confirmation};
    }

    private String[] promptChangePassword() {
        String password = getRequiredInput("New password: ");

        if (password == null) {
            return null;
        }

        String confirmation = getRequiredInput(
                "Confirm new password: "
        );

        if (confirmation == null) {
            return null;
        }

        return new String[]{password, confirmation};
    }

    private void changePassword() {
        String[] passwords = promptChangePassword();

        if (passwords == null) {
            return;
        }

        display(controller.changePassword(
                currentUser, passwords[0], passwords[1]
        ));
    }

    // ---------------- MAIN MENU ----------------

    private void showMainMenu() {
        while (true) {

//            display(controller.showMainMenu(currentUser));

            int choice = getInputInt(
                    1,
                    isAdmin() ? 9 : 6,
                    controller.showMainMenu(currentUser)
            );

            if (choice == -1) {
                return;
            }

            if (isAdmin()) {
                switch (choice) {
                    case 1:
                        browseMenu("briefs");
                        break;
                    case 2:
                        browseMenu("agents");
                        break;
                    case 3:
                        browseMenu("facilities");
                        break;
                    case 4:
                        browseMenu("missions");
                        break;
                    case 5:
                        changePassword();
                        break;
                    case 6:
                        createUser();
                        break;
                    case 7:
                        manageAccess();
                        break;
                    case 8:
                        reviewAuditLog();
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1:
                        browseMenu("briefs");
                        break;
                    case 2:
                        browseMenu("agents");
                        break;
                    case 3:
                        browseMenu("facilities");
                        break;
                    case 4:
                        browseMenu("missions");
                        break;
                    case 5:
                        changePassword();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    // ---------------- BROWSE MENUS ----------------

    private void browseMenu(String itemType) {

        while (true) {

//            display(controller.browseMainMenu(
//                    currentUser, itemType
//            ));

            int choice = getInputInt(
                    1,
                    isAdmin() ? 5 : 3,
                    controller.browseMainMenu(
                            currentUser, itemType
                    )
            );

            if (choice == -1) {
                return;
            }

            if (isAdmin()) {
                switch (choice) {
                    case 1:
                        listEntries(itemType);
                        break;
                    case 2:
                        readEntry(itemType);
                        break;
                    case 3:
                        addEntry(itemType);
                        break;
                    case 4:
                        editEntry(itemType);
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1:
                        listEntries(itemType);
                        break;
                    case 2:
                        readEntry(itemType);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    // ---------------- RECORD OPERATIONS ----------------

    private void listEntries(String itemType) {
        display(controller.listEntries(currentUser, itemType));
    }

    private void readEntry(String itemType) {
        int id = getPositiveInt(
                capitalize(itemType) + " ID: "
        );

        if (id == -1) {
            return;
        }

        display(controller.readEntry(
                currentUser, itemType, id
        ));
    }

    private void addEntry(String itemType) {
        List<String> fields = promptRecordFields(itemType);

        if (fields == null) {
            return;
        }

        display(controller.addEntry(
                currentUser, itemType, fields
        ));
    }

    private void editEntry(String itemType) {
        int id = getPositiveInt(
                capitalize(itemType) + " ID to edit: "
        );

        if (id == -1) {
            return;
        }

        List<String> fields = promptRecordFields(itemType);

        if (fields == null) {
            return;
        }

        display(controller.editEntry(
                currentUser, itemType, id, fields
        ));
    }

    /**
     * Collects fields in the exact order expected by Controller.
     *
     * briefs:
     * missionID, title, date, content
     *
     * agents:
     * facilityID, name, date_of_birth, date_of_death, notes
     *
     * facilities:
     * name, abbreviation
     *
     * missions:
     * title, facilityID
     */
    private List<String> promptRecordFields(String itemType) {

        List<String> fields = new ArrayList<>();

        switch (itemType) {

            case "briefs":
                if (!addRequiredField(fields, "Mission ID: ")) {
                    return null;
                }

                if (!addRequiredField(fields, "Brief title: ")) {
                    return null;
                }

                if (!addRequiredField(fields, "Date: ")) {
                    return null;
                }

                if (!addRequiredField(fields, "Brief content: ")) {
                    return null;
                }
                break;

            case "agents":
                if (!addRequiredField(fields, "Facility ID: ")) {
                    return null;
                }

                if (!addRequiredField(fields, "Agent name: ")) {
                    return null;
                }

                if (!addRequiredField(fields, "Date of birth: ")) {
                    return null;
                }

                String dod = getOptionalInput(
                        "Date of death (leave blank if none): "
                );

                if (dod == null) {
                    return null;
                }

                String notes = getOptionalInput(
                        "Notes (leave blank if none): "
                );

                if (notes == null) {
                    return null;
                }

                fields.add(dod);
                fields.add(notes);
                break;

            case "facilities":
                if (!addRequiredField(fields, "Facility name: ")) {
                    return null;
                }

                if (!addRequiredField(
                        fields, "Facility abbreviation: "
                )) {
                    return null;
                }
                break;

            case "missions":
                if (!addRequiredField(fields, "Mission title: ")) {
                    return null;
                }

                if (!addRequiredField(fields, "Facility ID: ")) {
                    return null;
                }
                break;

            default:
                System.out.println("Invalid record type.");
                return null;
        }

        return fields;
    }

    private boolean addRequiredField(
            List<String> fields, String message) {

        String input = getRequiredInput(message);

        if (input == null) {
            return false;
        }

        fields.add(input);
        return true;
    }

    // ---------------- AGENT / MISSION ASSIGNMENT ----------------

    private void assignAgentToMission() {
        int agentId = getPositiveInt("Agent ID: ");

        if (agentId == -1) {
            return;
        }

        int missionId = getPositiveInt("Mission ID: ");

        if (missionId == -1) {
            return;
        }

        display(controller.assignAgentToMission(
                currentUser, agentId, missionId
        ));
    }

    // ---------------- USER MANAGEMENT ----------------

    private void createUser() {
        String username = getRequiredInput("New username: ");

        if (username == null) {
            return;
        }

        display(controller.createUser(currentUser, username));
    }

    private void manageAccess() {

        while (true) {
            String input = getRequiredInput(
                    "Enter Promote or Demote followed by username: "
            );

            if (input == null) {
                return;
            }

            String[] parts = input.split("\\s+", 2);

            if (parts.length == 2
                    && (parts[0].equalsIgnoreCase("promote")
                    || parts[0].equalsIgnoreCase("demote"))) {

                String action = parts[0].equalsIgnoreCase("promote")
                        ? "Promote" : "Demote";

                display(controller.manageAccess(
                        currentUser, parts[1], action
                ));

                return;
            }

            System.out.println(
                    "Invalid input. Use Promote username or Demote username."
            );
        }
    }

    // ---------------- AUDIT LOG ----------------

    private void reviewAuditLog() {
        display(controller.reviewAuditLog(currentUser));
    }

    // ---------------- MESSAGES ----------------

    private void browseMessages() {

        while (true) {

            display("""
                    ----------Browse messages----------

                    1. List your messages
                    2. Leave a message to an agent
                    3. Go back to main menu
                    """);

            int choice = getInputInt(
                    1, 3, "Enter an option number: "
            );

            if (choice == -1 || choice == 3) {
                return;
            }

            switch (choice) {
                case 1:
                    display(controller.viewMessages(currentUser));
                    break;

                case 2:
                    sendMessage();
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void sendMessage() {

        String agentName = getRequiredInput("Agent name: ");

        if (agentName == null) {
            return;
        }

        String message = getRequiredInput("Message content: ");

        if (message == null) {
            return;
        }

        display(controller.sendMessage(
                currentUser, agentName, message
        ));
    }
}
