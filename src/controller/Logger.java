package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class Logger {
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