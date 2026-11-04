package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteDataStoreTest {

    private SQLiteDataStore store;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        String databaseUrl =
                "jdbc:sqlite:" + tempDir.resolve("test.db");

        store = new SQLiteDataStore(databaseUrl);
        User user = store.addUser(new User(-1,"user1")); //just to test i can retrieve a user
        User user2 = store.addUser(new User(-1,"user2"));  //just to test i can retrieve a user
        store.addLog(new LoggedAction(1, user, "date1", "record1")); //just to test i can retrieve a Log
        store.addLog(new LoggedAction(0, user, "date2", "record2")); //just to test i can retrieve a log
    }

    @AfterEach
    void tearDown() {
        store.close();
    }

    @Test
    void listMissionsReturnsMissions() {
        List<Mission> missions = store.listMissions();

        assertFalse(missions.isEmpty());
    }


    @Test
    void getMissionReturnsNullForMissingId() {
        assertNull(store.getMission(-1));
    }

    @Test
    void initDatabaseCanBeCalledAgain() {
        assertDoesNotThrow(store::initDatabase);
        assertFalse(store.listMissions().isEmpty());

        assertFalse(store.listFacilities().isEmpty());
        assertFalse(store.listBriefs().isEmpty());
        assertFalse(store.listAgents().isEmpty());
    }


    @Test
    void closeDatabase(){
        store.close();
        assertDoesNotThrow(store::close);
        assertThrows(IllegalStateException.class, store::listMissions);
    }

    @Test
    void listFacilitiesReturnsFacilities() {
        List<Facility> facilities = store.listFacilities();
        assertFalse(facilities.isEmpty());
    }

    @Test
    void listBriefsReturnsBriefs() {
        List<Brief> briefs = store.listBriefs();
        assertFalse(briefs.isEmpty());
    }

    @Test
    void listAgentsReturnsAgents() {
        List<Agent> agents = store.listAgents();
        assertFalse(agents.isEmpty());
    }


    @Test
    void listUsersReturnsUsers() {
        List<User> users = store.listUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    void listLogsReturnsLogs() {
        List<LoggedAction> logs = store.listLogs();
        assertFalse(logs.isEmpty());
    }


    @Test
    void getFacilityReturnsNullForNonExistingId() {
        assertNull(store.getFacility(-1));
    }

    @Test
    void getBriefReturnsNullForNonExistingId() {
        assertNull(store.getBrief(-1));
    }

    @Test
    void getAgentReturnsNullForNonExistingId() {
        assertNull(store.getAgent(-1));
    }


    //get methods return existing records
    @Test
    void getFacilityReturnsExistingFacility() {
        List<Facility> facilities = store.listFacilities();

        if (!facilities.isEmpty()) {
            Facility expected = facilities.get(0);
            Facility actual = store.getFacility(expected.getFacilityID());


            assertNotNull(expected);
            assertEquals(expected.getFacilityID(), actual.getFacilityID());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
        } else {
            store.getFacility(-1);
        }

    }

    @Test
    void getBriefReturnsExistingBrief() {
        List<Brief> briefs = store.listBriefs();

        if (!briefs.isEmpty()) {
            Brief expected = briefs.get(0);
            Brief actual = store.getBrief(expected.getBriefID());

            assertNotNull(expected);
            assertEquals(expected.getBriefID(), actual.getBriefID());
            assertEquals(expected.getMissionID(), actual.getMissionID());
            assertEquals(expected.getTitle(), actual.getTitle());
            assertEquals(expected.getDate(), actual.getDate());
            assertEquals(expected.getContent(), actual.getContent());
        } else {
            store.getBrief(-1);
        }

    }

    @Test
    void getAgentReturnsExistingAgent() {
        List<Agent> agents = store.listAgents();

        if (!agents.isEmpty()) {
            Agent expected = agents.get(0);
            Agent actual = store.getAgent(expected.getAgentID());


            assertNotNull(expected);
            assertEquals(expected.getAgentID(), actual.getAgentID());
            assertEquals(expected.getMissionID(), actual.getMissionID());
            assertEquals(expected.getFacilityID(), actual.getFacilityID());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getDate_of_birth(), actual.getDate_of_birth());
            assertEquals(expected.getDate_of_death(), actual.getDate_of_death());
            assertEquals(expected.getNotes(), actual.getNotes());
        } else {
            store.getAgent(-1);
        }

    }

    @Test
    void getMissionReturnsExistingMission() {
        List<Mission> missions = store.listMissions();

        if (!missions.isEmpty()) {
            Mission expected = missions.get(0);

            Mission actual = store.getMission(expected.getMissionID());

            assertNotNull(actual);
            assertEquals(expected.getMissionID(), actual.getMissionID());
            assertEquals(expected.getTitle(), actual.getTitle());
            assertEquals(expected.getFacilityID(), actual.getFacilityID());
        } else {
            store.getMission(-1);
        }

    }

    @Test
    void getUserReturnsExistingUser() {
        List<User> users = store.listUsers();

        if (!users.isEmpty()) {
            User expected = users.get(0);
            User actual = store.getUser(expected.getUserID());

            assertNotNull(actual);
            assertEquals(expected.getUserID(), actual.getUserID());
            assertEquals(expected.getUsername(), actual.getUsername());
        } else {
            store.getUser(-1);
        }
    }

    @Test
    void listLogsReturnsExistingLogs() {
        User user = store.getUser(1);
        List<LoggedAction> logs = store.listLogs(user.getUserID());

        if (!logs.isEmpty()) {
            logs.forEach(log -> {
                assertEquals(log.getUser().getUserID(), user.getUserID());
            });
        } else {
            store.listLogs(-1);
        }
    }


    //adding records
    @Test
    void addFacilityInsertsCorrectly() {
        Facility expected = new Facility("f1", "ab1");

        Facility actual = store.addFacility(expected);

        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAbbreviation(), actual.getAbbreviation());

        expected = actual;
        actual = store.getFacility(expected.getFacilityID());

        assertNotNull(actual);
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
    }

    @Test
    void addBriefInsertsCorrectly() {
        Brief expected = new Brief(1, "b1", "1990-03-12", "content 1");

        Brief actual = store.addBrief(expected);

        assertNotNull(actual);
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getContent(), actual.getContent());

        expected = actual;
        actual = store.getBrief(expected.getBriefID());
        assertNotNull(actual);
        assertEquals(expected.getBriefID(), actual.getBriefID());
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    void addAgentInsertsCorrectly() {
        Agent expected = new Agent(1, 1, "a1", "1990-02-01", "2000-02-01", "note1");
        Agent actual = store.addAgent(expected);

        assertNotNull(actual);
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDate_of_birth(), actual.getDate_of_birth());
        assertEquals(expected.getDate_of_death(), actual.getDate_of_death());
        assertEquals(expected.getNotes(), actual.getNotes());

        expected = actual;
        actual = store.getAgent(expected.getAgentID());

        assertNotNull(actual);
        assertEquals(expected.getAgentID(), actual.getAgentID());
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDate_of_birth(), actual.getDate_of_birth());
        assertEquals(expected.getDate_of_death(), actual.getDate_of_death());
        assertEquals(expected.getNotes(), actual.getNotes());
    }

    @Test
    void addMissionInsertsCorrectly() {
        Mission expected = new Mission("test1title", 1);
        Mission actual = store.addMission(expected);

        assertNotNull(actual);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getFacilityID(), actual.getFacilityID());


        expected = actual;
        actual = store.getMission(expected.getMissionID());

        assertNotNull(actual);
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
    }

    @Test
    void addLogInsertsCorrectly() {

        User user = store.listUsers().get(0);
        if (user != null) {
            int origUserLogsSize = store.listLogs(user.getUserID()).size();

            LoggedAction expected = new LoggedAction(1, user, "2024-10-25", "Agents");
            LoggedAction actual = store.addLog(expected);
            int afterUserLogsSize = store.listLogs(user.getUserID()).size();

            assertNotNull(actual);
            assertEquals(expected.getUser().getUserID(), actual.getUser().getUserID());
            assertEquals(expected.getAction(), actual.getAction());
            assertEquals(expected.getDateTime(), actual.getDateTime());
            assertEquals(expected.getRecord(), actual.getRecord());
            assertEquals(origUserLogsSize + 1, afterUserLogsSize);
        }
    }

    @Test
    void addUserInsertsCorrectly() {
        User user = new User(-1, "testUser1");
        User actual = store.addUser(user);

        assertNotNull(actual);
        assertEquals(user.getUsername(), actual.getUsername());

        User expected = store.getUser(actual.getUserID());

        assertNotNull(expected);
        assertEquals(expected.getUserID(), actual.getUserID());
        assertEquals(expected.getUsername(), actual.getUsername());
    }

    @Test
    void assignAgentToMissionSuccessfully() {
        // Get an existing mission
        Mission mission = store.listMissions().get(0);

        // Create a new agent at the mission's facility
        Agent agent = store.addAgent(
                new Agent(
                        null,
                        mission.getFacilityID(),
                        "Test Agent",
                        "1990-01-01",
                        null,
                        "Test notes"
                )
        );

        // Assign the agent to the mission
        assertDoesNotThrow(() ->
                store.assignAgentToMission(
                        agent.getAgentID(),
                        mission.getMissionID()
                )
        );

        // Verify the assignment
        List<Agent> assignedAgents =
                store.listAgentsForMission(mission.getMissionID());

        assertTrue(
                assignedAgents.stream()
                        .anyMatch(a -> a.getAgentID() == agent.getAgentID())
        );
    }

    @Test
    void assignAgentToMultipleMissions() {

        // Get an existing facility
        Facility facility = store.listFacilities().get(0);

        // Create two missions
        Mission mission1 = store.addMission(
                new Mission("Test Mission 1", facility.getFacilityID())
        );

        Mission mission2 = store.addMission(
                new Mission("Test Mission 2", facility.getFacilityID())
        );

        // Create one agent
        Agent agent = store.addAgent(
                new Agent(
                        null,
                        facility.getFacilityID(),
                        "Multi Mission Agent",
                        "1990-01-01",
                        null,
                        "Test notes"
                )
        );

        // Assign the same agent to both missions
        store.assignAgentToMission(
                agent.getAgentID(),
                mission1.getMissionID()
        );

        store.assignAgentToMission(
                agent.getAgentID(),
                mission2.getMissionID()
        );

        // Retrieve the agent's missions
        List<Mission> assignedMissions =
                store.listMissionsForAgent(agent.getAgentID());

        // Verify both missions are assigned
        assertEquals(2, assignedMissions.size());

        assertTrue(assignedMissions.stream()
                .anyMatch(m -> m.getMissionID() == mission1.getMissionID()));

        assertTrue(assignedMissions.stream()
                .anyMatch(m -> m.getMissionID() == mission2.getMissionID()));
    }

    @Test
    void assignAgentsFromDifferentFacilitiesToMission() {

        // Create two different facilities
        Facility facility1 = store.addFacility(
                new Facility("Facility A", "FA")
        );

        Facility facility2 = store.addFacility(
                new Facility("Facility B", "FB")
        );

        // Create a mission run by Facility A
        Mission mission = store.addMission(
                new Mission("Joint Operation", facility1.getFacilityID())
        );

        // Create one agent from each facility
        Agent agent1 = store.addAgent(
                new Agent(null, facility1.getFacilityID(),
                        "Agent A", "1990-01-01", null, "Test")
        );

        Agent agent2 = store.addAgent(
                new Agent(null, facility2.getFacilityID(),
                        "Agent B", "1990-01-01", null, "Test")
        );

        // Assign both agents to the same mission
        store.assignAgentToMission(
                agent1.getAgentID(), mission.getMissionID()
        );

        store.assignAgentToMission(
                agent2.getAgentID(), mission.getMissionID()
        );

        // Retrieve agents assigned to the mission
        List<Agent> assignedAgents =
                store.listAgentsForMission(mission.getMissionID());

        // Verify both agents are assigned
        assertEquals(2, assignedAgents.size());

        assertTrue(assignedAgents.stream()
                .anyMatch(a -> a.getAgentID() == agent1.getAgentID()));

        assertTrue(assignedAgents.stream()
                .anyMatch(a -> a.getAgentID() == agent2.getAgentID()));
    }

    @Test
    void assignAgentToMissionRejectsDuplicateAssignment() {

        // Get an existing mission and facility
        Mission mission = store.listMissions().get(0);
        Facility facility = store.getFacility(mission.getFacilityID());

        // Create an agent
        Agent agent = store.addAgent(
                new Agent(
                        null,
                        facility.getFacilityID(),
                        "Duplicate Test Agent",
                        "1990-01-01",
                        null,
                        "Test notes"
                )
        );

        // Assign the agent for the first time
        store.assignAgentToMission(
                agent.getAgentID(),
                mission.getMissionID()
        );

        // Verify that assigning the same agent again is rejected
        assertThrows(IllegalArgumentException.class, () ->
                store.assignAgentToMission(
                        agent.getAgentID(),
                        mission.getMissionID()
                )
        );
    }

    @Test
    void unassignAgentFromMissionSuccessfully() {

        // Get an existing mission
        Mission mission = store.listMissions().get(0);

        // Get the facility running the mission
        Facility facility = store.getFacility(mission.getFacilityID());

        // Create an agent
        Agent agent = store.addAgent(
                new Agent(
                        null,
                        facility.getFacilityID(),
                        "Unassign Test Agent",
                        "1990-01-01",
                        null,
                        "Test notes"
                )
        );

        // Assign the agent to the mission
        store.assignAgentToMission(
                agent.getAgentID(),
                mission.getMissionID()
        );

        // Remove the agent from the mission
        store.unassignAgentFromMission(
                agent.getAgentID(),
                mission.getMissionID()
        );

        // Retrieve the mission's assigned agents
        List<Agent> assignedAgents =
                store.listAgentsForMission(mission.getMissionID());

        // Verify the agent is no longer assigned
        assertFalse(
                assignedAgents.stream()
                        .anyMatch(a -> a.getAgentID() == agent.getAgentID())
        );
    }

    @Test
    void listMissionsForAgentReturnsAssignedMissions() {

        // Get an existing facility
        Facility facility = store.listFacilities().get(0);

        // Create two missions
        Mission mission1 = store.addMission(
                new Mission("Retrieval Mission 1", facility.getFacilityID())
        );

        Mission mission2 = store.addMission(
                new Mission("Retrieval Mission 2", facility.getFacilityID())
        );

        // Create an agent
        Agent agent = store.addAgent(
                new Agent(
                        null,
                        facility.getFacilityID(),
                        "Retrieval Test Agent",
                        "1990-01-01",
                        null,
                        "Test notes"
                )
        );

        // Assign the agent to both missions
        store.assignAgentToMission(
                agent.getAgentID(), mission1.getMissionID()
        );

        store.assignAgentToMission(
                agent.getAgentID(), mission2.getMissionID()
        );

        // Retrieve the agent's missions
        List<Mission> missions =
                store.listMissionsForAgent(agent.getAgentID());

        // Verify both missions are returned
        assertEquals(2, missions.size());

        assertTrue(missions.stream()
                .anyMatch(m -> m.getMissionID() == mission1.getMissionID()));

        assertTrue(missions.stream()
                .anyMatch(m -> m.getMissionID() == mission2.getMissionID()));
    }

    @Test
    void listAgentsForMissionReturnsAssignedAgents() {

        // Get an existing facility
        Facility facility = store.listFacilities().get(0);

        // Create a mission
        Mission mission = store.addMission(
                new Mission("Agent Retrieval Mission", facility.getFacilityID())
        );

        // Create two agents
        Agent agent1 = store.addAgent(
                new Agent(
                        null, facility.getFacilityID(),
                        "Agent One", "1990-01-01", null, "Test"
                )
        );

        Agent agent2 = store.addAgent(
                new Agent(
                        null, facility.getFacilityID(),
                        "Agent Two", "1990-01-01", null, "Test"
                )
        );

        // Assign both agents to the mission
        store.assignAgentToMission(
                agent1.getAgentID(), mission.getMissionID()
        );

        store.assignAgentToMission(
                agent2.getAgentID(), mission.getMissionID()
        );

        // Retrieve agents assigned to the mission
        List<Agent> agents =
                store.listAgentsForMission(mission.getMissionID());

        // Verify both agents are returned
        assertEquals(2, agents.size());

        assertTrue(agents.stream()
                .anyMatch(a -> a.getAgentID() == agent1.getAgentID()));

        assertTrue(agents.stream()
                .anyMatch(a -> a.getAgentID() == agent2.getAgentID()));
    }

    @Test
    void listBriefsForMissionReturnsBriefs() {

        // Get an existing facility
        Facility facility = store.listFacilities().get(0);

        // Create a mission
        Mission mission = store.addMission(
                new Mission("Brief Retrieval Mission", facility.getFacilityID())
        );

        // Create two briefs for the mission
        Brief brief1 = store.addBrief(
                new Brief(
                        mission.getMissionID(),
                        "Brief One",
                        "2026-01-15",
                        "First test brief"
                )
        );

        Brief brief2 = store.addBrief(
                new Brief(
                        mission.getMissionID(),
                        "Brief Two",
                        "2026-01-16",
                        "Second test brief"
                )
        );

        // Retrieve briefs for the mission
        List<Brief> briefs =
                store.listBriefsForMission(mission.getMissionID());

        // Verify both briefs are returned
        assertEquals(2, briefs.size());

        assertTrue(briefs.stream()
                .anyMatch(b -> b.getBriefID() == brief1.getBriefID()));

        assertTrue(briefs.stream()
                .anyMatch(b -> b.getBriefID() == brief2.getBriefID()));

        // Create another mission without briefs
        Mission emptyMission = store.addMission(
                new Mission("Empty Mission", facility.getFacilityID())
        );

        // Verify that it returns an empty list
        assertTrue(
                store.listBriefsForMission(emptyMission.getMissionID()).isEmpty()
        );
    }
}
