package controllers.Authentification;

import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FailedController {
    @FXML
    private Label paymen_txt;
    @FXML
    private Button back_btn;

    private Reservation reservation;

    public void initialize(URL url, ResourceBundle rb) {
        this.reservation = new Reservation();
    }
    public void setData(Reservation r) {
        String value;
        this.reservation = r;
        value = "There's an Error in your online payment for your Reservation of the Staduim : " + r.getStadium().getReference() + ",";
        paymen_txt.setText(value);

    }
    @FXML
    private void redirectToListReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/ViewReservationPlayer.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
