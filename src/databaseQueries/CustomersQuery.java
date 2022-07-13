package databaseQueries;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Handles all customer queries to the database
 * @author Alexander Padilla
 */
public class CustomersQuery {

    /**
     * gets all the customers from the database based on country and division
     *
     * @param country the name of the country
     * @param division the name of the division
     * @return list of customers
     * @throws SQLException if an SQL exception occurs
     */
    public static ObservableList<Customer> getAllCustomersByDivision(String country, String division) throws SQLException {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String query;
//        String query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM client_schedule.customers;";

        if(country.equals("All")) {
            query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, customers.Division_ID " +
                    "FROM client_schedule.customers AS customers " +
                    "   INNER JOIN client_schedule.first_level_divisions AS divisions ON customers.Division_ID = divisions.Division_ID " +
                    "   INNER JOIN client_schedule.countries AS countries ON countries.Country_ID = divisions.Country_ID " +
                    "ORDER BY Customer_Name;";
//                    "ORDER BY Customer_Name;";
        }
        else {
            if(division == null || division.equals("All")) {
                query = String.format("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, customers.Division_ID FROM client_schedule.customers AS customers INNER JOIN client_schedule.first_level_divisions AS divisions ON customers.Division_ID = divisions.Division_ID INNER JOIN client_schedule.countries AS countries ON countries.Country_ID = divisions.Country_ID WHERE countries.Country='%s' ORDER BY Customer_Name;", country);
            }
            else {
                query = String.format("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, customers.Division_ID FROM client_schedule.customers AS customers INNER JOIN client_schedule.first_level_divisions AS divisions ON customers.Division_ID = divisions.Division_ID INNER JOIN client_schedule.countries AS countries ON countries.Country_ID = divisions.Country_ID WHERE countries.Country='%s' AND divisions.Division='%s' ORDER BY Customer_Name;", country, division);
            }
        }

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                String customerID = resultSet.getString("Customer_ID");
                String customerName = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                String divisionID = resultSet.getString("customers.Division_ID");
                Customer customer = new Customer(customerID, customerName, address, postalCode, phone, divisionID);
                customers.add(customer);
            }

            return customers;
        } catch (SQLException err) {
            System.out.println("Error: " + err.getMessage());
//            err.printStackTrace();
            return null;
        }
    }

    /**
     * gets all the customers from the database
     *
     * @return list of customers
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        try {
            String query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM client_schedule.customers ORDER BY Customer_ID;";
//            String query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, divisions.Division, countries.Country, customers.Division_ID\n" +
//                    "FROM client_schedule.customers AS customers\n" +
//                    "\tINNER JOIN client_schedule.first_level_divisions AS divisions ON customers.Division_ID = divisions.Division_ID\n" +
//                    "    INNER JOIN client_schedule.countries AS countries ON countries.Country_ID = divisions.Country_ID\n" +
//                    "ORDER BY Country;";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                String customerID = resultSet.getString("Customer_ID");
                String customerName = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                String divisionID = resultSet.getString("Division_ID");
                Customer customer = new Customer(customerID, customerName, address, postalCode, phone, divisionID);
                customers.add(customer);
            }

            return customers;
        } catch (SQLException err) {
            System.out.println("Error: " + err.getMessage());
            return null;
        }
    }

    /**
     * gets next customer ID in the database
     *
     * @return next customer ID available
     */
    public static String getNextCustomerID() {
        int nextID = 0;
        String lastCustomerIDQuery = "SELECT MAX(Customer_ID) FROM client_schedule.customers;";
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(lastCustomerIDQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                nextID = resultSet.getInt(1) + 1;
            }

            return "" + nextID;
        } catch (SQLException | RuntimeException err) {
            return err.getMessage();
        }
    }

    /**
     * adds customer to the database
     *
     * @param customer the customer object holding information to add to the database
     * @param loggedInUserName the username of the logged-in user
     * @return boolean value whether the customer was added to the database successfully
     */
    public static boolean addCustomer(Customer customer, String loggedInUserName) {
        String query = String.format("INSERT INTO client_schedule.customers VALUES(%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s);",
                Integer.parseInt(customer.getCustomerID()),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getPostalCode(),
                customer.getPhone(),
                LocalDateTime.now(ZoneOffset.UTC),
                loggedInUserName,
                LocalDateTime.now(ZoneOffset.UTC),
                loggedInUserName,
                Integer.parseInt(customer.getDivisionID())
                );
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            int addedBoolean = preparedStatement.executeUpdate();

            return addedBoolean == 1;
        } catch (SQLException | RuntimeException err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * updates customer in the database
     *
     * @param customer the customer object holding information to update a specific customer in the database
     * @param loggedInUserName the username of the logged-in user
     * @return boolean value whether the customer was updated in the database successfully
     */
    public static boolean updateCustomer(Customer customer, String loggedInUserName) {
        String query = String.format("UPDATE client_schedule.customers SET Customer_Name='%s', Address='%s', Postal_Code='%s', Phone='%s',Last_Update='%s', Last_Updated_By='%s', Division_ID=%s WHERE Customer_ID=%s;",
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getPostalCode(),
                customer.getPhone(),
                LocalDateTime.now(ZoneOffset.UTC),
                loggedInUserName,
                Integer.parseInt(customer.getDivisionID()),
                Integer.parseInt(customer.getCustomerID())
        );

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            int updatedBoolean = preparedStatement.executeUpdate();

            return updatedBoolean == 1;
        } catch (SQLException | RuntimeException err) {
//            System.out.println(err);
            err.printStackTrace();
            return false;
        }
    }

    /**
     * deletes a customer in the database
     *
     * @param customerID the customer ID to search for in database
     * @return boolean value whether the customer was deleted or not
     */
    public static boolean deleteCustomer(String customerID) {
        String query = String.format("DELETE FROM client_schedule.customers WHERE Customer_ID=%s", customerID);
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            int deleteBool = preparedStatement.executeUpdate();

            return deleteBool == 1;
        } catch (SQLException | RuntimeException err) {
            err.printStackTrace();
            return false;
        }
    }
}
