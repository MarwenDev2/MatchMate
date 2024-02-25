package controller;
import  entities.*;
import Service.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class NewEvent  implements Initializable {
    ObservableList<Evenement> list;

    EventService eventService;

    @FXML
    private Button AddEvent;
    @FXML
    private Button AddButton;

    @FXML
    private TableColumn<Evenement, Integer> ParticipantColumn;
    @FXML
    private TableColumn<Evenement, Integer> ClubColumn;

    @FXML
    private TableColumn<Evenement, Date> EndColumn;
    @FXML
    private TableColumn<Evenement, Float> PriceColumn;

    @FXML
    private TableColumn<Evenement, Integer> IdColumn;

    @FXML
    private TableColumn<Evenement, String> ImageColumn;

    @FXML
    private TableColumn<Evenement, String> NameColumn;

    @FXML
    private TableColumn<Evenement, Date> StartColumn;

    @FXML
    private TableView<Evenement> TableView;

    @FXML
    private TableColumn<Evenement, String> UserColumn;

    @FXML
    private Button DetailsButton;


    public static Club club;

    @FXML
    private Button showButton;

    @FXML
    private Button ButtonUpdate;
    Evenement e;
    private int clubId;
    public int getClubId() {
        return clubId;
    }
    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NewEvent.getClub(club);
        eventService = new EventService();
        list = eventService.ReadAll(SharedData.getClubId());
        e = eventService.readById(SharedEventData.getEventId());
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        StartColumn.setCellValueFactory(new PropertyValueFactory<>("DateDeb"));
        EndColumn.setCellValueFactory(new PropertyValueFactory<>("DateFin"));
        ClubColumn.setCellValueFactory(new PropertyValueFactory<>("idClub"));
        ParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("nbrParticipant"));
        PriceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        TableView.setItems(list);

        DetailsButton.setOnAction(event -> {
            Evenement selectedEvenement = TableView.getSelectionModel().getSelectedItem();
            if (selectedEvenement != null) {
                GoToDetails(selectedEvenement.getId());
            }
        });
    }


    @FXML
    void GoToAddScene(ActionEvent event) {
        try {
            // Load the Event.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();

            // Show the Event.fxml scene
            Stage stage = (Stage) AddEvent.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void RowClicked() {
        Evenement clickedEvenement = TableView.getSelectionModel().getSelectedItem();
        if (clickedEvenement != null) {
            SharedData.setClubId(clickedEvenement.getId());
        }
    }



    private static void getClub(Club idClub) {
    }


    @FXML
    void GoToDetails(int eventid) {

        try {
            SharedEventData.setEventId(eventid);
            System.out.println(eventid);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDetails.fxml"));
            Parent root = loader.load();

            // Load the CSS file/ Create a new scene with the loaded root and set the CSS stylesheet
            Scene scene = new Scene(root);

            // Get the stage and set the new scene
            Stage stage = (Stage) DetailsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void GoToDetails(ActionEvent actionEvent) {
    }
}

