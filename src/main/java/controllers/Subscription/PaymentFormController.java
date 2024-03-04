package controllers.Subscription;

import entities.Offer;
import entities.SessionManager;
import entities.Subscription;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import services.Subscription.OfferDAO;
import services.Subscription.SubscriptionDAO;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Optional;

public class PaymentFormController {

    @FXML
    private Button bronzeButton;

    @FXML
    private Button silverButton;

    @FXML
    private Button goldButton;

    private OfferDAO offerDAO;
    private SubscriptionDAO subscriptionDAO;
    private User currentUser = SessionManager.getInstance().getCurrentUser();

    public PaymentFormController() {
        offerDAO = new OfferDAO();
        subscriptionDAO = new SubscriptionDAO();
    }

    @FXML
    private void selectBronze() {
        showSubscriptionConfirmation("Bronze");
    }

    @FXML
    private void selectSilver() {
        showSubscriptionConfirmation("Silver");
    }

    @FXML
    private void selectGold() {
        showSubscriptionConfirmation("Gold");
    }

    private void showSubscriptionConfirmation(String subscriptionType) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Subscription Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to subscribe to the " + subscriptionType + " plan?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            createSubscription(subscriptionType, currentUser.getId());
        } else {
            System.out.println(subscriptionType + " subscription canceled");
        }
    }

    private void createSubscription(String subscriptionType, int userId) {
        Offer offer = offerDAO.findByName(subscriptionType);
        if (offer != null) {
            // Get the current date
            Date startDate = new Date(Calendar.getInstance().getTimeInMillis());

            // Calculate the end date (30 days after the start date)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, 30);
            Date endDate = new Date(calendar.getTimeInMillis());

            // Create the subscription object
            Subscription subscription = new Subscription(new User(userId), offer, startDate, endDate);

            // Save the subscription
            boolean success = subscriptionDAO.save(subscription);
            if (success) {
                showAlert("Subscription Confirmation", subscriptionType + " subscription created successfully.");
                redirectToViewClub();
            } else {
                showAlert("Error", "Failed to create subscription.");
            }
        } else {
            showAlert("Error", "Offer not found.");
        }
    }

    private void showAlert(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void redirectToViewClub() {
        try {
            // Load the view club FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClub/ViewClub.fxml"));
            Parent root = loader.load();

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(root);

            // Get the stage from the bronze button (or any other button)
            Stage stage = (Stage) bronzeButton.getScene().getWindow();

            // Set the scene on the stage and show the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
