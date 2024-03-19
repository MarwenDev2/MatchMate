package controllers.Reservation;

import controllers.SharedData;
import entities.Club;
import entities.Reservation;
import entities.Stadium;
import entities.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.Club.ClubDAO;

import services.Reservation.ReservationDAO;
import services.Stadium.StadiumDAO;
import services.User.UserDAO;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ViewReservationController {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button addButton;
    @FXML
    private Button chartButton;
    @FXML
    private ImageView player;
    @FXML
    private ImageView stadium;
    @FXML
    private Label viewClubsButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button resetButton;
    @FXML
    private DatePicker datepicker;
    @FXML
    private ComboBox<String> playersCombo;
    @FXML
    private ComboBox<String> stadiumsCombo;
    @FXML
    private ComboBox<String> typesCombo;
    @FXML
    private Button returnButton;
    private List<String> players ,stadiums, status = Arrays.asList("assigned", "completed", "canceled");
    private ReservationDAO rsDAO;
    private int userId=8 , idClub = SharedData.getClubId();
    private User user;
    private UserDAO usDAO;
    private String ref = SharedData.getStadiumref();
    private ClubDAO cd;
    private Club club;
    private Stadium s;
    private int selectedPlayerId ;
    private List<User> Players;
    private StadiumDAO sDAO;
    private  List<Reservation> reservations;

    public ViewReservationController() {
        rsDAO = new ReservationDAO();
        usDAO = new UserDAO();
        club = new Club();
        s=new Stadium();
        cd = new ClubDAO();
        sDAO = new StadiumDAO();
    }

    @FXML
    public void initialize() {
        System.out.println("Reference"+ref);
        s=sDAO.findById(ref);

        user = usDAO.findById(userId);
        club = cd.findById(idClub);
        System.out.println("Club"+club);
        System.out.println("User"+user);
        reservations = new ArrayList<>();
        players = new ArrayList<>();
        stadiums =  new ArrayList<>();

        for(Stadium s : sDAO.findAll())
            stadiums.add(s.getReference());
        stadiumsCombo.setItems(FXCollections.observableArrayList(stadiums));

        typesCombo.setItems(FXCollections.observableArrayList(status));

        if (user.getRole().equals("player")) {
            // If user is a player, hide player selection and disable adding reservations
            playersCombo.setVisible(false);
            player.setVisible(false);
            addButton.setVisible(true);
            stadiumsCombo.setVisible(false);
            stadium.setVisible(false);

            // Set selected player ID to current user's ID
            selectedPlayerId = userId;
        }
        else {
            stadiumsCombo.setVisible(true);
            stadium.setVisible(true);
            playersCombo.setVisible(true);
            player.setVisible(true);
            // If user is not a player, populate playersCombo with all players
            Players = usDAO.findAllByRole("player");
            for (User u : Players)
                players.add(u.getFirstName() + "  " + u.getLastName());
            playersCombo.setItems(FXCollections.observableArrayList(players));

            playersCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Get the selected player's ID based on the selected player's name
                    selectedPlayerId = Players.get(playersCombo.getSelectionModel().getSelectedIndex()).getId();
                }
            });
        }
        populateTableView();
        populateCards(reservations);

        searchButton.setOnAction(event -> searchReservations());
        resetButton.setOnAction(event -> resetFilters());

        addButton.setOnAction(event -> {
            try {
                SharedData.setIdUser(userId);
                System.out.println(userId);
                Parent root = FXMLLoader.load(getClass().getResource("/Reservation/NewReservation.fxml"));
                System.out.println(SharedData.getIdUser());
                Scene scene = new Scene(root, 1300.0, 660.0);
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        returnButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Stadium/ViewStadium/ViewStadium.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) returnButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        viewClubsButton.setOnMouseClicked(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Club/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) viewClubsButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        chartButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Chart.fxml"));
                Parent root = loader.load();
                StatisticsController statisticsController = loader.getController();
                // Pass any required data to the StatisticsController if needed
                // For example:
                statisticsController.setReservations(reservations);
                Scene scene = new Scene(root);
                Stage stage = (Stage) chartButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //searchField.setOnKeyReleased(this::handleSearchOnKeyPress);

    }
    private void populateTableView() {
        if(user.getRole().equals("player"))
            reservations = rsDAO.findFilteredReservations(ref,user.getId(),null,null);
        else
            reservations=rsDAO.findFilteredReservations(ref,-1,null,null);
    }

    private void searchReservations() {
        String selectedType = typesCombo.getValue();
        String selectedStadium = stadiumsCombo.getValue();
        Date selectedDate = (datepicker.getValue() != null) ? Date.valueOf(datepicker.getValue()) : null;
        System.out.println(selectedPlayerId);

        // Call the appropriate method in ReservationDAO to retrieve filtered reservations
        List<Reservation> filteredReservations = rsDAO.findFilteredReservations(selectedStadium, selectedPlayerId, selectedType, selectedDate);
        populateCards(filteredReservations);
    }

    private void resetFilters() {
        playersCombo.setValue(null);
        typesCombo.setValue(null);
        datepicker.setValue(null);

        // Reset TableView to show all reservations
        populateTableView();
        populateCards(reservations);
    }
    private void populateCards(List<Reservation> reservations) {
        HBox cardsContainer = new HBox(); // Use HBox instead of VBox
        cardsContainer.setSpacing(20); // Spacing between cards

        System.out.println(reservations);

        for (Reservation res : reservations) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/ReservationCard.fxml"));
                AnchorPane card = loader.load();
                System.out.println(res);
                // Accessing controller of the card
                ReservationCardController cardController = loader.getController();
                cardController.setData(res); // Pass club data to the card controller
                System.out.println(cardController);

                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scrollPane.setContent(cardsContainer);
    }
}