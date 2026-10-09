import java.util.Scanner;

// Command-line interface for TopSecret.
// Validates user input, asks ProgramControl for results, and displays them.
public class UserInterface {
    public UserInterface(ProgramControlInterface  control) {
        int test = 5;
    }

    public int run(String[] args) {
        String optionsList = """
                Available Options:
                
                1. List missions
                2. Search missions
                3. View mission
                4. Change password
                5. Exit""";

        String separator = """
            ------------------------------""";

        Scanner scnr = new Scanner(System.in);
        int currOptionNum;

        System.out.println(separator);
        System.out.println("Welcome to TopSecret\n");
        System.out.println(optionsList);
        System.out.println(separator);

        System.out.print("Enter an option number: ");

        while (!scnr.hasNextInt()) {
            scnr.next();
            System.out.println(separator);
            System.out.println(optionsList);
            System.out.println(separator);

            System.out.print("Enter an option number: ");
        }

        currOptionNum = scnr.nextInt();

        while (currOptionNum != 5) {
            switch (currOptionNum) {
                case 1:
                    // TODO: Implement mission list
                    break;
                case 2:
                    // TODO: Implement mission search
                    break;
                case 3:
                    // TODO: Implement mission view
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
                scnr.next();
                System.out.println(separator);
                System.out.println(optionsList);
                System.out.println(separator);

                System.out.print("Enter an option number: ");
            }

            currOptionNum = scnr.nextInt();
        }

        return 0;
    }
}