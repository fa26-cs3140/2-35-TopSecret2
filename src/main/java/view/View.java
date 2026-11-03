package view;

import model.*;

import java.util.List;

public abstract class View {
    // Display information
//    abstract String promptMessage(String message);
//    protected boolean isAdmin;
//    public boolean isAdmin() {
//        return isAdmin;
//    }


    abstract String singleMissionView(Mission mission);
    abstract String listMissionsView(List<Mission> missions);
    abstract String singleFacilityView(Facility facility);
    abstract String listFacilitiesView(List<Facility> facilities);
    abstract String singleBriefView(Brief brief);
    abstract String listBriefsView(List<Brief> briefs);
    abstract String singleAgentView(Agent agent);
    abstract String listAgentsView(List<Agent> agents);
    abstract String singleUserView(User user);
    abstract String listUsersView(List<User> users);
    abstract String singleLogView(LoggedAction log);
    abstract String listLogsView(List<LoggedAction> logs);

    abstract String mainMenuView();
    abstract String browseMissionsView();
    abstract String browseFacilitiesView();
    abstract String browseBriefsView();
    abstract String browseAgentsView();
}
