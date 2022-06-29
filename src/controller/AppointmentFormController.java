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
    private LocalTime startTimeChosen;
    private LocalDateTime startDateTimeChosen;
    private static String contactChosen;
    private static String customerChosen;
    private static String userChosen;
    private static boolean fieldsEmpty = true;
    private static final HashSet<LocalDate> fullyScheduledDates = new HashSet<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateChosen = LocalDate.now().minusDays(1);
        timeDurationChoice = "15 minutes";
        duration = TimeController.minimumTimeDurationMinutes;

        disableDatesOnDatePicker();
        datePicked();
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

    public static void getParentNodeData(String form, int userIDNum) {
        typeOfForm = form;
        userID =  String.valueOf(userIDNum);
    }

    public static void getAppointmentChosen(Appointment a) {
        appointment = a;
    }

    public void loadAddAppointmentForm() {
        appointmentIDTextField.setText(AppointmentsQuery.getNextAppointmentID());
        appointmentFormTitle.setText("Add Appointment");
        saveButton.setText("Add");

        datePickerField.setValue(nextDateAvailable());
        dateChosen = datePickerField.getValue();

        timeDurationComboBox.getSelectionModel().selectFirst();
        handleTimeDurationChoice();

        startTimesComboBox.getSelectionModel().selectFirst();
        handleStartTimeChoice();

        contactsComboBox.getSelectionModel().selectFirst();
        handleContactChoice();

        customersComboBox.getSelectionModel().selectFirst();
        handleCustomerChoice();

        userIDComboBox.getSelectionModel().selectFirst();
        handleUserChoice();
    }

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
        startTimesComboBox.getSelectionModel().select(appointmentStart.format(TimeController.dateTimeFormatter));
        handleStartTimeChoice();

        contactsComboBox.getSelectionModel().select(appointment.getContactID());
        customersComboBox.getSelectionModel().select(appointment.getCustomerID());
        userIDComboBox.getSelectionModel().select(appointment.getUserID());

    }

    public void disableDatesOnDatePicker() {
        getAllDatesThatAreFullyBookedForAYear();
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
//                        LocalDateTime estDateTime = TimeController.estTimeObject.toLocalDateTime();
                        System.out.println("offset hours: " + (TimeController.offsetSecondsTotal/3600));
                        LocalDateTime lastAppointmentSlotTimeToday = TimeController.lastAppointmentSlotTimeToday;
                        LocalDateTime timeNow = LocalDateTime.now();
                        LocalDate tomorrow = LocalDate.now().plusDays(1);
                        LocalDate oneYearFromToday = LocalDate.now().plusYears(1);
                        int offsetHours = TimeController.offsetSecondsTotal/3600;

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
//                        if(empty || date.isBefore(today) || date.isAfter(oneYearFromToday) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
//                            setDisable(true);
//                        }

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

//    public void disableDatesOnDatePicker() {
//        datePickerField.setDayCellFactory(datePicker -> new DateCell() {
//            @Override
//            public void updateItem(LocalDate date, boolean empty) {
//                super.updateItem(date, empty);
//                LocalDate today = LocalDate.now();
//                LocalDate tomorrow = LocalDate.now().plusDays(1);
//                LocalDate threeMonthsFromToday = LocalDate.now().plusWeeks(12);
//                if(empty || date.isBefore(tomorrow) || date.isAfter(threeMonthsFromToday) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)  {
//                    setDisable(true);
//                }
//            }
//
//        });
//    }

    public void getAllDatesThatAreFullyBookedForAYear() {
        LocalDate date = LocalDate.now();
        LocalDate oneYearFromNow = date.plusYears(1);

        while(!date.isEqual(oneYearFromNow)) {
            int offsetHours = TimeController.offsetSecondsTotal/3600;

            if(offsetHours >= 16) {
                if(date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            }
            else if(offsetHours >= 3) {
                if(date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            }
            else {
                if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            }

            boolean fifteenMinuteSlotIsNotAvailable = timeSlotsAvailable(date, TimeController.minimumTimeDurationMinutes);

            if(fifteenMinuteSlotIsNotAvailable) {
                fullyScheduledDates.add(date);
                System.out.println("fifteen Minute Slots not Available on " + date);
            }

            date = date.plusDays(1);
        }
    }

    public LocalDate nextDateAvailable() {
        LocalDateTime startDatetime = TimeController.openTime;
        LocalDate startDate = startDatetime.toLocalDate();
        LocalDateTime lastAppointmentSlotTimeToday = TimeController.lastAppointmentSlotTimeToday;
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate oneYearFromNow = timeNow.plusYears(1).toLocalDate();

        while(startDate.isBefore(oneYearFromNow)) {
//        while(true) {
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

//            if(startDate.getDayOfWeek() != DayOfWeek.SATURDAY && startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
//                break;
//            }

            startDate = startDate.plusDays(1);
        }

        return startDate;
    }

    public void datePicked() {
        datePickerField.valueProperty().addListener((ov, oldValue, newValue) -> {
            datePickerField.setValue(newValue);
            dateChosen = datePickerField.getValue();
            loadTimeDurationComboBox();
            loadStartTimesComboBox();

            handleStartTimeChoice();
        });
    }

    private void loadTimeDurationComboBox() {
        timeDurationComboBox.getItems().clear();

        timeDurationComboBox.getItems().add("15 minutes");

        boolean thirtyMinuteSlotIsNotAvailable = timeSlotsAvailable(datePickerField.getValue(), 30);

        if(!thirtyMinuteSlotIsNotAvailable) {
            timeDurationComboBox.getItems().add("30 minutes");

            boolean fortyFiveMinuteSlotIsNotAvailable = timeSlotsAvailable(datePickerField.getValue(), 45);

            if(!fortyFiveMinuteSlotIsNotAvailable) {
                timeDurationComboBox.getItems().add("45 minutes");

                boolean sixtyMinuteSlotIsNotAvailable = timeSlotsAvailable(datePickerField.getValue(), 60);

                if(!sixtyMinuteSlotIsNotAvailable) {
                    timeDurationComboBox.getItems().add("60 minutes");
                }
            }
        }

        timeDurationComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleTimeDurationChoice() {
        timeDurationChoice = timeDurationComboBox.getSelectionModel().getSelectedItem();
        if(timeDurationChoice != null) {
//            Replaces all non-digit with blank using regex: the remaining string contains only digits.
            duration = Integer.parseInt(timeDurationChoice.replaceAll("[\\D]", ""));
            loadStartTimesComboBox();
        }
    }

    public boolean timeSlotsAvailable(LocalDate date, int appointmentDuration) {
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
        LocalTime startTime = TimeController.openTime.toLocalTime(); // open time in ny in our time

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endOfDateTime = startDateTime.plusHours(TimeController.amountOfHoursOfficeIsOpen);

        LocalDateTime startOfAppointment = startDateTime;
        LocalDateTime endOfAppointment;

        HashSet<Boolean> existsSet = new HashSet<>();
        boolean slotExists;

        while (startOfAppointment.isBefore(endOfDateTime)) {
            endOfAppointment = startOfAppointment.plusMinutes(appointmentDuration).minusMinutes(1);

            if (startOfAppointment.isAfter(endOfDateTime.minusMinutes(appointmentDuration))) {
                break;
            }

            if(date.equals(LocalDate.now()) && startOfAppointment.isBefore(oneHourFromNow)) {
                startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                continue;
            }

            slotExists = AppointmentsQuery.appointmentOverlapsOrExists(startOfAppointment, endOfAppointment);
            existsSet.add(slotExists);

            if(existsSet.contains(false)) {
                return false;
            }

            startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
        }
        return true;
    }

    public void loadStartTimesComboBox() {
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
        LocalTime startTime = TimeController.openTime.toLocalTime(); // open time in ny in our time

        LocalDateTime startDateTime = LocalDateTime.of(dateChosen, startTime);
        LocalDateTime endDateTime = startDateTime.plusHours(TimeController.amountOfHoursOfficeIsOpen);

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

        if(setTimeSlots.size() > 0) {
            while (startOfAppointment.isBefore(endDateTime)) {
//                makes sure start time does not go over end time
                if (startOfAppointment.isAfter(endDateTime.minusMinutes(duration))) {
                    break;
                }

                endOfAppointment = startOfAppointment.plusMinutes(duration).minusMinutes(1);

                if(startOfAppointment.isBefore(oneHourFromNow) && dateChosen.equals(LocalDate.now())) {
                    startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                    continue;
                }

                boolean notDuringBreak = true;

                if(offsetHours >= 3 && offsetHours <= 15) {
                    assert dateChosen != null;
                    if(!dateChosen.getDayOfWeek().equals(DayOfWeek.MONDAY) && !dateChosen.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                        if(startOfAppointment.isEqual(breakBeginning)) {
                            notDuringBreak = false;
                        } else if(startOfAppointment.isAfter(breakBeginning) && startOfAppointment.isBefore(breakEnding)) {
                            notDuringBreak = false;
                        } else if(endOfAppointment.isEqual(breakEnding)) {
                            notDuringBreak = false;
                        } else if(endOfAppointment.isAfter(breakBeginning) && endOfAppointment.isBefore(breakEnding)) {
                            notDuringBreak = false;
                        }
                    }
                }

                if(!notDuringBreak) {
                    System.out.println("during break bro");
                    startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                    continue;
                }

                boolean appointmentExistsAlreadyOrOverlaps = AppointmentsQuery.appointmentOverlapsOrExists(startOfAppointment, endOfAppointment);

                if(!appointmentExistsAlreadyOrOverlaps) {
                    startTimesComboBox.getItems().add(startOfAppointment.format(TimeController.dateTimeFormatter));
                }

                startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
            }
        } else {
            while (startOfAppointment.isBefore(endDateTime)) {
                if (startOfAppointment.isAfter(endDateTime.minusMinutes(duration))) {
                    break;
                }

                endOfAppointment = startOfAppointment.plusMinutes(duration).minusMinutes(1);

                if(startOfAppointment.isBefore(oneHourFromNow) && dateChosen != null && dateChosen.equals(LocalDate.now())) {
                    startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                    continue;
                }

                boolean notDuringBreak = true;

                if(offsetHours >= 3 && offsetHours <= 15) {
                    assert dateChosen != null;
                    if(!dateChosen.getDayOfWeek().equals(DayOfWeek.MONDAY) && !dateChosen.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                        if(startOfAppointment.isEqual(breakBeginning)) {
                            notDuringBreak = false;
                        } else if(startOfAppointment.isAfter(breakBeginning) && startOfAppointment.isBefore(breakEnding)) {
                            notDuringBreak = false;
                        } else if(endOfAppointment.isEqual(breakEnding)) {
                            notDuringBreak = false;
                        } else if(endOfAppointment.isAfter(breakBeginning) && endOfAppointment.isBefore(breakEnding)) {
                            notDuringBreak = false;
                        }
                    }
                }

                if(!notDuringBreak) {
                    startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
                    continue;
                }

                startTimesComboBox.getItems().add(startOfAppointment.format(TimeController.dateTimeFormatter));

                startOfAppointment = startOfAppointment.plusMinutes(TimeController.minimumTimeDurationMinutes);
            }
        }

        startTimesComboBox.getSelectionModel().selectFirst();
    }


    @FXML
    public void handleStartTimeChoice() {
//        String timeString = startTimesComboBox.getSelectionModel().getSelectedItem();
//        if(timeString != null) {
//            startTimeChosen = LocalTime.parse(timeString, TimeController.timeFormatter);
//        }
//        LocalDateTime appointmentStart = startTimesComboBox.getSelectionModel().getSelectedItem();
//        if(appointmentStart != null) {
//            startDateTimeChosen = appointmentStart;
//        }
        String appointmentStart = startTimesComboBox.getSelectionModel().getSelectedItem();
        if(appointmentStart != null) {
            startDateTimeChosen = LocalDateTime.parse(appointmentStart, TimeController.dateTimeFormatter);
        }
    }

    public void loadContactChoiceBox() throws SQLException {
        ObservableList<Contact> contacts = ContactsQuery.getAllContacts();
        assert contacts != null;

        for(Contact c : contacts) {
            contactsComboBox.getItems().add(c.getContactID());
        }
    }

    @FXML
    public void handleContactChoice() {
        contactChosen = contactsComboBox.getSelectionModel().getSelectedItem();
    }

    public void loadCustomerChoiceBox() throws SQLException {
        ObservableList<Customer> customers = CustomersQuery.getAllCustomers();
        assert customers != null;

        for(Customer c : customers) {
//            customersComboBox.getItems().add(String.format("ID: %s  %s", c.getCustomerID(), c.getCustomerName()));
            customersComboBox.getItems().add(c.getCustomerID());
        }
    }

    @FXML
    public void handleCustomerChoice() {
        customerChosen = customersComboBox.getSelectionModel().getSelectedItem();
    }

    public void loadUserChoiceBox() throws SQLException {
        ObservableList<User> userIDs = UserQuery.getAllUsers();
        assert userIDs != null;

        for(User u : userIDs) {
            userIDComboBox.getItems().add(String.valueOf(u.getUserID()));
        }
    }

    @FXML
    public void handleUserChoice() {
        userChosen = userIDComboBox.getSelectionModel().getSelectedItem();
    }

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

    @FXML
    void saveAppointment() {
        checkIfFieldsAreEmpty();

        if(!fieldsEmpty) {
            // turn to utc in backend
            assert startTimeChosen != null;
            LocalDateTime appointmentStartDateTime = LocalDateTime.parse(startTimesComboBox.getSelectionModel().getSelectedItem(), TimeController.dateTimeFormatter);
            LocalDateTime appointmentEndDateTime = appointmentStartDateTime.plusMinutes(duration).minusMinutes(1);
//            String endTime = appointmentStartDateTime.plusMinutes(duration).minusSeconds(1).format(TimeController.timeFormatter);
//            LocalDate startDate = appointmentStartDateTime.toLocalDate();
//            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTimeChosen);
//            LocalDateTime startDateTime = LocalDateTime.parse(startTimeChosen, TimeController.dateTimeFormatter);
//            startOfAppointment.format(TimeController.timeFormatter)
//            LocalDateTime endDateTime = startDateTime.plusMinutes(duration);
//            LocalDate endDate = endDateTime.toLocalDate();

            Appointment newAppointment = new Appointment(
                    appointmentIDTextField.getText(),
                    titleTextField.getText(),
                    descriptionTextField.getText(),
                    locationTextField.getText(),
                    typeTextField.getText(),
                    appointmentStartDateTime.toLocalDate(),
//                    datePickerField.getValue(),
//                    startTimeChosen.format(TimeController.timeFormatter),
//                    endDate,
//                    endTime,
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

    @FXML
    void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
