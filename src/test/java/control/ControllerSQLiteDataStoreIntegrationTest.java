package control;

import java.nio.file.Path;
import java.util.List;
import model.AccessControlInterface;
import model.AuditLogInterface;
import model.SQLiteDataStore;
import model.UserManagerInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Integration tests: a real SQLiteDataStore on a test database
// behind the full Controller. Users, access, audit, and messages
// stay mocked: this file tests the controller-to-store wiring
// only, that is, reads, adds, edits, and assignments against
// real rows. The test database seeds the dataset TSVs on
// construction: 6 facilities, 100 missions, 100 briefs, and
// 10 agents, so a record added here takes the next id.
// Run: .\gradlew test --tests "control.ControllerSQLiteDataStoreIntegrationTest" --console=plain
public class ControllerSQLiteDataStoreIntegrationTest {
    @TempDir
    Path tempDir;

    private SQLiteDataStore store;
    private Controller controller;

    @BeforeEach
    void setUp() throws Exception {
        String databaseUrl = "jdbc:sqlite:" + tempDir.resolve("test.db");
        store = new SQLiteDataStore(databaseUrl);
        AccessControlInterface access = mock(AccessControlInterface.class);
        when(access.isAdmin("boss")).thenReturn(true);
        controller = new Controller(mock(UserManagerInterface.class), access,
                store, mock(AuditLogInterface.class), mock(MessageManager.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        store.close();
    }

    @Test
    void listEntriesShowsSeededFacilities() {
        String list = controller.listEntries("boss", "facilities");
        assertTrue(list.contains("1 : Blackwood Annex"));
        assertTrue(list.contains("2 : Rose Garden"));
    }

    @Test
    void listEntriesShowsSeededAgents() {
        String list = controller.listEntries("boss", "agents");
        assertTrue(list.contains("1 : Vanguard"));
        assertTrue(list.contains("2 : Ghost"));
    }

    @Test
    void listEntriesShowsSeededMissions() {
        assertTrue(controller.listEntries("boss", "missions")
                .contains("1 : Operation Sandtrap"));
    }

    @Test
    void readEntryShowsSeededBrief() {
        String brief = controller.readEntry("boss", "briefs", 1);
        assertTrue(brief.contains("Title: Operation Sandtrap"));
        assertTrue(brief.contains("Date: 1970-11-03"));
    }

    @Test
    void readEntryShowsSeededAgent() {
        String agent = controller.readEntry("boss", "agents", 2);
        assertTrue(agent.contains("Name: Ghost"));
        assertTrue(agent.contains("DoB: 1992-11-22"));
    }

    @Test
    void readEntryNotFoundOnRealStore() {
        assertEquals("No brief found with this id",
                controller.readEntry("boss", "briefs", 999));
    }

    @Test
    void addEntryPersistsBriefInRealStore() {
        String list = controller.addEntry("boss", "briefs",
                List.of("1", "New Brief", "2026-01-01", "New content"));
        assertTrue(list.contains("New Brief"));
        assertEquals("New Brief", store.getBrief(101).getTitle());
    }

    @Test
    void addEntryPersistsAgentInRealStore() {
        String list = controller.addEntry("boss", "agents",
                List.of("1", "New Agent", "1990-01-01", "2000-01-01", "Notes"));
        assertTrue(list.contains("New Agent"));
        assertEquals("New Agent", store.getAgent(11).getName());
    }

    @Test
    void addEntryPersistsFacilityInRealStore() {
        String list = controller.addEntry("boss", "facilities",
                List.of("New Facility", "NF"));
        assertTrue(list.contains("New Facility"));
        assertEquals("New Facility", store.getFacility(7).getName());
    }

    @Test
    void editEntryUpdatesRealBrief() {
        // Fails until the store implements updateBrief: the
        // update is still a null stub, so the title stays put.
        controller.editEntry("boss", "briefs", 1,
                List.of("1", "Edited Title", "1970-11-03", "Edited content"));
        assertEquals("Edited Title", store.getBrief(1).getTitle());
    }

    @Test
    void editEntryRejectsUnknownIdOnRealStore() {
        assertEquals("No brief found with this id",
                controller.editEntry("boss", "briefs", 999,
                        List.of("1", "X", "2026-01-01", "Y")));
    }

    @Test
    void assignAgentToMissionWorksOnRealStore() {
        // Mission 2 starts empty: the seed assigns Vanguard to mission 1.
        assertEquals("Agent 2 assigned to mission 2",
                controller.assignAgentToMission("boss", 2, 2));
        assertEquals(1, store.listAgentsForMission(2).size());
        assertEquals("Ghost", store.listAgentsForMission(2).get(0).getName());
    }

    @Test
    void assignAgentToMissionRejectsUnknownAgent() {
        assertEquals("Agent does not exist.",
                controller.assignAgentToMission("boss", 999, 2));
        assertTrue(store.listAgentsForMission(2).isEmpty());
    }
}
