import database.Agent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentTest {

    @Test
    public void testAgent1() {
        Agent a1 = new Agent(1, "Jack Williams", "1998-05-01", "2022-11-12", "sample notes1");

        assertEquals(1, a1.getId());
        assertEquals("Jack Williams", a1.getName());
        assertEquals("1998-05-01", a1.getDate_of_birth());
        assertEquals("2022-11-12", a1.getDate_of_death());
        assertEquals("sample notes1", a1.getNotes());

    }


    @Test
    public void testAgent2() {
        Agent a1 = new Agent(1, "Jack Williams", "1998-05-01", "2022-11-12", "sample notes1");
        Agent a2 = new Agent(2, "Jack Willy", "1998-08-01", "2022-08-12", "sample notes2");

        assertEquals(1, a1.getId());
        assertEquals("Jack Williams", a1.getName());
        assertEquals("1998-05-01", a1.getDate_of_birth());
        assertEquals("2022-11-12", a1.getDate_of_death());
        assertEquals("sample notes1", a1.getNotes());

        assertEquals(2, a2.getId());
        assertEquals("Jack Willy", a2.getName());
        assertEquals("1998-08-01", a2.getDate_of_birth());
        assertEquals("2022-08-12", a2.getDate_of_death());
        assertEquals("sample notes2", a2.getNotes());
    }
}
