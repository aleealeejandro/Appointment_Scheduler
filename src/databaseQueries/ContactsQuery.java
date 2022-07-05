package databaseQueries;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Alexander Padilla
 */
public class ContactsQuery {

    /**
     * gets all contacts from the database
     *
     * @return list of contacts
     */
    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String query = "SELECT Contact_ID, Contact_Name, Email FROM client_schedule.contacts ORDER BY Contact_ID;";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet contactsSet = preparedStatement.executeQuery();

            while(contactsSet.next()) {
                String contactID = contactsSet.getString("Contact_ID");
                String contactName = contactsSet.getString("Contact_Name");
                String email = contactsSet.getString("Email");
                Contact contact = new Contact(contactID, contactName, email);
                contacts.add(contact);
            }

            return contacts;
        } catch (SQLException err) {
            System.out.println("Error: " + err.getMessage());
            return null;
        }
    }
}