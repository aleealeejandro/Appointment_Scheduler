package model;

/**
 *
 * @author Alexander Padilla
 */
public class Division {
    private int divisionID;
    private String division;
    private int countryID;

    /**
     * Division constructor
     *
     * @param divisionID the id of the specific division
     * @param division the name of the division
     * @param countryID the id of the specific country
     */
    public Division(int divisionID, String division, int countryID) {
        this.divisionID = divisionID;
        this.division = division;
        this.countryID = countryID;
    }

    /**
     * Division constructor()
     *
     * @param divisionID the id of the specific division based on the
     * @param division the name of the division
     */
    public Division(int divisionID, String division) {
        this.divisionID = divisionID;
        this.division = division;
    }

    /**
     * Division constructor()
     *
     * @param countryID the id of the specific country
     */
    public Division(int countryID) {
        this.countryID = countryID;
    }

    /**
     * @return the division's ID
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @param divisionID the division's ID to set
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * @return the division's name
     */
    public String getDivision() {
        return division;
    }

    /**
     * @param division the division's name to set
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * @return the division's country ID
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * @param countryID the division's country ID to set
     */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}
