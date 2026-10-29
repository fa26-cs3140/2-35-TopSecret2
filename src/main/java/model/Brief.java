package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Brief {

    private int briefID, missionID;
    private String title, date, content;

    /**
     * used to create new Briefs for insertion to the database
     *
     * @param missionID
     * @param title
     * @param date
     * @param content
     */
    public Brief(int missionID, String title, String date, String content) {
        this(-1, missionID, title, date, content);
    }

    /**
     * used to store existing briefs from the database
     * The {@code briefID} parameter will be ignored when inserting to DB because database automatically assigns one when inserting.
     *
     * @param briefID
     * @param missionID
     * @param title
     * @param date
     * @param content
     */
    public Brief(int briefID, int missionID, String title, String date, String content) {
        this.briefID = briefID;
        this.missionID = missionID;
        this.title = title;
        setDate(date);
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public String getDate() {
        return date;
    }

    private void setDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate.parse(date, formatter);
            this.date = date;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date. Expected a valid date in YYYY-MM-DD format.", e
            );
        }
    }

    public String getTitle() {
        return title;
    }


    public int getMissionID() {
        return missionID;
    }


    public int getBriefID() {
        return briefID;
    }

}
