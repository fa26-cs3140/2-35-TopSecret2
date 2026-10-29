package view;

import java.util.List;

public interface View {
    // Display information
    String formatMessage(String message);

    String formatRecords(String title, List<String> records);

    String formatRecord(String title, String record);

    // Display errors
    String formatError(String error);

//    /**
//     * prompts user for their username and password and returns them in String array
//     * @return {@code String[]} 1st element is username, 2nd element is password
//     */
//    String[] prmptUsrNameAndPwd();
//
//    /**
//     * displays the
//     */
//    void displayMainMenu();
}
