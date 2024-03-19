package controllers;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ClubDAO;
import services.ReservationDAO;
import services.StadiumDAO;
import services.UserDAO;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NewReservationController {

    @FXML
    private ListView availableTimesList;
    @FXML
    private Button returnButton;
    @FXML
    private Label reservationsButton;
    @FXML
    private Text year;
    @FXML
    private Text month;
    @FXML
    private FlowPane calendar;
    ZonedDateTime dateFocus;
    ZonedDateTime today;
    private Time startTime; // Example start time
    private Time endTime; // Example end time
    private final int matchDuration = 90; // Example match duration in minutes
    private final int breakDuration = 15;
    private String ref = SharedData.getStadiumref();
    private int idClub = SharedData.getClubId();
    private Club club;
    private ClubDAO cd;
    private Stadium stadium;
    private StadiumDAO sd;
    private UserDAO userDAO;
    private User user;
    private ReservationDAO rd;
    private List<Reservation> availablesReservations;
    private LocalDate reserv;
    private Reservation res ;


    public NewReservationController() {
        rd = new ReservationDAO();
        cd = new ClubDAO();
        sd = new StadiumDAO();
        user=new User();
        res = new Reservation();
        availablesReservations = new ArrayList<>();
        userDAO = new UserDAO();
    }

    public void initialize() {
        res = rd.findById(SharedData.getIdRes());
        user = userDAO.findById(SharedData.getIdUser());
        System.out.println(SharedData.getIdUser());
        System.out.println("dddd"+user);
        stadium=sd.findById(ref);

        club = cd.findById(idClub);
        startTime = club.getStartTime();
        endTime = club.getEndTime();

        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();


        returnButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Reservation/ViewReservation.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) returnButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        reservationsButton.setOnMouseClicked(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Reservation/ViewReservation.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) reservationsButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    private void drawCalendar() {
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        LocalDate currentDate = LocalDate.now();

        int monthMaxDate = dateFocus.getMonth().maxLength();
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j + 1) + (7 * i);
                if (calculatedDate > dateOffset) {
                    int currentDayOfMonth = calculatedDate - dateOffset;
                    if (currentDayOfMonth <= monthMaxDate) {
                        Text date = new Text(String.valueOf(currentDayOfMonth));
                        double textTranslationY = - (rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);

                        LocalDate selectedDate = LocalDate.of(dateFocus.getYear(), dateFocus.getMonthValue(), currentDayOfMonth);
                        this.reserv=selectedDate;
                        System.out.println(reserv);
                        if (selectedDate.isBefore(currentDate)) {
                            // Disable dates before the current day
                            rectangle.setFill(Color.GRAY);
                            stackPane.setDisable(true);
                        } else {
                            stackPane.setOnMouseClicked(e -> displayReservations(selectedDate));
                        }
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }


    private List<String> calculateAvailableTimes(LocalDate selectedDate) {
        LocalTime startTimeLocal = startTime.toLocalTime();
        LocalTime endTimeLocal = endTime.toLocalTime();

        List<String> availableTimes = new ArrayList<>();
        LocalTime currentTime = startTimeLocal;

        // Get reservations for the selected date
        List<Reservation> reservationsForDate = availablesReservations.stream()
                .filter(reservation -> reservation.getDate().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());

        while (currentTime.plusMinutes(matchDuration).isBefore(endTimeLocal.plusMinutes(matchDuration))) {
            LocalTime endTimeOfMatch = currentTime.plusMinutes(matchDuration);

            // Check if this time slot conflicts with any existing reservation
            boolean conflict = false;
            for (Reservation reservation : reservationsForDate) {
                LocalTime resStartTime = reservation.getStartTime().toLocalTime();
                LocalTime resEndTime = reservation.getEndTime().toLocalTime();
                if ((currentTime.equals(resStartTime) || endTimeOfMatch.equals(resStartTime)) ||
                        (currentTime.equals(resEndTime) || endTimeOfMatch.equals(resEndTime)) ||
                        (currentTime.isAfter(resStartTime) && currentTime.isBefore(resEndTime)) ||
                        (endTimeOfMatch.isAfter(resStartTime) && endTimeOfMatch.isBefore(resEndTime)) ||
                        (currentTime.isBefore(resStartTime) && endTimeOfMatch.isAfter(resEndTime))) {
                    conflict = true;
                    break;
                }
            }
            // If there's no conflict, add this time slot
            if (!conflict) {
                availableTimes.add(currentTime + " -> " + endTimeOfMatch); // Format the time slot
            }
            currentTime = endTimeOfMatch.plusMinutes(breakDuration);
        }
        return availableTimes;
    }
    /* OLD WAY
    private void displayReservations1(LocalDate selectedDate) {
        availablesReservations=rd.findFilteredReservations(ref,-1,null,Date.valueOf(selectedDate));
        System.out.println(availablesReservations);
        List<String> availableTimes = calculateAvailableTimes(selectedDate);
        System.out.println("hello"+ selectedDate);
        this.reserv=selectedDate;
        ObservableList<String> observableList = FXCollections.observableArrayList(availableTimes);
        availableTimesList.setItems(observableList); // Assuming availableTimesList is the ListView
    }*/
    private void displayReservations(LocalDate selectedDate) {
        availablesReservations = rd.findFilteredReservations(ref, -1, null, Date.valueOf(selectedDate));
        System.out.println(availablesReservations);

        List<String> availableTimes = calculateAvailableTimes(selectedDate);
        System.out.println("hello" + selectedDate);
        this.reserv = selectedDate;

        availableTimesList.getItems().clear(); // Clear previous items

        for (String time : availableTimes) {
            VBox card = new VBox();
            card.getStyleClass().add("card");
            card.setPadding(new Insets(10));
            card.setSpacing(5);
            card.setAlignment(Pos.CENTER);

            Label timeLabel = new Label(time);
            timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            timeLabel.setTextFill(Color.BLACK); // Set text color to black
            timeLabel.setAlignment(Pos.CENTER);

            card.getChildren().add(timeLabel);

            card.setOnMouseClicked(event -> {
                // Handle selection of the time slot
                System.out.println("Selected time: " + time);
                // Reset background color of all cards
                for (Object item : availableTimesList.getItems()) {
                    Node node = (Node) item;
                    node.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");
                }
                // Change background color of the selected card
                card.setStyle("-fx-background-color: #007BFF;"); // Blue color
                // You can perform further actions here, such as enabling a "Book Now" button
            });

            availableTimesList.getItems().add(card);
        }
    }


    @FXML
    public void validateReservation() {
        LocalDate selectedDate = dateFocus.toLocalDate(); // Use the selected date from the calendar
        ObservableList<Node> selectedCards = availableTimesList.getSelectionModel().getSelectedItems();
        if (selectedDate == null || selectedCards.isEmpty()) {
            showAlert("Error", "Please select both a date and time slots to make a reservation.", Alert.AlertType.ERROR);
            return;
        }
        else {
            Payment p  = new Payment(stadium.getPrice(),"reservation",user);
            // Proceed with payment
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Payment Confirmation");
            confirmationAlert.setHeaderText("Proceed with Payment");
            confirmationAlert.setContentText("Do you want to proceed with the payment for the reservation?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Navigate to the payment screen
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Payment/Payment.fxml"));
                    Parent paymentRoot = loader.load();
                    PaymentProcess paymentController = loader.getController();
                    /// paymentController.setReservationDetails(selectedDate, selectedCards); // Pass reservation details to the payment controller
                    Scene paymentScene = new Scene(paymentRoot);
                    Stage stage = (Stage) returnButton.getScene().getWindow();
                    stage.setScene(paymentScene);
                    stage.show(); // Wait for the payment to complete
                    if (paymentController.isPaymentSuccessful()) {
                        saveReservation(selectedCards); // Save the reservation if payment is successful
                    } else {
                        showAlert("Error", "Payment was not successful. Please try again.", Alert.AlertType.ERROR);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load payment screen.", Alert.AlertType.ERROR);
                }
            }
        }
    }

    private void saveReservation(ObservableList<Node> selectedCards) {
        for (Node selectedCard : selectedCards) {
            Label timeLabel = (Label) ((VBox) selectedCard).getChildren().get(0); // Get the time label from the VBox
            String selectedTime = timeLabel.getText(); // Retrieve the time text
            LocalTime startTime = LocalTime.parse(selectedTime.split(" -> ")[0]);
            LocalTime endTime = LocalTime.parse(selectedTime.split(" -> ")[1]);

            Reservation reservation = new Reservation(user, stadium, Date.valueOf(reserv), Time.valueOf(startTime), Time.valueOf(endTime), "assigned");
            System.out.println(reservation);
            boolean saved = rd.save(reservation); // Save the reservation to the database
            if (saved) {
                System.out.println("Reservation successfully saved.");
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/Reservation/ViewReservation.fxml"));
                    Scene scene = new Scene(root, 1300, 660);
                    Stage stage = (Stage) returnButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Failed to save reservation.");
            }
        }
        showAlert("Success", "Reservation successfully made.", Alert.AlertType.INFORMATION);
    }

    private void navigateToViewReservation() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Reservation/ViewReservation.fxml"));
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
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
