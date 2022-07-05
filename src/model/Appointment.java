package model;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public Appointment(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    //    public Appointment(String appointmentID,
//                       String title,
//                       String description,
//                       String location,
//                       String type,
//                       LocalDate startDate,
//                       String startTime,
//                       LocalDate endDate,
//                       String endTime,
//                       String customerID,
//                       String userID,
//                       String contactID) {
//        this.appointmentID = appointmentID;
//        this.title = title;
//        this.description = description;
//        this.location = location;
//        this.type = type;
//        this.startDate = startDate;
//        this.startTime = startTime;
//        this.endDate = endDate;
//        this.endTime = endTime;
//        this.customerID = customerID;
//        this.userID = userID;
//        this.contactID = contactID;
//    }
//
//    public Appointment(LocalDateTime start, LocalDateTime end) {
//        this.start = start;
//        this.end = end;
//    }
//
//    public String getAppointmentID() {
//        return appointmentID;
//    }
//
//    public void setAppointmentID(String appointmentID) {
//        this.appointmentID = appointmentID;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public LocalDate getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(LocalDate startDate) {
//        this.startDate = startDate;
//    }
//
//    public LocalDateTime getStart() {
//        return start;
//    }
//
//    public void setStart(LocalDateTime start) {
//        this.start = start;
//    }
//
//    public String getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(String startTime) {
//        this.startTime = startTime;
//    }
//
//    public LocalDate getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(LocalDate endDate) {
//        this.endDate = endDate;
//    }
//
//    public LocalDateTime getEnd() {
//        return end;
//    }
//
//    public void setEnd(LocalDateTime end) {
//        this.end = end;
//    }
//
//    public String getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(String endTime) {
//        this.endTime = endTime;
//    }
//
//    public String getCustomerID() {
//        return customerID;
//    }
//
//    public void setCustomerID(String customerID) {
//        this.customerID = customerID;
//    }
//
//    public String getUserID() {
//        return userID;
//    }
//
//    public void setUserID(String userID) {
//        this.userID = userID;
//    }
//
//    public String getContactID() {
//        return contactID;
//    }
//
//    public void setContactID(String contactID) {
//        this.contactID = contactID;
//    }
}

