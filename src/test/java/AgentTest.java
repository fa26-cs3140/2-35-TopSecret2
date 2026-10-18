import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentTest {

    @Test
    public void testAgent1() {
        Agent a1 = new Agent(1,2,3, "Jack Williams", "1998-05-01", "2022-11-12", "sample notes1");

        assertEquals(1,a1.getAgentID());
        assertEquals(2, a1.getMissionID());
        assertEquals(3, a1.getFacilityID());
        assertEquals("Jack Williams", a1.getName());
        assertEquals("1998-05-01",a1.getDate_of_birth());
        assertEquals("2022-11-12",a1.getDate_of_death());
        assertEquals("sample notes1", a1.getNotes());
    }

    @Test
    public void testAgent2() {
        Agent a1 = new Agent(2,3, "Jack Williams", "1998-05-01", "2022-11-12", "sample notes1");

        assertEquals(-1,a1.getAgentID());
        assertEquals(2, a1.getMissionID());
        assertEquals(3, a1.getFacilityID());
        assertEquals("Jack Williams", a1.getName());
        assertEquals("1998-05-01",a1.getDate_of_birth());
        assertEquals("2022-11-12",a1.getDate_of_death());
        assertEquals("sample notes1", a1.getNotes());
    }


    @Test
    public void testAgent3() {
        Agent a1 = new Agent(4, 5, "Jackie Will", "1990-05-01",
                "2022-10-12", "sample notes1");
        Agent a2 = new Agent(2, 3, "Jack Williams", "1998-05-01",
                "2022-11-12", "sample notes2");

        assertEquals(4, a1.getAgentID());
        assertEquals("Jackie Will", a1.getName());
        assertEquals("1990-05-01", a1.getDate_of_birth());
        assertEquals("2022-10-12", a1.getDate_of_death());
        assertEquals("sample notes1", a1.getNotes());

        assertEquals(2, a2.getAgentID());
        assertEquals("Jack Williams", a2.getName());
        assertEquals("1998-05-01", a2.getDate_of_birth());
        assertEquals("2022-11-12", a2.getDate_of_death());
        assertEquals("sample notes2", a2.getNotes());
    }
}
