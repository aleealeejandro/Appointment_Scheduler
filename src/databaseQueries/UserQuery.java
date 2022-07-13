package databaseQueries;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles all user queries to the database
 * @author Alexander Padilla
 */
public class UserQuery {

    /**
     * gets all the users from the database
     *
     * @return list of users
     */
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            String allUsersQuery = "SELECT User_ID, User_Name FROM client_schedule.users ORDER BY User_ID;";
//            String allUsersQuery = "SELECT User_ID, User_Name FROM client_schedule.users ORDER BY User_Name;";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(allUsersQuery);
            ResultSet userSet = preparedStatement.executeQuery();

            while(userSet.next()) {
                int userID = userSet.getInt("User_ID");
                String username = userSet.getString("User_Name");
                User user = new User(userID, username);
                users.add(user);
            }

            return users;
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * checks if valid user login information based on username and password
     *
     * @param username the user's username
     * @param password the user's password
     * @return positive integer if database finds a matching record
     * @throws SQLException if an SQL exception occurs
     */
    public static int checkUser(String username, String password) throws SQLException {
        try {
            String query = "SELECT User_ID FROM client_schedule.users WHERE User_Name=\"" + username + "\" AND Password=\"" + password + "\"";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("User_ID");
            } else {
                return -1;
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return -2;
        }
    }

//    public static String getUserIDFromUserName(String userName) {
//        try {
//            String query = String.format("SELECT User_ID FROM client_schedule.users WHERE User_Name='%s'", userName);
//            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if(resultSet.next()) {
//                int userId = resultSet.getInt("User_ID");
//                return String.valueOf(userId);
//            } else {
//                return null;
//            }
//        } catch (SQLException err) {
//            err.printStackTrace();
//            return null;
//        }
//    }

    /**
     * gets username based on given user ID
     *
     * @param userId the user's ID
     * @return username
     */
    public static String getUserNameFromUserID(int userId) {
        try {
            String query = String.format("SELECT User_Name FROM client_schedule.users WHERE User_ID=%s", userId);
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("User_Name");
            } else {
                return null;
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }
}
