public class Facility {
    private int facilityID;
    private String name, abbreviation;


    /**
     * usually used for data insertion to database
     * @param name
     * @param abbreviation
     */
    public Facility(String name, String abbreviation) {
        this(-1, name, abbreviation);
    }



    /**
     *  for database use only, not to be used for database data insertion.
     *  The {@code facilityID} parameter will be ignored because database automatically assigns one when inserting.
     * @param facilityID
     * @param name
     * @param abbreviation
     */
    public Facility(int facilityID, String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.facilityID = facilityID;
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
}
