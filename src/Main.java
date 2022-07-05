import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Alexander Padilla
 */
public class Main extends Application {

    /**
     * sets main scene to start
     *
     * @param primaryStage the first stage on start
     * @throws Exception if an exception occurs
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("./view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * This is the first method that gets called when the user runs the program.
     *
     * @param args string array of command line arguments
     * */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
