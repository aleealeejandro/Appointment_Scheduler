package databaseQueries;

import controller.MainUIController;
import controller.TimeController;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import model.Appointment;
import java.sql.*;
import java.time.*;

/**
 * Handles all appointment queries to the database
 * @author Alexander Padilla
 */
public class AppointmentsQuery {

    /**
     * gets all appointments from database based on user selected filter and datetime chosen
     *
     * @param filterBy filter type for query chosen by the user
     * @param timeNow datetime when this method was called
     * @param dateTimeChosen datetime that user chose
     * @return list of appointments
     * @throws SQLException if an SQL exception occurs
     */
    public static ObservableList<Appointment> getAllAppointments(String filterBy, LocalDateTime timeNow, LocalDateTime dateTimeChosen) throws SQLException {
        String timeNowString = TimeController.getUtcDatetime(timeNow).format(TimeController.timestampFormatter);
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
//        String query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' ORDER BY Start;", timeNowString);
        String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID ORDER BY Start;";

        if(filterBy != null) {
            switch (filterBy) {
                case "All":
//                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' ORDER BY Start;", timeNowString);
                    query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID ORDER BY Start;";
                    break;
                case "Month":
//                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.getFirstOfMonthDateTime(dateTimeChosen)) + "%", TimeController.getUtcDatetime(TimeController.getLastOfMonthDateTime(dateTimeChosen).minusMinutes(1)) + "%");
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE Start BETWEEN '%s' AND '%s' ORDER BY Start;", TimeController.getUtcDatetime(TimeController.getFirstOfMonthDateTime(dateTimeChosen)) + "%", TimeController.getUtcDatetime(TimeController.getLastOfMonthDateTime(dateTimeChosen).minusMinutes(1)) + "%");
                    break;
                case "Week":
//                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.getStartOfWeekDateTime(dateTimeChosen)) + "%", TimeController.getUtcDatetime(TimeController.getEndOfWeekDateTime(dateTimeChosen).minusMinutes(1)) + "%");
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE Start BETWEEN '%s' AND '%s' ORDER BY Start;", TimeController.getUtcDatetime(TimeController.getStartOfWeekDateTime(dateTimeChosen)) + "%", TimeController.getUtcDatetime(TimeController.getEndOfWeekDateTime(dateTimeChosen).minusMinutes(1)) + "%");
                    break;
                case "Today":
                case "Day":
//                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.getOpenOrCloseTime(dateTimeChosen, true)), TimeController.getUtcDatetime(TimeController.getOpenOrCloseTime(dateTimeChosen, false).minusMinutes(1)));
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE Start BETWEEN '%s' AND '%s' ORDER BY Start;", TimeController.getUtcDatetime(TimeController.getOpenOrCloseTime(dateTimeChosen, true)), TimeController.getUtcDatetime(TimeController.getOpenOrCloseTime(dateTimeChosen, false).minusMinutes(1)));
                    break;
                case "My Appointments":
//                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND User_ID='%s' ORDER BY Start;", timeNowString, MainUIController.loggedInUserID);
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE User_ID='%s' ORDER BY Start;", MainUIController.loggedInUserID);
                    break;
            }
        }

//        "DELETE FROM client_schedule.appointments WHERE End <= '%s';
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet appointmentsSet = preparedStatement.executeQuery();

            while(appointmentsSet.next()) {
                String appointmentID = appointmentsSet.getString("Appointment_ID");
                String title = appointmentsSet.getString("Title");
                String description = appointmentsSet.getString("Description");
                String location = appointmentsSet.getString("Location");
                String type = appointmentsSet.getString("Type");
                assert false;

                LocalDateTime  startTimeObject = appointmentsSet.getTimestamp("Start").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                startTimeObject = TimeController.getSystemDatetime(startTimeObject);
                String startTime = startTimeObject.format(TimeController.timeFormatter);
                LocalDate startDate = startTimeObject.toLocalDate();

                LocalDateTime endTimeObject = appointmentsSet.getTimestamp("End").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                endTimeObject = TimeController.getSystemDatetime(endTimeObject);
                String endTime = endTimeObject.format(TimeController.timeFormatter);
                LocalDate endDate = endTimeObject.toLocalDate();

                String customerID = appointmentsSet.getString("Customer_ID");
                String userID = appointmentsSet.getString("User_ID");
                String contactID = appointmentsSet.getString("a.Contact_ID");
                String contactName = appointmentsSet.getString("c.Contact_Name");
                Appointment appointment = new Appointment(appointmentID, title, description, location, type, startDate, startTime, endDate, endTime, customerID, userID, contactID, contactName);
                appointments.add(appointment);
            }

            return appointments;
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * gets all the appointments for the specific contact
     *
     * @param timeNow datetime when this method was called
     * @param contactIdString the contact ID to query
     * @return filtered list of appointments
     */
    public static ObservableList<Appointment> getAllAppointmentsByContact(LocalDateTime timeNow, String contactIdString) {
        String timeNowString = TimeController.getUtcDatetime(timeNow).format(TimeController.timestampFormatter);
        int contactId = Integer.parseInt(contactIdString);
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
//        String query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND a.Contact_ID='%s' ORDER BY Start;", timeNowString, contactId);
                    String query = String.format("SELECT Appointment_ID, Description, Type, Start, End, Customer_ID FROM client_schedule.appointments WHERE Contact_ID='%s' ORDER BY Start;", contactId);
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            ResultSet appointmentsSet = preparedStatement.executeQuery();

            while(appointmentsSet.next()) {
                String appointmentID = appointmentsSet.getString("Appointment_ID");
                String description = appointmentsSet.getString("Description");
                String type = appointmentsSet.getString("Type");

                LocalDateTime  startTimeObject = appointmentsSet.getTimestamp("Start").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                startTimeObject = TimeController.getSystemDatetime(startTimeObject);
                String startTime = startTimeObject.format(TimeController.timeFormatter);
                LocalDate startDate = startTimeObject.toLocalDate();

                LocalDateTime endTimeObject = appointmentsSet.getTimestamp("End").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                endTimeObject = TimeController.getSystemDatetime(endTimeObject);
                String endTime = endTimeObject.format(TimeController.timeFormatter);
                LocalDate endDate = endTimeObject.toLocalDate();

                String customerID = appointmentsSet.getString("Customer_ID");
                Appointment appointment = new Appointment(appointmentID, description, type, startDate, startTime, endDate, endTime, customerID);
                appointments.add(appointment);
            }

            return appointments;
        } catch (SQLException err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * deletes appointment in database
     *
     * @param appointmentID the appointment ID to search for in database
     * @return boolean value whether the appointment was deleted or not
     */
    public static boolean deleteAppointment(String appointmentID) {
        String query = "DELETE FROM client_schedule.appointments WHERE Appointment_ID=\"" + appointmentID + "\";";
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            int deleteBool = preparedStatement.executeUpdate();

            return deleteBool == 1;
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * checks if appointment overlaps pre-existing appointments or exists already in the database
     *
     * @param startDatetime the start of the appointment to use in the query
     * @param endDatetime the end of the appointment to use in the query
     * @return boolean value whether the appointment already exists or overlaps existing appointments in database
     */
    public static boolean appointmentOverlapsOrExists(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        startDatetime = TimeController.getUtcDatetime(startDatetime);
        endDatetime = TimeController.getUtcDatetime(endDatetime);
        String startDatetimeString = startDatetime.format(TimeController.timestampFormatter);
        String endDatetimeString = endDatetime.format(TimeController.timestampFormatter);
        String query = "SELECT COUNT(Appointment_ID) FROM client_schedule.appointments WHERE (Start <= ? AND End >= ?) OR (Start <= ? AND End >= ?) OR (Start Between ? AND ?) OR (END Between ? AND ?);";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            preparedStatement.setString(1, startDatetimeString);
            preparedStatement.setString(2, startDatetimeString);
            preparedStatement.setString(3, endDatetimeString);
            preparedStatement.setString(4, endDatetimeString);
            preparedStatement.setString(5, startDatetimeString);
            preparedStatement.setString(6, endDatetimeString);
            preparedStatement.setString(7, startDatetimeString);
            preparedStatement.setString(8, endDatetimeString);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;

            while(resultSet.next()) {
                count = resultSet.getInt(1);
            }
            // returns true or false
//            System.out.println("count: " + count);
            return count > 0;
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * gets all the appointments for the given day from the database
     *
     * @param startDatetime the start of the day to use in the query
     * @param endDatetime the end of the day to use in the query
     * @return list of appointments for the specific date
     */
    public static ObservableList<Appointment> getAppointmentsForDay(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        ObservableList<Appointment> setAppointments = FXCollections.observableArrayList();
        startDatetime = TimeController.getUtcDatetime(startDatetime);
        endDatetime = TimeController.getUtcDatetime(endDatetime);
        String startDatetimeString = startDatetime + "%";
        String endDatetimeString = endDatetime + "%";
        String query =  "SELECT Start, End FROM client_schedule.appointments WHERE Start BETWEEN ? AND ? ORDER BY Start ASC;";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            preparedStatement.setString(1, startDatetimeString);
            preparedStatement.setString(2, endDatetimeString);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                LocalDateTime start = resultSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = resultSet.getTimestamp("End").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                start = TimeController.getSystemDatetime(start);
                end = TimeController.getSystemDatetime(end);
                Appointment appointment = new Appointment(start, end);
                setAppointments.add(appointment);
            }

            return setAppointments;
        } catch (SQLException | RuntimeException err) {
            return null;
        }
    }

    /**
     * adds appointment to the database
     *
     * @param appointment the appointment object holding information to add to the database
     * @param loggedInUserName the username of the logged-in user
     * @return boolean value whether the appointment was added to the database successfully
     */
    public static boolean addAppointment(Appointment appointment, String loggedInUserName) {
        LocalTime startTime = LocalTime.parse(appointment.getStartTime(), TimeController.timeFormatter);
        LocalTime endTime = LocalTime.parse(appointment.getEndTime(), TimeController.timeFormatter);
        LocalDateTime start = LocalDateTime.of(appointment.getStartDate(), startTime);
        LocalDateTime end = LocalDateTime.of(appointment.getEndDate(), endTime);
//        change from SYSTEM to UTC LocalDateTime
        start = TimeController.getUtcDatetime(start);
        end = TimeController.getUtcDatetime(end);
        String query = String.format("INSERT INTO client_schedule.appointments VALUES(%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s);",
                Integer.parseInt(appointment.getAppointmentID()),
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getLocation(),
                appointment.getType(),
                start,
                end,
                LocalDateTime.now(ZoneOffset.UTC),
                loggedInUserName,
                LocalDateTime.now(ZoneOffset.UTC),
                loggedInUserName,
                Integer.parseInt(appointment.getCustomerID()),
                Integer.parseInt(appointment.getUserID()),
                Integer.parseInt(appointment.getContactID())
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
     * updates appointment in the database
     *
     * @param appointment the appointment object holding information to update a specific appointment in the database
     * @param loggedInUserName the username of the logged-in user
     * @return boolean value whether the appointment was updated in the database successfully
     */
    public static boolean updateAppointment(Appointment appointment, String loggedInUserName) {
        LocalTime startTime = LocalTime.parse(appointment.getStartTime(), TimeController.timeFormatter);
        LocalTime endTime = LocalTime.parse(appointment.getEndTime(), TimeController.timeFormatter);
        LocalDateTime start = LocalDateTime.of(appointment.getStartDate(), startTime);
//        LocalDateTime end = LocalDateTime.of(appointment.getStartDate(), endTime);
        LocalDateTime end = LocalDateTime.of(appointment.getEndDate(), endTime);
        start = TimeController.getUtcDatetime(start);
        end = TimeController.getUtcDatetime(end);

        String query = String.format("UPDATE client_schedule.appointments SET Title='%s', Description='%s', Location='%s', Type='%s', Start='%s', End='%s', Last_Update='%s', Last_Updated_By='%s', Customer_ID=%s, User_ID=%s, Contact_ID=%s WHERE Appointment_ID=%s;",
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getLocation(),
                appointment.getType(),
                start,
                end,
                LocalDateTime.now(ZoneOffset.UTC),
                loggedInUserName,
                Integer.parseInt(appointment.getCustomerID()),
                Integer.parseInt(appointment.getUserID()),
                Integer.parseInt(appointment.getContactID()),
                Integer.parseInt(appointment.getAppointmentID())
        );

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            int addedBoolean = preparedStatement.executeUpdate();

            return addedBoolean == 1;
        } catch (SQLException | RuntimeException err) {
            err.printStackTrace();
//            System.out.println(err);
            return false;
        }
    }

    /**
     * gets next appointment ID in the database
     *
     * @return next appointment ID available
     */
    public static String getNextAppointmentID() {
        int nextID = 0;
        String query = "SELECT MAX(Appointment_ID) FROM client_schedule.appointments;";
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
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
     * queries database to check if there are any appointments within fifteen minutes
     *
     * @param timeNow the time when checkIfAppointmentWithinFifteenMinutes() was called
     * @param fifteenMinutesFromNow fifteen minutes after timeNow parameter
     * @return appointment that is within 15 minutes
     */
    public static Appointment checkIfAppointmentWithinFifteenMinutes(LocalDateTime timeNow, LocalDateTime fifteenMinutesFromNow) {
        timeNow = TimeController.getUtcDatetime(timeNow);
        fifteenMinutesFromNow = TimeController.getUtcDatetime(fifteenMinutesFromNow);
        String timeNowString = timeNow.format(TimeController.timestampFormatter);
        String fifteenMinutesFromNowString = fifteenMinutesFromNow.format(TimeController.timestampFormatter);
        String query = "SELECT Appointment_ID, Start, End, User_ID FROM client_schedule.appointments WHERE Start BETWEEN ? AND ?;";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            preparedStatement.setString(1, timeNowString);
            preparedStatement.setString(2, fifteenMinutesFromNowString);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                String appointmentID = resultSet.getString("Appointment_ID");
                LocalDateTime  startTimeObject = resultSet.getTimestamp("Start").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                startTimeObject = TimeController.getSystemDatetime(startTimeObject);
                String startTime = startTimeObject.format(TimeController.timeFormatter);
                LocalDate startDate = startTimeObject.toLocalDate();

                LocalDateTime endTimeObject = resultSet.getTimestamp("End").toLocalDateTime();
//                change from UTC to SYSTEM LocalDateTime
                endTimeObject = TimeController.getSystemDatetime(endTimeObject);
                String endTime = endTimeObject.format(TimeController.timeFormatter);
                LocalDate endDate = endTimeObject.toLocalDate();

                String userID = resultSet.getString("User_ID");
                return new Appointment(appointmentID, startDate, startTime, endDate, endTime, userID);
            }

            return null;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * queries database for different appointment types for a specific month
     *
     * @param monthStart the start of the month
     * @param monthEnd the end of the month
     * @return data for a pie chart
     */
    public static ObservableList<PieChart.Data> numberOfAppointmentsByTypeAndMonth(LocalDateTime monthStart, LocalDateTime monthEnd) {
        monthStart = TimeController.getUtcDatetime(monthStart);
        monthEnd = TimeController.getUtcDatetime(monthEnd).minusMinutes(1);
        String query = "SELECT Type, COUNT(Appointment_ID) FROM client_schedule.appointments WHERE Start BETWEEN ? AND ? GROUP BY Type ORDER BY COUNT(Appointment_ID) DESC;";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            preparedStatement.setString(1, monthStart.format(TimeController.timestampFormatter));
            preparedStatement.setString(2, monthEnd.format(TimeController.timestampFormatter));
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                pieChartData.add(new PieChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }

            if(pieChartData.isEmpty()) {
                pieChartData.add(new PieChart.Data("No appointments scheduled for this month", 0));
            }

            return pieChartData;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}




//        f.  Write code that generates accurate information in each of the following reports and will display the reports in the user interface:
//
//
//        Note: You do not need to save and print the reports to a file or provide a screenshot.
//
//
//        •  the total number of customer appointments by type and month
//
//        •  a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID
//
//        •  an additional report of your choice that is different from the two other required reports in this prompt and from the user log-in date and time stamp that will be tracked in part C