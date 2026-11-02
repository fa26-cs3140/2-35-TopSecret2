package view;

import model.*;

import java.util.List;

public interface View {
    // Display information
    String promptMessage(String message);


    String singleMissionView(Mission mission);
    String listMissionsView(List<Mission> missions);
    String singleFacilityView(Facility facility);
    String listFacilitysView(List<Facility> facilities);
    String singleBriefView(Brief brief);
    String listBriefsView(List<Brief> briefs);
    String singleAgentView(Agent agent);
    String listAgentsView(List<Agent> agents);
    String singleUserView(User user);
    String listUsersView(List<User> users);
    String singleLogView(LoggedAction log);
    String listLogsView(List<LoggedAction> logs);

    String mainMenuView();
    String browseMissionsView();
    String browseFacilitiesView();
    String browseBriefsView();
    String browseAgentsView();
    String createAccountView();
    String loginView();


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
