package controller;

import databaseQueries.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.HashSet;
import java.util.ResourceBundle;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 *
 * @author Alexander Padilla
 */
public class AppointmentFormController implements Initializable {
    @FXML public AnchorPane mainPanel;
    @FXML public Button saveButton;
    @FXML public Label appointmentFormTitle;
    @FXML public Button cancelButton;
    @FXML public TextField appointmentIDTextField;
    @FXML public TextField titleTextField;
    @FXML public TextField descriptionTextField;
    @FXML public TextField locationTextField;
    @FXML public TextField typeTextField;
    @FXML public DatePicker datePickerField;
    @FXML public ComboBox<String> timeDurationComboBox;
    @FXML public ComboBox<String> userIDComboBox;
//    @FXML public ComboBox<User> userIDComboBox;
    @FXML private ComboBox<String> contactsComboBox;
    @FXML private ComboBox<String> customersComboBox;
    @FXML private ComboBox<String> startTimesComboBox;
//    @FXML private ComboBox<LocalDateTime> startTimesComboBox;
    private static Appointment appointment;
    private static LocalDate dateChosen;
    private static String userID;
    private static String typeOfForm;
    private static String timeDurationChoice;
    private static int duration;
    private static boolean fieldsEmpty = true;
    private static final HashSet<LocalDate> fullyScheduledDates = new HashSet<>();

    /**
     * uses initialize to initialize this scene
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fullyScheduledDates.clear();
        dateChosen = LocalDate.now().minusDays(1);
        timeDurationChoice = "15 minutes";
        duration = TimeController.minimumTimeDurationMinutes;

        disableDatesOnDatePicker();
        datePickerListener();
        try {
//            loadTimeDurationComboBox();
//            loadStartTimesComboBox();
            loadContactChoiceBox();
            loadCustomerChoiceBox();
            loadUserChoiceBox();

            if(typeOfForm.equals("addAppointmentForm")) {
                loadAddAppointmentForm();
            } else if(typeOfForm.equals("updateAppointmentForm")) {
                loadUpdateAppointmentForm();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets parent node data
     * @param form the type of form
     * @param loggedInUserIDNum logged in user's username
     */
    public static void getParentNodeData(String form, int loggedInUserIDNum) {
        typeOfForm = form;
        userID =  String.valueOf(loggedInUserIDNum);
    }

    /**
     * gets appointment selected data from parent stage table view
     * @param a the appointment object
     */
    public static void getAppointmentChosen(Appointment a) {
        appointment = a;
    }

    /**
     * loads add appointment form
     */
    public void loadAddAppointmentForm() {
        appointmentIDTextField.setText(AppointmentsQuery.getNextAppointmentID());
        appointmentFormTitle.setText("Add Appointment");
        saveButton.setText("Add");

        datePickerField.setValue(nextDateAvailable());
        dateChosen = datePickerField.getValue();

        timeDurationComboBox.getSelectionModel().selectFirst();
        handleTimeDurationChoice();

        startTimesComboBox.getSelectionModel().selectFirst();
//        handleStartTimeChoice();

        contactsComboBox.getSelectionModel().selectFirst();

        customersComboBox.getSelectionModel().selectFirst();

        userIDComboBox.getSelectionModel().selectFirst();
    }

    /**
     * loads update appointment form
     */
    public void loadUpdateAppointmentForm() {
        appointmentFormTitle.setText("Update Appointment");
        saveButton.setText("Update");
        appointmentIDTextField.setText(appointment.getAppointmentID());
        titleTextField.setText(appointment.getTitle());
        descriptionTextField.setText(appointment.getDescription());
        locationTextField.setText(appointment.getLocation());
        typeTextField.setText(appointment.getType());
        datePickerField.setValue(appointment.getStartDate());

        LocalTime startTime = LocalTime.parse(appointment.getStartTime(), TimeController.timeFormatter);
        LocalTime endTime = LocalTime.parse(appointment.getEndTime(), TimeController.timeFormatter);
        LocalDateTime startDateTime = LocalDateTime.of(appointment.getStartDate(), startTime);
        LocalDateTime endDateTime = LocalDateTime.of(appointment.getEndDate(), endTime);
        duration = (int) MINUTES.between(startDateTime, endDateTime) + 1;

        timeDurationComboBox.getSelectionModel().select(duration + " minutes");

        LocalTime appointmentStartTime = LocalTime.parse(appointment.getStartTime(), TimeController.timeFormatter);
        LocalDateTime appointmentStart = LocalDateTime.of(appointment.getStartDate(), appointmentStartTime);
        startTimesComboBox.getSelectionModel().select(appointmentStart.format(TimeController.timeFormatter));
//        handleStartTimeChoice();

        contactsComboBox.getSelectionModel().select(appointment.getContactID());
        customersComboBox.getSelectionModel().select(appointment.getCustomerID());
        userIDComboBox.getSelectionModel().select(appointment.getUserID());

    }

    /**
     * disables dates on date-picker
     */
    public void disableDatesOnDatePicker() {
        findAllDatesThatAreFullyBookedForAYear();
        System.out.println("offset hours: " + (TimeController.offsetSecondsTotal/3600));

        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        LocalDateTime timeNow = LocalDateTime.now();
                        LocalDateTime lastAppointmentSlotTimeToday = TimeController.getOpenOrCloseTime(timeNow, false).minusMinutes(TimeController.minimumTimeDurationMinutes);
                        LocalDate tomorrow = LocalDate.now().plusDays(1);
                        LocalDate oneYearFromToday = LocalDate.now().plusYears(1);
                        int offsetHours = TimeController.offsetSecondsTotal/3600;

                        if(!fullyScheduledDates.contains(date)) {
                            setDisable(false);
                        }

                        if(offsetHours >= 16) {
                            if(empty || date.isBefore(today) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.MONDAY)  {
                                setDisable(true);
                            }
                        } else if(offsetHours >= 3) {
                            if(empty || date.isBefore(today) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
                                setDisable(true);
                            }
                        } else {
                            if(empty || date.isBefore(today) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
                                setDisable(true);
                            }
                        }

                        if(date.equals(LocalDate.now()) && timeNow.isAfter(lastAppointmentSlotTimeToday.minusHours(1))) {
                            setDisable(true);
                        }
//                        automatically disables day if after last appointment slot time at 8:45 PM EST
                        if(timeNow.isAfter(lastAppointmentSlotTimeToday) && date.isBefore(tomorrow)) {
                            setDisable(true);
                        }

                        if(fullyScheduledDates.contains(date)) {
                            setDisable(true);
                        }

                    }
                };
            }
        };

        datePickerField.setDayCellFactory(dayCellFactory);
    }

    /**
     * finds all dates that are fully booked
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

            boolean fifteenMinuteSlotAvailable = timeSlotsAvailable(TimeController.minimumTimeDurationMinutes, date);

            if(!fifteenMinuteSlotAvailable) {
                fullyScheduledDates.add(date);
                System.out.println("fifteen Minute Slots not Available on " + date);
            }

            date = date.plusDays(1);
        }
    }

    /**
     * finds the next date available
     *
     * @return the next date available
     */
    public LocalDate nextDateAvailable() {
        LocalDateTime startDatetime = TimeController.openTime;
        LocalDate startDate = startDatetime.toLocalDate();
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime lastAppointmentSlotTimeToday = TimeController.getOpenOrCloseTime(timeNow, false).minusMinutes(TimeController.minimumTimeDurationMinutes);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate oneYearFromNow = timeNow.plusYears(1).toLocalDate();

        while(startDate.isBefore(oneYearFromNow)) {
            if(startDate.isBefore(today)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            if(startDate.equals(LocalDate.now()) && timeNow.isAfter(lastAppointmentSlotTimeToday.minusHours(1))) {
                startDate = startDate.plusDays(1);
                continue;
            }

            if(timeNow.isAfter(lastAppointmentSlotTimeToday) && startDate.isBefore(tomorrow)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            if(fullyScheduledDates.contains(startDate)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            int offsetHours = TimeController.offsetSecondsTotal/3600;

            if(offsetHours >= 16) {
                if(startDate.getDayOfWeek() != DayOfWeek.SUNDAY && startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
                    break;
                }
            }
            else if(offsetHours >= 3) {
                if(startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    break;
                }
            }
            else {
                if(startDate.getDayOfWeek() != DayOfWeek.SATURDAY && startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    break;
                }
            }

            startDate = startDate.plusDays(1);
        }

        return startDate;
    }

    /**
     * creates a listener for the date-picker
     */
    public void datePickerListener() {
        datePickerField.valueProperty().addListener((ov, oldValue, newValue) -> {
            datePickerField.setValue(newValue);
            dateChosen = datePickerField.getValue();
            loadTimeDurationComboBox();
            loadStartTimesComboBox();

//            handleStartTimeChoice();
        });
    }

    /**
     * loads combo-box with time durations
     */
    private void loadTimeDurationComboBox() {
        timeDurationComboBox.getItems().clear();

        timeDurationComboBox.getItems().add("15 minutes");

        boolean thirtyMinuteSlotIsAvailable = timeSlotsAvailable(30, datePickerField.getValue());

        if(thirtyMinuteSlotIsAvailable) {
            timeDurationComboBox.getItems().add("30 minutes");

            boolean fortyFiveMinuteSlotIsAvailable = timeSlotsAvailable(45, datePickerField.getValue());

            if(fortyFiveMinuteSlotIsAvailable) {
                timeDurationComboBox.getItems().add("45 minutes");

                boolean sixtyMinuteSlotIsAvailable = timeSlotsAvailable(60, datePickerField.getValue());

                if(sixtyMinuteSlotIsAvailable) {
                    timeDurationComboBox.getItems().add("60 minutes");
                }
            }
        }

        timeDurationComboBox.getSelectionModel().selectFirst();
    }

    /**
     * handles combo-box selection
     */
    @FXML
    public void handleTimeDurationChoice() {
        timeDurationChoice = timeDurationComboBox.getSelectionModel().getSelectedItem();
        if(timeDurationChoice != null) {
//            Replaces all non-digit with blank using regex: the remaining string contains only digits.
            duration = Integer.parseInt(timeDurationChoice.replaceAll("[\\D]", ""));
            loadStartTimesComboBox();
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
//        dateChosen = datePickerField.getValue();
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

        HashSet<Boolean> existsSet = new HashSet<>();
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
            existsSet.add(slotExists);

//            if(!slotExists) {
            if(existsSet.contains(false)) {
                return true;
            }

            startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
        }

        return false;
    }

    /**
     * loads combo-box with start times based on time duration
     */
    public void loadStartTimesComboBox() {
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);

        LocalDateTime startDateTime = LocalDateTime.of(dateChosen, LocalTime.now());
        startDateTime = TimeController.getOpenOrCloseTime(startDateTime, true);
        LocalDateTime endDateTime = TimeController.getOpenOrCloseTime(startDateTime, false);

        LocalDateTime startOfAppointment = startDateTime;
        LocalDateTime endOfAppointment;

        startTimesComboBox.getItems().clear();

        int offsetHours = TimeController.offsetSecondsTotal/3600;
        LocalDateTime secondShiftBeginning = LocalDateTime.of(dateChosen, TimeController.openTime.toLocalTime());
        LocalDateTime secondShiftEnding = LocalDateTime.of(dateChosen.plusDays(1), LocalTime.of(0, 0));

        LocalDateTime breakBeginning = secondShiftBeginning.minusHours(TimeController.amountOfHoursOfficeIsClosed);
        LocalDateTime breakEnding = secondShiftBeginning;

        LocalDateTime firstShiftBeginning = LocalDateTime.of(dateChosen, LocalTime.of(0, 0));
        LocalDateTime firstShiftEnding = breakBeginning;

        if(offsetHours >= 3 && offsetHours <= 15) {
            if (dateChosen.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                startOfAppointment = secondShiftBeginning;
                endDateTime = secondShiftEnding;
            } else if (dateChosen.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                startOfAppointment = firstShiftBeginning;
                endDateTime = firstShiftEnding;
            } else {
                startOfAppointment = firstShiftBeginning;
                endDateTime = secondShiftEnding;
            }

            System.out.printf("\nfirstShiftBeginning: %s\nfirstShiftEnding: %s\nbreakBeginning: %s\nbreakEnding: %s\nsecondShiftBeginning: %s\nsecondShiftEnding: %s\n%n", firstShiftBeginning, firstShiftEnding, breakBeginning, breakEnding, secondShiftBeginning, secondShiftEnding);
        }

        ObservableList<Appointment> setTimeSlots = AppointmentsQuery.getAppointmentsForDay(startOfAppointment, endDateTime);
        assert setTimeSlots != null;

        while (startOfAppointment.isBefore(endDateTime)) {
//            makes sure start time does not go over end time
            if (startOfAppointment.isAfter(endDateTime.minusMinutes(duration))) {
                break;
            }

            endOfAppointment = startOfAppointment.plusMinutes(duration).minusMinutes(1);

            if(startOfAppointment.isBefore(oneHourFromNow) && dateChosen.equals(LocalDate.now())) {
                startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                continue;
            }


            if(offsetHours >= 3 && offsetHours <= 15 && startOfAppointment.isEqual(breakBeginning)) {
                assert dateChosen != null;
                if(!dateChosen.getDayOfWeek().equals(DayOfWeek.MONDAY) && !dateChosen.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                        System.out.println("during break bro");
                        startOfAppointment = breakEnding;
                }
            }

            if(setTimeSlots.size() > 0) {
                boolean appointmentExistsAlreadyOrOverlaps = AppointmentsQuery.appointmentOverlapsOrExists(startOfAppointment, endOfAppointment);

                if (!appointmentExistsAlreadyOrOverlaps) {
                    startTimesComboBox.getItems().add(startOfAppointment.format(TimeController.timeFormatter));
                }
            } else {
                startTimesComboBox.getItems().add(startOfAppointment.format(TimeController.timeFormatter));
            }

            startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
        }

        startTimesComboBox.getSelectionModel().selectFirst();
    }


//    /**
//     * handles combo-box selection
//     */
//    @FXML
//    public void handleStartTimeChoice() {
//        String appointmentStart = startTimesComboBox.getSelectionModel().getSelectedItem();
//
//        if(appointmentStart != null) {
//            LocalTime startTimeChosen = LocalTime.parse(appointmentStart, TimeController.timeFormatter);
//        }
//    }

    /**
     * loads combo-box with contacts
     *
     * @throws SQLException if an SQL exception occurs
     */
    public void loadContactChoiceBox() throws SQLException {
        ObservableList<Contact> contacts = ContactsQuery.getAllContacts();
        assert contacts != null;

        for(Contact c : contacts) {
            contactsComboBox.getItems().add(c.getContactID());
        }
    }

    /**
     * loads combo-box with customers
     *
     * @throws SQLException if an SQL exception occurs
     */
    public void loadCustomerChoiceBox() throws SQLException {
        ObservableList<Customer> customers = CustomersQuery.getAllCustomers();
        assert customers != null;

        for(Customer c : customers) {
//            customersComboBox.getItems().add(String.format("ID: %s  %s", c.getCustomerID(), c.getCustomerName()));
            customersComboBox.getItems().add(c.getCustomerID());
        }
    }

    /**
     * loads combo-box with users
     *
     * @throws SQLException if an SQL exception occurs
     */
    public void loadUserChoiceBox() throws SQLException {
        ObservableList<User> userIDs = UserQuery.getAllUsers();
        assert userIDs != null;

        for(User u : userIDs) {
            userIDComboBox.getItems().add(String.valueOf(u.getUserID()));
        }
    }

    /**
     * checks if text-fields are empty
     */
    private void checkIfFieldsAreEmpty() {
        if(titleTextField.getText().isEmpty() ||
                descriptionTextField.getText().isEmpty() ||
                locationTextField.getText().isEmpty() ||
                titleTextField.getText().isEmpty() ||
                typeTextField.getText().isEmpty()
        ) {
            fieldsEmpty = true;
            Alert emptyFieldAlert = new Alert(Alert.AlertType.INFORMATION);
            emptyFieldAlert.setTitle("Notice");
            emptyFieldAlert.setHeaderText("Empty Fields");
            emptyFieldAlert.setContentText("Please fill in all fields.");
            emptyFieldAlert.showAndWait();
        } else {
            fieldsEmpty = false;
        }
    }

    /**
     * save button handler
     */
    @FXML
    void saveAppointment() {
        checkIfFieldsAreEmpty();

        if(!fieldsEmpty) {
            // turn to utc in backend
            LocalTime appointmentStartTime = LocalTime.parse(startTimesComboBox.getSelectionModel().getSelectedItem(), TimeController.timeFormatter);
            LocalDateTime appointmentStartDateTime = LocalDateTime.of(datePickerField.getValue(), appointmentStartTime);
            LocalDateTime appointmentEndDateTime = appointmentStartDateTime.plusMinutes(duration).minusMinutes(1);

            Appointment newAppointment = new Appointment(
                    appointmentIDTextField.getText(),
                    titleTextField.getText(),
                    descriptionTextField.getText(),
                    locationTextField.getText(),
                    typeTextField.getText(),
                    appointmentStartDateTime.toLocalDate(),
                    appointmentStartDateTime.toLocalTime().format(TimeController.timeFormatter),
                    appointmentEndDateTime.toLocalDate(),
                    appointmentEndDateTime.toLocalTime().format(TimeController.timeFormatter),
                    customersComboBox.getSelectionModel().getSelectedItem(),
                    userIDComboBox.getSelectionModel().getSelectedItem(),
                    contactsComboBox.getSelectionModel().getSelectedItem()
            );

            boolean addOrUpdateAppointmentConfirmed = false;

            if(typeOfForm.equals("addAppointmentForm")) {
                addOrUpdateAppointmentConfirmed = AppointmentsQuery.addAppointment(newAppointment, userID);
            } else if(typeOfForm.equals("updateAppointmentForm")) {
                addOrUpdateAppointmentConfirmed = AppointmentsQuery.updateAppointment(newAppointment, userID);
            }

            if(addOrUpdateAppointmentConfirmed) {
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * button clicked handler
     */
    @FXML
    void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
