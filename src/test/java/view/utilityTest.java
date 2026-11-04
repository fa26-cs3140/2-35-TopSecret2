package view;

import model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class utilityTest {

    @Test
    void missionToStr() {
        Mission mission = new Mission(1, "MissionTitle", 2);
        String actual = Utility.missionToStr(mission);

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
        String actual = Utility.facilityToStr(facility);

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
        String actual = Utility.briefToStr(b);

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
        Agent agent = new Agent(1, 3,"AgentName", "1999-02-23", null, "notes");
        String actual = Utility.agentToStr(agent);

        String expected = """
                Agent ID: 1
                Facility ID: 3
                Name: AgentName
                Date Of Birth: 1999-02-23
                Date Of Death:\s
                Notes: notes
                """;
        assertEquals(expected, actual);

    }

    @Test
    void userToStr() {
        User user = new User(1, "userName");
        String actual = Utility.userToStr(user);
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
        String actual = Utility.logToStr(log);

        String expected = """
                Action: 1
                User Name: userName
                User ID: 1
                Date: 1999-02-23
                Record: record
                """;
        assertEquals(expected, actual);
    }

    @Test
    void messageToStr() {
        Agent agent = new Agent(1,3,"AgentName", "1999-02-23", null, "notes");
        Message message = new Message(agent, "Message");
        String actual = Utility.messageToStr(message);

        String expected = """
                Recepient Agent ID: 1
                Recepient Name: AgentName
                Message: Message
                """;
        assertEquals(expected, actual);
    }

    @Test
    void missionsToStr() {
        List<Mission> missions = List.of(
                new Mission(1, "MissionTitle1", 2),
                new Mission(2, "MissionTitle2", 3)
        );

        List<String> actual = Utility.missionsToStr(missions);

        List<String> expected = List.of(
                Utility.missionToStr(missions.get(0)),
                Utility.missionToStr(missions.get(1))
        );

        assertEquals(expected, actual);
    }

    @Test
    void facilitiesToStr() {
        List<Facility> facilities = List.of(
                new Facility(1, "FacilityName1", "FT"),
                new Facility(2, "FacilityName2", "HQ")
        );

        List<String> actual = Utility.facilitiesToStr(facilities);

        List<String> expected = List.of(
                Utility.facilityToStr(facilities.get(0)),
                Utility.facilityToStr(facilities.get(1))
        );

        assertEquals(expected, actual);
    }

    @Test
    void briefsToStr() {
        List<Brief> briefs = List.of(
                new Brief(1, 2, "BriefTitle1", "1999-02-15", "Content1"),
                new Brief(2, 3, "BriefTitle2", "2000-03-16", "Content2")
        );

        List<String> actual = Utility.briefsToStr(briefs);

        List<String> expected = List.of(
                Utility.briefToStr(briefs.get(0)),
                Utility.briefToStr(briefs.get(1))
        );

        assertEquals(expected, actual);
    }

    @Test
    void agentsToStr() {
        List<Agent> agents = List.of(
                new Agent(1, 3, "AgentName1", "1999-02-23", null, "notes1"),
                new Agent(2, 4, "AgentName2", "2000-03-24", null, "notes2")
        );

        List<String> actual = Utility.agentsToStr(agents);

        List<String> expected = List.of(
                Utility.agentToStr(agents.get(0)),
                Utility.agentToStr(agents.get(1))
        );

        assertEquals(expected, actual);
    }

    @Test
    void usersToStr() {
        List<User> users = List.of(
                new User(1, "userName1"),
                new User(2, "userName2")
        );

        List<String> actual = Utility.usersToStr(users);

        List<String> expected = List.of(
                Utility.userToStr(users.get(0)),
                Utility.userToStr(users.get(1))
        );

        assertEquals(expected, actual);
    }

    @Test
    void logsToStr() {
        User user1 = new User(1, "userName1");
        User user2 = new User(2, "userName2");

        List<LoggedAction> logs = List.of(
                new LoggedAction(1, user1, "1999-02-23", "record1"),
                new LoggedAction(2, user2, "2000-03-24", "record2")
        );

        List<String> actual = Utility.logsToStr(logs);

        List<String> expected = List.of(
                Utility.logToStr(logs.get(0)),
                Utility.logToStr(logs.get(1))
        );

        assertEquals(expected, actual);
    }

    @Test
    void messagesToStr() {
        Agent agent1 = new Agent(1, 3, "AgentName1", "1999-02-23", null, "notes1");
        Agent agent2 = new Agent(2, 4, "AgentName2", "2000-03-24", null, "notes2");

        List<Message> messages = List.of(
                new Message(agent1, "Message1"),
                new Message(agent2, "Message2")
        );

        List<String> actual = Utility.messagesToStr(messages);

        List<String> expected = List.of(
                Utility.messageToStr(messages.get(0)),
                Utility.messageToStr(messages.get(1))
        );

        assertEquals(expected, actual);
    }
}