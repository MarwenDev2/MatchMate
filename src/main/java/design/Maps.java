package design;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Maps extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Load Google Maps HTML file
        webEngine.load(getClass().getResource("/Maps.html").toExternalForm());

        // Enable JavaScript communication
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javafx", new JavaBridge());
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Google Maps Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Java class to bridge JavaScript and Java code
    public class JavaBridge {
        // Method to receive location data from JavaScript
        public void printLocation(String location) {
            System.out.println("Selected location: " + location);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
