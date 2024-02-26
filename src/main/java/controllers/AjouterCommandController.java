package controllers;

import entities.Command;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import service.CommandService;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class AjouterCommandController {

    private final CommandService cs = new CommandService();

    @FXML
    private Button afficherC;

    @FXML
    private Button ajouterC;

    @FXML
    private DatePicker dateC;

    @FXML
    private TextField idUserC;

    @FXML
    private TextField refC;

    @FXML
    private TextField totalC;

    @FXML
    void afficherC(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherCommand.fxml"));
            refC.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }



    void ajouterC(ActionEvent event) {
        Command c = new Command(refC.getText(),dateC.getValue(),totalC.getText(), parseInt(idUserC.getText()));
        cs.add(c);
        System.out.println(c);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("success");
        alert.setContentText("commande ajoutée");
        alert.showAndWait();
    }


}