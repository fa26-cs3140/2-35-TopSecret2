package database;

public class Facility {
    private int id;
    private String name, abbreviation;

    public Facility(int id, String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public int getId() {
        return id;
    }
}
