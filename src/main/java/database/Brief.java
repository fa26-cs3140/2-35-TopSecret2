package database;

public class Brief {

    private int briefID, missionID;
    private String title, date, content;

    public Brief(int briefID, int missionID, String title, String date, String content) {
        setBriefID(briefID);
        setMissionID(missionID);
        setTitle(title);
        setDate(date);
        setContent(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMissionID() {
        return missionID;
    }

    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }

    public int getBriefID() {
        return briefID;
    }

    public void setBriefID(int briefID) {
        this.briefID = briefID;
    }
}
