package view;

import model.*;

import java.util.List;

public class WebView extends View {
    public WebView(boolean isAdmin) {
        super(isAdmin);
    }

//    /// @param message
//    /// @return
//    @Override
//    public String promptMessage(String message) {
//        return "";
//    }

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
    public String listFacilitysView(List<Facility> facilities) {
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
        return "";
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

    /// @param error
    /// @return
//    @Override
//    public String formatError(String error) {
//        return "";
//    }

//    public String generateHTML(String title, String content) {
//        return "";
//    }
}
