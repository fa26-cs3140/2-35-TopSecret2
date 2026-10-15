import java.util.Scanner;
import java.util.List;

// Command-line interface for TopSecret.
// Validates user input, asks ProgramControl for results, and displays them.
public class UserInterface {
    private String separator = """
            ------------------------------""";
    private DataStoreInterface storageInterface;
    private SearchInterface searchInterface;
    private AuthenticationInterface authentication;
    private Scanner scnr;

    public UserInterface(ProgramControlInterface control) {
        storageInterface = new SQLiteDataStore();
        searchInterface = new Search((SQLiteDataStore) storageInterface);
        authentication = new Authentication();
        scnr = new Scanner(System.in);
    }

    private int credentialStep() throws TopSecretException {
        if (!authentication.credentialExists()) {
            String proposedUsername = "";
            String proposedPassword = "";

            System.out.print("Create a username: ");
            if (scnr.hasNext()) {
                proposedUsername += scnr.next();
            }
            System.out.print("Create a password: ");
            if (scnr.hasNext()) {
                proposedPassword += scnr.next();
            }

            boolean validUsername = authentication
                    .isValidUsername(proposedUsername);

            boolean validPassword = authentication
                    .isValidPassword(proposedPassword);

            while (!validUsername | !validPassword) {
                System.out.println("Invalid credentials, try again");

                proposedUsername = "";
                proposedPassword = "";

                System.out.print("Create a username: ");
                if (scnr.hasNext()) {
                    proposedUsername += scnr.next();
                }
                System.out.print("Create a password: ");
                if (scnr.hasNext()) {
                    proposedPassword += scnr.next();
                }

                validUsername = authentication
                        .isValidUsername(proposedUsername);

                validPassword = authentication
                        .isValidPassword(proposedPassword);
            }

            try {
                authentication.createCredentials(proposedUsername, proposedPassword);
            } catch (TopSecretException e) {
                throw new TopSecretException(
                        "Unable to create credential file",
                        e);
            }

            return -1;
        }

        String loginUsername = "";
        String loginPassword = "";

        System.out.println("Log In:");

        System.out.print("Username: ");
        if (scnr.hasNext()) {
            loginUsername += scnr.next();
        }

        System.out.print("Password: ");
        if (scnr.hasNext()) {
            loginPassword += scnr.next();
        }

        boolean validUsername = authentication
                .isValidUsername(loginUsername);

        boolean validPassword = authentication
                .isValidPassword(loginPassword);

        while (!validUsername | !validPassword) {
            System.out.println("Invalid credentials, try again");

            loginUsername = "";
            loginPassword = "";

            System.out.print("Username: ");
            if (scnr.hasNext()) {
                loginUsername += scnr.next();
            }
            System.out.print("Password: ");
            if (scnr.hasNext()) {
                loginPassword += scnr.next();
            }

            validUsername = authentication
                    .isValidUsername(loginUsername);

            validPassword = authentication
                    .isValidPassword(loginPassword);
        }

        boolean correctCredentials = authentication
                .verifyCredentials(loginUsername, loginPassword);

        while (!correctCredentials) {
            System.out.println("Incorrect credentials, try again");

            loginUsername = "";
            loginPassword = "";

            System.out.print("Username: ");
            if (scnr.hasNext()) {
                loginUsername += scnr.next();
            }
            System.out.print("Password: ");
            if (scnr.hasNext()) {
                loginPassword += scnr.next();
            }

            correctCredentials = authentication
                    .verifyCredentials(loginUsername, loginPassword);
        }


        return 0;
    }

    public int run(String[] args) {
        String optionsList = """
                Available Options:
                
                1. List missions
                2. Search missions
                3. View mission
                4. Change password
                5. Exit""";

        int credentialStatus;
        int currOptionNum;

        try {
            credentialStatus = credentialStep();
        } catch (TopSecretException e) {
            return 1;
        }

        switch (credentialStatus) {
            case -1:
                return 0;
            case 0:
                break;
        }

        System.out.println(separator);
        System.out.println("Welcome to TopSecret\n");
        System.out.println(optionsList);
        System.out.println(separator);

        System.out.print("Enter an option number: ");

        while (!scnr.hasNextInt()) {
            scnr.nextLine();
            System.out.println(separator);
            System.out.println(optionsList);
            System.out.println(separator);

            System.out.print("Enter an option number: ");
        }

        currOptionNum = scnr.nextInt();
        scnr.nextLine();

        while (currOptionNum != 5) {
            switch (currOptionNum) {
                case 1:
                    showFileList();
                    break;
                case 2:
                    // TODO: Implement mission search
                    System.out.print("Enter a search phrase: ");
                    String phrase = scnr.nextLine();
                    searchOption(phrase);
                    break;
                case 3:
                    System.out.print("Enter mission ID: ");
                    String id = scnr.nextLine();
                    viewMissionById(id);
                    break;
                case 4:
                    // TODO: Implement password change
                    System.out.print("Enter new password: ");
                    String newPass1 = scnr.nextLine();

                    System.out.print("Confirm new password: ");
                    String newPass2 = scnr.nextLine();

                    changePwdOption(newPass1, newPass2);

                default:
                    break;
            }

            System.out.println(separator);
            System.out.println(optionsList);
            System.out.println(separator);
            System.out.print("Enter an option number: ");

            while (!scnr.hasNextInt()) {
                scnr.nextLine();
                System.out.println(separator);
                System.out.println(optionsList);
                System.out.println(separator);

                System.out.print("Enter an option number: ");
            }

            currOptionNum = scnr.nextInt();
            scnr.nextLine();
        }

        return 0;
    }

    public void showFileList() {
        List<Mission> missionList = storageInterface.listMissions();

        System.out.println(separator);

        if (!missionList.isEmpty()) {
            System.out.println("Available mission files:\n");

            for (int currIdx = 0; currIdx < missionList.size(); ++currIdx) {
                Mission currMission = missionList.get(currIdx);
                System.out.println(missionList.get(currIdx).getId() + ". " + currMission.getTitle());
            }
        } else {
            System.out.println("No mission files available");
        }

        System.out.println(separator);
    }

    public void searchOption(String phrase) {
        List<Mission> results = searchInterface.search(phrase);

        System.out.println(separator);

        if (results == null || results.isEmpty()) {
            System.out.println("No missions found matching: " + phrase);
        } else {
            System.out.println("Search results for: " + phrase + "\n");

            for (Mission mission : results) {
                System.out.println("Title: " + mission.getTitle());
                System.out.println("Date: " + mission.getDate());
                System.out.println("Brief: " + mission.getBrief());
                System.out.println(separator);
            }
        }

    }

    public void viewMissionById(String id) {
        List<Mission> missionList = storageInterface.listMissions();

        System.out.println(separator);

        for (Mission mission : missionList) {
            if (String.valueOf(mission.getId()).equals(id)) {
                System.out.println("Mission Details");
                System.out.println("Title: " + mission.getTitle());
                System.out.println("Date: " + mission.getDate());
                System.out.println("Brief: " + mission.getBrief());
                System.out.println(separator);
                return;
            }
        }
    }

    public void changePwdOption(String newPass1, String newPass2) {
        try {
            authentication.changePassword(newPass1, newPass2);
            System.out.println("Password changed successfully.");
        } catch (TopSecretException e) {
            System.out.println("Unable to change password: " + e.getMessage());
        }
    }



}