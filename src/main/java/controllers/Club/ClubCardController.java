package controllers.Club;

import controllers.SharedData;
import entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import services.Club.ClubDAO;
import services.Image.ImageDAO;

import java.io.IOException;
import java.util.List;

public class ClubCardController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button viewStadiumsButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Label governorateLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Label stadiumNbrLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView clubImageView;
    @FXML
    private Button editbtn;
    @FXML
    private Button deletebtn;



    private Club club;
    private ClubDAO clubDAO;
    private ImageDAO imageDAO;
    private User currentUser = SessionManager.getInstance().getCurrentUser();

    public void initialize() {
        clubDAO = new ClubDAO();
        imageDAO = new ImageDAO();

        if(currentUser.getRole()== Role.player){
            editbtn.setVisible(false);
            deletebtn.setVisible(false);
        } else {
            editbtn.setVisible(true);
            deletebtn.setVisible(true);
        }

    }

    public void setData(Club club) {
        this.club = club;
        nameLabel.setText(club.getName());
        governorateLabel.setText(String.valueOf(club.getGovernorate()));
        cityLabel.setText(String.valueOf(club.getCity()));
        startTimeLabel.setText(club.getStartTime().toString());
        endTimeLabel.setText(club.getEndTime().toString());
        stadiumNbrLabel.setText(String.valueOf(club.getStadiumNbr()));
        descriptionLabel.setText(club.getDescription());
        System.out.println(club.getId());
        List<Image> images = imageDAO.findByObjectId(club.getId(), "club");
        if (!images.isEmpty()) {
            // Get the first image URL (assuming you store multiple images for a club)
            String imageUrl = images.get(0).getUrl(); // Change this accordingly if needed

            // Load the image and set it to the ImageView
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageUrl);
            clubImageView.setImage(image);
        }

        // Add an event handler to show "View stadiums" button when a card is clicked
        rootPane.setOnMouseClicked(event -> showViewStadiumsButton());
        viewStadiumsButton.setOnAction(event -> openViewStadiums(club.getId()));
    }

    private void showViewStadiumsButton() {
        viewStadiumsButton.setVisible(true);
    }

    @FXML
    private void viewStadiums() {
        if (club != null) {
            openViewStadiums(club.getId());
        }
    }

    private void openViewStadiums(int clubId) {
        try {
            SharedData.setClubId(clubId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStadium/ViewStadium.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) viewStadiumsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editClub() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewClub/NewClub.fxml"));
            BorderPane root = loader.load();
            NewClubController newClubController = loader.getController();
            newClubController.populateFieldsWithClubData(club);
            // Get the scene from any UI component within the current scene
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void deleteClub() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirm Delete");
        confirmationAlert.setContentText("Are you sure you want to delete the club?");

        // Add OK and Cancel buttons to the confirmation dialog
        confirmationAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the confirmation dialog and wait for user input
        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // User clicked OK, proceed with deletion
                boolean isDeleted = clubDAO.delete(club);
                if (isDeleted) {
                    // Remove the deleted club from the UI
                    rootPane.getChildren().clear(); // Clear the card from the parent container
                    reloadViewClubPage();
                    showAlert("Success", "Club deleted successfully.", Alert.AlertType.INFORMATION);

                } else {
                    showAlert("Error", "Failed to delete club.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    private void reloadViewClubPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClub/ViewClub.fxml"));
            Parent root = loader.load();

            // Get the controller
            ViewClubController controller = loader.getController();

            // Set any necessary data in the controller
            // For example, you might need to pass user information or other context

            // Replace the content of the current scene with the reloaded ViewClubPage
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
