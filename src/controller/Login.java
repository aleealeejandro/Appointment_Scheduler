package controller;

import databaseQueries.UserQuery;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 *
 * @author Alexander Padilla
 */
public class Login implements Initializable {
    private ResourceBundle resourceBundle;
    @FXML private BorderPane mainPanel;
    @FXML private Button loginButton;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label timezoneLabel;
    @FXML private Label localTimezoneLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    /**
     * uses initialize to initialize this scene
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = ResourceBundle.getBundle("language/language", Locale.getDefault());

        setTextFieldEventHandlers();
        usernameField.setText("test");
        passwordField.setText("test");
        getAndSetTimeZone();

        if(Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("fr")) {
            setUpStageAccordingToLanguage();
        }

        Logger.createFile();
    }

    /**
     * sets the event handlers for the form text fields
     */
    public void setTextFieldEventHandlers() {
        int maxCharacters = 50;
        usernameField.addEventFilter(KeyEvent.KEY_TYPED, TextFieldHandler.setMaxLengthAndPattern(maxCharacters, "regular"));
        passwordField.addEventFilter(KeyEvent.KEY_TYPED, TextFieldHandler.setMaxLengthAndPattern(maxCharacters, "password"));
    }
    /**
     * sets up the stage based on the systems' language default
     */
    public void setUpStageAccordingToLanguage() {
        usernameLabel.setText(resourceBundle.getString("username"));
        passwordLabel.setText(resourceBundle.getString("password"));
        loginButton.setText(resourceBundle.getString("login"));
        timezoneLabel.setText(resourceBundle.getString("time_zone"));
    }

    /**
     * gets timezone from system and sets timezone label in stage
     */
    public void getAndSetTimeZone() {
        ZoneId zoneID = ZoneId.systemDefault();
        TimeZone timeZoneObject = TimeZone.getTimeZone(zoneID);
        String timeZone = timeZoneObject.getID().replace('_', ' ');
        localTimezoneLabel.setText(timeZone);
    }

    /**
     * checks if text-fields are empty
     * @param username username text-field input
     * @param password password text-field input
     */
    private void checkIfFieldsNotEmpty(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) {
            Alert emptyFieldAlert = new Alert(Alert.AlertType.INFORMATION);
            emptyFieldAlert.setTitle(resourceBundle.getString("error_title"));
            emptyFieldAlert.setHeaderText(resourceBundle.getString("fields_empty_error_header"));
            emptyFieldAlert.setContentText(resourceBundle.getString("missing_input_message"));
            emptyFieldAlert.showAndWait();
        }
    }

    /**
     * event handler for when the login button is clicked
     */
    @FXML
    void loginButtonClicked() {
        checkIfFieldsNotEmpty(usernameField.getText(), passwordField.getText());

        try {
            int loginConfirmed =  UserQuery.checkUser(usernameField.getText(), passwordField.getText());
            String username = usernameField.getText();

            if(loginConfirmed > -1) {
                Logger.logLoginEntry(usernameField.getText(), passwordField.getText(), true);
                usernameField.clear();
                passwordField.clear();
                usernameField.setDisable(true);
                passwordField.setDisable(true);
                loginButton.setDisable(true);
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("../view/MainUI.fxml"));
                MainUIController.getUserData(loginConfirmed, username);
                Scene scene = new Scene(root);
//                stage.setTitle("Appointment Scheduler");
                stage.setScene(scene);
                stage.showAndWait();

                if(!stage.isShowing()) {
                    System.out.println("Logged out on window close");
                    Logger.logLogout(username);
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);
                    loginButton.setDisable(false);
                }

            } else {
                Logger.logLoginEntry(usernameField.getText(), passwordField.getText(), false);
                passwordField.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(resourceBundle.getString("error_title"));
                alert.setContentText(resourceBundle.getString("error_text"));
                alert.setHeaderText(resourceBundle.getString("error_header"));
                alert.showAndWait();
            }
        } catch (SQLException | NullPointerException | IOException err) {
            err.printStackTrace();
        }
    }
}
