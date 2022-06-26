package databaseQueries;

import controller.MainUIController;
import controller.TimeController;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class AppointmentsQuery {
    public static ObservableList<Appointment> getAllAppointments(String filterBy, LocalDateTime timeNow, LocalDateTime dateTimeChosen) throws SQLException {
        String timeNowString = TimeController.getUtcDatetime(timeNow).format(TimeController.timestampFormatter);
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' ORDER BY Start;", timeNowString);

        if(filterBy != null) {
            switch (filterBy) {
                case "All":
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' ORDER BY Start;", timeNowString);
                    break;
                case "Month":
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.getFirstOfMonthDateTime(dateTimeChosen)) + "%", TimeController.getUtcDatetime(TimeController.getLastOfMonthDateTime(dateTimeChosen)) + "%");
                    break;
                case "Week":
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.getStartOfWeekDateTime(dateTimeChosen)) + "%", TimeController.getUtcDatetime(TimeController.getEndOfWeekDateTime(dateTimeChosen)) + "%");
                    break;
                case "Today":
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.openTime), TimeController.getUtcDatetime(TimeController.closeTime));
                    break;
                case "Day":
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND Start BETWEEN '%s' AND '%s' ORDER BY Start;", timeNowString, TimeController.getUtcDatetime(TimeController.getStartOfDayDateTime(dateTimeChosen)), TimeController.getUtcDatetime(TimeController.getStartOfDayDateTime(dateTimeChosen).plusHours(TimeController.amountOfHoursOfficeIsOpen)));
                    break;
                case "My Appointments":
                    query = String.format("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, a.Contact_ID, c.Contact_Name FROM client_schedule.appointments AS a INNER JOIN client_schedule.contacts AS c ON a.Contact_ID = c.Contact_ID WHERE End >= '%s' AND User_ID='%s' ORDER BY Start;", timeNowString, MainUIController.loggedInUserID);
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

    public static boolean appointmentOverlapsOrExists(LocalDateTime startDatetime, LocalDateTime endDatetime) {
//        change from SYSTEM to UTC LocalDateTime
        startDatetime = TimeController.getUtcDatetime(startDatetime);
        endDatetime = TimeController.getUtcDatetime(endDatetime);
        String startDatetimeString = startDatetime.format(TimeController.timestampFormatter);
        String endDatetimeString = endDatetime.format(TimeController.timestampFormatter);
        String query = "SELECT COUNT(Appointment_ID) FROM client_schedule.appointments WHERE (Start <= ? AND End >= ?) OR (Start <= ? AND End >= ?);";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            preparedStatement.setString(1, startDatetimeString);
            preparedStatement.setString(2, startDatetimeString);
            preparedStatement.setString(3, endDatetimeString);
            preparedStatement.setString(4, endDatetimeString);
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
//            System.out.println(err);
            err.printStackTrace();
            return false;
        }
    }
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

    public static int checkIfAppointmentWithinFifteenMinutes(LocalDateTime timeNow, LocalDateTime fifteenMinutesFromNow) {
        timeNow = TimeController.getUtcDatetime(timeNow);
        fifteenMinutesFromNow = TimeController.getUtcDatetime(fifteenMinutesFromNow);
        String timeNowString = timeNow.format(TimeController.timestampFormatter);
        String fifteenMinutesFromNowString = fifteenMinutesFromNow.format(TimeController.timestampFormatter);
        String query = "SELECT COUNT(Appointment_ID) FROM client_schedule.appointments WHERE Start BETWEEN ? AND ?;";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(query);
            preparedStatement.setString(1, timeNowString);
            preparedStatement.setString(2, fifteenMinutesFromNowString);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;

            while(resultSet.next()) {
                count = resultSet.getInt(1);
            }

            // returns true or false
            return count;
        } catch(SQLException e) {
            e.printStackTrace();
            return -1;
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