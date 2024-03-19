package controllers.Reservation;

import controllers.SharedData;
import entities.Club;
import entities.Reservation;
import entities.Stadium;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.Club.ClubDAO;
import services.Reservation.ReservationDAO;
import services.Stadium.StadiumDAO;
import services.User.UserDAO;
import java.io.IOException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.time.LocalDate;

public class View {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button addButton;
    @FXML
    private VBox reservationsBox;
    @FXML
    private Label viewClubsButton;
    private Button returnButton;
    @FXML
    private Button validateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ImageView deleteImage;
    @FXML
    private Text year;
    @FXML
    private Text month;
    @FXML
    private FlowPane calendar;
    ZonedDateTime dateFocus;
    ZonedDateTime today;
    private ReservationDAO rsDAO;
    private int userId=9 , idClub = SharedData.getClubId();
    private User user;
    private String ref = SharedData.getStadiumref();
    private ClubDAO cd;
    private Club club;
    private int selectedPlayerId ;
    private List<User> Players;
    private StadiumDAO sDAO;
    private boolean isBefore;
    private Stadium stadium;
    private UserDAO userDAO;
    private ReservationDAO rd;
    private List<Reservation> availablesReservations;
    private LocalDate reserv;
    private Reservation res ;

    private  List<Reservation> reservations;

    public View() {
        rsDAO = new ReservationDAO();
        userDAO = new UserDAO();
        club = new Club();
        cd = new ClubDAO();
        sDAO = new StadiumDAO();
        user = new User();
        rd = new ReservationDAO();
    }
    public void initialize() {
        res = rd.findById(SharedData.getIdRes());
        user = userDAO.findById(userId);
        System.out.println(SharedData.getIdUser());
        System.out.println("dddd"+user);
        stadium=sDAO.findById(ref);
        System.out.println(stadium);


        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();

    }
    @FXML
    void backOneMonth(javafx.event.ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }
    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    private void drawCalendar(){
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        int monthMaxDate = dateFocus.getMonth().maxLength();
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();
        LocalDate currentDate1 = LocalDate.now();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth =(calendarWidth/7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight/6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j+1)+(7*i);
                if(calculatedDate > dateOffset){
                    int currentDate = calculatedDate - dateOffset;
                    if(currentDate <= monthMaxDate){
                        Text dateText = new Text(String.valueOf(currentDate));
                        double textTranslationY = - (rectangleHeight / 2) * 0.75;
                        dateText.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(dateText);

                        LocalDate selectedDate = LocalDate.of(dateFocus.getYear(), dateFocus.getMonthValue(), currentDate);
                        if (!rd.findFilteredReservations(stadium.getReference(), -1, null, Date.valueOf(selectedDate)).isEmpty()) {
                            rectangle.setFill(Color.LIGHTBLUE); // Color the square if there are reservations for this date
                        }
                        stackPane.setOnMouseClicked(event -> {
                            displayReservations(selectedDate);
                        });
                        if (selectedDate.isBefore(currentDate1)) {
                            // Disable dates before the current day
                            rectangle.setFill(Color.GRAY);
                            stackPane.setDisable(true);
                        }
                    }
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }


    private void displayReservations(LocalDate selectedDate) {
        // Clear previous reservations
        reservationsBox.getChildren().clear();
        // Fetch reservations for the selected date
        List<Reservation> reservations = rd.findFilteredReservations(stadium.getReference(), -1, null, Date.valueOf(selectedDate));

        // Populate reservations in the VBox as cards
        for (Reservation reservation : reservations) {
            VBox card = createReservationCard(reservation);
            reservationsBox.getChildren().add(card);
        }
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox();
        card.getStyleClass().add("reservation-card");
        card.setPadding(new Insets(10));
        card.setSpacing(5);

        // Add reservation details to the card
        Label playerLabel = createLabelWithIcon("Player : " + reservation.getPlayer().getFirstName() +"   "+ reservation.getPlayer().getLastName(), "/Images/player.png");
        Label stadiumLabel = createLabelWithIcon("Stadium  : " + reservation.getStadium().getReference(), "/Images/stadium.png");
        Label startLabel = createLabelWithIcon("Start Time  : " + reservation.getStartTime(), "/Images/time.png");
        Label endLabel = createLabelWithIcon("End Time  : " + reservation.getEndTime(), "/Images/time.png");
        Label dateLabel = createLabelWithIcon("Date  : " + reservation.getDate(), "/Images/date.png");
        Label typeLabel = createLabelWithIcon("Type  : " + reservation.getType(), "/Images/status.png");

        Button deleteButton = new Button("Delete");
        if (isPastOrToday(reservation.getDate()) && !reservation.getType().equals("canceled")) {
            deleteButton.setText("Cancel");
            deleteButton.setOnAction(event -> cancelReservation(reservation));
        } else {
            deleteButton.setText("Delete");
            deleteButton.setOnAction(event -> deleteReservation(reservation));
        }

        card.getChildren().addAll(playerLabel, stadiumLabel, dateLabel, startLabel, endLabel, typeLabel, deleteButton);
        return card;
    }

    private Label createLabelWithIcon(String text, String iconPath) {
        Label label = new Label(text);
        Image iconImage = new Image(getClass().getResourceAsStream(iconPath));
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(16); // Set the width of the icon
        iconView.setFitHeight(16); // Set the height of the icon
        label.setGraphic(iconView); // Set the icon as graphic for the label
        return label;
    }

    private void populateTableView() {
            reservations = rsDAO.findFilteredReservations(ref,user.getId(),null,null);
    }
    public void validateReservation(Reservation r){
        if (r != null) {
            r.setType("completed");
            rd.update(r);
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
                boolean isUpdated = rd.update(res);
                if (isUpdated) {
                    // Update the TableView after cancellation
                    showAlert("Success", "Reservation canceled successfully.", Alert.AlertType.INFORMATION);
                    displayReservations(res.getDate().toLocalDate());
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
                boolean isDeleted = rd.delete(res.getId());
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
