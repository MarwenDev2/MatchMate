package tests;
import Service.ParticipantService;
import entities.*;
import Service.EventService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MeuCardEvent.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Event");
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}