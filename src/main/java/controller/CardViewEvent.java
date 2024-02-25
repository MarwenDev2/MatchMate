package controller;

import Service.EventService;
import Service.ImageService;
import Service.ParticipantService;
import entities.Evenement;
import entities.Image;
import entities.Participants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.naming.Name;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CardViewEvent implements Initializable {
    @FXML
    private AnchorPane CardForm;

    @FXML
    private Label EndDateLabel;

    @FXML
    private ImageView ImageEvent;

    @FXML
    private Label NameLabel;

    @FXML
    private Button ParticipateButton;

    @FXML
    private Label ParticpatorsLabel;

    @FXML
    private Label PriceLabel;

    @FXML
    private Label Start_Date;
    private Evenement event;
    public void setdata(Evenement event) {
        List<Evenement> events = new ArrayList<>();
        EventService eventService = new EventService();
        events = eventService.ReadAll();

        ImageService imageService = new ImageService();
        Image ie = new Image();
        int id = imageService.getImageIdByEventId(event.getId());

        ie = imageService.readById(id);
        if (event != null) {
            this.event = event;
            System.out.println(event);
            NameLabel.setText(event.getName());
            PriceLabel.setText(String.valueOf(event.getPrice()));
            ParticpatorsLabel.setText(String.valueOf(event.getNbrParticipant()));
            Start_Date.setText(String.valueOf(event.getDateDeb()));
            EndDateLabel.setText(String.valueOf(event.getDateFin()));
            javafx.scene.image.Image image = new javafx.scene.image.Image("File:" + ie.getUrl(), 94, 180, false, true);
            ImageEvent.setImage(image);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void AddParticipation(ActionEvent actionEvent) {
        Participants p1=new Participants(1,event);
        System.out.println(event);
        ParticipantService participantService=new ParticipantService();
        participantService.add_Participant(p1);

    }
}
