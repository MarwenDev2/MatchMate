package controller;

import Service.*;
import entities.Evenement;
import entities.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.DataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
public class AddEvent {
    @FXML
    private DatePicker DateDeb;
    @FXML
    private TextField NbreParticipant;

    @FXML
    private TextField PriceField;
    @FXML
    private Label EndDateLabel;
    @FXML
    private Label StartDateLabel;
    @FXML
    private Label NameLabel;
    @FXML
    private Label ParticipantLabel;
    @FXML
    private DatePicker DateEnd;

    @FXML
    private Button AddButton;


    @FXML
    private ImageView ImageView;

    @FXML
    private Button PicButton;

    @FXML
    private Button ShowEventButton;

    @FXML
    private AnchorPane TextName;

    private Image selectedImage;
    @FXML
    private TextField NameField;
    int idEvent=0;
    int idImage=0;

    public boolean getErrors() {
        clearErrorLabels(); // Clear any previous error messages

        boolean hasErrors = false;

        // Validate NameField
        if (NameField.getText().isBlank()) {
            setNameError("Le nom est invalide");
            hasErrors = true;
        }

        // Validate Start Date and End Date
        LocalDate startDate = DateDeb.getValue();
        LocalDate endDate = DateEnd.getValue();

        if (startDate == null || endDate == null) {
            setStartDateError("Veuillez sélectionner une date de début et une date de fin");
            hasErrors = true;
        } else {
            LocalDate currentDate = LocalDate.now();
            if (startDate.isAfter(endDate)) {
                setEndDateError("La date de fin doit être postérieure à la date de début");
                hasErrors = true;
            }
            if (startDate.isBefore(currentDate)) {
                setStartDateError("La date de début doit être ultérieure à la date actuelle");
                hasErrors = true;
            }
        }

        // Validate Nombre Participant
        if (!Pattern.matches("\\d{1,2}", NbreParticipant.getText())) {
            setParticipantError("Le nombre de participants est invalide");
            hasErrors = true;
        }

        return hasErrors;
    }


    private void clearErrorLabels() {
        EndDateLabel.setText("");
        StartDateLabel.setText("");
        NameLabel.setText("");
        ParticipantLabel.setText("");
    }

    private void setNameError(String message) {
        NameLabel.setTextFill(Color.RED);
        NameLabel.setText(message);
    }

    private void setStartDateError(String message) {
        StartDateLabel.setTextFill(Color.RED);
        StartDateLabel.setText(message);
    }

    private void setEndDateError(String message) {
        EndDateLabel.setTextFill(Color.RED);
        EndDateLabel.setText(message);
    }

    private void setParticipantError(String message) {
        ParticipantLabel.setTextFill(Color.RED);
        ParticipantLabel.setText(message);
    }
    void RedirctToHome(ActionEvent event) {
        try {
            // Load the Event.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event.fxml"));
            Parent root = loader.load();

            // Show the Event.fxml scene
            Stage stage = (Stage) AddButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddEventButton(ActionEvent event) {
        if(!getErrors()) {
            String name = NameField.getText();
            LocalDate dateDeb = DateDeb.getValue();
            LocalDate dateEnd = DateEnd.getValue();
            int nbrPart = Integer.parseInt(NbreParticipant.getText());
            Float price;
            try {
                price = Float.parseFloat(PriceField.getText());
            } catch (NumberFormatException e) {
                // Handle the case where the price field is not a valid float
                // You may display an error message or take appropriate action
                return;
            }

            // Construct the Event object
              ClubDAO Dao=new ClubDAO();
            Evenement evenementObj = new Evenement(name, Date.valueOf(dateDeb), Date.valueOf(dateEnd), Dao.findById(SharedData.getClubId()), nbrPart,price); // Assuming 0 for idUser

            // Call the add method from EventService to add the event to the database
            EventService eventService = new EventService();
            int eventId = eventService.add(evenementObj);
            System.out.println("Event ID: " + eventId); // Print the ID of the newly added event

            // Check if an image is selected
            if (selectedImage != null) {
                // Call the add method from ImageService to add the image to the database
                ImageService imageService = new ImageService();
                int imageId = imageService.add(selectedImage);
                System.out.println("Image ID: " + imageId); // Print the ID of the newly added image

                // Add the IDs of the new event and image to the imageevent table
                eventService.addImage(eventId, imageId);

                // Commit changes to the database
                commitChanges();
            }

        }
        else
            System.out.println("error");
        RedirctToHome(event);

    }



    private void commitChanges() {
        Connection cnx= DataSource.getInstance().getCnx();
        try {
            // Commit the transaction
            cnx.commit();
            System.out.println("Changes committed successfully.");
        } catch (SQLException e) {
            System.out.println("Error committing changes: " + e.getMessage());
        }
    }


    @FXML
    void GoToHome(ActionEvent event) {
        try {
            // Load the Event.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event.fxml"));
            Parent root = loader.load();

            // Show the Event.fxml scene
            Stage stage = (Stage) ShowEventButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML

    void handleLoadImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImage = new Image(file.getName(), file.getAbsolutePath(), "event");
            javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
            ImageView.setImage(image);
        }
    }

}
