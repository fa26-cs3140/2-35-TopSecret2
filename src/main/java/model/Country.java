package model;

public class Country {

    private int countryID;
    private String name;

    // Constructor for creating a new country
    public Country(String name) {
        this.name = name;
    }

    // Constructor for retrieving an existing country
    public Country(int countryID, String name) {
        this.countryID = countryID;
        this.name = name;
    }

    public int getCountryID() {
        return countryID;
    }

    public String getName() {
        return name;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public void setName(String name) {
        this.name = name;
    }
}
