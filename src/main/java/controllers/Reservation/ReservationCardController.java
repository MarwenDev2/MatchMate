package controllers;


import entities.Image;
import entities.Reservation;
import entities.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import services.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
public class ReservationCardController {

    @FXML
    private Button validateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label playerLabel;
    @FXML
    private Label stadiumLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView stadiumImageView;
    private final int userId=9;
    private User user;
    private UserDAO usDAO;
    private ImageStadiumDAO imageDAO;
    private ReservationDAO resDAO;
    private List<Image> images;
    private boolean isBefore;
    private int currentIndex = 0;
    private Timeline timeline;

    public void initialize() {
        imageDAO = new ImageStadiumDAO();
        resDAO = new ReservationDAO();
        usDAO = new UserDAO();
        images = new ArrayList<>();
        user = usDAO.findById(userId);
    }

    public void setData(Reservation reservation) {
        playerLabel.setText(reservation.getPlayer().getFirstName()+"    "+reservation.getPlayer().getLastName());
        stadiumLabel.setText(String.valueOf(reservation.getStadium().getReference()));
        dateLabel.setText(String.valueOf(reservation.getDate().toString()));
        startTimeLabel.setText(reservation.getStartTime().toString());
        endTimeLabel.setText(reservation.getEndTime().toString());
        descriptionLabel.setText(reservation.getType());

        images = imageDAO.findByIDStadium(reservation.getStadium().getReference(), "stadium");
        startSlideShow();

        isPastOrToday(reservation.getDate());

        if (user.getRole().equals("fieldOwner") && isBefore) {
            // Show the validate button if conditions are met
            validateButton.setVisible(true);
        } else {
            // Hide the validate button otherwise
            validateButton.setVisible(false);
        }
        validateButton.setOnAction(event -> validateReservation(reservation));

        if (user.getRole().equals("player") && isPastOrToday(reservation.getDate())) {
            deleteButton.setText("Cancel");
            deleteButton.setOnAction(event -> cancelReservation(reservation));
        } else if (isPastOrToday(reservation.getDate())) {
            deleteButton.setVisible(false);
        } else {
            deleteButton.setText("Delete");
            deleteButton.setOnAction(event -> deleteReservation(reservation));
        }
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

    public void validateReservation(Reservation r){
        if (r != null) {
            r.setType("completed");
            resDAO.update(r);
            setData(r);
            showAlert("Success", "Reservation validated successfully.", Alert.AlertType.INFORMATION);
        }
    }
    private boolean isPastOrToday(Date date) {
        java.util.Date today = new java.util.Date();
        isBefore = date.compareTo(new java.sql.Date(today.getTime())) >= 0;
        return isBefore;
    }
    private void cancelReservation(Reservation res) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirm Cancel");
        confirmationAlert.setContentText("Are you sure you want to cancel the reservation?");

        // Add OK and Cancel buttons to the confirmation dialog
        confirmationAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the confirmation dialog and wait for user input
        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // User clicked OK, proceed with cancellation
                res.setType("canceled");
                boolean isUpdated = resDAO.update(res);
                if (isUpdated) {
                    // Update the TableView after cancellation
                    showAlert("Success", "Reservation canceled successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to cancel reservation.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void deleteReservation(Reservation res) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirm Delete");
        confirmationAlert.setContentText("Are you sure you want to delete this reservation?");

        // Add OK and Cancel buttons to the confirmation dialog
        confirmationAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the confirmation dialog and wait for user input
        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // User clicked OK, proceed with deletion
                boolean isDeleted = resDAO.delete(res.getId());
                if (isDeleted) {
                    // Update the filtered data after deletion
                    // filteredData.getSource().remove(club);
                    showAlert("Success", "Reservation deleted successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to delete reservation.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}