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

// This would create a production database that would entangle with the real database at runtime.
//    @Test
//    void defaultConstructorTest() {
//        store.close();
//
//        store = new SQLiteDataStore();
//
//        assertNotNull(store);
//    }

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
        assertNull(store.getAgentByID(-1));
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
            Agent actual = store.getAgentByID(expected.getAgentID());


            assertNotNull(expected);
            assertEquals(expected.getAgentID(), actual.getAgentID());
            assertEquals(expected.getFacilityID(), actual.getFacilityID());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getDate_of_birth(), actual.getDate_of_birth());
            assertEquals(expected.getDate_of_death(), actual.getDate_of_death());
            assertEquals(expected.getNotes(), actual.getNotes());
        } else {
            store.getAgentByID(-1);
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
    void getUserByIDReturnsExistingUser() {
        List<User> users = store.listUsers();

        if (!users.isEmpty()) {
            User expected = users.get(0);
            User actual = store.getUserByID(expected.getUserID());

            assertNotNull(actual);
            assertEquals(expected.getUserID(), actual.getUserID());
            assertEquals(expected.getUsername(), actual.getUsername());
        } else {
            store.getUserByID(-1);
        }
    }

    @Test
    void listLogsReturnsExistingLogs() {
        User user = store.getUserByID(1);
        List<LoggedAction> logs = store.listLogs(user.getUserID());

        if (!logs.isEmpty()) {
            logs.forEach(log -> {
                assertEquals(log.getUser().getUserID(), user.getUserID());
            });
        } else {
            store.listLogs(-1);
        }
    }

    @Test
    void addFacilityInsertsCorrectly() {
        Facility expected = new Facility("f1", "ab1", 1);

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
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDate_of_birth(), actual.getDate_of_birth());
        assertEquals(expected.getDate_of_death(), actual.getDate_of_death());
        assertEquals(expected.getNotes(), actual.getNotes());

        expected = actual;
        actual = store.getAgentByID(expected.getAgentID());

        assertNotNull(actual);
        assertEquals(expected.getAgentID(), actual.getAgentID());
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

        User expected = store.getUserByID(actual.getUserID());

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
                new Facility("Facility A", "FA", 1)
        );

        Facility facility2 = store.addFacility(
                new Facility("Facility B", "FB", 1)
        );

        // Create a mission run by Facility A
        Mission mission = store.addMission(
                new Mission("Joint Operation", facility1.getFacilityID())
        );

        // Create one agent from each facility
        Agent agent1 = store.addAgent(
                new Agent(facility1.getFacilityID(),
                        "Agent A", "1990-01-01", null, "Test")
        );

        Agent agent2 = store.addAgent(
                new Agent(facility2.getFacilityID(),
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
                        facility.getFacilityID(),
                        "Agent One", "1990-01-01", null, "Test"
                )
        );

        Agent agent2 = store.addAgent(
                new Agent(
                        facility.getFacilityID(),
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

    @Test
    void getUserByNameReturnsExistingUser() {
        List<User> users = store.listUsers();

        if (!users.isEmpty()) {
            User expected = users.get(0);
            User actual = store.getUserByName(expected.getUsername());

            assertNotNull(actual);
            // Checks if the user ID matches
            assertEquals(expected.getUserID(), actual.getUserID());
            // Checks if the username matches
            assertEquals(expected.getUsername(), actual.getUsername());
        } else {
            store.getUserByName("user1");
        }
    }

    @Test
    void addMessageInsertsCorrectly() {
        Message testMessage = new Message(
                -1,
                store.getUserByID(1),
                store.getAgentByID(1),
                "Spend the dollar.",
                false
                );

        Message insertedMessage = store.addMessage(testMessage);

        assertNotNull(insertedMessage);
        assertEquals(
                testMessage.getSender().getUserID(),
                insertedMessage.getSender().getUserID()
        );
        assertEquals(
                testMessage.getRecipient().getAgentID(),
                insertedMessage.getRecipient().getAgentID()
        );
        assertEquals(testMessage.getMessageContent(),
                insertedMessage.getMessageContent()
        );
        assertEquals(testMessage.isRead(), insertedMessage.isRead());
    }

    @Test
    void updateMessageModifies() {
        Message testMessage = new Message(
                -1,
                store.getUserByID(1),
                store.getAgentByID(1),
                "Spend the dollar.",
                false
        );

        Message insertedMessage = store.addMessage(testMessage);

        Message modifiedMessage = new Message(
                insertedMessage.getMessageID(),
                insertedMessage.getSender(),
                insertedMessage.getRecipient(),
                insertedMessage.getMessageContent(),
                insertedMessage.isRead()
        );

        Message newlyUpdatedMessage =
                store.updateMessage(modifiedMessage);

        assertNotNull(newlyUpdatedMessage);
        assertEquals(
                modifiedMessage.getMessageContent(),
                newlyUpdatedMessage.getMessageContent()
        );
        assertEquals(
                modifiedMessage.isRead(),
                newlyUpdatedMessage.isRead()
        );
    }

    @Test
    void addCountryInsertsCorrectly() {
        Country testCountry = new Country("Wakanda");

        Country insertedCountry = store.addCountry(testCountry);

        assertNotNull(insertedCountry);
        assertEquals(testCountry.getName(), insertedCountry.getName());
        assertTrue((insertedCountry.getCountryID() > 0));
    }

    @Test
    void getCountryReturnsCorrectly() {
        Country testCountry = new Country("Soviet Union");

        Country insertedCountry = store.addCountry(testCountry);

        Country retrievedCountry = store.getCountry(
                insertedCountry.getCountryID()
        );

        assertNotNull(retrievedCountry);
        assertEquals(
                insertedCountry.getCountryID(),
                retrievedCountry.getCountryID()
        );
        assertEquals(insertedCountry.getName(),
                retrievedCountry.getName()
        );
    }

    @Test
    void updateCountryModifies() {
        Country testCountry = new Country("Soviet Union");

        Country insertedCountry = store.addCountry(testCountry);

        Country modifiedCountry = new Country(
                insertedCountry.getCountryID(),
                insertedCountry.getName()
        );

        Country newlyInsertedCountry =
                store.updateCountry(modifiedCountry);

        assertNotNull(newlyInsertedCountry);
        assertEquals(
                modifiedCountry.getCountryID(),
                newlyInsertedCountry.getCountryID()
                );
        assertEquals(
                modifiedCountry.getName(),
                newlyInsertedCountry.getName()
        );
    }

    @Test
    void listCountriesValidity() {
        List<Country> countriesList = store.listCountries();

        int countriesListLength = countriesList.size();

        assertTrue((countriesListLength == 196));
    }

    @Test
    void accessCountryFacilities() {
        Country testCountry = new Country("Soviet Union");
        Country storedCountry = store.addCountry(testCountry);

        Facility testFacility = new Facility(
                "Lubyanka",
                "LBY",
                storedCountry.getCountryID()
        );

        Facility storedFacility = store.addFacility(testFacility);

        List<Facility> countryFacilities =
                store.listFacilitiesForCountry(
                        storedCountry.getCountryID()
                );

        if (countryFacilities.isEmpty()) {
            fail();
        }

        Facility retrievedFacility = countryFacilities.getFirst();

        assertEquals(storedFacility.getFacilityID(),
                retrievedFacility.getFacilityID()
        );
        assertEquals(
                storedFacility.getName(),
                retrievedFacility.getName()
        );
        assertEquals(
                storedFacility.getAbbreviation(),
                retrievedFacility.getAbbreviation()
        );
        assertEquals(
                storedFacility.getCountryID(),
                retrievedFacility.getCountryID()
                );
    }

    @Test
    void updateMissionModifies() {
        Country testCountry = new Country("Soviet Union");
        Country storedCountry = store.addCountry(testCountry);

        Facility testFacility = new Facility(
                "Lubyanka",
                "LBY",
                storedCountry.getCountryID()
        );

        Facility storedFacility = store.addFacility(testFacility);

        Mission testMission = new Mission(
                "Monitor Reagan",
                storedFacility.getFacilityID()
        );

        Mission storedMission = store.addMission(testMission);

        Country testCountry2 = new Country("East Germany");

        Country storedCountry2 = store.addCountry(testCountry2);

        Facility testFacility2 = new Facility(
                "Karlshorst",
                "KST",
                storedCountry2.getCountryID()
        );

        Facility storedFacility2 = store.addFacility(testFacility2);

        Mission updatedMission = new Mission(
                storedMission.getMissionID(),
                "Analyze Reagan",
                storedFacility2.getFacilityID()
                );

        Mission newStoredMission = store.updateMission(updatedMission);

        assertNotNull(newStoredMission);
        assertEquals(
                storedMission.getMissionID(),
                newStoredMission.getMissionID()
        );
        assertEquals(
                updatedMission.getTitle(),
                newStoredMission.getTitle()
        );
        assertEquals(
            updatedMission.getFacilityID(),
            newStoredMission.getFacilityID()
        );
    }

    @Test
    void getAgentByNameReturnAgent() {
        Country storedCountry = store.addCountry(
                new Country("Soviet Union")
        );

        Facility storedFacility = store.addFacility(
               new Facility(
                       "Bolshoy Dom",
                       "BHD",
                       storedCountry.getCountryID()
               )
        );

        Agent testAgent = new Agent(
                storedFacility.getFacilityID(),
                "Vladimir Putin",
                "1952-10-07",
                null,
                "Second Chief Directorate"
                );

        Agent storedAgent = store.addAgent(testAgent);

        Agent returnedAgent = store.getAgentByName("Vladimir Putin");

        assertNotNull(returnedAgent);
        assertEquals(
                storedAgent.getAgentID(),
                returnedAgent.getAgentID()
        );
        assertEquals(storedAgent.getName(), returnedAgent.getName());
        assertEquals(
                storedAgent.getDate_of_birth(),
                returnedAgent.getDate_of_birth()
        );
        assertEquals(
                storedAgent.getDate_of_death(),
                returnedAgent.getDate_of_death()
        );
        assertEquals(storedAgent.getNotes(), returnedAgent.getNotes());
    }

    @Test
    void getAgentByNameNullForNonexistent() {
        Agent returnedAgent = store.getAgentByName("Agent T");

        assertNull(returnedAgent);
    }

    @Test
    void updateAgentModifies() {
        Country storedCountry = store.addCountry(
                new Country("Soviet Union")
        );

        Facility storedFacility1 = store.addFacility(
                new Facility(
                        "Lubyanka",
                        "LBY",
                        storedCountry.getCountryID()
                )
        );

        Facility storedFacility2 = store.addFacility(
                new Facility(
                        "Bolshoy Dom",
                        "BHD",
                        storedCountry.getCountryID()
                )
        );

        Agent storedAgent = store.addAgent(
                new Agent(
                        storedFacility1.getFacilityID(),
                        "Secret7",
                        "1975-07-13",
                        null,
                        ""
                )
        );

        Agent updatedAgent = new Agent(
                storedAgent.getAgentID(),
                storedFacility2.getFacilityID(),
                "SecretSeven",
                "1974-07-13",
                "2004-04-09",
                "MIA"
                );

        Agent returnedAgent = store.updateAgent(updatedAgent);

        assertNotNull(returnedAgent);
        assertEquals(
                storedAgent.getAgentID(),
                returnedAgent.getAgentID()
        );
        assertEquals(
                storedFacility2.getFacilityID(),
                returnedAgent.getFacilityID()
        );
        assertEquals(
                updatedAgent.getName(),
                returnedAgent.getName()
                );
        assertEquals(
                updatedAgent.getDate_of_birth(),
                returnedAgent.getDate_of_birth()
                );
        assertEquals(
                updatedAgent.getDate_of_death(),
                returnedAgent.getDate_of_death()
        );
        assertEquals(
                updatedAgent.getNotes(),
                returnedAgent.getNotes()
                );
    }

    @Test
    void updateFacilityModifies() {
        Country storedCountry1 = store.addCountry(
                new Country("Soviet Union")
        );

        Country storedCountry2 = store.addCountry(
                new Country("Russian Federation")
        );

        Facility storedFacility = store.addFacility(
                new Facility(
                        "Lubyanka",
                        "LBY",
                        storedCountry1.getCountryID()
                )
        );

        Facility updatedFacility = new Facility(
                storedFacility.getFacilityID(),
                "Lubyanka Building",
                "LYB",
                storedCountry2.getCountryID()
                );

        Facility newStoredFacility =
                store.updateFacility(updatedFacility);

        assertNotNull(newStoredFacility);
        assertEquals(
                storedFacility.getFacilityID(),
                newStoredFacility.getFacilityID()
        );
        assertEquals(
                updatedFacility.getName(),
                newStoredFacility.getName()
        );
        assertEquals(
                updatedFacility.getAbbreviation(),
                newStoredFacility.getAbbreviation()
        );
        assertEquals(
                updatedFacility.getCountryID(),
                newStoredFacility.getCountryID()
        );
    }

    @Test
    void updateBriefsModifies() {
        Brief storedBrief = store.addBrief(
                new Brief(
                98,
                "Evaluate PRISM Leak",
                "2014-08-18",
                "Understand lost information about PRISM"
                )
        );

        Brief updatedBrief = new Brief(
                storedBrief.getBriefID(),
                98,
                "Evaluate PRISM Leaks",
                "2014-08-19",
                "Understand revealed information about PRISM"
                );

        Brief newStoredBrief = store.updateBrief(updatedBrief);

        assertNotNull(newStoredBrief);
        assertEquals(
                updatedBrief.getBriefID(),
                newStoredBrief.getBriefID()
                );
        assertEquals(
                updatedBrief.getMissionID(),
                newStoredBrief.getMissionID()
        );
        assertEquals(
                updatedBrief.getTitle(),
                newStoredBrief.getTitle()
        );
        assertEquals(
                updatedBrief.getDate(),
                newStoredBrief.getDate()
        );
        assertEquals(
                updatedBrief.getContent(),
                newStoredBrief.getContent()
        );
    }

    @Test
    void messagesListForAgent() {
        Agent testAgent1 = store.getAgentByID(1);

        Message sentMessage = store.addMessage(
                new Message(
                        store.getUserByID(1),
                        testAgent1,
                        "Find the coin."
                )
        );

        List<Message> messageList =
                store.listMessagesForAgent(testAgent1);

        assertNotNull(messageList);
        assertFalse(messageList.isEmpty());
        Message retrievedMessage = messageList.getFirst();

        assertEquals(
                sentMessage.getMessageID(),
                retrievedMessage.getMessageID()
                );
        assertEquals(
                sentMessage.getSender().getUserID(),
                retrievedMessage.getSender().getUserID()
        );
        assertEquals(
                sentMessage.getRecipient().getAgentID(),
                retrievedMessage.getRecipient().getAgentID()
                );
        assertEquals(
                sentMessage.getMessageContent(),
                retrievedMessage.getMessageContent()
                );
        assertEquals(
                sentMessage.isRead(),
                retrievedMessage.isRead()
        );
    }

    @Test
    void messagesListEmptyForAgent() {
        Agent testAgent1 = store.getAgentByID(1);

        List<Message> messageList =
                store.listMessagesForAgent(testAgent1);

        assertNotNull(messageList);
        assertTrue(messageList.isEmpty());
    }
}
