import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {

    @Test
    void constructorStoresMissionFields() {
        Mission mission = new Mission(
                1,
                "Test Mission",
                "2026-10-09",
                "This is a test mission brief."
        );

        assertEquals(1, mission.getId());
        assertEquals("Test Mission", mission.getTitle());
        assertEquals("2026-10-09", mission.getDate());
        assertEquals("This is a test mission brief.", mission.getBrief());
    }
}
