package controllers.Club;

import controllers.SharedData;
import entities.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import org.controlsfx.control.Notifications;
import services.Club.ClubDAO;
import services.Image.ImageDAO;
import services.Notification.NotificationDAO;
import services.Stadium.StadiumDAO;
import services.Subscription.OfferDAO;
import services.Subscription.SubscriptionDAO;
import services.User.UserDAO;

import javax.swing.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.*;




public class ViewClubController {

    private User currentUser = SessionManager.getInstance().getCurrentUser();
    @FXML
    private Button pdfButton;
    @FXML
    private ComboBox<String> governorateCombo;
    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private Button chartsButton;
    @FXML
    private Button viewStadiumsButton;
    @FXML
    private TextField searchField;
    @FXML
    private Label viewClubsButton;
    @FXML
    private Label reservationsButton;
    private ClubDAO clubDAO;
    private StadiumDAO stadiumDAO;
    private Club club;
    private OfferDAO offerDAO;
    private SubscriptionDAO subscriptionDAO;


    private List<Club> originalData = new ArrayList<>();

    private List<String> governorateList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();

    @FXML
    private ScrollPane scrollPane;
    private UserDAO us;
    private NotificationDAO notificationDAO;





    private int userId;

    public ViewClubController() {
        clubDAO = new ClubDAO();
        offerDAO = new OfferDAO();
        subscriptionDAO = new SubscriptionDAO();
        us = new UserDAO();
        notificationDAO = new NotificationDAO();
    }
    // Set the NotificationDAO instance
    public void setNotificationDAO(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @FXML
    public void initialize() {

        System.out.println(currentUser);
        userId = currentUser.getId();
        System.out.println(userId);
        SharedData.setUserId(userId);
        System.out.println(SharedData.getUserId());
        // Fetch and store original data
        System.out.println(us.getUserById(userId));

        if (currentUser.getRole() == Role.fieldOwner){
            originalData = clubDAO.findByUser(userId);
             } else {
            originalData = clubDAO.findAll();
        }
        populateCards(originalData);

        List<String> governorates = getGovernoratesFromTunisia();
        governorateCombo.setItems(FXCollections.observableArrayList(governorates));

        // Add listener to governorate ComboBox
        governorateCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Populate city ComboBox based on selected governorate
                List<String> cities = getCitiesForGovernorate(newValue);
                cityCombo.setItems(FXCollections.observableArrayList(cities));

                // Automatically trigger search when a governorate is selected
                searchClubs();
            }
        });

// Add listener to city ComboBox
        cityCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Automatically trigger search when a city is selected
            searchClubs();
        });


       viewClubsButton.setOnMouseClicked(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) viewClubsButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

       if (currentUser.getRole() == Role.player){
           addButton.setVisible(false);
       }else {
           addButton.setVisible(true);
       }

        addButton.setOnAction(this::handleAddButtonAction);


        searchField.setOnKeyReleased(this::handleSearchOnKeyPress);


        chartsButton.setOnAction(this::handleChartsButtonAction);

        if( currentUser.getRole()==Role.player || currentUser.getRole()==Role.fieldOwner ){
            chartsButton.setVisible(false);}
        else {
            chartsButton.setVisible(true);}


        refreshButton.setOnAction(event -> refreshPage());

        if(subscriptionDAO.userExistsInSubscriptionTable(currentUser.getId())){
            pdfButton.setVisible(true);
        } else {
            pdfButton.setVisible(false);
        }


    }

    private void refreshPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) refreshButton.getScene().getWindow();
            stage.setScene(null);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void handleChartsButtonAction(ActionEvent event) {
        try {
            // Load the FXML file for the charts
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClubCharts.fxml"));
            Parent root = loader.load();

            // Create a new stage for the charts
            Stage chartStage = new Stage();
            chartStage.setScene(new Scene(root,1000, 800));

            // Pass necessary data to the controller if needed
            ClubChartsController chartsController = loader.getController();
            // Implement logic to fetch and display statistics based on user's role
            chartsController.setClubs(originalData);

            // Set the title of the stage
            chartStage.setTitle("Club Statistics");

            // Show the stage
            chartStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        // Fetch the user's current club count from the database
        if (currentUser.getRole()==Role.fieldOwner){
        int userClubCount = clubDAO.getClubCountForUser(userId);
        System.out.println("clubsnumbernow: " + userClubCount);

        if (userClubCount == 0) {
            // User doesn't have any clubs yet, allow creating one for free
            navigateToAddClubPage();
        } else {
            // User has existing clubs, check subscription status
            Subscription userSubscription = subscriptionDAO.findByUserId(userId);
            System.out.println("finduserbyid sub: " + userSubscription);

            if (userSubscription != null) {
                // User has a subscription
                Offer offer = userSubscription.getOfferId();

                // Check if the subscription has expired
                Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
                Date endDate = userSubscription.getEndDate();
                if (currentDate.after(endDate)) {
                    // Subscription has expired
                    // Delete the old subscription from the database
                    subscriptionDAO.delete(userSubscription.getId());

                    // Prompt the user to subscribe again
                    showAlert("Subscription Expired Your subscription has expired. Please subscribe again.",true);
                } else {
                    // Subscription is active
                    // Calculate the number of days left for the subscription to expire
                    long daysLeft = (endDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);

                    // Check if the user has reached the maximum club limit based on the subscription offer
                    int allowedClubs = offer.getNbrClub() + 1;
                    System.out.println("allowed clubs: " + allowedClubs);
                    if (userClubCount >= allowedClubs) {
                        // User has reached the maximum number of clubs allowed by the subscription
                        showAlert("Subscription Limit Reached You have reached the maximum number of clubs allowed by your subscription.",true);

                        // Show alert with the number of days left
                        showAlert("Subscription Status is active. " + daysLeft + " days left until expiration.", true);
                    } else {
                        // Proceed to club creation without showing the active subscription alert
                        navigateToAddClubPage();
                    }
                }
            } else {
                // User doesn't have a subscription, prompt to subscribe
                showAlert("Subscribe Required You need to subscribe before creating more clubs.", false);
            }
        }
    } else {
            navigateToAddClubPage();
        }
    }




    private void showAlert(String contentText, boolean isOkButtonOnly) {
        Alert alert;
        if (isOkButtonOnly) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
        } else {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
        }
        alert.setHeaderText(null);
        alert.setContentText(contentText);

        if (!isOkButtonOnly) {
            // Add "OK" and "Cancel" buttons
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, cancelButton);
        } else {
            // Add only "OK" button
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (!isOkButtonOnly && result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            // Redirect to subscription page if OK button is clicked
            redirectToSubscriptionPage();
        } else {
            // User clicked cancel or OK, do nothing
        }
    }






    private void navigateToAddClubPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/NewClub/NewClub.fxml"));
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean userHasClub() {
        return !originalData.isEmpty();
    }
    // Method to display an alert


    public void initData(int clubId) {
        clubDAO = new ClubDAO();
        stadiumDAO = new StadiumDAO();
        club = clubDAO.findById(clubId);

    }

    private void redirectToSubscriptionPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaymentForm/PaymentForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading PaymentForm.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void populateCards(List<Club> clubs) {
        HBox cardsContainer = new HBox(); // Use HBox instead of VBox
        cardsContainer.setSpacing(20); // Spacing between cards

        System.out.println(clubs);

        for (Club club : clubs) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClub/CardView.fxml"));
                AnchorPane card = loader.load();
                System.out.println(club);
                // Accessing controller of the card
                ClubCardController cardController = loader.getController();
                cardController.setData(club); // Pass club data to the card controller
                System.out.println(cardController);

                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scrollPane.setContent(cardsContainer);
    }






@FXML
   private void handleSearchOnKeyPress(KeyEvent event) {
       // Get the text from the search field
       String searchText = searchField.getText().trim();

       // If the search field is empty, repopulate the cards with all clubs
       if (searchText.isEmpty()) {
           populateCards(originalData); // Repopulate the cards with all clubs
           return;
       }

       // Perform a search for clubs based on the search text
       List<Club> searchResult = clubDAO.searchByName(searchText);

       // Update the displayed cards with the search results
       populateCards(searchResult); // Populate the cards with search results
   }



    private List<String> getGovernoratesFromTunisia() {
        // Fetch governorates from a database or any other source
        // For now, return a sample list
        return List.of("Ariana", "Beja", "Ben Arous", "Bizerte", "Gabes", "Gafsa", "Jendouba", "Kairouan", "Kasserine",
                "Kebili", "Kef", "Mahdia", "Manouba", "Medenine", "Monastir", "Nabeul", "Sfax", "Sidi Bouzid", "Siliana",
                "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan");
    }

    private List<String> getCitiesForGovernorate(String governorate) {
        // Fetch cities for the specified governorate from a database or any other source
        // For now, return a sample list
        switch (governorate) {
            case "Ariana":
                return List.of("Ariana", "Ettadhamen-Mnihla", "Raoued", "Sidi Thabet");
            case "Beja":
                return List.of("Beja", "Amdoun", "Goubellat", "Medjez El Bab", "Nefza", "Teboursouk", "Testour", "Thibar");
            case "Ben Arous":
                return List.of("Ben Arous", "Bou Mhel el-Bassatine", "El Mourouj", "Ezzahra", "Fouchana", "Hammam Lif", "Hammam Chott", "Megrine", "Mohamedia-Fouchana", "Radès");
            case "Bizerte":
                return List.of("Bizerte", "Ghar El Melh", "Mateur", "Menzel Bourguiba", "Menzel Jemil", "Ras Jebel", "Sejnane", "Tinja", "Utique", "Zarzouna");
            case "Gabes":
                return List.of("Gabes", "El Hamma", "Gabès Médina", "Ghannouch", "Mareth", "Menzel El Habib", "Metouia", "Nouvelle Matmata", "Oudhref", "Ouled Ahmed Timoul", "Rafraf");
            case "Gafsa":
                return List.of("Gafsa", "Belkhir", "El Guettar", "Gafsa Nord", "Guetar", "Mdhilla", "Metlaoui", "Moulares", "Oum El Araies", "Redeyef", "Sened", "Sidi Aïch");
            case "Jendouba":
                return List.of("Jendouba", "Aïn Draham", "Balta-Bou Aouane", "Bou Salem", "Fernana", "Ghardimaou", "Jendouba Nord", "Jendouba Sud", "Oued Melliz", "Tabarka");
            case "Kairouan":
                return List.of("Kairouan", "Bou Hajla", "Chebika", "El Alâa", "Haffouz", "Hajeb El Ayoun", "Kairouan Nord", "Kairouan Sud", "Nasrallah", "Oueslatia", "Sbikha");
            case "Kasserine":
                return List.of("Kasserine", "Ezzouhour", "Fériana", "Foussana", "Hassi El Ferid", "Hidra", "Jedelienne", "Kasserine Nord", "Kasserine Sud", "Sbeitla", "Sbiba", "Thala");
            case "Kebili":
                return List.of("Kebili", "Douz", "El Golâa", "Kebili Nord", "Kebili Sud", "Souk Lahad", "Souk El Ahed", "Souk Naâmane", "Tataouine", "Toujane");
            case "Kef":
                return List.of("Kef", "Dahmani", "Jerissa", "Kalaat Khasba", "Kef Est", "Kef Ouest", "Ksour", "Nebeur", "Sakiet Sidi Youssef", "Sakiet Sidi Youssef");
            case "Mahdia":
                return List.of("Mahdia", "Bou Merdes", "Chebba", "El Djem", "Essouassi", "Hebira", "Kerker", "Mahdia", "Melloulèche", "Ouled Chamekh", "Tlelsa");
            case "Manouba":
                return List.of("Manouba", "Borj El Amri", "Djedeida", "Douar Hicher", "El Batan", "La Mornaguia", "Manouba Sud", "Mornag", "Oued Ellil", "Tebourba");
            case "Medenine":
                return List.of("Medenine", "Ben Gardane", "Djerba Ajim", "Djerba Houmt Souk", "Djerba Midoun", "Medenine Nord", "Medenine Sud", "Sidi Makhlouf", "Zarzis");
            case "Monastir":
                return List.of("Monastir", "Bekalta", "Bembla", "Jemmal", "Khniss", "Ksibet El Mediouni", "Moknine", "Monastir", "Ouerdanine", "Sahline Moôtmar", "Sayada-Lamta-Bou Hajar");
            case "Nabeul":
                return List.of("Nabeul", "Béni Khalled", "Béni Khiar", "Bou Argoub", "Dar Chaâbane El Fehri", "El Haouaria", "Grombalia", "Hammam Ghezèze", "Hammamet", "Kélibia", "Korba", "Menzel Bouzelfa", "Menzel Temime", "Nabeul", "Soliman", "Takelsa");
            case "Sfax":
                return List.of("Sfax", "Agareb", "Bir Ali Ben Khalifa", "El Amra", "Ghraïba", "Jebiniana", "Kerkennah", "Mahres", "Menhzel Chaker", "Sakiet Eddaïer", "Sakiet Ezzit", "Sfax Ouest", "Sfax Sud", "Sfax Ville", "Thyna");
            case "Sidi Bouzid":
                return List.of("Sidi Bouzid", "Bir El Hafey", "Cebbala Ouled Asker", "Jilma", "Menzel Bouzaiene", "Meknassy", "Mezzouna", "Ouled Haffouz", "Regueb", "Sidi Ali Ben Aoun", "Sidi Bouzid Est", "Sidi Bouzid Ouest");
            case "Siliana":
                return List.of("Siliana", "Bargou", "Bou Arada", "El Aroussa", "Gaâfour", "Kesra", "Makthar", "Rouhia", "Sidi Bou Rouis", "Siliana Nord", "Siliana Sud");
            case "Sousse":
                return List.of("Sousse", "Akouda", "Bouficha", "Enfidha", "Hammam Sousse", "Hergla", "Kalâa Kebira", "Kalâa Seghira", "Kondar", "M'saken", "Sidi Bou Ali", "Sidi El Héni", "Sousse Jaouhara", "Sousse Médina", "Sousse Riadh");
            case "Tataouine":
                return List.of("Tataouine", "Bir Lahmar", "Dehiba", "Ghomrassen", "Remada", "Samar", "Smar", "Tataouine Nord", "Tataouine Sud");
            case "Tozeur":
                return List.of("Tozeur", "Degache", "Hamet Jerid", "Nafta", "Tameghza", "Tozeur");
            case "Tunis":
                return List.of("Tunis", "Ain Zaghouan", "Bab El Bhar", "Bab Souika", "Carthage", "Cité El Khadra", "Djebel Jelloud", "El Kabaria", "El Menzah", "El Omrane", "El Ouardia", "Ettahrir", "Ezzouhour", "Hraïria", "La Goulette", "La Marsa", "Le Bardo", "Le Kram", "Medina", "Séjoumi", "Sidi El Béchir", "Sidi Hassine");
            case "Zaghouan":
                return List.of("Zaghouan", "Bir Mcherga", "El Fahs", "Nadhour", "Zaghouan Nord", "Zaghouan Sud");
            default:
                return Collections.emptyList();
        }
    }

    private void searchClubs() {
        String selectedGovernorate = governorateCombo.getValue();
        String selectedCity = cityCombo.getValue();

        // Call the appropriate method in ReservationDAO to retrieve filtered reservations
        List<Club> filteredClubs = clubDAO.findFilteredClubs(selectedGovernorate, selectedCity);
        populateCards(filteredClubs);
    }

    private void resetFilters() {
        governorateCombo.setValue(null);
        cityCombo.setValue(null);

        // Reset TableView to show all reservations

        populateCards(originalData);
    }
    @FXML
    void HandlePdf(ActionEvent event){
        try {
            createPDF();
            printPDF("SubscriptionInformation.pdf");

        } catch (IOException | PrinterException e){
            e.printStackTrace();
        }
    }

    private void printPDF(String pdfFilePath) throws IOException, PrinterException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PDFPrintable(document));
            if (job.printDialog()) {
                job.print();
            }
        }
    }

    private void createPDF() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set color for table header background
                contentStream.setNonStrokingColor(0.8f, 0.8f, 0.8f); // Light gray

                // Draw table header background
                contentStream.fillRect(50, 690, 500, 20);

                // Set color for table header text
                contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f); // Black

                // Set font and font size for table header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Add table headers
                contentStream.beginText();
                contentStream.newLineAtOffset(60, 700);
                contentStream.showText("Name");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Start Date");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("End Date");
                contentStream.newLineAtOffset(70, 0); // Adjusted offset to create space between "End Date" and "Price"
                contentStream.showText("Price");
                contentStream.newLineAtOffset(50, 0); // Adjusted offset to create more space between "Price" and "Participant Number"
                contentStream.showText("Subscription");
                contentStream.newLineAtOffset(80, 0); // Adjusted offset to create more space between "Participant Number" and "Image"
                contentStream.showText("Image");
                contentStream.endText();

                // Fetch event data from the EventService
                SubscriptionDAO subscriptions = new SubscriptionDAO();
                List<Subscription> allSubscription = subscriptions.findByUser(currentUser);

                // Fetch image data for each event from the ImageService
                ImageDAO imageService = new ImageDAO();

                // Set font and font size for table content
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                // Set text color (black) for table content
                contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f); // Black

                float y = 670;
                for (Subscription event : allSubscription) {
                    // Draw table row borders
                    contentStream.setStrokingColor(0.0f, 0.0f, 0.0f); // Black
                    contentStream.setLineWidth(0.5f);
                    contentStream.moveTo(50, y);
                    contentStream.lineTo(550, y);
                    contentStream.stroke();

                    // Draw event data
                    contentStream.beginText();
                    contentStream.newLineAtOffset(60, y - 10);
                    contentStream.showText(event.getUserId().getFirstName());
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(event.getStartDate().toString());
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(event.getEndDate().toString());
                    contentStream.newLineAtOffset(70, 0);
                    contentStream.showText(String.valueOf(event.getOfferId().getPrice()));
                    contentStream.newLineAtOffset(70, 0);
                    contentStream.showText(String.valueOf(event.getOfferId().getName()));
                    contentStream.endText();



                    // Draw event image
                    try {
                        String imageId = currentUser.getImage();
                        if (imageId != null) {
                                File file = new File(imageId);
                                URL imageUrl = file.toURI().toURL();

                                try (InputStream inputStream = imageUrl.openStream()) {
                                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(inputStream), "image");
                                    // Draw the image
                                    contentStream.drawImage(pdImage, 450, y - 60, 100, 60); // Adjust the coordinates and size as needed
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                    } catch (MalformedURLException e) {
                        // Handle malformed URL
                        e.printStackTrace();
                    }

                    y -= 80; // Decrease the vertical spacing between rows
                }
            }

            document.save("SubscriptionInformation.pdf");
        }
    }




    @FXML
    private void btnNotifcationOnAction() {
        // Fetch notifications for the current user
        List<Notification> currentUserNotifications = notificationDAO.getNotificationsForUser(currentUser);

        // Display the notifications
        if (!currentUserNotifications.isEmpty()) {
            displayNotifications(currentUserNotifications);
        } else {
            displayNoNotificationsMessage();
        }
    }

    // Helper method to display notifications
    private void displayNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            showNotification(notification);
        }
    }

    // Helper method to show a single notification
    private void showNotification(Notification notification) {
        Notifications.create()
                .title("Notification " + notification.getId())
                .text(notification.getText())
                .hideAfter(Duration.seconds(3)) // Display for 5 seconds
                .showInformation();
    }

    // Helper method to display a message when no notifications are found
    private void displayNoNotificationsMessage() {
        Notifications.create()
                .title("No Notifications")
                .text("No notifications found.")
                .hideAfter(Duration.seconds(3)) // Display for 5 seconds
                .showInformation();
    }



}