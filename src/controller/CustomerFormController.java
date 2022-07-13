package controller;

import databaseQueries.CountriesQuery;
import databaseQueries.CustomersQuery;
import databaseQueries.DivisionsQuery;
import databaseQueries.UserQuery;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Handles customer form logic
 * @author Alexander Padilla
 */
public class CustomerFormController implements Initializable {
    @FXML public AnchorPane mainPanel;
    @FXML public Button saveButton;
    @FXML public Label customerFormTitle;
    @FXML public TextField customerIDTextField;
    @FXML public TextField customerNameTextField;
    @FXML public TextField addressTextField;
    @FXML public TextField postalCodeTextField;
    @FXML public TextField phoneNumberTextField;
    @FXML public ComboBox<String> countriesComboBox;
    @FXML public ComboBox<String> divisionComboBox;
    @FXML public Button cancelButton;
    private static String typeOfForm;
    private static Customer customer;
    private static String countryChoice;
    private static String divisionChoice;
    private boolean fieldsEmpty = true;
    private static String loggedInUserName;

    /**
     * uses initialize to initialize this scene
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTextFieldEventHandlers();

        try {
            loadCountryChoicesInChoiceBox();

            if(typeOfForm.equals("addCustomerForm")) {
                loadAddCustomerForm();
            } else if(typeOfForm.equals("updateCustomerForm")) {
                loadUpdateCustomerForm();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets the event handlers for the form text fields
     */
    public void setTextFieldEventHandlers() {
        int maxCharacters = 50;
        customerNameTextField.addEventFilter(KeyEvent.KEY_TYPED, TextFieldHandler.setMaxLengthAndPattern(maxCharacters, "regular"));
        addressTextField.addEventFilter(KeyEvent.KEY_TYPED, TextFieldHandler.setMaxLengthAndPattern(100, "regular"));
        postalCodeTextField.addEventFilter(KeyEvent.KEY_TYPED, TextFieldHandler.setMaxLengthAndPattern(maxCharacters, "regular"));
        phoneNumberTextField.addEventFilter(KeyEvent.KEY_TYPED, TextFieldHandler.setMaxLengthAndPattern(maxCharacters, "regular"));
    }

    /**
     * gets parent node data
     * @param form the type of form
     * @param loggedInUserIDNum logged in user's username
     */
    public static void getParentNodeData(String form, int loggedInUserIDNum) {
        typeOfForm = form;
        loggedInUserName = UserQuery.getUserNameFromUserID(loggedInUserIDNum);
    }

    /**
     * gets customer selected data from parent stage table view
     * @param c the customer object
     */
    public static void getCustomerChosen(Customer c) {
        customer = c;
    }

    /**
     * loads add customer form
     */
    public void loadAddCustomerForm() {
        customerFormTitle.setText("Add Customer");
        customerIDTextField.setText(CustomersQuery.getNextCustomerID());
        countriesComboBox.getSelectionModel().selectFirst();
        divisionComboBox.getSelectionModel().selectFirst();
        divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
        countryChoice = countriesComboBox.getSelectionModel().getSelectedItem();
        saveButton.setText("Add");
    }

    /**
     * loads update customer form
     * @throws SQLException if an SQL exception occurs
     */
    public void loadUpdateCustomerForm() throws SQLException {
        int divisionId = Integer.parseInt(customer.getDivisionID());

        try {
            Country country = DivisionsQuery.getCountryFromDivisionID(divisionId);
            Division division = DivisionsQuery.getDivisionFromDivisionID(divisionId);
            System.out.printf("Name: %s\n  ID: %s%n", customer.getCustomerName(), customer.getDivisionID());

            customerFormTitle.setText("Update Customer");
            customerIDTextField.setText(customer.getCustomerID());
            customerNameTextField.setText(customer.getCustomerName());
            addressTextField.setText(customer.getAddress());
            postalCodeTextField.setText(customer.getPostalCode());
            phoneNumberTextField.setText(customer.getPhone());
            assert country != null;
            countriesComboBox.getSelectionModel().select(country.getCountry());
            countryChoice = countriesComboBox.getSelectionModel().getSelectedItem();
            handleCountryChoice();

            assert division != null;
            divisionComboBox.getSelectionModel().select(division.getDivision());
            divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();

            saveButton.setText("Update");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * loads countries into a combo-box
     * @throws SQLException if an SQL exception occurs
     */
    private void loadCountryChoicesInChoiceBox() throws SQLException {
        ObservableList<Country> countries = CountriesQuery.getAllCountries();
        assert countries != null;

        for(Country c : countries) {
            countriesComboBox.getItems().add(c.getCountry());
        }

        loadDivisionChoicesInChoiceBox();
    }

    /**
     * handles combo-box selection
     */
    @FXML
    void handleCountryChoice() {
        countryChoice = countriesComboBox.getSelectionModel().getSelectedItem();
        loadDivisionChoicesInChoiceBox();
        divisionComboBox.getSelectionModel().selectFirst();
        divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * loads divisions into a combo-box
     */
    private void loadDivisionChoicesInChoiceBox() {
        divisionComboBox.getItems().clear();
        ObservableList<Division> divisions = DivisionsQuery.getAllDivisions(countriesComboBox.getSelectionModel().getSelectedItem());
        assert divisions != null;

        for(Division d : divisions) {
            divisionComboBox.getItems().add(d.getDivision());
        }

    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleDivisionChoice() {
        divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * checks if text-fields are empty
     */
    private void checkIfFieldsAreEmpty() {
        if(customerNameTextField.getText().isEmpty() ||
                addressTextField.getText().isEmpty() ||
                postalCodeTextField.getText().isEmpty() ||
                phoneNumberTextField.getText().isEmpty()
        ) {
            fieldsEmpty = true;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notice");
            alert.setHeaderText("Empty Field/s");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
        } else {
            fieldsEmpty = false;
        }
    }

    /**
     * handles button click
     */
    @FXML
    void handleSaveButton() {
        checkIfFieldsAreEmpty();

        if(!fieldsEmpty) {
            Division division = DivisionsQuery.getDivisionFromDivisionName(divisionComboBox.getSelectionModel().getSelectedItem());
            assert division != null;

            Customer customer = new Customer(
                customerIDTextField.getText(),
                customerNameTextField.getText(),
                addressTextField.getText(),
                postalCodeTextField.getText(),
                phoneNumberTextField.getText(),
                String.valueOf(division.getDivisionID())
            );

            boolean addOrUpdateCustomerConfirmed = true;

            if(typeOfForm.equals("addCustomerForm")) {
                addOrUpdateCustomerConfirmed = CustomersQuery.addCustomer(customer, loggedInUserName);
            }
            else if(typeOfForm.equals("updateCustomerForm")) {
                addOrUpdateCustomerConfirmed = CustomersQuery.updateCustomer(customer, loggedInUserName);
            }

            if(addOrUpdateCustomerConfirmed) {
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            }
        }

    }

    /**
     * handles button click
     */
    @FXML
    void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}



