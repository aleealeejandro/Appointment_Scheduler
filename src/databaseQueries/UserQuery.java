package databaseQueries;

//import databaseUtils.DatabaseConnection;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserQuery {
    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            String allUsersQuery = "SELECT User_ID, User_Name FROM client_schedule.users ORDER BY User_ID;";
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

    public static int checkUser(String username, String password) throws SQLException {
        try {
            String query = "SELECT User_ID, User_Name, Password FROM client_schedule.users WHERE User_Name=\"" + username + "\" AND Password=\"" + password + "\"";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                int userID = resultSet.getInt("User_ID");
                return userID;
            } else {
                return -1;
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return -2;
        }
    }

    public static String getUserNameFromUserID(int userId) throws SQLException {
        try {
            String query = String.format("SELECT User_Name FROM client_schedule.users WHERE User_ID=%s", userId);
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                String userName = resultSet.getString("User_Name");
                return userName;
            } else {
                return null;
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }
}
