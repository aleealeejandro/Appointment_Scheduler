package model;

/**
 *
 * @author Alexander Padilla
 */
public class Country {
    private String countryID;
    private String country;

    /**
     * Country constructor()
     *
     * @param countryID the id of the specific country
     * @param country the name of the country
     */
    public Country(String countryID, String country) {
        this.countryID = countryID;
        this.country = country;
    }

    /**
     * @return the country's ID
     */
    public String getCountryID() {
        return countryID;
    }

    /**
     * @param countryID the country's ID to set
     */
    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    /**
     * @return the country's name
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country's name to set
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
