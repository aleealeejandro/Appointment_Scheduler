package databaseQueries;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;
import model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles all country queries to the database
 * @author Alexander Padilla
 */
public class CountriesQuery {

    /**
     * gets all countries from the database
     *
     * @return list of countries
     */
    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> countries = FXCollections.observableArrayList();
        String query = "SELECT Country_ID, Country FROM client_schedule.countries ORDER BY Country_ID;";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet countriesSet = preparedStatement.executeQuery();

            while(countriesSet.next()) {
                String countryID = countriesSet.getString("Country_ID");
                String countryName = countriesSet.getString("Country");
                Country country = new Country(countryID, countryName);
                countries.add(country);
            }
            return countries;
        } catch(SQLException err) {
            System.out.println("Error: " + err.getMessage());
            return null;
        }
    }

    /**
     * gets countryID from the database based on user selected country name
     *
     * @return country ID
     */
    public static Country getCountryID(String countryChoice) {
        String query = "SELECT Country_ID, Country FROM client_schedule.countries WHERE Country=\"" + countryChoice + "\";";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                String countryID = result.getString("Country_ID");
                String countryName = result.getString("Country");
                return new Country(countryID, countryName);
            }

        } catch(SQLException err) {
            System.out.println("Error: " + err.getMessage());
        }

        return null;
    }

//    public static Country getCountryQuery(int countryId) {
//        String query = "SELECT Country FROM client_schedule.countries WHERE Country=\"" + countryId + "\";";
//
//        try {
//            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
//            ResultSet result = preparedStatement.executeQuery();
//
//            while(result.next()) {
//                String countryID = result.getString("Country_ID");
//                String countryName = result.getString("Country");
//                return new Country(countryID, countryName);
//            }
//
//        } catch(SQLException err) {
//            System.out.println("Error: " + err.getMessage());
//        }
//
//        return null;
//    }
}
