package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryTest {
    @Test
    void setNameChanges() {
        Country testCountry = new Country("Soviet Union");
        testCountry.setName("Russian Federation");

        assertEquals("Russian Federation", testCountry.getName());
    }

    @Test
    void setCountryIDChanges() {
        Country testCountry = new Country("Soviet Union");
        testCountry.setCountryID(1922);

        assertEquals(1922, testCountry.getCountryID());
    }
}
