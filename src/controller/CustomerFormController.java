package controller;

import databaseQueries.CountriesQuery;
import databaseQueries.CustomersQuery;
import databaseQueries.DivisionsQuery;
import databaseQueries.UserQuery;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

//    Thread myThread = new Thread() {
//        @Override
//        public void run() {
//            //my code that runs with a loop
//            //while loop here that runs and needs to execute method every 30 seconds, if condition met continue else break;
//        };
//     myThread.start();
//    }

    public static void getParentNodeData(String form, int loggedInUserIDNum) {
        typeOfForm = form;
        loggedInUserName = UserQuery.getUserNameFromUserID(loggedInUserIDNum);
    }

    public static void getCustomerChosen(Customer c) {
        customer = c;
    }

    public void loadAddCustomerForm() {
        customerFormTitle.setText("Add Customer");
        customerIDTextField.setText(CustomersQuery.getNextCustomerID());
        countriesComboBox.getSelectionModel().selectFirst();
        countryChoice = countriesComboBox.getSelectionModel().getSelectedItem();
        saveButton.setText("Add");
    }

    public void loadUpdateCustomerForm() throws SQLException {
        int divisionId = Integer.parseInt(customer.getDivisionID());
        Country country = new Country("1","U.S");

        try {
            country = DivisionsQuery.getCountryFromDivisionID(divisionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customerFormTitle.setText("Update Customer");
        customerIDTextField.setText(customer.getCustomerID());
        customerNameTextField.setText(customer.getCustomerName());
        addressTextField.setText(customer.getAddress());
        postalCodeTextField.setText(customer.getPostalCode());
        phoneNumberTextField.setText(customer.getPhone());
        assert country != null;
        countriesComboBox.getSelectionModel().select(country.getCountry());
        countryChoice = countriesComboBox.getSelectionModel().getSelectedItem();
        saveButton.setText("Update");
    }

    private void loadCountryChoicesInChoiceBox() throws SQLException {
        ObservableList<Country> countries = CountriesQuery.getAllCountries();
        assert countries != null;

        for(Country c : countries) {
            countriesComboBox.getItems().add(c.getCountry());
        }
//        countriesComboBox.getSelectionModel().selectFirst();
        loadDivisionChoicesInChoiceBox();
    }

    @FXML
    void handleCountryChoice() throws SQLException {
//        countriesComboBox.getSelectionModel().getSelectedItem();
        countryChoice = countriesComboBox.getSelectionModel().getSelectedItem();
        loadDivisionChoicesInChoiceBox();
        divisionComboBox.getSelectionModel().selectFirst();
        divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
    }

    private void loadDivisionChoicesInChoiceBox() throws SQLException {
        divisionComboBox.getItems().clear();
        ObservableList<Division> divisions = DivisionsQuery.getAllDivisions(countryChoice);
        assert divisions != null;

        for(Division d : divisions) {
            divisionComboBox.getItems().add(d.getDivision());
        }

        if(typeOfForm.equals("addCustomerForm")) {
            divisionComboBox.getSelectionModel().selectFirst();
            divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
        } else if(typeOfForm.equals("updateCustomerForm")) {
            int divisionId = Integer.parseInt(customer.getDivisionID());
            Division division = new Division(1,"Alabama");

            try {
                division = DivisionsQuery.getDivisionFromDivisionID(divisionId);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            assert division != null;
            divisionComboBox.getSelectionModel().select(division.getDivision());
            divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
        }
    }

    @FXML
    public void handleDivisionChoice() {
        divisionChoice = divisionComboBox.getSelectionModel().getSelectedItem();
    }

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

    @FXML
    void handleSaveButton() throws SQLException {
        checkIfFieldsAreEmpty();

        if(!fieldsEmpty) {
            Division division = DivisionsQuery.getDivisionFromDivisionName(divisionChoice);
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

    @FXML
    void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}



