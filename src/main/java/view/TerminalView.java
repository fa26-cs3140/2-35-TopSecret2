package view;

import model.*;

import java.util.List;
import java.util.Scanner;

public class TerminalView extends View {
    public void promptMessage(String message) {
    }

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

    public String browseMessagesView(){
        return "";
    }

    public String manageUsersView(List<User> users, User currentUser) {
        return "";
    }

    public String reviewAuditLogView(List<LoggedAction> logs) {
        return "";
    }

    public String listMessagesView(List<Message> messages){
        return "";
    }


    /**
     * prompts user for their username and password and returns them in String array
     *
     * @return {@code String[]} 1st element is username, 2nd element is password
     */
    public String[] promptLogin(Scanner scnr) {
        return null;
    }


    /**
     * prompts user to create a username and password and returns them in String array
     * @return
     */
    public String[] promptSignup(Scanner scnr) {
        return null;
    }

    /**
     * Prompts the user to change their password.
     */
    public String[] promptChangePassword(Scanner scnr) {
        return null;
    }

    // ---------------- RECORD INPUT ----------------

    /**
     * @return String[] containing mission title and facility ID.
     */
    public String[] promptAddMission(Scanner scnr) {
        return null;
    }

    /**
     * @return String[] containing facility name and abbreviation.
     */
    public String[] promptAddFacility(Scanner scnr) {
        return null;
    }

    /**
     * @return String[] containing mission ID, title, date,
     * and content.
     */
    public String[] promptAddBrief(Scanner scnr) {
        return null;
    }

    /**
     * @return String[] containing mission ID, facility ID,
     * name, DOB, DOD, and notes.
     */
    public String[] promptAddAgent(Scanner scnr) {
        return null;
    }

    /**
     * @return String containing the record ID.
     */
    public String promptRecordID(Scanner scnr, String itemType) {
        return "";
    }


    // ---------------- USER MANAGEMENT INPUT ----------------

    /**
     * @return String containing the new username.
     */
    public String promptCreateUser(Scanner scnr) {
        return "";
    }

    /**
     * @return String[] containing action (promote/demote)
     * and the username to modify.
     */
    public String[] promptManageAccess(Scanner scnr) {
        return null;
    }

    /**
     * @return String containing the username to modify.
     */
    public String promptSelectUser(Scanner scnr) {
        return "";
    }


    // ---------------- MESSAGE INPUT ----------------

    /**
     * @return String[] containing recipient agent ID
     * and message content.
     */
    public String[] promptCreateMessage(Scanner scnr) {
        return null;
    }


    // ---------------- GENERAL PROMPTS ----------------

    /**
     * @return Selected menu option.
     */
    public int promptMainMenuChoice(Scanner scnr) {
        return 0;
    }

    /**
     * @return Selected browse menu option.
     */
    public int promptBrowseMenuChoice(Scanner scnr, String itemType) {
        return 0;
    }

    /**
     * @return Selected yes/no response.
     */
    public boolean promptConfirmation(Scanner scnr, String message) {
        return false;
    }

    /**
     * @return Selected option (e.g. promote/demote).
     */
    public String promptOption(Scanner scnr, String message) {
        return "";
    }


    // ---------------- ERROR / STATUS VIEWS ----------------

    public String formatError(String error) {
        return "";
    }

    public String formatSuccess(String message) {
        return "";
    }


}
