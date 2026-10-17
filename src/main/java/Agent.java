public class Agent {
    private int agentID, missionID, facilityID;
    private String name, date_of_birth, date_of_death, notes;

    /**
     * usually used for data insertion to database
     * @param name
     * @param date_of_birth
     * @param date_of_death
     * @param notes
     */
    public Agent(int missionID, int facilityID, String name, String date_of_birth, String date_of_death, String notes) {
        this(-1, missionID, facilityID, name, date_of_birth, date_of_death, notes);
    }

    /**
     * for database use only, not to be used for database data insertion.
     * The {@code agentID} parameter will be ignored because database automatically assigns one when inserting.
     *
     * @param agentID
     * @param name
     * @param date_of_birth
     * @param date_of_death
     * @param notes
     */
    public Agent(int agentID, int missionID, int facilityID, String name, String date_of_birth, String date_of_death, String notes) {
        this.agentID = agentID;
        this.missionID = missionID;
        this.facilityID = facilityID;
        this.name = name;
        this.date_of_birth = date_of_birth;
        this.date_of_death = date_of_death;
        this.notes = notes;
    }

    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    public int getMissionID() {
        return missionID;
    }

    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }

    public int getAgentID() {
        return agentID;
    }

    public String getNotes() {
        return notes;
    }

    public String getDate_of_death() {
        return date_of_death;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getName() {
        return name;
    }
}
