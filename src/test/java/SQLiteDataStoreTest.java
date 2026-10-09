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
        }
    }
}
