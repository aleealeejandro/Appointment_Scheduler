package model;

/**
 * creates model for contact
 * @author Alexander Padilla
 */
public class Contact {
    private String  contactID;
    private String contactName;
    private String email;

    /**
     * Contact constructor
     *
     * @param contactID the id of the specific contact
     * @param contactName the name of the contact
     * @param email the email address of the contact
     */
    public Contact(String contactID, String contactName, String email) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.email = email;
    }

    /**
     * @return the contact's ID
     */
    public String getContactID() {
        return contactID;
    }

    /**
     * @param contactID the contact's ID to set
     */
    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    /**
     * @return the contact's name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName the contact's name to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the contact's email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
