package model;

public class Country {
    private String countryID;
    private String country;

    public Country(String countryID, String country) {
        this.countryID = countryID;
        this.country = country;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
