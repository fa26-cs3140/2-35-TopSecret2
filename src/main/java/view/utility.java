package view;

import model.*;

public class utility {

    static void main() {
        missionToStr(new Mission(1,"title",2));
    }

    public static String missionToStr (Mission m) {
        String str = """
                Mission ID: %s
                Facility ID: %s
                Title: %s
                """;
        return String.format(str, m.getMissionID(), m.getFacilityID(), m.getTitle());
    }
    public static String facilityToStr (Facility f) {
        String str  = """
                Facility ID: %s
                Name: %s
                Abbreviation: %s
                """;
        return String.format(str, f.getFacilityID(), f.getName(), f.getAbbreviation());
    }

    public static String briefToStr (Brief b) {
        String str = """
                Brief ID: %s
                Mission ID: %s
                Title: %s
                Date: %s
                Content: %s
                """;
        return String.format(str, b.getBriefID(), b.getMissionID(), b.getTitle(), b.getDate(), b.getContent());
    }

    public static String agentToStr (Agent a) {
        String str = """
                Agent ID: $s
                Mission ID: %s
                Facility ID: %s
                Name: %s
                Date Of Birth: %s
                Date Of Death: %s
                Notes: %s
                """;
        return String.format(str, a.getAgentID(), (a.getMissionID() != null ? a.getMissionID() : ""),
                a.getFacilityID(), a.getName(), a.getDate_of_birth(), a.getDate_of_death()==null ? "" : a.getDate_of_death() ,
                a.getNotes() == null ? "" : a.getNotes());
    }

    public static String userToStr (User user) {
        String str = """
                User ID: %s
                Username: %s
                """;
        return String.format(str, user.getUserID(), user.getUsername());
    }

    public static String logToStr (LoggedAction log) {
        String str = """
                Action: %s
                User Name: %s
                User ID: %s
                Date:  %s
                Record: %s
                """;
        return String.format(str, log.getAction(),log.getUser().getUsername(), log.getUser().getUserID(), log.getDateTime(), log.getRecord());
    }

    public static String messageToStr (Message m) {
        String str = """
                Recepient Agent ID: %s
                Recepient Name: %s
                Message: %s
                """;
        return String.format(str, m.getRecipient().getAgentID(), m.getRecipient().getName(), m.getMessageContent());
    }

}
