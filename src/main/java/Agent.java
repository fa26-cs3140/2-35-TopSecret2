import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

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
        setDate_of_birth(date_of_birth);
        setDate_of_death(date_of_death);
        this.notes = notes;
    }

    public int getFacilityID() {
        return facilityID;
    }


    public int getMissionID() {
        return missionID;
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

    public void setDate_of_death(String date_of_death) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate deathDate = LocalDate.parse(date_of_death, formatter);

            if (getDate_of_birth() != null) {
                LocalDate birthDate = LocalDate.parse(getDate_of_birth(), formatter);

                if (deathDate.isBefore(birthDate)) {
                    throw new IllegalArgumentException(
                            "Date of death cannot be before date of birth."
                    );
                }
            }

            this.date_of_death = date_of_death;

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date. Expected a valid date in YYYY-MM-DD format.", e
            );
        }
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate.parse(date_of_birth, formatter);
            this.date_of_birth = date_of_birth;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date. Expected a valid date in YYYY-MM-DD format.", e
            );
        }
    }

    public String getName() {
        return name;
    }
}
