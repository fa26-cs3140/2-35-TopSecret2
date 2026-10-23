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
        store.addUser(new User(1,"user1"));

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
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAbbreviation(), actual.getAbbreviation());


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
        assertEquals(expected.getBriefID(), actual.getBriefID());
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getContent(), actual.getContent());


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
        assertEquals(expected.getAgentID(), actual.getAgentID());
        assertEquals(expected.getMissionID(), actual.getMissionID());
        assertEquals(expected.getFacilityID(), actual.getFacilityID());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDate_of_birth(), actual.getDate_of_birth());
        assertEquals(expected.getDate_of_death(), actual.getDate_of_death());
        assertEquals(expected.getNotes(), actual.getNotes());

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

}
