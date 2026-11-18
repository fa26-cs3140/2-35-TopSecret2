package model;

public class Facility {

    private int facilityID;
    private int countryID;
    private String name, abbreviation;

    // Used for data insertion
    public Facility(String name, String abbreviation, int countryID) {
        this(-1, name, abbreviation, countryID);
    }

    // Used when retrieving an existing Facility from the database
    public Facility(int facilityID, String name, String abbreviation, int countryID) {
        this.facilityID = facilityID;
        this.name = name;
        this.abbreviation = abbreviation;
        this.countryID = countryID;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public int getFacilityID() {
        return facilityID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}
