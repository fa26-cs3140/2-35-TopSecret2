package view;

import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class utilityTest {

    @Test
    void missionToStr() {
        Mission mission = new Mission(1, "MissionTitle", 2);
        String actual = utility.missionToStr(mission);

        String expected = """
                Mission ID: 1
                Facility ID: 2
                Title: MissionTitle
                """;

        assertEquals(expected, actual);
    }

    @Test
    void facilityToStr() {
        Facility facility = new Facility(1, "FacilityName", "FT");
        String actual = utility.facilityToStr(facility);

        String expected = """
                Facility ID: 1
                Name: FacilityName
                Abbreviation: FT
        """;
        assertEquals(expected, actual);
    }

    @Test
    void briefToStr() {
        Brief b = new Brief(1, 2, "BriefTitle", "1999-02-15", "Content");
        String actual = utility.briefToStr(b);

        String expected = """
                Brief ID: 1
                Mission ID: 2
                Title: BriefTitle
                Date: 1999-02-15
                Content: Content
        """;
        assertEquals(expected, actual);
    }

    @Test
    void agentToStr() {
        Agent agent = new Agent(1, 2,3,"AgentName", "1999-02-23", null, "notes");
        String actual = utility.agentToStr(agent);

        String expected = """
                Agent ID: 1
                Mission ID: 2
                Facility ID: 3
                Name: AgentName
                Date Of Birth: 1999-02-23
                Date Of Death: 
                Notes: notes
        """;
        assertEquals(expected, actual);

    }

    @Test
    void userToStr() {
        User user = new User(1, "userName");
        String actual = utility.userToStr(user);
        String expected = """
                User ID: 1
                Username: userName
        """;
        assertEquals(expected, actual);
    }

    @Test
    void logToStr() {
        User user = new User(1, "userName");
        LoggedAction log = new LoggedAction(1, user, "1999-02-23", "record");
        String actual = utility.logToStr(log);

        String expected = """
                Action: 1
                User Name: userName
                User ID: 1
                Date:  1999-02-23
                Record: record
                """;
    }

    @Test
    void messageToStr() {
        Agent agent = new Agent(1, 2,3,"AgentName", "1999-02-23", null, "notes");
        Message message = new Message(agent, "Message");
        String actual = utility.messageToStr(message);

        String expected = """
                Recepient Agent ID: 1
                Recepient Name: AgentName
                Message: Message
                """;
        assertEquals(expected, actual);
    }
}