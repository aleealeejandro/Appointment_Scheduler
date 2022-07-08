package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.util.Objects;

/**
 *
 * @author Alexander Padilla
 */
public class Logger {

    /**
     * creates a file if the file doesn't already exist
     */
    public static void createFile() {
        try {
            File file = new File("./login_activity.txt");
            if(file.createNewFile()) {
                System.out.printf("%s file created%n", file);
            }
//            else {
//                System.out.printf("%s already exists%n", file);
//            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * logs login entry into file
     *
     * @param username username input
     * @param password password input
     * @param succeeded logging on successfully boolean value
     */
    public static void logLoginEntry(String username, String password, boolean succeeded) {
        if(Objects.equals(username, "")) {
            username = "NULL";
        }
        if(Objects.equals(password, "")) {
            password = "NULL";
        }

        try {
            FileWriter logWriter = new FileWriter("./login_activity.txt", true);
            logWriter.write(String.format("USERNAME: %s  PASSWORD: %s  LOGGED_IN_SUCCESS: %s  DATETIME: %s\n", username, password, succeeded, LocalDateTime.now(ZoneOffset.UTC)));
            logWriter.close();
        } catch(IOException e) {
            System.out.println("error logging");
            e.printStackTrace();
        }
    }

    /**
     * logs logout entry into file
     *
     * @param username the logged-in user's username
     */
    public static void logLogout(String username) {
        try {
            FileWriter logWriter = new FileWriter("./login_activity.txt", true);
            logWriter.write(String.format("USERNAME: %s  LOGGED_OUT_DATETIME: %s\n", username, LocalDateTime.now(ZoneOffset.UTC)));
            logWriter.close();
        } catch(IOException e) {
            System.out.println("error logging");
            e.printStackTrace();
        }
    }
}