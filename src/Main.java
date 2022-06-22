//import databaseUtils.DatabaseConnection;
import helper.JDBC;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Main extends Application {
    private ResourceBundle resourceBundle;
    @FXML private BorderPane mainPanel;
    @FXML private Menu languageMenu;
    @FXML private CheckMenuItem englishMenuItem;
    @FXML private CheckMenuItem frenchMenuItem;
    @FXML private Button exitButton;
    @FXML private Button loginButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label timezoneLabel;
    @FXML private Label localTimezoneLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("./view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**this is the main method.
     * This is the first method that gets called when you run program.
     * */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
