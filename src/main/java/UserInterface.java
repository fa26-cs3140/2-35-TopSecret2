import java.util.List;

// Command-line interface for TopSecret.
// Validates user input, asks ProgramControl for results, and displays them.
public class UserInterface {

    // Help text shown when the user enters invalid input.
    static final String USAGE =
            "Usage:\n"
                    + "  java -jar TopSecret.jar                 list available files\n"
                    + "  java -jar TopSecret.jar <number>        display a file\n"
                    + "  java -jar TopSecret.jar <number> <key>  display a file using an alternate key";

    private final ProgramControl control;

    // Stores the ProgramControl the UI will ask for files and contents.
    public UserInterface(ProgramControl control) {
        this.control = control;
    }

    // Runs the program for the given arguments. Returns 0 on success, 1 on error.
    public int run(String[] args) {
        try {
            if (args == null || args.length == 0) {
                return showFileList();
            }
            if (args.length > 2) {
                return fail("Too many arguments.");
            }

            int fileNumber = parseFileNumber(args[0]);
            if (fileNumber < 1) {
                return fail("'" + args[0] + "' is not a valid file number.");
            }

            String keyPath = null;
            if (args.length == 2) {
                keyPath = args[1].trim();
                if (keyPath.isEmpty()) {
                    return fail("Alternate key cannot be blank.");
                }
            }

            System.out.println(control.getFileContents(fileNumber, keyPath));
            return 0;
        } catch (TopSecretException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (RuntimeException e) {
            System.err.println("Error: an unexpected problem occurred.");
            return 1;
        }
    }

    // Prints the list of files, numbered starting at 01.
    int showFileList() {
        List<String> files = control.listFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files available.");
            return 0;
        }
        for (int i = 0; i < files.size(); i++) {
            System.out.println(formatListEntry(i + 1, files.get(i)));
        }
        return 0;
    }

    // Formats one line of the file list as a two-digit number and name.
    static String formatListEntry(int number, String fileName) {
        return String.format("%02d %s", number, fileName);
    }

    // Converts the user's file number to an int, or -1 if invalid.
    static int parseFileNumber(String text) {
        if (text == null || !text.trim().matches("\\d{1,9}")) {
            return -1;
        }
        int n = Integer.parseInt(text.trim());
        return n >= 1 ? n : -1;
    }

    // Prints the error with help text. Returns 1 when the user's input is invalid.
    private int fail(String message) {
        System.err.println("Error: " + message);
        System.err.println(USAGE);
        return 1;
    }
}