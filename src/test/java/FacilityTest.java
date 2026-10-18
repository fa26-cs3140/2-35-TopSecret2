import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FacilityTest {

    @Test
    public void facilityTest1(){
        Facility f1 = new Facility(1,"Startrek", "SK");

        assertEquals(1,f1.getFacilityID());
        assertEquals("Startrek", f1.getName());
        assertEquals("SK", f1.getAbbreviation());

    }

    @Test
    public void facilityTest2(){
        Facility f1 = new Facility("Startrek", "SK");

        assertEquals(-1,f1.getFacilityID());
        assertEquals("Startrek", f1.getName());
        assertEquals("SK", f1.getAbbreviation());

    }

    @Test
    public void facilityTest3(){
        Facility f1 = new Facility(1,"Startrek", "SK");
        Facility f2 = new Facility(2,"Sandtrap", "ST");

        assertEquals(1,f1.getFacilityID());
        assertEquals("Startrek", f1.getName());
        assertEquals("SK", f1.getAbbreviation());

        assertEquals(2,f2.getFacilityID());
        assertEquals("Sandtrap", f2.getName());
        assertEquals("ST", f2.getAbbreviation());
    }


}
