package model;

/**
 *
 * @author Alexander Padilla
 */

public class User {
    private int userID;
    private String userName;
    private String password;

    public User(int userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }


    /**
     * @return user's id number
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID user's id number to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName user's name to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password user's password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
