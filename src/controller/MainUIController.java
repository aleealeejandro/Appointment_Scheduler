package controller;

import databaseQueries.AppointmentsQuery;
import databaseQueries.CountriesQuery;
import databaseQueries.CustomersQuery;
import databaseQueries.DivisionsQuery;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Country;
import model.Customer;
import model.Division;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class MainUIController implements Initializable {
    @FXML public TextField searchCustomersTextField;
    @FXML public TabPane tabPane;
    @FXML public Button logoutButton;
    @FXML public ChoiceBox<String> searchCustomersByChoiceBox;
    @FXML public ChoiceBox<String> searchAppointmentsByChoiceBox;
    @FXML public ChoiceBox<String> filterCustomersByCountryChoiceBox;
    @FXML public ChoiceBox<String> filterCustomersByDivisionChoiceBox;
    @FXML public Label divisionLabel;
    @FXML public DatePicker appointmentDatePickerField;
    @FXML private AnchorPane mainPanel;
//    @FXML private Button exitButton;
    @FXML private Button addAppointmentButton;
    @FXML private Button updateAppointmentButton;
    @FXML private Button deleteAppointmentButton;
//    @FXML private Button appointmentReportsButton;
    @FXML private Button addCustomerButton;
    @FXML private Button updateCustomerButton;
    @FXML private Button deleteCustomerButton;
//    @FXML private Button customerReportsButton;
    @FXML private ChoiceBox<String> filterAppointmentsByChoiceBox;
    @FXML public TableView<Appointment> appointmentsTable;
    @FXML public TableView<Customer> customersTable;
    @FXML public Button refreshCustomerTableButton;
    @FXML public static Button refreshAppointmentTableButton;
    private String appointmentFilterChoiceBoxChoice;
    private String searchAppointmentsByChoiceBoxChoice;
//    private String customerSearchFilterChoiceBoxChoice;
    public static int loggedInUserID;
    private static String loggedInUsername;

    private String countryFilterChoice;
    private String divisionFilterChoice;
    private LocalDate dateChosen;

    @FXML public TextField searchAppointmentsTextField;
    public static LocalDateTime timeRightNow;

    public void initialize(URL url, ResourceBundle rb) {
        tabPane.setTabMinWidth(75);
        tabPane.setTabMinHeight(25);
        dateChosen = LocalDate.now().plusDays(1);
        datePicked();
        disableDatesOnDatePicker();


        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(MainUIController::runTask, 0, 10, TimeUnit.SECONDS);

        loadAppointmentFilterChoices();
        loadAppointmentSearchFilterChoiceBoxChoices();

        try {
            loadCountryChoicesInChoiceBox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loadCustomerChoiceBoxChoices();
        loadFilteredCustomersTable();
        disableEnableUpdateAndDeleteAppointmentButtons();
        disableEnableUpdateAndDeleteCustomerButtons();
        disableUpdateAndDeleteCustomerButtons();
        appointmentWithinFifteenMinutes(AppointmentsQuery.checkIfAppointmentWithinFifteenMinutes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(15)));
    }
    public static void getUserData(int id, String username) {
        loggedInUserID = id;
        loggedInUsername = username;
    }

    public static void runTask() {
//        MainUIController ui = new MainUIController();
//        ui.loadFilteredAppointmentsTable();
        timeRightNow = LocalDateTime.now();
        System.out.println(timeRightNow.format(TimeController.timestampFormatter) + "   Running the task every 10 seconds.");
//        refreshAppointmentTableButton.fire();

    }
    public static void appointmentWithinFifteenMinutes(int appointmentInFifteen) {
        if(appointmentInFifteen > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notice");
            alert.setHeaderText("Upcoming Appointment");
            alert.setContentText("Howdy, there is an appointment within fifteen minutes.");
            alert.showAndWait();
        }
    }

//    public static void appointmentEndedAlready(Appointment appointment) {
//
////        if(endedAlready) {
////            System.out.println();
////        }
//    }

    private void loadAppointmentFilterChoices() {
        filterAppointmentsByChoiceBox.getItems().addAll("All", "Month", "Week", "Today", "My Appointments");
        filterAppointmentsByChoiceBox.getSelectionModel().selectFirst();
        appointmentFilterChoiceBoxChoice = filterAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();

//        appointmentFilterChoiceBoxChoice = "All";
//        filterAppointmentsByChoiceBox.setValue(appointmentFilterChoiceBoxChoice);
        loadFilteredAppointmentsTable();
    }

    private void loadAppointmentSearchFilterChoiceBoxChoices() {
        searchAppointmentsByChoiceBox.getItems().addAll("Contact", "Type");
        searchAppointmentsByChoiceBox.getSelectionModel().selectFirst();
        searchAppointmentsByChoiceBoxChoice = searchAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();
//        searchAppointmentsByChoiceBoxChoice = "Contact";
//        searchAppointmentsByChoiceBox.setValue(appointmentFilterChoiceBoxChoice);
        loadFilteredAppointmentsTable();
    }

    private void loadCustomerChoiceBoxChoices() {
        searchCustomersByChoiceBox.getItems().addAll("Customer ID", "Name", "Division ID");
        searchCustomersByChoiceBox.getSelectionModel().selectFirst();
//        customerSearchFilterChoiceBoxChoice = searchCustomersByChoiceBox.getSelectionModel().getSelectedItem();

        loadFilteredCustomersTable();
    }

    @FXML
    void appointmentTableViewRowChosen() {
        disableEnableUpdateAndDeleteAppointmentButtons();
    }

    @FXML
    void customerTableViewRowChosen() {
        disableEnableUpdateAndDeleteCustomerButtons();
    }

    private void disableEnableUpdateAndDeleteAppointmentButtons() {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if(appointment == null) {
            disableUpdateAndDeleteAppointmentButtons();
        }
        else {
            enableUpdateAndDeleteAppointmentButtons();

//            LocalDateTime appointmentStartDateTime = LocalDateTime.of(appointment.getStartDate(), LocalTime.parse(appointment.getStartTime(), TimeController.timeFormatter));
//            LocalDateTime appointmentEndDateTime = LocalDateTime.of(appointment.getEndDate(), LocalTime.parse(appointment.getEndTime(), TimeController.timeFormatter));
//            if(appointmentStartDateTime.isBefore(timeRightNow) || appointmentEndDateTime.isAfter(timeRightNow)) {
//                System.out.println(appointmentStartDateTime + " is before " + timeRightNow);
//                disableUpdateAndDeleteAppointmentButtons();
//            }
//            else {
//                enableUpdateAndDeleteAppointmentButtons();
//            }
        }
//        assert appointment != null;

    }

    private void disableUpdateAndDeleteAppointmentButtons() {
        updateAppointmentButton.setDisable(true);
        deleteAppointmentButton.setDisable(true);
    }

    private void enableUpdateAndDeleteAppointmentButtons() {
        updateAppointmentButton.setDisable(false);
        deleteAppointmentButton.setDisable(false);
    }

    private void disableEnableUpdateAndDeleteCustomerButtons() {
        Customer customer = customersTable.getSelectionModel().getSelectedItem();

        if(customer == null) {
            disableUpdateAndDeleteCustomerButtons();
        } else {
            enableUpdateAndDeleteCustomerButtons();
        }
    }

    private void disableUpdateAndDeleteCustomerButtons() {
        updateCustomerButton.setDisable(true);
        deleteCustomerButton.setDisable(true);
    }

    private void enableUpdateAndDeleteCustomerButtons() {
        updateCustomerButton.setDisable(false);
        deleteCustomerButton.setDisable(false);
    }

    @FXML
    void handleAppointmentButtonClicked(ActionEvent event) throws SQLException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        Object source = event.getSource();
        String path = "";
        String typeOfForm;

        if (addAppointmentButton.equals(source)) {
            typeOfForm = "addAppointmentForm";
            path = "../view/AppointmentForm.fxml";
            AppointmentFormController.getParentNodeData(typeOfForm, loggedInUserID);
        } else if (updateAppointmentButton.equals(source)) {
            typeOfForm = "updateAppointmentForm";
            path = "../view/AppointmentForm.fxml";
            AppointmentFormController.getParentNodeData(typeOfForm, loggedInUserID);
            AppointmentFormController.getAppointmentChosen(appointmentsTable.getSelectionModel().getSelectedItem());
        } else if (deleteAppointmentButton.equals(source)) {
            path = "delete appointment";
        }

        if(!path.equals("delete appointment")) {
            fxmlLoader.setLocation(getClass().getResource(path));

            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            dialog.showAndWait();

            if(!dialog.isShowing()) {
                loadFilteredAppointmentsTable();
                searchAppointmentsTextField.clear();
            }

        } else {
            String id = appointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String starttime = appointmentsTable.getSelectionModel().getSelectedItem().getStartTime();
            String endtime = appointmentsTable.getSelectionModel().getSelectedItem().getEndTime();
            String startdate = appointmentsTable.getSelectionModel().getSelectedItem().getStartDate().format(TimeController.dateFormatter);

            Alert deletionAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deletionAlert.setTitle("Notice");
            deletionAlert.setHeaderText("Are you sure you want to delete the following?");
            deletionAlert.setContentText(String.format("Appointment_ID: %s\nTime: %s - %s\nDate: %s", id, starttime, endtime, startdate));

            Optional<ButtonType> result = deletionAlert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK){
//                if(result.get() == ButtonType.OK){
                boolean deletionConfirmation = AppointmentsQuery.deleteAppointment(id);

                if(deletionConfirmation) {
                    loadFilteredAppointmentsTable();
                    searchAppointmentsTextField.clear();
                    disableUpdateAndDeleteAppointmentButtons();
                }
            }

        }
    }

    @FXML
    void handleCustomerButtonClicked(ActionEvent event) throws SQLException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        Object source = event.getSource();
        String path = "";
        String typeOfForm;

        if (addCustomerButton.equals(source)) {
            typeOfForm = "addCustomerForm";
            path = "../view/CustomerForm.fxml";
            CustomerFormController.getParentNodeData(typeOfForm, loggedInUserID);
        } else if (updateCustomerButton.equals(source)) {
            typeOfForm = "updateCustomerForm";
            path = "../view/CustomerForm.fxml";
            CustomerFormController.getParentNodeData(typeOfForm, loggedInUserID);
            CustomerFormController.getCustomerChosen(customersTable.getSelectionModel().getSelectedItem());
        } else if (deleteCustomerButton.equals(source)) {
            path = "delete customer";
        }

        if(!path.equals("delete customer")) {
            fxmlLoader.setLocation(getClass().getResource(path));

            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            dialog.showAndWait();

            if(!dialog.isShowing()) {
                loadFilteredCustomersTable();
                searchCustomersTextField.clear();
            }


        } else {
            String id = customersTable.getSelectionModel().getSelectedItem().getCustomerID();
            String name = customersTable.getSelectionModel().getSelectedItem().getCustomerName();

            Alert deletionAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deletionAlert.setTitle("Notice");
            deletionAlert.setHeaderText("Are you sure you want to delete the following?");
            deletionAlert.setContentText(String.format("Warning: Deleting a customer will delete their appointments as well.\n\nCustomer ID: %s\nCustomer Name: %s", id, name));

            Optional<ButtonType> result = deletionAlert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                boolean deletionConfirmation = CustomersQuery.deleteCustomer(id);

                if(deletionConfirmation) {
                    loadFilteredCustomersTable();
                    loadFilteredAppointmentsTable();
                    searchCustomersTextField.clear();
                    disableUpdateAndDeleteCustomerButtons();
                }
            }


        }
    }

    @FXML
    public void handleAppointmentFilterChoice() {
        appointmentFilterChoiceBoxChoice = filterAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();
//        loadFilteredAppointmentsTable();

        if(appointmentFilterChoiceBoxChoice != null && (appointmentFilterChoiceBoxChoice.equals("All") || appointmentFilterChoiceBoxChoice.equals("Today") || appointmentFilterChoiceBoxChoice.equals("My Appointments"))) {
            appointmentDatePickerField.setValue(null);
            dateChosen = appointmentDatePickerField.getValue();
            appointmentDatePickerField.setDisable(true);
            appointmentDatePickerField.setVisible(false);
        } else {
            appointmentDatePickerField.setValue(TimeController.nextDateAvailable());
            dateChosen = appointmentDatePickerField.getValue();
            appointmentDatePickerField.setDisable(false);
            appointmentDatePickerField.setVisible(true);
        }

        loadFilteredAppointmentsTable();
    }

    private Predicate<Appointment> createAppointmentPredicate(String searchText) {
        return appointment -> {
            if(searchText == null || searchText.isEmpty()) {
                return true;
            }
            return searchFindAppointments(appointment, searchText);
        };
    }

    private boolean searchFindAppointments(Appointment appointment, String searchText){
        boolean isInList = false;

        if(searchAppointmentsByChoiceBoxChoice.equals("Contact")) {
//            isInList = appointment.getContactID().contains(searchText);
            isInList = appointment.getContactName().toLowerCase().contains(searchText.toLowerCase());
        }
        else if(searchAppointmentsByChoiceBoxChoice.equals("Type")) {
            isInList = appointment.getType().toLowerCase().contains(searchText.toLowerCase());
        }

        return isInList;
    }

    public void loadFilteredAppointmentsTable() {
//        NEW ---------------------------------------------------------------------------------------------------------------------------------------------------
//        ObservableList<Appointment> appointmentsList = getAppointmentList();
        ObservableList<Appointment> appointmentsList = getAppointmentList();
        assert appointmentsList != null;
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(appointmentsList);

        searchAppointmentsTextField.textProperty().addListener((observable, oldValue, newValue) ->
            filteredAppointments.setPredicate(createAppointmentPredicate(newValue))
        );

        appointmentsTable.setItems(filteredAppointments);
//        NEW ---------------------------------------------------------------------------------------------------------------------------------------------------
        disableEnableUpdateAndDeleteAppointmentButtons();
    }

    private ObservableList<Appointment> getAppointmentList() {
        LocalDateTime dateTimeChosen;

        if(dateChosen != null) {
            dateTimeChosen = LocalDateTime.of(dateChosen, TimeController.timeNow);
        } else {
            dateTimeChosen = LocalDateTime.now();
        }

        try {
//            return AppointmentsQuery.getAllAppointments(appointmentFilterChoiceBoxChoice, LocalDateTime.now());
            return AppointmentsQuery.getAllAppointments(appointmentFilterChoiceBoxChoice, LocalDateTime.now(), dateTimeChosen);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Predicate<Customer> createCustomerPredicate(String searchText) {
        return customer -> {
            if(searchText == null || searchText.isEmpty()) {
                return true;
            }
            return searchFindCustomers(customer, searchText);
        };
    }

    private boolean searchFindCustomers(Customer customer, String searchText){
        boolean isInList = false;

        if(searchCustomersByChoiceBox.getSelectionModel().getSelectedItem().equals("Customer ID")) {
//            isInList = customer.getCustomerID().contains(searchText);
            isInList = customer.getCustomerID().equals(searchText);
        }
        else if(searchCustomersByChoiceBox.getSelectionModel().getSelectedItem().equals("Name")) {
            isInList = customer.getCustomerName().toLowerCase().contains(searchText.toLowerCase());
        }
        else if(searchCustomersByChoiceBox.getSelectionModel().getSelectedItem().equals("Division ID")) {
//            isInList = customer.getDivisionID().contains(searchText);
            isInList = customer.getDivisionID().equals(searchText);
        }

        return isInList;
    }

    public void loadFilteredCustomersTable() {
//        NEW ---------------------------------------------------------------------------------------------------------------------------------------------------
        ObservableList<Customer> customersList = getCustomersList();
        assert customersList != null;
        FilteredList<Customer> filteredCustomers = new FilteredList<>(customersList);

        searchCustomersTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredCustomers.setPredicate(createCustomerPredicate(newValue))
        );

        customersTable.setItems(filteredCustomers);
        loadFilteredAppointmentsTable();
//        NEW ---------------------------------------------------------------------------------------------------------------------------------------------------
        disableEnableUpdateAndDeleteCustomerButtons();
    }

    private ObservableList<Customer> getCustomersList() {
        try {
            return CustomersQuery.getAllCustomersByDivision(countryFilterChoice, divisionFilterChoice);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void logoutButtonClicked() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        // do what has to be done before closing stage
        Logger.logLogout(loggedInUsername);

        stage.close();
    }

    @FXML
    public void handleCustomerSearchFilterChoice() {
//        customerSearchFilterChoiceBoxChoice = searchCustomersByChoiceBox.getSelectionModel().getSelectedItem();
        searchCustomersTextField.clear();
        loadFilteredCustomersTable();
    }

    @FXML
    public void handleAppointmentSearchFilterChoice() {
        searchAppointmentsByChoiceBoxChoice = searchAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();
        searchAppointmentsTextField.clear();
        loadFilteredAppointmentsTable();
    }

    @FXML
    public void handleCustomerDivisionChoiceBoxChoice() {
        divisionFilterChoice = filterCustomersByDivisionChoiceBox.getSelectionModel().getSelectedItem();
        loadFilteredCustomersTable();
    }

    private void loadCountryChoicesInChoiceBox() throws SQLException {
        ObservableList<Country> countries = CountriesQuery.getAllCountries();
        assert countries != null;

        filterCustomersByCountryChoiceBox.getItems().add("All");

        for(Country c : countries) {
            filterCustomersByCountryChoiceBox.getItems().add(c.getCountry());
        }

        filterCustomersByCountryChoiceBox.getSelectionModel().selectFirst();

        loadDivisionChoicesInChoiceBox();
    }

    private void loadDivisionChoicesInChoiceBox() throws SQLException {
        filterCustomersByDivisionChoiceBox.getItems().clear();
        ObservableList<Division> divisions = DivisionsQuery.getAllDivisions(countryFilterChoice);
        assert divisions != null;

        filterCustomersByDivisionChoiceBox.getItems().add("All");

        for(Division d : divisions) {
            filterCustomersByDivisionChoiceBox.getItems().add(d.getDivision());
        }

        filterCustomersByDivisionChoiceBox.getSelectionModel().selectFirst();
        countryFilterChoice = filterCustomersByCountryChoiceBox.getSelectionModel().getSelectedItem();//-------------------------------------------------------=====================
        divisionFilterChoice = filterCustomersByDivisionChoiceBox.getSelectionModel().getSelectedItem();
        loadFilteredCustomersTable();

    }

    @FXML
    public void handleCustomerCountryChoiceBoxChoice() throws SQLException {
        countryFilterChoice = filterCustomersByCountryChoiceBox.getSelectionModel().getSelectedItem();

        if(countryFilterChoice.equals("All")) {
//            filterCustomersByDivisionChoiceBox.setDisable(true);
            filterCustomersByDivisionChoiceBox.setVisible(false);
            divisionLabel.setVisible(false);
        } else {
//            filterCustomersByDivisionChoiceBox.setDisable(false);
            filterCustomersByDivisionChoiceBox.setVisible(true);
            divisionLabel.setVisible(true);
        }

        loadDivisionChoicesInChoiceBox();
    }

    public void disableDatesOnDatePicker() {
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        int offsetSeconds = TimeController.estOffSet.compareTo(TimeController.systemOffSet);
                        LocalDateTime lastAppointmentSlotTimeToday = LocalDateTime.of(LocalDate.now(), LocalTime.of(21,45)).plusSeconds(offsetSeconds);
                        LocalDateTime timeNow = LocalDateTime.now();
                        LocalDate tomorrow = LocalDate.now().plusDays(1);
//                        LocalDate threeMonthsFromToday = LocalDate.now().plusWeeks(12);
                        LocalDate ninetyDaysFromToday = LocalDate.now().plusDays(90);
                        if(empty || date.isBefore(today) || date.isAfter(ninetyDaysFromToday) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
                            setDisable(true);
                        }

//                        automatically disables day if after last appointment slot time at 8:45 PM EST
                        if(timeNow.isAfter(lastAppointmentSlotTimeToday) && date.isBefore(tomorrow)) {
                            setDisable(true);
                        }
                    }
                };
            }
        };

        appointmentDatePickerField.setDayCellFactory(dayCellFactory);
    }

    public void datePicked() {
        appointmentDatePickerField.valueProperty().addListener((ov, oldValue, newValue) -> {
            appointmentDatePickerField.setValue(newValue);
            dateChosen = appointmentDatePickerField.getValue();
//            appointmentDatePickerField.
            System.out.println(dateChosen);
            loadFilteredAppointmentsTable();
        });
    }

}
