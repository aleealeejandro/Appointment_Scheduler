package model;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * creates model for appointment
 * @author Alexander Padilla
 */
public class Appointment {
    private String appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDate startDate;
    private LocalDateTime start;
    private String startTime;
    private LocalDate endDate;
    private LocalDateTime end;
    private String endTime;
    private String customerID;
    private String userID;
    private String contactID;
    private String contactName;

    /**
     * Appointment constructor
     *
     * @param appointmentID the id of the specific contact
     * @param title the appointment title
     * @param description the appointment title
     * @param location the location of the appointment
     * @param type the appointment type
     * @param startDate the start date of the appointment
     * @param startTime the start time of the appointment
     * @param endDate the end date of the appointment
     * @param endTime the end time of the appointment
     * @param customerID the customer ID associated with this appointment
     * @param userID the user ID associated with this appointment
     * @param contactID the contact ID associated with this appointment
     * @param contactName the contact name based on the contact ID
     */
    public Appointment(String appointmentID, String title, String description, String location, String type, LocalDate startDate, String startTime, LocalDate endDate, String endTime, String customerID, String userID, String contactID, String contactName) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * Appointment constructor
     *
     * @param appointmentID the id of the specific contact
     * @param title the appointment title
     * @param description the appointment title
     * @param location the location of the appointment
     * @param type the appointment type
     * @param startDate the start date of the appointment
     * @param startTime the start time of the appointment
     * @param endDate the end date of the appointment
     * @param endTime the end time of the appointment
     * @param customerID the customer ID associated with this appointment
     * @param userID the user ID associated with this appointment
     * @param contactID the contact ID associated with this appointment
     */
    public Appointment(String appointmentID, String title, String description, String location, String type, LocalDate startDate, String startTime, LocalDate endDate, String endTime, String customerID, String userID, String contactID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * Appointment constructor
     *
     * @param appointmentID the id of the specific contact
     * @param description the appointment title
     * @param type the appointment type
     * @param startDate the start date of the appointment
     * @param startTime the start time of the appointment
     * @param endDate the end date of the appointment
     * @param endTime the end time of the appointment
     * @param customerID the customer ID associated with this appointment
     */
    public Appointment(String appointmentID, String description, String type, LocalDate startDate, String startTime, LocalDate endDate, String endTime, String customerID) {
        this.appointmentID = appointmentID;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.customerID = customerID;
    }

    /**
     * Appointment constructor
     *
     * @param appointmentID the id of the specific contact
     * @param startDate the start date of the appointment
     * @param startTime the start time of the appointment
     * @param endDate the end date of the appointment
     * @param endTime the end time of the appointment
     * @param userID the user ID associated with this appointment
     */
    public Appointment(String appointmentID, LocalDate startDate, String startTime, LocalDate endDate, String endTime, String userID) {
        this.appointmentID = appointmentID;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.userID = userID;
    }

    /**
     * Appointment constructor
     *
     * @param start the start datetime of the appointment
     * @param end the end datetime of the appointment
     */
    public Appointment(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @return the appointment's ID
     */
    public String getAppointmentID() {
        return appointmentID;
    }

    /**
     * @param appointmentID the appointment's ID to set
     */
    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    /**
     * @return the appointment's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the appointment's title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the appointment's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the appointment's description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the appointment's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the appointment's location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the appointment's type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the appointment's type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the appointment's start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the appointment's start date to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the appointment's start datetime
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * @param start the appointment's start datetime to set
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * @return the appointment's start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the appointment's start time to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the appointment's end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the appointment's end date to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the appointment's end datetime
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * @param end the appointment's end datetime to set
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * @return the appointment's end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the appointment's end time to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the appointment's associated customer ID
     */
    public String getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID the appointment's associated customer ID to set
     */
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    /**
     * @return the appointment's user customer ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the appointment's associated user ID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the appointment's associated contact ID
     */
    public String getContactID() {
        return contactID;
    }

    /**
     * @param contactID the appointment's associated contact ID to set
     */
    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    /**
     * @return the appointment's associated contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName the appointment's associated contact name to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

}

