package ch.nostromo.steamah;

import java.io.PrintWriter;

import java.io.StringWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Steam Achievement Hunter 
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class SteamahApp extends Application {

    public static final String APP_TITLE = "Steam Achievement Hunter 0.1";

    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Show exceptions 
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            StringWriter stackTrace = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stackTrace));
            showTextAreaDialog(AlertType.ERROR, "Uncaught Exception", "StackTrace", stackTrace.toString());
        });

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
       
        // Fire up the ui
        scene.getStylesheets().add("dark.css");
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Show any kind of information in an alert box / text area.
     * 
     * @param type AlertType
     * @param title Window title
     * @param label Description
     * @param text Content
     */
    public static void showTextAreaDialog(AlertType type, String title, String label, String text) {
        GridPane expandableContent = new GridPane();
        expandableContent.add(new Label(label), 0, 0);

        TextArea stText = new TextArea(text);
        GridPane.setVgrow(stText, Priority.ALWAYS);
        GridPane.setHgrow(stText, Priority.ALWAYS);
        expandableContent.add(stText, 0, 1);
        
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.getDialogPane().setExpandableContent(expandableContent);
        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setPrefSize(800, 600);
        alert.showAndWait();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
 
}