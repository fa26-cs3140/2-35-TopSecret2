import java.util.Scanner;
import java.util.List;

// Command-line interface for TopSecret.
// Validates user input, asks ProgramControl for results, and displays them.
public class UserInterface {
    private String separator = """
            ------------------------------""";
    private DataStoreInterface storageInterface;
    private SearchInterface searchInterface;

    public UserInterface(ProgramControlInterface control) {
        storageInterface = new SQLiteDataStore();
        searchInterface = new Search((SQLiteDataStore) storageInterface);
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
                    System.out.print("Enter mission ID: ");
                    String id = scnr.nextLine();
                    viewMissionById(id);
                    break;
                case 4:
                    // TODO: Implement password change

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


}