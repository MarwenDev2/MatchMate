package controllers.Stadium;

import controllers.SharedData;
import entities.Role;
import entities.SessionManager;
import entities.Stadium;
import entities.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.Club.ClubDAO;
import services.Reservation.ReservationDAO;
import services.Stadium.StadiumDAO;
import services.Subscription.OfferDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ViewStadiumController {


    @FXML
    private Button backButton;
    @FXML
    private Button addButton;
    @FXML
    private Label viewClubsButton;

    private User currentUser = SessionManager.getInstance().getCurrentUser();

    private StadiumDAO stadiumDAO;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ComboBox<String> prizeCombo;

    @FXML
    private ComboBox<String> rateCombo;

    private int clubId;
    private ClubDAO clubDAO;
    private OfferDAO offerDAO;
    private ReservationDAO reservationDAO;

    private static final String DISTINCT_PRIZES_QUERY = "SELECT DISTINCT prize FROM offer";
    private static final String DISTINCT_RATES_QUERY = "SELECT DISTINCT rate FROM club";


    public ViewStadiumController() {
        stadiumDAO = new StadiumDAO();
        List<Stadium> stadiums = new ArrayList<>();
        clubDAO = new ClubDAO();
        offerDAO = new OfferDAO();
        reservationDAO = new ReservationDAO();
    }



    @FXML
    public void initialize() {
        clubId = SharedData.getClubId();

        // Load stadiums for the selected club
        loadStadiumsForClub(clubId);
        checkAndDisplayMaintenanceAlert();


        List<Integer> offerPrizes = stadiumDAO.getDistinctPrizes();
        List<String> prizeStrings = offerPrizes.stream().map(String::valueOf).collect(Collectors.toList());
        prizeCombo.setItems(FXCollections.observableArrayList(prizeStrings));

        // Populate rate ComboBox
        List<Integer> clubRates = stadiumDAO.getDistinctRates();
        List<String> rateStrings = clubRates.stream().map(String::valueOf).collect(Collectors.toList());
        rateCombo.setItems(FXCollections.observableArrayList(rateStrings));

        viewClubsButton.setOnMouseClicked(event -> {
            try {
                // Check for maintenance and display alert
                // Navigate to view clubs
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) viewClubsButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/NewStadium/NewStadium.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Set event handler for back button
        backButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (currentUser.getRole() == Role.fieldOwner || currentUser.getRole() == Role.Admin){
            addButton.setVisible(true);
        }

        prizeCombo.setOnAction(event -> searchStadiums());
        rateCombo.setOnAction(event -> searchStadiums());

    }

    private void searchStadiums() {
        // Get selected rate from rateCombo
        Integer selectedRate;
        String rateValue = rateCombo.getValue();
        if (rateValue != null && !rateValue.isEmpty()) {
            selectedRate = Integer.valueOf(rateValue);
        } else {
            selectedRate = null;
        }

// Get selected prize from prizeCombo
        Integer selectedPrize;
        String prizeValue = prizeCombo.getValue();
        if (prizeValue != null && !prizeValue.isEmpty()) {
            selectedPrize = Integer.valueOf(prizeValue);
        } else {
            selectedPrize = null;
        }

        // Get all stadiums from the database
        List<Stadium> allStadiums = stadiumDAO.findAllByClub(clubId);

        // Filter stadiums based on the selected rate and prize
        List<Stadium> filteredStadiums = allStadiums.stream()
                .filter(stadium -> (selectedRate == null || stadium.getRate() == selectedRate))
                .filter(stadium -> (selectedPrize == null || stadium.getPrice() == selectedPrize))
                .collect(Collectors.toList());

        // Update the view with the filtered stadiums
           populateStadiumCards(filteredStadiums);
    }

    private void loadStadiumsForClub(int clubId) {
        List<Stadium> stadiums;

        // Check if the current user is a field owner
        if (currentUser.getRole() == Role.fieldOwner) {
            // Get all stadiums owned by the field owner
            stadiums = stadiumDAO.findAllByClub(clubId);
         /*   System.out.println("test1"+stadiums.get(0).getMaintenance());

            // Check each stadium for maintenance
            for (Stadium stadium : stadiums) {
                // If the stadium has more than 2 reservations, set maintenance to true
                if (stadium.getMaintenance() == 1) {
                    // Notify the field owner
                    showAlert("Stadium Maintenance Alert", "Stadium " + stadium.getReference() +
                            " requires maintenance due to having more than two reservations.");
                    // Update the stadium in the database

                }
            } */
        } else {
            // If the current user is not a field owner, load stadiums as usual
            stadiums = stadiumDAO.findAllByClub(clubId);
        }

        // Populate the stadium cards in the view
        populateStadiumCards(stadiums);
    }
    private void populateStadiumCards(List<Stadium> stadiums) {
        HBox cardsContainer = new HBox(); // Use HBox to contain stadium cards
        cardsContainer.setSpacing(20); // Spacing between cards

        for (Stadium stadium : stadiums) {
            try {
                SharedData.setRefStd(stadium.getReference());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStadium/StadiumCard.fxml"));
                Parent card = loader.load();

                // Accessing controller of the card
                StadiumCardController cardController = loader.getController();
                cardController.setData(stadium); // Pass stadium data to the card controller

                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scrollPane.setContent(cardsContainer);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    private void checkAndDisplayMaintenanceAlert() {
        List<Stadium> stadiums = stadiumDAO.findAllByClub(clubId);
        for (Stadium stadium : stadiums) {
            if (stadium.getMaintenance() == 1) {
                // Display alert for maintenance
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Maintenance Alert");
                alert.setHeaderText("Maintenance Required");
                alert.setContentText("Stadium " + stadium.getReference() + " requires maintenance due to excessive reservations.");
                alert.show();
            }
        }

    }
}
