package database;

public class Agent {
    private int id;
    private String name, date_of_birth, date_of_death, notes;

    public Agent(int id, String name, String date_of_birth, String date_of_death, String notes) {
        this.id = id;
        this.name = name;
        this.date_of_birth = date_of_birth;
        this.date_of_death = date_of_death;
        this.notes = notes;
    }


    public int getId() {
        return id;
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
