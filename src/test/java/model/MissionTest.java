package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {

    @Test
    void constructorStoresMissionFields() {
        Mission m1 = new  Mission("model.Mission 1", 1);

        assertEquals(-1, m1.getMissionID());
        assertEquals("model.Mission 1", m1.getTitle());
        assertEquals(1, m1.getFacilityID());
    }

    @Test
    void constructorStoresMissionFields1() {
        Mission m1 = new  Mission(5,"model.Mission 1", 1);

        assertEquals(5, m1.getMissionID());
        assertEquals("model.Mission 1", m1.getTitle());
        assertEquals(1, m1.getFacilityID());
    }

    @Test
    void constructorStoresMissionFields2() {
        Mission m1 = new Mission("model.Mission 1", 1);
        Mission m3 = new Mission("model.Mission 2", 3);

        assertEquals(-1, m1.getMissionID());
        assertEquals("model.Mission 1", m1.getTitle());
        assertEquals(1, m1.getFacilityID());

        assertEquals(-1, m3.getMissionID());
        assertEquals("model.Mission 2", m3.getTitle());
        assertEquals(3, m3.getFacilityID());
    }
}
