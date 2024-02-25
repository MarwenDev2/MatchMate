package controller;

import Service.EventService;
import Service.ImageService;
import entities.Evenement;
import entities.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ShowEventDetails implements Initializable {

    @FXML
    private Button DeleteButton;

    @FXML
    private Button EditButton;


    @FXML
    private Label EndDateLabel;

    @FXML
    private Button HomeButton;

    @FXML
    private Label NameLabel;

    @FXML
    private Label ParticipLabel;

    @FXML
    private Label PriceLabel;

    @FXML
    private Label StartDateLabel, idClub;
    Evenement e;
    @FXML
    private ImageView imageView;







    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventService es = new EventService();
        ImageService imageService = new ImageService();
            e = es.readById(SharedEventData.getEventId());
            NameLabel.setText(e.getName());
            StartDateLabel.setText(e.getDateDeb().toString());
            EndDateLabel.setText(e.getDateFin().toString());
            ParticipLabel.setText(String.valueOf(e.getNbrParticipant()));
            PriceLabel.setText(String.valueOf(e.getPrice()));
            idClub.setText(String.valueOf(e.getIdClub().getId()));

            int imageid = imageService.getImageIdByEventId(e.getId());
            Image i = imageService.readById(imageid);
            if (i != null) {
                // Assuming i.getUrl() returns the correct file path
                String imageUrl = "file:///" + i.getUrl().replace("\\", "/"); // Prepend "file:///" and replace backslashes with forward slashes
                javafx.scene.image.Image javafxImage = new javafx.scene.image.Image(imageUrl);
                imageView.setImage(javafxImage);
            } else {
                // Handle the case when no image is found for the event
                System.out.println("No image found for the event.");
            }

    }


    public void HandleDelete(javafx.event.ActionEvent actionEvent) throws SQLException {
        ImageService imageService=new ImageService();
        EventService eventService=new EventService();
        Evenement evenement =new Evenement();
        Image image=new Image();
        int imageid;
        evenement =eventService.readById(SharedEventData.getEventId());
        imageid=imageService.getImageIdByEventId(evenement.getId());
        image=imageService.readById(imageid);
        imageService.delete(image);
        eventService.Delete(evenement);


    }


    public void GoToScene(ActionEvent actionEvent) {
        try {
            UpdateEvent.getClub(e.getIdClub());
            // Load the Event.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
            Parent root = loader.load();

            // Show the Event.fxml scene
            Stage stage = (Stage) HomeButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}



