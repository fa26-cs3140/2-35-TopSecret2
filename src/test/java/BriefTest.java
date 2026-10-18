import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BriefTest {

    @Test
    public void briefTest1() {
        Brief b = new Brief(1, "t1", "1990-12-30", "con1");

        assertEquals(-1, b.getBriefID());
        assertEquals(1, b.getMissionID());
        assertEquals("t1", b.getTitle());
        assertEquals("1990-12-30", b.getDate());
        assertEquals("con1", b.getContent());
    }

    @Test
    public void briefTest2() {
        Brief b = new Brief(25,1, "t1", "1990-12-30", "con1");

        assertEquals(25, b.getBriefID());
        assertEquals(1, b.getMissionID());
        assertEquals("t1", b.getTitle());
        assertEquals("1990-12-30", b.getDate());
        assertEquals("con1", b.getContent());
    }

    @Test
    public void invalidDateTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Brief(1, "t1", "22222-25-30", "con1");
        });
    }

    @Test
    public void invalidDateTest2() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Brief(1, "t1", "1990/12/02", "con1");
        });
    }

    @Test
    public void validDateTest() {
        assertDoesNotThrow(() -> {
            new Brief(1, "t1", "1990-12-02", "con1");
        });
    }
}
