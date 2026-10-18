public class Log {

    private int logID;
    private String eventType, contentType, username, time;

    /**
     * used for creation of new logs
     *
     * @param eventType
     * @param contentType
     * @param username
     * @param time
     */
    public  Log(String eventType, String contentType, String username, String time) {
        this(-1,eventType,contentType,username,time);
    }

    /**
     * used to store existing logs from database
     *
     * @param logID
     * @param eventType
     * @param contentType
     * @param username
     * @param time
     */
    public  Log(int logID, String eventType, String contentType, String username, String time) {
        this.logID = logID;
        this.eventType = eventType;
        this.contentType = contentType;
        this.username = username;
        this.time = time;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
