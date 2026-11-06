// Integration tests for the model layer: a real SQLiteDataStore on a
// temporary database working together with the real Facility, Mission,
// Brief and Agent classes and the seeded dataset TSVs. Nothing is mocked.
// Run: .\gradlew test --tests "model.ModelSQLiteDataStoreIntegrationTest" --console=plain

package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelSQLiteDataStoreIntegrationTest {

    @TempDir
    Path tempDir;

    private String databaseUrl;
    private SQLiteDataStore store;

    private Facility headquarters;
    private Facility outpost;

    @BeforeEach
    void setUp() {
        databaseUrl = "jdbc:sqlite:" + tempDir.resolve("model-integration.db");
        store = new SQLiteDataStore(databaseUrl);

        headquarters = store.addFacility(
                new Facility("Integration HQ", "IHQ"));

        outpost = store.addFacility(
                new Facility("Integration Outpost", "IOP"));
    }

    @AfterEach
    void tearDown() {
        store.close();
    }

    // ---------- Helpers ----------

    private Agent newAgent(Facility facility, String name) {
        return store.addAgent(new Agent(
                facility.getFacilityID(),
                name,
                "1990-01-01",
                null,
                "integration test agent"));
    }

    private Mission newMission(Facility facility, String title) {
        return store.addMission(
                new Mission(title, facility.getFacilityID()));
    }

    private static boolean hasAgent(List<Agent> agents, Agent agent) {
        return agents.stream()
                .anyMatch(a -> a.getAgentID() == agent.getAgentID());
    }

    private static boolean hasMission(List<Mission> missions, Mission mission) {
        return missions.stream()
                .anyMatch(m -> m.getMissionID() == mission.getMissionID());
    }

    private Agent seededAgent(String name) {
        return store.listAgents().stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("seeded agent missing: " + name));
    }

    // ---------- Seeded Dataset ----------

    @Test
    void seededAgentsAreAssignedToTheirTsvMissions() {
        assertTrue(hasMission(
                store.listMissionsForAgent(
                        seededAgent("Vanguard").getAgentID()),
                store.getMission(1)));

        assertTrue(hasMission(
                store.listMissionsForAgent(
                        seededAgent("Ghost").getAgentID()),
                store.getMission(10)));

        assertTrue(hasMission(
                store.listMissionsForAgent(
                        seededAgent("Jackal").getAgentID()),
                store.getMission(58)));
    }

    @Test
    void seededDataOnlyReferencesExistingRecords() {
        for (Mission mission : store.listMissions()) {
            assertNotNull(store.getFacility(mission.getFacilityID()),
                    "mission " + mission.getMissionID() + " has no facility");
        }

        for (Brief brief : store.listBriefs()) {
            assertNotNull(store.getMission(brief.getMissionID()),
                    "brief " + brief.getBriefID() + " has no mission");
        }

        for (Agent agent : store.listAgents()) {
            assertNotNull(store.getFacility(agent.getFacilityID()),
                    "agent " + agent.getName() + " has no facility");

            assertFalse(
                    store.listMissionsForAgent(agent.getAgentID()).isEmpty(),
                    "agent " + agent.getName() + " has no mission");
        }
    }

    // ---------- Joint Operations ----------

    @Test
    void jointOperationKeepsEachAgentsHomeFacility() {
        Mission mission = newMission(
                headquarters, "Operation Joint Venture");

        Agent local = newAgent(headquarters, "Local Agent");
        Agent visitor = newAgent(outpost, "Visiting Agent");

        store.assignAgentToMission(
                local.getAgentID(), mission.getMissionID());

        store.assignAgentToMission(
                visitor.getAgentID(), mission.getMissionID());

        List<Agent> team =
                store.listAgentsForMission(mission.getMissionID());

        assertEquals(2, team.size());

        for (Agent member : team) {
            int expectedFacility =
                    member.getAgentID() == local.getAgentID()
                            ? headquarters.getFacilityID()
                            : outpost.getFacilityID();

            assertEquals(expectedFacility, member.getFacilityID());
        }

        assertEquals(
                headquarters.getFacilityID(),
                store.getMission(mission.getMissionID()).getFacilityID());
    }

    @Test
    void agentCanServeMissionsRunByDifferentFacilities() {
        Mission hqMission = newMission(headquarters, "HQ Mission");
        Mission outpostMission = newMission(outpost, "Outpost Mission");

        Agent agent = newAgent(outpost, "Busy Agent");

        store.assignAgentToMission(
                agent.getAgentID(), hqMission.getMissionID());

        store.assignAgentToMission(
                agent.getAgentID(), outpostMission.getMissionID());

        List<Mission> missions =
                store.listMissionsForAgent(agent.getAgentID());

        assertEquals(2, missions.size());

        for (Mission mission : missions) {
            int expectedFacility =
                    mission.getMissionID() == hqMission.getMissionID()
                            ? headquarters.getFacilityID()
                            : outpost.getFacilityID();

            assertEquals(expectedFacility, mission.getFacilityID());
        }

        assertEquals(
                outpost.getFacilityID(),
                store.getAgent(agent.getAgentID()).getFacilityID());
    }

    // ---------- Missions and Briefs ----------

    @Test
    void newMissionStartsWithNoBriefsOrAgents() {
        Mission mission = newMission(headquarters, "Empty Mission");

        assertTrue(
                store.listBriefsForMission(mission.getMissionID()).isEmpty());

        assertTrue(
                store.listAgentsForMission(mission.getMissionID()).isEmpty());
    }

    @Test
    void briefsStayWithTheirOwnMission() {
        Mission first = newMission(headquarters, "First Mission");
        Mission second = newMission(outpost, "Second Mission");

        Brief a = store.addBrief(new Brief(
                first.getMissionID(), "Brief A", "2026-11-01", "first a"));

        Brief b = store.addBrief(new Brief(
                first.getMissionID(), "Brief B", "2026-11-02", "first b"));

        Brief c = store.addBrief(new Brief(
                second.getMissionID(), "Brief C", "2026-11-03", "second c"));

        List<Brief> firstBriefs =
                store.listBriefsForMission(first.getMissionID());

        List<Brief> secondBriefs =
                store.listBriefsForMission(second.getMissionID());

        assertEquals(2, firstBriefs.size());
        assertEquals(1, secondBriefs.size());

        assertTrue(firstBriefs.stream()
                .allMatch(br -> br.getMissionID() == first.getMissionID()));

        assertEquals(c.getBriefID(), secondBriefs.get(0).getBriefID());

        assertEquals(first.getMissionID(),
                store.getBrief(a.getBriefID()).getMissionID());

        assertEquals(first.getMissionID(),
                store.getBrief(b.getBriefID()).getMissionID());
    }

    @Test
    void updatingMissionKeepsBriefsAndAgents() {
        Mission mission = newMission(headquarters, "Old Title");
        Agent agent = newAgent(headquarters, "Loyal Agent");

        store.assignAgentToMission(
                agent.getAgentID(), mission.getMissionID());

        store.addBrief(new Brief(
                mission.getMissionID(),
                "Kept Brief",
                "2026-11-05",
                "still here"));

        store.updateMission(new Mission(
                mission.getMissionID(),
                "New Title",
                outpost.getFacilityID()));

        Mission updated = store.getMission(mission.getMissionID());

        assertEquals("New Title", updated.getTitle());
        assertEquals(outpost.getFacilityID(), updated.getFacilityID());

        assertEquals(1,
                store.listBriefsForMission(mission.getMissionID()).size());

        assertTrue(hasAgent(
                store.listAgentsForMission(mission.getMissionID()), agent));

        assertEquals(
                "New Title",
                store.listMissionsForAgent(agent.getAgentID())
                        .get(0).getTitle());
    }

    // ---------- Assignment Changes ----------

    @Test
    void unassigningOneMissionKeepsOtherAssignments() {
        Mission keep = newMission(headquarters, "Mission To Keep");
        Mission drop = newMission(outpost, "Mission To Drop");

        Agent agent = newAgent(headquarters, "Reassigned Agent");

        store.assignAgentToMission(
                agent.getAgentID(), keep.getMissionID());

        store.assignAgentToMission(
                agent.getAgentID(), drop.getMissionID());

        store.unassignAgentFromMission(
                agent.getAgentID(), drop.getMissionID());

        List<Mission> missions =
                store.listMissionsForAgent(agent.getAgentID());

        assertEquals(1, missions.size());
        assertEquals(keep.getMissionID(), missions.get(0).getMissionID());

        assertFalse(hasAgent(
                store.listAgentsForMission(drop.getMissionID()), agent));

        assertNotNull(store.getAgent(agent.getAgentID()),
                "agent record must not be deleted");
    }

    @Test
    void rejectedAssignmentsDoNotChangeData() {
        Mission mission = newMission(headquarters, "Guarded Mission");
        Agent agent = newAgent(outpost, "Careful Agent");

        store.assignAgentToMission(
                agent.getAgentID(), mission.getMissionID());

        int unknownId = 999_999;

        assertThrows(IllegalArgumentException.class,
                () -> store.assignAgentToMission(
                        agent.getAgentID(), unknownId));

        assertThrows(IllegalArgumentException.class,
                () -> store.assignAgentToMission(
                        unknownId, mission.getMissionID()));

        assertThrows(IllegalArgumentException.class,
                () -> store.assignAgentToMission(
                        agent.getAgentID(), mission.getMissionID()));

        assertThrows(IllegalArgumentException.class,
                () -> store.unassignAgentFromMission(
                        unknownId, mission.getMissionID()));

        assertEquals(1,
                store.listMissionsForAgent(agent.getAgentID()).size());

        assertEquals(1,
                store.listAgentsForMission(mission.getMissionID()).size());
    }

    @Test
    void missionsRequireAnExistingFacility() {
        Mission mission = newMission(headquarters, "Real Mission");

        assertThrows(IllegalArgumentException.class,
                () -> store.addMission(
                        new Mission("Orphan Mission", 999_999)));

        assertThrows(IllegalArgumentException.class,
                () -> store.updateMission(
                        new Mission(
                                mission.getMissionID(),
                                "Moved",
                                999_999)));

        Mission unchanged = store.getMission(mission.getMissionID());

        assertEquals("Real Mission", unchanged.getTitle());
        assertEquals(
                headquarters.getFacilityID(),
                unchanged.getFacilityID());
    }

    // ---------- Persistence ----------

    @Test
    void dataSurvivesReopeningTheDatabase() {
        Mission mission = newMission(
                headquarters, "Persistent Mission");

        Agent agent = newAgent(outpost, "Persistent Agent");

        store.assignAgentToMission(
                agent.getAgentID(), mission.getMissionID());

        store.addBrief(new Brief(
                mission.getMissionID(),
                "Saved Brief",
                "2026-11-06",
                "on disk"));

        int missionCount = store.listMissions().size();
        int agentCount = store.listAgents().size();

        store.close();

        store = new SQLiteDataStore(databaseUrl);

        assertEquals(missionCount, store.listMissions().size());
        assertEquals(agentCount, store.listAgents().size());

        assertTrue(hasAgent(
                store.listAgentsForMission(mission.getMissionID()), agent));

        assertTrue(hasMission(
                store.listMissionsForAgent(agent.getAgentID()), mission));

        assertEquals(
                "Saved Brief",
                store.listBriefsForMission(mission.getMissionID())
                        .get(0).getTitle());
    }
}
