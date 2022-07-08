package model;

/**
 *
 * @author Alexander Padilla
 */
public class Customer {
    private String customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String divisionID;

    /**
     * Customer constructor
     *
     * @param customerID the id of the specific customer
     * @param customerName the name of the customer
     * @param address the customer's address
     * @param postalCode the postal code of the customer's address
     * @param phone the customer's phone number
     * @param divisionID the division ID based on the location of the customer
     */
    public Customer(String customerID, String customerName, String address, String postalCode, String phone, String divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    /**
     * @return the customer's ID
     */
    public String getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID the customer's ID to set
     */
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    /**
     * @return the customer's name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customer's name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the customer's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the customer's address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the customer's postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the customer's postal code to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the customer's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the customer's phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the customer's division ID
     */
    public String getDivisionID() {
        return divisionID;
    }

    /**
     * @param divisionID the customer's division ID to set
     */
    public void setDivisionID(String divisionID) {
        this.divisionID = divisionID;
    }
}
