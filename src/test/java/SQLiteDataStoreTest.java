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
    void getMissionReturnsExistingMission() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Mission> missions = store.listMissions();
            Mission first = missions.get(0);

            Mission result = store.getMission(first.getId());

            assertNotNull(result);
            assertEquals(first.getId(), result.getId());
            assertEquals(first.getTitle(), result.getTitle());
            assertEquals(first.getDate(), result.getDate());
            assertEquals(first.getBrief(), result.getBrief());
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

            Facility expected = facilities.get(0);
            Facility actual = store.getFacility(expected.getFacilityID());


            assertNotNull(expected);
            assertEquals(expected.getFacilityID(), actual.getFacilityID());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
        }
    }

    @Test
    void getBriefReturnsExistingBrief() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Brief> briefs = store.listBriefs();

            Brief expected = briefs.get(0);
            Brief actual = store.getBrief(expected.getBriefID());


            assertNotNull(expected);
            assertEquals(expected.getBriefID(), actual.getBriefID());
            assertEquals(expected.getMissionID(), actual.getMissionID());
            assertEquals(expected.getTitle(), actual.getTitle());
            assertEquals(expected.getDate(), actual.getDate());
            assertEquals(expected.getContent(), actual.getContent());
        }
    }

    @Test
    void getAgentReturnsExistingAgent() {
        try (SQLiteDataStore store = new SQLiteDataStore()) {
            List<Agent> agents = store.listAgents();

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
            Brief expected = new Brief(1,"b1", "1990-03-12","content 1");

            Brief actual =  store.addBrief(expected);

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


}
