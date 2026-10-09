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

    public UserInterface(ProgramControlInterface control) {
        storageInterface = new SQLiteDataStore();
        searchInterface = new Search((SQLiteDataStore) storageInterface);
        authentication = new Authentication();
    }

    public int run(String[] args) {
        String optionsList = """
                Available Options:
                
                1. List missions
                2. Search missions
                3. View mission
                4. Change password
                5. Exit""";

        Scanner scnr = new Scanner(System.in);
        int currOptionNum;

        System.out.println(separator);
        System.out.println("Welcome to TopSecret\n");
        System.out.println(optionsList);
        System.out.println(separator);

        System.out.print("Enter an option number: ");

        while (!scnr.hasNextInt()) {
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
                    break;
                case 4:
                    System.out.print("Enter new password: ");
                    String newPass1 = scnr.nextLine();

                    System.out.print("Confirm new password: ");
                    String newPass2 = scnr.nextLine();

                    try {
                        authentication.changePassword(newPass1, newPass2);
                        System.out.println("Password changed successfully.");
                    } catch (TopSecretException e) {
                        System.out.println("Unable to change password: " + e.getMessage());
                    }
                    break;
                default:
                    break;
            }

            System.out.println(separator);
            System.out.println(optionsList);
            System.out.println(separator);
            System.out.print("Enter an option number: ");

            while (!scnr.hasNextInt()) {
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
                System.out.println((currIdx + 1) + ". " + currMission.getTitle());
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


}