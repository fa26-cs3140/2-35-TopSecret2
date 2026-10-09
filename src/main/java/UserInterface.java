import java.util.Scanner;
import java.util.List;

// Command-line interface for TopSecret.
// Validates user input, asks ProgramControl for results, and displays them.
public class UserInterface {
    private String separator = """
            ------------------------------""";
    private DataStoreInterface storageInterface;

    public UserInterface(ProgramControlInterface  control) {
        storageInterface = new SQLiteDataStore();
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

        while (currOptionNum != 5) {
            switch (currOptionNum) {
                case 1:
                    showFileList();
                    break;
                case 2:
                    // TODO: Implement mission search
                    break;
                case 3:
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

}