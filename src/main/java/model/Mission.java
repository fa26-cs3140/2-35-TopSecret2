package model;

public class Mission {
    private int missionID, facilityID;
    private String title;

    /**
     * for creation of a new model.Mission.
     *
     * @param title
     * @param facilityID
     */
    public Mission(String title, int facilityID) {
        this(-1,title,facilityID);
    }


    /**
     * for storage of existing mission.
     *
     * @param missionID
     * @param title
     * @param facilityID
     */
    public Mission(int missionID, String title, int facilityID) {
        this.missionID = missionID;
        this.title = title;
        this.facilityID = facilityID;
    }

    public int getMissionID() {
        return  this.missionID;
    }

    public String getTitle() {
        return this.title;
    }

    public int getFacilityID() {
        return this.facilityID;
    }



}
