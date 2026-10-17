import database.Facility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FacilityTest {

    @Test
    public void facilityTest1(){
        Facility f1 = new Facility(1,"Startrek", "SK");

        assertEquals(1,f1.getId());
        assertEquals(f1.getName(),"Startrek");
        assertEquals(f1.getAbbreviation(), "SK");

    }

    @Test
    public void facilityTest2(){
        Facility f1 = new Facility(1,"Startrek", "SK");
        Facility f2 = new Facility(2,"Sandtrap", "ST");

        assertEquals(1,f1.getId());
        assertEquals(f1.getName(),"Startrek");
        assertEquals(f1.getAbbreviation(), "SK");

        assertEquals(2,f2.getId());
        assertEquals(f2.getName(),"Sandtrap");
        assertEquals(f2.getAbbreviation(), "ST");
    }


}
