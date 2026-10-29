package view;

import java.util.List;

public class TerminalView implements View {
    /// @param message
    /// @return
    @Override
    public String formatMessage(String message) {
        return "";
    }

    /// @param title
    /// @param records
    /// @return
    @Override
    public String formatRecords(String title, List<String> records) {
        return "";
    }

    /// @param title
    /// @param record
    /// @return
    @Override
    public String formatRecord(String title, String record) {
        return "";
    }

    /// @param error
    /// @return
    @Override
    public String formatError(String error) {
        return "";
    }

    /**
     * prompts user for their username and password and returns them in String array
     *
     * @return {@code String[]} 1st element is username, 2nd element is password
     */
    public String[] promptLogin() {
        return null;
    }


    /**
     * prompts user to create a username and password and returns them in String array
     * @return
     */
    public String[] promptSignup() {
        return null;
    }

    /**
     * displays the main menu of options and returns the selected option by user
     * @return the selected option by the user
     */
    public int displayMainMenu() {
        return 0;
    }

}
