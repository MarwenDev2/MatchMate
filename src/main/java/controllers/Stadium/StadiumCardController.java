package controllers.Stadium;

import controllers.SharedData;
import entities.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.Image.ImageStadiumDAO;
import services.Notification.NotificationDAO;
import services.Stadium.StadiumDAO;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class StadiumCardController implements Initializable {

    @FXML
    private ImageView heartImageView;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView stadiumImageView;
    @FXML
    private Label referenceLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label widthLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label rateLabel;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button likeButton;
    @FXML
    private ImageView blackImage;

    @FXML
    private ImageView regularHeartImageView;
    @FXML
    private ImageView redHeartImageView;
    private User currentUser = SessionManager.getInstance().getCurrentUser();
    private Stadium stadium;
    private ImageStadiumDAO imageStadiumDAO;
    private List<Image> images;
    private int currentIndex = 0;
    private Timeline timeline;
    private StadiumDAO stadiumDAO;

    private NotificationDAO notification;


    public StadiumCardController() {
        imageStadiumDAO = new ImageStadiumDAO();
        stadiumDAO = new StadiumDAO();
        notification = new NotificationDAO();
    }






    public void setData(Stadium stadium) {


        this.stadium = stadium;
        referenceLabel.setText(stadium.getReference());
        heightLabel.setText(String.valueOf(stadium.getHeight()));
        widthLabel.setText(String.valueOf(stadium.getWidth()));
        priceLabel.setText(String.valueOf(stadium.getPrice()));
        rateLabel.setText(String.valueOf(stadiumDAO.getStadiumsLikes(stadium.getReference())));
        // Load and display images
        images = imageStadiumDAO.findByIDStadium(stadium.getReference(), "Stadium");
        startSlideShow();


        // Add event handlers for edit and delete buttons
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete(event));

        if (currentUser.getRole()==Role.Admin || currentUser.getRole()==Role.fieldOwner){
            editButton.setVisible(true);
            deleteButton.setVisible(true);
            likeButton.setVisible(false);
        }
        else
            likeButton.setVisible(true);


    }

    private void startSlideShow() {
        if (images.isEmpty()) {
            return;
        }

        // Display the first image immediately
        String firstImageUrl = images.get(0).getUrl();
        javafx.scene.image.Image firstImage = new javafx.scene.image.Image(firstImageUrl);
        stadiumImageView.setImage(firstImage);

        // Start the slideshow after 3 seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            currentIndex = (currentIndex + 1) % images.size();
            String imageUrl = images.get(currentIndex).getUrl();
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageUrl);
            stadiumImageView.setImage(image);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void HandleLike(ActionEvent event) {
        Like l = stadiumDAO.findAllLikes(stadium.getReference(), currentUser.getId());
        if (l != null) {
            // If already liked, then remove the like
            stadiumDAO.removeLike(l);
            showAlert("Success", "You have removed your like for this stadium.", Alert.AlertType.INFORMATION);
        } else {
            // If not liked, then add the like
            stadiumDAO.saveLike(new Like(stadium, currentUser));
            showAlert("Success", "Thank you for liking this stadium!", Alert.AlertType.INFORMATION);
        }

        // Update the like button based on the current status
        updateLikeButton();
    }
    private void updateLikeButton() {
        // Check if the current user has already liked the stadium
        Like l = stadiumDAO.findAllLikes(stadium.getReference(), currentUser.getId());
        if (l != null) {
            // The user has liked the stadium
            regularHeartImageView.setVisible(false);
            redHeartImageView.setVisible(true);
        } else {
            // The user has not liked the stadium
            regularHeartImageView.setVisible(true);
            redHeartImageView.setVisible(false);
        }
        rateLabel.setText(String.valueOf(stadiumDAO.getStadiumsLikes(stadium.getReference())));
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewStadium/NewStadium.fxml"));
            BorderPane root = loader.load();
            NewStadiumController newClubController = loader.getController();
            newClubController.populateFieldsWithStadium(stadium.getReference());
            // Get the scene from any UI component within the current scene
            rootPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDelete(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete this stadium?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User confirmed deletion
                deleteStadium();
            }
        });
    }

    private void deleteStadium() {
        // Perform deletion operation
        // For example, using a DAO class
        StadiumDAO stadiumDAO = new StadiumDAO();
        boolean deleted = stadiumDAO.delete(stadium);

        if (deleted) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Format the current date and time
            String formattedDateTime = LocalDateTime.now().format(formatter);
            Notification notification1 = new Notification(
                    stadium.getClub().getUser(),
                    "A stadium was successfully deleted for Club Name: " + stadium.getClub().getName() +
                            " on " + formattedDateTime);
            notification.save(notification1);
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStadium/ViewStadium.fxml"));
            try {
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Stadium successfully deleted
            // Close the card or update the UI accordingly
        } else {
            // Show an error message if deletion failed
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to delete the stadium. Please try again.");
            errorAlert.showAndWait();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Like l = stadiumDAO.findAllLikes(SharedData.getRefStd(), currentUser.getId());
        if (l != null) {
            // The user has liked the stadium
            regularHeartImageView.setVisible(false);
            redHeartImageView.setVisible(true);
        } else {
            // The user has not liked the stadium
            regularHeartImageView.setVisible(true);
            redHeartImageView.setVisible(false);
        }

    }
}
