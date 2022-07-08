package controller;

import databaseQueries.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 *
 * @author Alexander Padilla
 */
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
    @FXML public Label tableFilterLabel;
    @FXML public PieChart appointmentTypesPieChart;
    @FXML public ListView<String> fullyBookedDatesListView;
    @FXML public ComboBox<String> reportsByMonthComboBox;
    @FXML public TableView<Appointment> contactScheduleTable;
    @FXML public ChoiceBox<String> filterAppointmentsByContactsChoiceBox;
    @FXML public Label reportsTableFilterLabel;
    @FXML public Label totalAppointmentsLabel;
    @FXML public Tab appointmentsTab;
    @FXML public Tab customersTab;
    @FXML public Tab reportsTab;
    @FXML public TableColumn<Appointment, String> appointmentIDColumn;
    @FXML public TableColumn<Appointment, String> appointmentIDColumnReports;
    @FXML private AnchorPane mainPanel;
    @FXML private Button addAppointmentButton;
    @FXML private Button updateAppointmentButton;
    @FXML private Button deleteAppointmentButton;
    @FXML private Button addCustomerButton;
    @FXML private Button updateCustomerButton;
    @FXML private Button deleteCustomerButton;
    @FXML private ChoiceBox<String> filterAppointmentsByChoiceBox;
    @FXML public TableView<Appointment> appointmentsTable;
    @FXML public TableView<Customer> customersTable;
    private String appointmentFilterChoiceBoxChoice;
    private String searchAppointmentsByChoiceBoxChoice;
    public static int loggedInUserID;
    private static String loggedInUsername;
    private String countryFilterChoice;
    private String divisionFilterChoice;
    private LocalDate dateChosen;
    @FXML public TextField searchAppointmentsTextField;
    public static LocalDateTime timeRightNow;
    private static final HashSet<String> fullyScheduledDates = new HashSet<>();

    /**
     * uses initialize to initialize this scene
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL url, ResourceBundle rb) {
        fullyScheduledDates.clear();
        tabListener();

        tabPane.setTabMinWidth(75);
        tabPane.setTabMinHeight(25);
        dateChosen = LocalDate.now().plusDays(1);
        datePickerListener();
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
        appointmentWithinFifteenMinutes();
        loadMonthsInComboBox();
        loadContactsScheduleChoiceBox();
    }

    /**
     * loads tab content on click
     */
    public void tabListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            switch(newTab.getText()) {
                case "Appointments":
                    loadFilteredAppointmentsTable();
                    break;
                case "Customers":
                    loadFilteredCustomersTable();
                    break;
                case "Reports":
                    loadFullyBookedDatesReport();
                    loadContactScheduleTableReport();
                    numberOfAppointmentsReport();
                    break;
            }
        });
    }

    /**
     * gets user data from the parent stage
     *
     * @param id the logged-in user's ID
     * @param username the logged-in user's username
     */
    public static void getUserData(int id, String username) {
        loggedInUserID = id;
        loggedInUsername = username;
    }

    /**
     * runs task on every 10 seconds
     */
    private static void runTask() {
        timeRightNow = LocalDateTime.now();
    }

    /**
     * shows alert box if appointment is within fifteen minutes
     */
    public static void appointmentWithinFifteenMinutes() {
        Appointment appointment = AppointmentsQuery.checkIfAppointmentWithinFifteenMinutes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(15));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        if(appointment != null) {
            alert.setHeaderText("Upcoming Appointment");
            alert.setContentText(String.format("Howdy, there is an appointment within fifteen minutes.\nAppointment ID: %s\n%s-%s %s\nUser ID: %s", appointment.getAppointmentID(), appointment.getStartTime(), appointment.getEndTime(), appointment.getStartDate().format(TimeController.dateFormatter), appointment.getUserID()));
        } else {
            alert.setHeaderText("No Upcoming Appointments");
            alert.setContentText("Howdy, there are no appointments within fifteen minutes.");
        }
        alert.showAndWait();
    }

    /**
     * loads combo-box choices
     */
    private void loadAppointmentFilterChoices() {
//        filterAppointmentsByChoiceBox.getItems().addAll("All", "Month", "Week", "Day", "Today", "My Appointments");
        filterAppointmentsByChoiceBox.getItems().addAll("All", "Month", "Week", "Day", "Today");
        filterAppointmentsByChoiceBox.getSelectionModel().selectFirst();
        appointmentFilterChoiceBoxChoice = filterAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();
    }

    /**
     * loads combo-box choices
     */
    private void loadAppointmentSearchFilterChoiceBoxChoices() {
        searchAppointmentsByChoiceBox.getItems().addAll("Contact", "Type");
        searchAppointmentsByChoiceBox.getSelectionModel().selectFirst();
        searchAppointmentsByChoiceBoxChoice = searchAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();
    }

    /**
     * loads combo-box choices
     */
    private void loadCustomerChoiceBoxChoices() {
        searchCustomersByChoiceBox.getItems().addAll("Customer ID", "Name", "Division ID");
        searchCustomersByChoiceBox.getSelectionModel().selectFirst();

        loadFilteredCustomersTable();
    }

    /**
     * handles table view row selection
     */
    @FXML
    void appointmentTableViewRowChosen() {
        disableEnableUpdateAndDeleteAppointmentButtons();
    }

    /**
     * handles table view row selection
     */
    @FXML
    void customerTableViewRowChosen() {
        disableEnableUpdateAndDeleteCustomerButtons();
    }

    /**
     * disables or enables update and delete buttons
     */
    private void disableEnableUpdateAndDeleteAppointmentButtons() {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();


        if(appointment == null) {
            disableUpdateAndDeleteAppointmentButtons();
        } else {
            LocalDateTime appointmentStartDateTime = LocalDateTime.of(appointment.getStartDate(), LocalTime.parse(appointment.getStartTime(), TimeController.timeFormatter));
            LocalDateTime appointmentEndDateTime = LocalDateTime.of(appointment.getEndDate(), LocalTime.parse(appointment.getEndTime(), TimeController.timeFormatter));

            if(appointmentStartDateTime.isBefore(timeRightNow) && appointmentEndDateTime.isAfter(timeRightNow)) {
                disableUpdateAndDeleteAppointmentButtons();
            } else if(appointmentEndDateTime.isBefore(timeRightNow)) {
                updateAppointmentButton.setDisable(true);
                deleteAppointmentButton.setDisable(false);
            }
            else {
                enableUpdateAndDeleteAppointmentButtons();
            }
        }

    }

    /**
     * sets the color or the rows for the appointment tables
     */
    private void customiseFactory() {
        Callback<TableColumn<Appointment, String>, TableCell<Appointment, String>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Appointment, String> call(final TableColumn<Appointment, String> param) {
                            return new TableCell<>() {
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    if(empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        setText(item);
                                        TableRow<Appointment> row = getTableRow();
                                        String style;

                                        if(row != null && row.getItem() != null) {
                                            LocalDateTime appointmentStartDateTime = LocalDateTime.of(row.getItem().getStartDate(), LocalTime.parse(row.getItem().getStartTime(), TimeController.timeFormatter));
                                            LocalDateTime appointmentEndDateTime = LocalDateTime.of(row.getItem().getEndDate(), LocalTime.parse(row.getItem().getEndTime(), TimeController.timeFormatter));
                                            String appointmentStatus;

                                            if (appointmentStartDateTime.isBefore(timeRightNow) && appointmentEndDateTime.isAfter(timeRightNow)) {
                                                appointmentStatus = "in progress";
                                            } else if (appointmentEndDateTime.isBefore(timeRightNow)) {
                                                appointmentStatus = "past";
                                            } else {
                                                appointmentStatus = "upcoming";
                                            }

                                            switch (appointmentStatus) {
                                                case "in progress":
                                                    style = "-fx-background-color:#FFECB3;";
                                                    break;
                                                case "past":
                                                    style = "-fx-background-color:#FFCCBC;";
                                                    break;
                                                case "upcoming":
                                                default:
                                                    style = "-fx-background-color:#DCEDC8;";
                                                    break;
                                            }

                                            row.setStyle(style);
                                        }
                                    }
                                }
                            };
                    }
                };

        appointmentIDColumn.setCellFactory(cellFactory);
        appointmentIDColumnReports.setCellFactory(cellFactory);
    }

    /**
     * disables update and delete buttons
     */
    private void disableUpdateAndDeleteAppointmentButtons() {
        updateAppointmentButton.setDisable(true);
        deleteAppointmentButton.setDisable(true);
    }

    /**
     * enables update and delete buttons
     */
    private void enableUpdateAndDeleteAppointmentButtons() {
        updateAppointmentButton.setDisable(false);
        deleteAppointmentButton.setDisable(false);
    }

    /**
     * disables or enables update and delete buttons
     */
    private void disableEnableUpdateAndDeleteCustomerButtons() {
        Customer customer = customersTable.getSelectionModel().getSelectedItem();

        if(customer == null) {
            disableUpdateAndDeleteCustomerButtons();
        } else {
            enableUpdateAndDeleteCustomerButtons();
        }
    }

    /**
     * disables update and delete buttons
     */
    private void disableUpdateAndDeleteCustomerButtons() {
        updateCustomerButton.setDisable(true);
        deleteCustomerButton.setDisable(true);
    }

    /**
     * enables update and delete buttons
     */
    private void enableUpdateAndDeleteCustomerButtons() {
        updateCustomerButton.setDisable(false);
        deleteCustomerButton.setDisable(false);
    }

    /**
     * handles appointment button click
     *
     * @param event button clicked action event
     */
    @FXML
    void handleAppointmentButtonClicked(ActionEvent event) {
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
            String startTime = appointmentsTable.getSelectionModel().getSelectedItem().getStartTime();
            String endTime = appointmentsTable.getSelectionModel().getSelectedItem().getEndTime();
            String startDate = appointmentsTable.getSelectionModel().getSelectedItem().getStartDate().format(TimeController.dateFormatter);

            Alert deletionAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deletionAlert.setTitle("Notice");
            deletionAlert.setHeaderText("Are you sure you want to delete the following?");
            deletionAlert.setContentText(String.format("Appointment_ID: %s\nTime: %s - %s\nDate: %s", id, startTime, endTime, startDate));

            Optional<ButtonType> result = deletionAlert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK){
                boolean deletionConfirmation = AppointmentsQuery.deleteAppointment(id);

                if(deletionConfirmation) {
                    loadFilteredAppointmentsTable();
                    searchAppointmentsTextField.clear();
//                    disableUpdateAndDeleteAppointmentButtons();
                }
            }

        }
    }

    /**
     * handles customer button click
     *
     * @param event button clicked action event
     */
    @FXML
    void handleCustomerButtonClicked(ActionEvent event) {
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
                    searchCustomersTextField.clear();
                }
            }


        }
    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleAppointmentFilterChoice() {
        appointmentFilterChoiceBoxChoice = filterAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();

        if(appointmentFilterChoiceBoxChoice != null && (appointmentFilterChoiceBoxChoice.equals("All") || appointmentFilterChoiceBoxChoice.equals("Today"))) {
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

        LocalDateTime selectedDate = LocalDateTime.now().minusDays(3);

        if(appointmentDatePickerField.getValue() != null) {
            selectedDate = LocalDateTime.of(appointmentDatePickerField.getValue(), LocalTime.now());
        }

        switch (appointmentFilterChoiceBoxChoice) {
            case "All":
                tableFilterLabel.setText("All Appointments");
                break;
            case "Month":
                tableFilterLabel.setText(String.format("Appointments between %s - %s", TimeController.getFirstOfMonthDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter), TimeController.getLastOfMonthDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter)));
                break;
            case "Week":
                tableFilterLabel.setText(String.format("Appointments between %s - %s", TimeController.getStartOfWeekDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter), TimeController.getEndOfWeekDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter)));
                break;
            case "Day":
                tableFilterLabel.setText(String.format("Appointments on %s", TimeController.getOpenOrCloseTime(selectedDate, true).format(TimeController.dateFormatter)));
                break;
            case "Today":
                tableFilterLabel.setText("Appointments Today");
                break;
        }

        loadFilteredAppointmentsTable();
    }

    /**
     * checks if input is empty or null so that if it is not it can call the searchFindAppointments method to search for the input
     * the lambda function used in this method makes our code cleaner and less bulky
     * @param searchText text to search for
     */
    private Predicate<Appointment> createAppointmentPredicate(String searchText) {
        return appointment -> {
            if(searchText == null || searchText.isEmpty()) {
                return true;
            }
            return searchFindAppointments(appointment, searchText);
        };
    }

    /**
     * filters search with provided text
     *
     * @param appointment appointment object
     * @param searchText text to search for
     */
    private boolean searchFindAppointments(Appointment appointment, String searchText) {
        boolean isInList = false;

        if(searchAppointmentsByChoiceBoxChoice.equals("Contact")) {
            isInList = appointment.getContactName().toLowerCase().contains(searchText.toLowerCase());
        } else if(searchAppointmentsByChoiceBoxChoice.equals("Type")) {
            isInList = appointment.getType().toLowerCase().contains(searchText.toLowerCase());
        }

        return isInList;
    }

    /**
     * loads table view with filtered results
     * the lambda function used in this method makes our code cleaner and less bulky
     */
    private void loadFilteredAppointmentsTable() {

        ObservableList<Appointment> appointmentsList = getAppointmentList();
        assert appointmentsList != null;
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(appointmentsList);

        searchAppointmentsTextField.textProperty().addListener((observable, oldValue, newValue) ->
            filteredAppointments.setPredicate(createAppointmentPredicate(newValue))
        );

        appointmentsTable.setItems(filteredAppointments);
        disableEnableUpdateAndDeleteAppointmentButtons();

        if(appointmentsTable.getItems() != null) {
            customiseFactory();
        }
    }

    /**
     * gets all the appointments from the database
     * @return list of appointments
     */
    private ObservableList<Appointment> getAppointmentList() {
        LocalDateTime dateTimeChosen;

        if(dateChosen != null) {
            dateTimeChosen = LocalDateTime.of(dateChosen, LocalTime.now());
        } else {
            dateTimeChosen = LocalDateTime.now();
        }

        try {
            return AppointmentsQuery.getAllAppointments(filterAppointmentsByChoiceBox.getValue(), LocalDateTime.now(), dateTimeChosen);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * checks if input is empty or null so that if it is not it can call the searchFindAppointments method to search for the input
     * the lambda function used in this method makes our code cleaner and less bulky
     * @param searchText text to search for
     */
    private Predicate<Customer> createCustomerPredicate(String searchText) {
        return customer -> {
            if(searchText == null || searchText.isEmpty()) {
                return true;
            }
            return searchFindCustomers(customer, searchText);
        };
    }

    /**
     * filters search with provided text
     *
     * @param customer customer object
     * @param searchText text to search for
     */
    private boolean searchFindCustomers(Customer customer, String searchText) {
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

    /**
     * loads table view with filtered results
     * the lambda function used in this method makes our code cleaner and less bulky
     */
    public void loadFilteredCustomersTable() {
        ObservableList<Customer> customersList = getCustomersList();
        assert customersList != null;
        FilteredList<Customer> filteredCustomers = new FilteredList<>(customersList);

        searchCustomersTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredCustomers.setPredicate(createCustomerPredicate(newValue))
        );

        customersTable.setItems(filteredCustomers);
//        loadFilteredAppointmentsTable();
        disableEnableUpdateAndDeleteCustomerButtons();
    }

    /**
     * gets all the customers from the database
     *
     * @return list of customers
     */
    private ObservableList<Customer> getCustomersList() {
        try {
            return CustomersQuery.getAllCustomersByDivision(countryFilterChoice, divisionFilterChoice);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * handles logout button click
     */
    @FXML
    public void logoutButtonClicked() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        // do what has to be done before closing stage
        Logger.logLogout(loggedInUsername);

        stage.close();
    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleCustomerSearchFilterChoice() {
        searchCustomersTextField.clear();
        loadFilteredCustomersTable();
    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleAppointmentSearchFilterChoice() {
        searchAppointmentsByChoiceBoxChoice = searchAppointmentsByChoiceBox.getSelectionModel().getSelectedItem();
        searchAppointmentsTextField.clear();
        loadFilteredAppointmentsTable();
    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleCustomerDivisionChoiceBoxChoice() {
        divisionFilterChoice = filterCustomersByDivisionChoiceBox.getSelectionModel().getSelectedItem();
        loadFilteredCustomersTable();
    }

    /**
     * loads combo-box with choices
     *
     * @throws SQLException if an SQL exception occurs
     */
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

    /**
     * loads combo-box with choices
     */
    private void loadDivisionChoicesInChoiceBox() {
        filterCustomersByDivisionChoiceBox.getItems().clear();
        ObservableList<Division> divisions = DivisionsQuery.getAllDivisions(countryFilterChoice);
        assert divisions != null;

        filterCustomersByDivisionChoiceBox.getItems().add("All");

        for(Division d : divisions) {
            filterCustomersByDivisionChoiceBox.getItems().add(d.getDivision());
        }

        filterCustomersByDivisionChoiceBox.getSelectionModel().selectFirst();
        countryFilterChoice = filterCustomersByCountryChoiceBox.getSelectionModel().getSelectedItem();
        divisionFilterChoice = filterCustomersByDivisionChoiceBox.getSelectionModel().getSelectedItem();
        loadFilteredCustomersTable();

    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleCustomerCountryChoiceBoxChoice() {
        countryFilterChoice = filterCustomersByCountryChoiceBox.getSelectionModel().getSelectedItem();

        if(countryFilterChoice.equals("All")) {
            filterCustomersByDivisionChoiceBox.setVisible(false);
            divisionLabel.setVisible(false);
        } else {
            filterCustomersByDivisionChoiceBox.setVisible(true);
            divisionLabel.setVisible(true);
        }

        loadDivisionChoicesInChoiceBox();
    }

    /**
     * disables specific dates on this date-picker
     */
    public void disableDatesOnDatePicker() {
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate tomorrow = LocalDate.now().plusDays(1);
                        LocalDate oneYearFromToday = LocalDate.now().plusYears(1);

                        int offsetHours = TimeController.offsetSecondsTotal/3600;

                        if(offsetHours >= 16) {
                            if(empty || date.isBefore(tomorrow) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.MONDAY)  {
                                setDisable(true);
                            }
                        } else if(offsetHours >= 3) {
                            if(empty || date.isBefore(tomorrow) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
                                setDisable(true);
                            }
                        } else {
                            if(empty || date.isBefore(tomorrow) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
                                setDisable(true);
                            }
                        }

                    }
                };
            }
        };

        appointmentDatePickerField.setDayCellFactory(dayCellFactory);
    }

    /**
     * creates a listener for the date-picker
     * the lambda function used in this method makes our code cleaner and less bulky
     */
    public void datePickerListener() {
        appointmentDatePickerField.valueProperty().addListener((ov, oldValue, newValue) -> {
            appointmentDatePickerField.setValue(newValue);
            dateChosen = appointmentDatePickerField.getValue();
            loadFilteredAppointmentsTable();
            LocalDateTime selectedDate = LocalDateTime.now().minusDays(3);

            if(appointmentDatePickerField.getValue() != null) {
                selectedDate = LocalDateTime.of(appointmentDatePickerField.getValue(), LocalTime.now());
            }

            switch (appointmentFilterChoiceBoxChoice) {
                case "Month":
                    tableFilterLabel.setText(String.format("Appointments between %s - %s", TimeController.getFirstOfMonthDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter), TimeController.getLastOfMonthDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter)));
                    break;
                case "Week":
                    tableFilterLabel.setText(String.format("Appointments between %s - %s", TimeController.getStartOfWeekDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter), TimeController.getEndOfWeekDateTime(selectedDate).toLocalDate().format(TimeController.dateFormatter)));
                    break;
                case "Day":
                    tableFilterLabel.setText(String.format("Appointments on %s", selectedDate.toLocalDate().format(TimeController.dateFormatter)));
                    break;
            }

        });
    }

    /**
     * loads combo-box with choices
     */
    private void loadMonthsInComboBox() {
        LocalDate thisMonth = LocalDate.now();

        for(int i = 0; i <= 11; i++) {
            reportsByMonthComboBox.getItems().add(thisMonth.format(TimeController.monthYearFormatter));
            thisMonth = thisMonth.plusMonths(1);
        }

        reportsByMonthComboBox.getSelectionModel().selectFirst();

        numberOfAppointmentsReport();
    }

    /**
     * loads pie graph
     */
    public void numberOfAppointmentsReport() {
        YearMonth monthYear = java.time.YearMonth.parse(reportsByMonthComboBox.getSelectionModel().getSelectedItem(), TimeController.monthYearFormatter);
        LocalDateTime dateTime = LocalDateTime.of(monthYear.getYear(), monthYear.getMonth(), 1,0,0);
        LocalDateTime monthStart = TimeController.getFirstOfMonthDateTime(dateTime);
        LocalDateTime monthEnd = TimeController.getLastOfMonthDateTime(dateTime);
        ObservableList<PieChart.Data> appointmentsByType = AppointmentsQuery.numberOfAppointmentsByTypeAndMonth(monthStart, monthEnd);

        assert appointmentsByType != null;
        if(!appointmentsByType.isEmpty()) {
            appointmentTypesPieChart.setTitle(String.format("Appointments By Type in %s", reportsByMonthComboBox.getSelectionModel().getSelectedItem()));
            appointmentTypesPieChart.setData(appointmentsByType);
            handlePieSliceHovered();
        }

    }

    /**
     * loads tooltip to pie graph slices when hovering over a pie slice
     * the lambda function used in this method makes our code cleaner and less bulky
     */
    public void handlePieSliceHovered() {
        for(final PieChart.Data data : appointmentTypesPieChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                Tooltip tooltip = new Tooltip(data.getName() + " - " + (int) data.getPieValue());
                Tooltip.install(data.getNode(), tooltip);
            });
        }
    }

    /**
     * checks if time slots are available for the day
     *
     * @param appointmentDuration duration of the appointment
     * @param date date of appointment
     * @return boolean value regarding time slots availability
     */
    public boolean timeSlotsAvailable(int appointmentDuration, LocalDate date) {
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.now());
        startDateTime = TimeController.getOpenOrCloseTime(startDateTime, true);
        LocalDateTime endDateTime = TimeController.getOpenOrCloseTime(startDateTime, false);

        LocalDateTime startOfAppointment = startDateTime;
        LocalDateTime endOfAppointment;

        int offsetHours = TimeController.offsetSecondsTotal/3600;
        LocalDateTime secondShiftBeginning = LocalDateTime.of(date, TimeController.openTime.toLocalTime());
        LocalDateTime secondShiftEnding = LocalDateTime.of(date.plusDays(1), LocalTime.of(0, 0));

        LocalDateTime breakBeginning = secondShiftBeginning.minusHours(TimeController.amountOfHoursOfficeIsClosed);
        LocalDateTime breakEnding = secondShiftBeginning;

        LocalDateTime firstShiftBeginning = LocalDateTime.of(date, LocalTime.of(0, 0));
        LocalDateTime firstShiftEnding = breakBeginning;

        if(offsetHours >= 3 && offsetHours <= 15) {
            if (date.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                startOfAppointment = secondShiftBeginning;
                endDateTime = secondShiftEnding;
            } else if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                startOfAppointment = firstShiftBeginning;
                endDateTime = firstShiftEnding;
            } else {
                startOfAppointment = firstShiftBeginning;
                endDateTime = secondShiftEnding;
            }
        }

//        HashSet<Boolean> existsSet = new HashSet<>();
        boolean slotExists;

        while (startOfAppointment.isBefore(endDateTime)) {
            if (startOfAppointment.isAfter(endDateTime.minusMinutes(appointmentDuration))) {
                break;
            }

            endOfAppointment = startOfAppointment.plusMinutes(appointmentDuration).minusMinutes(1);

            if(startOfAppointment.isBefore(oneHourFromNow) && date.equals(LocalDate.now())) {
//            if(date.equals(LocalDate.now()) && startOfAppointment.isBefore(oneHourFromNow)) {
                startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                continue;
            }

            if(offsetHours >= 3 && offsetHours <= 15 && startOfAppointment.isEqual(breakBeginning)) {
                if(!date.getDayOfWeek().equals(DayOfWeek.MONDAY) && !date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                    startOfAppointment = breakEnding;
                }
            }

            slotExists = AppointmentsQuery.appointmentOverlapsOrExists(startOfAppointment, endOfAppointment);
//            existsSet.add(slotExists);

            if(!slotExists) {
//            if(existsSet.contains(false)) {
                return false;
            }

            startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
        }
        return true;
    }

    /**
     * finds all dates that are fully booked for a year
     */
    public void findAllDatesThatAreFullyBookedForAYear() {
        LocalDate date = LocalDate.now();
        LocalDate oneYearFromNow = date.plusYears(1);

        while(!date.isEqual(oneYearFromNow)) {
            int offsetHours = TimeController.offsetSecondsTotal/3600;

            if(offsetHours >= 16) {
                if(date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            } else if(offsetHours >= 3) {
                if(date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            } else {
                if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            }

            boolean fifteenMinuteSlotIsNotAvailable = timeSlotsAvailable(TimeController.minimumTimeDurationMinutes, date);

            if(fifteenMinuteSlotIsNotAvailable) {
                fullyScheduledDates.add(date.format(TimeController.dateFormatter));
            }

            date = date.plusDays(1);
        }
    }

    /**
     * loads fully booked dates report
     */
    public void loadFullyBookedDatesReport() {
        fullyScheduledDates.clear();

        findAllDatesThatAreFullyBookedForAYear();

        fullyBookedDatesListView.getItems().clear();

        if(!fullyScheduledDates.isEmpty()) {
            for (String date : fullyScheduledDates) {
                fullyBookedDatesListView.getItems().add(date);
            }
        }
    }

    /**
     * loads choice-box with contact ID's in reports tab
     */
    public void loadContactsScheduleChoiceBox() {
        ObservableList<Contact> contacts = ContactsQuery.getAllContacts();
        assert contacts != null;

        for(Contact c : contacts) {
            filterAppointmentsByContactsChoiceBox.getItems().add(String.valueOf(c.getContactID()));
        }

        filterAppointmentsByContactsChoiceBox.getSelectionModel().selectFirst();
    }


    /**
     * loads contact schedule table based on choice-box selection
     */
    public void loadContactScheduleTableReport() {
        contactScheduleTable.getItems().clear();
        String contactID = filterAppointmentsByContactsChoiceBox.getSelectionModel().getSelectedItem();
        ObservableList<Appointment> contactSchedule = AppointmentsQuery.getAllAppointmentsByContact(LocalDateTime.now(), contactID);
        contactScheduleTable.setItems(contactSchedule);

        if(contactSchedule != null) {
            totalAppointmentsLabel.setText(String.format("Total Appointments: %s", contactSchedule.size()));
        } else {
            totalAppointmentsLabel.setText("There are no appointments for this contact.");
        }

        if(contactScheduleTable.getItems() != null) {
            customiseFactory();
        }
    }

}
