package view;

import model.*;

import java.util.List;
import java.util.Scanner;

public class TerminalView extends View {
//    /// @param message
//    /// @return
//    @Override
//    public void promptMessage(String message) {
//        return "";
//    }

    public TerminalView(boolean isAdmin) {
        super(isAdmin);
    }

    public int getInputInt(Scanner scnr, int rangeMin, int rangeMax, String message) {
        return 0;
    }
    public int getInputInt(Scanner scnr, String message) {
        return getInputInt(scnr, Integer.MIN_VALUE, Integer.MAX_VALUE, message);
    }
    public String getInputStr(Scanner scnr, String regEx, String message) {
        return "";
    }
    public String getInputStr(Scanner scnr, String message) {
        return getInputStr(scnr, ".+", message);
    }

    /// @param mission
    /// @return
    @Override
    public String singleMissionView(Mission mission) {
        return "";
    }

    /// @param missions
    /// @return
    @Override
    public String listMissionsView(List<Mission> missions) {
        return "";
    }

    /// @param facility
    /// @return
    @Override
    public String singleFacilityView(Facility facility) {
        return "";
    }

    /// @param facilities
    /// @return
    @Override
    public String listFacilitiesView(List<Facility> facilities) {
        return "";
    }

    /// @param brief
    /// @return
    @Override
    public String singleBriefView(Brief brief) {
        return "";
    }

    /// @param briefs
    /// @return
    @Override
    public String listBriefsView(List<Brief> briefs) {
        return "";
    }

    /// @param agent
    /// @return
    @Override
    public String singleAgentView(Agent agent) {
        return "";
    }

    /// @param agents
    /// @return
    @Override
    public String listAgentsView(List<Agent> agents) {
        return "";
    }

    /// @param user
    /// @return
    @Override
    public String singleUserView(User user) {
        return "";
    }

    /// @param users
    /// @return
    @Override
    public String listUsersView(List<User> users) {
        return "";
    }

    /// @param log
    /// @return
    @Override
    public String singleLogView(LoggedAction log) {
        return "";
    }

    /// @param logs
    /// @return
    @Override
    public String listLogsView(List<LoggedAction> logs) {
        return "";
    }

    /// @return
    @Override
    public String mainMenuView() {
        String view = """
                ----------Main Menu----------
                
                """;
        return view;
    }

    /// @return
    @Override
    public String browseMissionsView() {
        return "";
    }

    /// @return
    @Override
    public String browseFacilitiesView() {
        return "";
    }

    /// @return
    @Override
    public String browseBriefsView() {
        return "";
    }

    /// @return
    @Override
    public String browseAgentsView() {
        return "";
    }

//    /// @return
//    @Override
//    public String createAccountView() {
//        return "";
//    }
//
//    /// @return
//    @Override
//    public String loginView() {
//        return "";
//    }

//    /// @param error
//    /// @return
//    @Override
//    public String formatError(String error) {
//        return "";
//    }

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
