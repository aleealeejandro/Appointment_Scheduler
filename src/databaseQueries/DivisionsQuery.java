package databaseQueries;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Division;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DivisionsQuery {
    public static ObservableList<Division> getAllDivisions(String countryName) throws SQLException {
        Country country = CountriesQuery.getCountryID(countryName);

        ObservableList<Division> divisions = FXCollections.observableArrayList();
        assert country != null;
        String query = "SELECT Division_ID, Division FROM client_schedule.first_level_divisions ORDER BY Division;";

        if(countryName == null) {
            query = "SELECT Division_ID, Division FROM client_schedule.first_level_divisions ORDER BY Division;";
        } else if(!countryName.equals("All")) {
            query = "SELECT Division_ID, Division FROM client_schedule.first_level_divisions WHERE Country_ID=" + country.getCountryID() + " ORDER BY Division;";
        }

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet divisionsSet = preparedStatement.executeQuery();

            while(divisionsSet.next()) {
                int divisionID = divisionsSet.getInt("Division_ID");
                String divisionName = divisionsSet.getString("Division");
                Division division = new Division(divisionID, divisionName);
                divisions.add(division);
            }
            return divisions;
        } catch(SQLException err) {
            System.out.println("Error: " + err.getMessage());
            return null;
        }
    }

    public static Country getCountryFromDivisionID(int divisionId) throws SQLException {
        String query = "SELECT divisions.Country_ID, Country FROM client_schedule.first_level_divisions divisions INNER JOIN client_schedule.countries countries ON divisions.Country_ID = countries.Country_ID WHERE Division_ID=\"" + divisionId + "\";";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                String countryID = result.getString("Country_ID");
                String countryName = result.getString("Country");
                Country country = new Country(countryID, countryName);

                System.out.println("Country: " + country.getCountry() + "\nCountry_ID:" + country.getCountryID());
                return country;
            }

        } catch(SQLException err) {
            System.out.println("Error: " + err.getMessage());
        }

        return null;
    }

    public static Division getDivisionFromDivisionID(int divisionId) throws SQLException {
        String query = "SELECT Division_ID, Division FROM client_schedule.first_level_divisions WHERE Division_ID=\"" + divisionId + "\";";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                int divisionID = result.getInt("Division_ID");
                String divisionName = result.getString("Division");

                return new Division(divisionID, divisionName);
            }

        } catch(SQLException err) {
            System.out.println("Error: " + err.getMessage());
        }

        return null;
    }

    public static Division getDivisionFromDivisionName(String divisionNameString) throws SQLException{
        String query = "SELECT Division_ID, Division FROM client_schedule.first_level_divisions WHERE Division=\"" + divisionNameString + "\";";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                int divisionID = result.getInt("Division_ID");
                String divisionName = result.getString("Division");

                return new Division(divisionID, divisionName);
            }

        } catch(SQLException err) {
            System.out.println("Error: " + err.getMessage());
        }

        return null;
    }

//    public static ObservableList<Division> getAllDivisions() throws SQLException {}
//    public static Division getDivisionID(String divisionName) throws SQLException {}
//    public static ObservableList<Division> getDivisionsByCountry(String country) throws SQLException {}
}
