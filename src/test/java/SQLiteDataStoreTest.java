import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteDataStoreTest {

    @Test
    void listMissionsReturnsMissions() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Mission> missions = store.listMissions();

            assertFalse(missions.isEmpty());
        }
    }


    @Test
    void getMissionReturnsNullForMissingId() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            assertNull(store.getMission(-1));
        }
    }

    @Test
    void initDatabaseCanBeCalledAgain() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            assertDoesNotThrow(store::initDatabase);
            assertFalse(store.listMissions().isEmpty());

            assertFalse(store.listFacilities().isEmpty());
            assertFalse(store.listBriefs().isEmpty());
            assertFalse(store.listAgents().isEmpty());
        }
    }

    @Test
    void listFacilitiesReturnsFacilities() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Facility> facilities = store.listFacilities();
            assertFalse(facilities.isEmpty());
        }
    }

    @Test
    void listBriefsReturnsBriefs() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Brief> briefs = store.listBriefs();
            assertFalse(briefs.isEmpty());
        }
    }

    @Test
    void listAgentsReturnsAgents() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Agent> agents = store.listAgents();
            assertFalse(agents.isEmpty());
        }
    }


    @Test
    void listUsersReturnsUsers() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<User> users = store.listUsers();
            assertFalse(users.isEmpty());
        }
    }

    @Test
    void listLogsReturnsLogs() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<LoggedAction> logs = store.listLogs();
            assertFalse(logs.isEmpty());
        }
    }


    @Test
    void getFacilityReturnsNullForNonExistingId() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            assertNull(store.getFacility(-1));
        }
    }

    @Test
    void getBriefReturnsNullForNonExistingId() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            assertNull(store.getBrief(-1));
        }
    }

    @Test
    void getAgentReturnsNullForNonExistingId() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            assertNull(store.getAgent(-1));
        }
    }


    //get methods return existing records
    @Test
    void getFacilityReturnsExistingFacility() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Facility> facilities = store.listFacilities();

            if (!facilities.isEmpty()){
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
    }

    @Test
    void getBriefReturnsExistingBrief() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Brief> briefs = store.listBriefs();

            if (!briefs.isEmpty()){
                Brief expected = briefs.get(0);
                Brief actual = store.getBrief(expected.getBriefID());

                assertNotNull(expected);
                assertEquals(expected.getBriefID(), actual.getBriefID());
                assertEquals(expected.getMissionID(), actual.getMissionID());
                assertEquals(expected.getTitle(), actual.getTitle());
                assertEquals(expected.getDate(), actual.getDate());
                assertEquals(expected.getContent(), actual.getContent());
            }else {
                store.getBrief(-1);
            }

        }
    }

    @Test
    void getAgentReturnsExistingAgent() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Agent> agents = store.listAgents();

            if(!agents.isEmpty()) {
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
            }else {
                store.getAgent(-1);
            }

        }
    }

    @Test
    void getMissionReturnsExistingMission() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Mission> missions = store.listMissions();

            if (!missions.isEmpty()) {
                Mission expected = missions.get(0);

                Mission actual = store.getMission(expected.getMissionID());

                assertNotNull(actual);
                assertEquals(expected.getMissionID(), actual.getMissionID());
                assertEquals(expected.getTitle(), actual.getTitle());
                assertEquals(expected.getFacilityID(), actual.getFacilityID());
            }else {
                store.getMission(-1);
            }

        }
    }

    @Test
    void getUserReturnsExistingUser() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<User> users = store.listUsers();

            if (!users.isEmpty()) {
                User expected = users.get(0);
                User actual = store.getUser(expected.getUserID());

                assertNotNull(actual);
                assertEquals(expected.getUserID(), actual.getUserID());
                assertEquals(expected.getUsername(), actual.getUsername());
            }else {
                store.getUser(-1);
            }
        }
    }

    @Test void listLogsReturnsExistingLogs() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            User user = store.getUser(1);
            List<LoggedAction> logs = store.listLogs(user.getUserID());

            if (!logs.isEmpty()) {
                logs.forEach(log -> {
                    assertEquals(log.getUser().getUserID(),user.getUserID());
                });
            }else{
                store.listLogs(-1);
            }
        }
    }


    //adding records
    @Test
    void addFacilityInsertsCorrectly() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
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
    }

    @Test
    void addBriefInsertsCorrectly() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
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
    }

    @Test
    void addAgentInsertsCorrectly() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
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
    }

    @Test
    void addMissionInsertsCorrectly() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            Mission expected = new Mission("test1title", 1);
            Mission actual = store.addMission(expected);

            assertNotNull(actual);
            assertEquals(expected.getMissionID(), actual.getMissionID());
            assertEquals(expected.getTitle(), actual.getTitle());
            assertEquals(expected.getFacilityID(), actual.getFacilityID());

            actual = store.getMission(expected.getMissionID());

            assertNotNull(actual);
            assertEquals(expected.getMissionID(), actual.getMissionID());
            assertEquals(expected.getTitle(), actual.getTitle());
            assertEquals(expected.getFacilityID(), actual.getFacilityID());
        }
    }

    @Test
    void addLogInsertsCorrectly() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {

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
    }

    @Test
    void addUserInsertsCorrectly() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            User user = new User(-1, "testUser1");
            User actual = store.addUser(user);

            assertNotNull(actual);
            assertEquals(user.getUsername(), actual.getUsername());

            User expected = store.getUser(user.getUserID());

            assertNotNull(actual);
            assertEquals(expected.getUserID(), actual.getUserID());
            assertEquals(expected.getUsername(), actual.getUsername());
        }
    }

}
