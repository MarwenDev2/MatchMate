package controllers.Club;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import controllers.SharedData;
import entities.*;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Club.ClubDAO;
import services.Image.ImageDAO;
import services.User.UserDAO;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewClubController {

    @FXML
    private Label titleLabel;

    @FXML
    private WebView mapWebView;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label viewClubsButton;
    @FXML
    private Label reservationsButton;
    @FXML
    private TextField clubNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<Integer> startHourComboBox;
    @FXML
    private ComboBox<Integer> startMinuteComboBox;
    @FXML
    private ComboBox<Integer> endHourComboBox;
    @FXML
    private ComboBox<Integer> endMinuteComboBox;
    @FXML
    private ComboBox<String> heightField;
    @FXML
    private FlowPane imageFlowPane;
    @FXML
    private ComboBox<String> widthField;
    @FXML
    private ScrollPane imageScrollPane;

    private ClubDAO clubDAO;
    private ImageDAO imageDAO;
    private Club C;
    private List<Image> uploadedImages; // Keep track of uploaded images
    private UserDAO userDAO;
    private User currentUser = SessionManager.getInstance().getCurrentUser();



    // map
    private static final Logger logger = LoggerFactory.getLogger(NewClubController.class);
    private static final int ZOOM_DEFAULT = 14;

    private final Coordinate centerCoordiante = new Coordinate(36.898871, 10.187932);

    private Marker markerClick;

    @FXML
    private MapView mapView;
    @FXML
    private TextField latField;
    @FXML
    private TextField longField;



    public NewClubController() {
        clubDAO = new ClubDAO();
        imageDAO = new ImageDAO();
        uploadedImages = new ArrayList<>();
        userDAO = new UserDAO();
        markerClick = Marker.createProvided(Marker.Provided.ORANGE).setVisible(true);
    }
    public void initialize() {
        // Fill the ComboBoxes with options for hours (0-23) and minutes (0-59)
        ObservableList<Integer> hoursList = FXCollections.observableArrayList();
        ObservableList<Integer> minutesList = FXCollections.observableArrayList();
       /* WebEngine webEngine = mapWebView.getEngine();
        webEngine.load(getClass().getResource("/Maps.html").toExternalForm()); */

        for (int i = 0; i < 24; i++) {
            hoursList.add(i);
        }

        for (int i = 0; i < 60; i++) {
            minutesList.add(i);
        }

        List<String> governorates = getGovernoratesFromTunisia();
        heightField.setItems(FXCollections.observableArrayList(governorates));

        // Add listener to governorate ComboBox
        heightField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Populate city ComboBox based on selected governorate
                List<String> cities = getCitiesForGovernorate(newValue);
                widthField.setItems(FXCollections.observableArrayList(cities));
            }
        });

        startHourComboBox.setItems(hoursList);
        endHourComboBox.setItems(hoursList);
        startMinuteComboBox.setItems(minutesList);
        endMinuteComboBox.setItems(minutesList);

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
            if(saveButton.getText().equals("Save")){
                initMapAndControls();
                mapView.setVisible(true);
                mapWebView.setVisible(false);
            }


        // Add listener to latitude and longitude fields
        latField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !longField.getText().isEmpty()) {
                double latitude = Double.parseDouble(newValue);
                double longitude = Double.parseDouble(longField.getText());
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        });

        longField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !latField.getText().isEmpty()) {
                double latitude = Double.parseDouble(latField.getText());
                double longitude = Double.parseDouble(newValue);
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        });


        WebEngine webEngine = mapWebView.getEngine();
        webEngine.load(getClass().getResource("/Maps.html").toExternalForm());

    }

    public void populateFieldsWithClubData(Club club) {
        titleLabel.setText("Change your Club");
        saveButton.setText("Update");
        mapView.setVisible(false);
        mapWebView.setVisible(true);
        int idC = club.getId();
        C = club;
        clubNameField.setText(club.getName());
        heightField.setValue(String.valueOf(club.getGovernorate()));
        widthField.setValue(String.valueOf(club.getCity()));
        // Set end time
        int startHour = club.getStartTime().getHours();
        int startMinute = club.getStartTime().getMinutes();
        startHourComboBox.setValue(startHour);
        startMinuteComboBox.setValue(startMinute);
        // Set end time
        int endHour = club.getEndTime().getHours();
        int endMinute = club.getEndTime().getMinutes();
        endHourComboBox.setValue(endHour);
        endMinuteComboBox.setValue(endMinute);
        // Set description
        descriptionArea.setText(club.getDescription());
        latField.setText(String.valueOf(club.getLatitude()));
        longField.setText(String.valueOf(club.getLongitude()));


        heightField.setDisable(true);
        widthField.setDisable(true);


        if (club.getLatitude() != 0.0 && club.getLongitude() != 0.0) {
            loadMap(club.getLatitude(), club.getLongitude());
        } else {
            initMapAndControls();
        }




        List<Image> images = imageDAO.findByObjectId(idC,"club");
        for (Image image : images) {
            addImageToFlowPane(image);
        }





    }

    private void loadMap(double latitude, double longitude) {
        WebEngine webEngine = mapWebView.getEngine();

        // Generate HTML content with the correct map URL
        String htmlContent = generateMapHtml(latitude, longitude);

        // Load the HTML content into the WebView
        webEngine.loadContent(htmlContent);
    }

    private String generateMapHtml(double latitude, double longitude) {
        // Construct the map URL based on the latitude and longitude
        String mapUrl = "https://maps.google.com/maps?q=" +
                latitude + "," + longitude + "&t=k&z=16&output=embed";

        // Generate HTML content with the correct map URL
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Google Maps Example</title>\n" +
                "    <style>\n" +
                "        /* Adjust the size and position of the map */\n" +
                "        #mapouter {\n" +
                "            position: relative;\n" +
                "            text-align: right;\n" +
                "            height: 500px; /* Adjust the height as needed */\n" +
                "            width: 500px; /* Adjust the width as needed */\n" +
                "        }\n" +
                "\n" +
                "        #gmap_canvas2 {\n" +
                "            overflow: hidden;\n" +
                "            background: none !important;\n" +
                "            height: 500px; /* Adjust the height as needed */\n" +
                "            width: 500px; /* Adjust the width as needed */\n" +
                "        }\n" +
                "\n" +
                "        #gmap_canvas {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            border: 0;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"mapouter\">\n" +
                "    <div id=\"gmap_canvas2\">\n" +
                "        <iframe id=\"gmap_canvas\"\n" +
                "                src=\"" + mapUrl + "\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\"></iframe>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
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



    @FXML
    public void saveClub() {

        String name = clubNameField.getText();
        String description = descriptionArea.getText();
        String governorate = heightField.getValue();
        String city = widthField.getValue();
        Integer startHour = startHourComboBox.getValue();
        Integer startMinute = startMinuteComboBox.getValue();
        Integer endHour = endHourComboBox.getValue();
        Integer endMinute = endMinuteComboBox.getValue();
        double latitude = Double.parseDouble(latField.getText());
        double longitude = Double.parseDouble(longField.getText());


        // 1. Empty Field Check
        if (name.isEmpty() || description.isEmpty() || governorate.isEmpty() || city.isEmpty()) {
            showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }
        // Check for empty or not selected values in ComboBoxes
        if (startHour == null || startMinute == null || endHour == null || endMinute == null) {
            showAlert("Error", "Please select start and end times.", Alert.AlertType.ERROR);
            return;
        } else {
            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            int timeDifference = endTotalMinutes - startTotalMinutes;

            if (timeDifference < 120) {
                showAlert("Error", "There must be a minimum of 2 hours between the start and end times.", Alert.AlertType.ERROR);
                return;
            }
        }


        // 3. Start Time vs End Time Check
        if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
            showAlert("Error", "Start time must be before end time.", Alert.AlertType.ERROR);
            return;
        }

        Time startTime = new Time(startHour, startMinute, 0);
        Time endTime = new Time(endHour, endMinute, 0);

        Club Club3 = new Club();
        Club3.setName(name);
        Club3.setEndTime(endTime);
        Club3.setStartTime(startTime);
        Club3.setDescription(description);
        Club3.setCity(city);
        Club3.setGovernorate(governorate);
        User u1 = new User();
        u1.setId(SharedData.getUserId());
        Club3.setUser(u1);


        if (titleLabel.getText().equals("Change your Club")) {
            int nbr = C.getStadiumNbr();
            Club3.setId(C.getId());
            Club3.setStadiumNbr(nbr);
            Club3.setId(C.getId());
            System.out.println(C.getId());
            Club3.setLatitude(latitude); // Set latitude
            Club3.setLongitude(longitude); // Set longitude
            if (clubDAO.update(Club3)) {
                showAlert("Success", "Club updated successfully.", Alert.AlertType.INFORMATION);
                saveImagesToDatabase(C.getId()); // Save uploaded images to the database
                redirectToViewClub();
            } else {
                showAlert("Error", "Failed to update club.", Alert.AlertType.ERROR);
            }
        } else {
            // Save new club
            Club3.setLatitude(latitude); // Set latitude
            Club3.setLongitude(longitude);
            Club3.setStadiumNbr(0);
            int idClub = clubDAO.save(Club3);
            if (idClub != 0) {
                showAlert("Success", "Club added successfully with ID: " + idClub, Alert.AlertType.INFORMATION);
                String number = String.valueOf(userDAO.getUserById(SharedData.getUserId()).getPhoneNumber());
                String a = "+216"+number;
             //   sendSMS.sms("Club added successfully with ID: " + idClub + ", and thank you"+ currentUser.getFirstName() + " " + currentUser.getLastName(), a);
                saveImagesToDatabase(idClub); // Save uploaded images to the database
                redirectToViewClub();
            } else {
                showAlert("Error", "Failed to add club.", Alert.AlertType.ERROR);
            }
        }
    }





    private void saveImagesToDatabase(int clubId) {
        for (Image image : uploadedImages) {
            imageDAO.save(image, clubId);
        }
    }
    public void uploadImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Images");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(imageFlowPane.getScene().getWindow());
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                String name = file.getName(); // Get the file name
                String url = file.toURI().toString(); // Get the file URL
                //String[] splitName = name.split("\\."); // Split the file name to get the extension
                //String type = splitName[splitName.length - 1]; // Get the file extension
                String type = "club";

                // Create an instance of your Image class
                Image image = new Image(name, url, type);
                uploadedImages.add(image); // Add Image to uploadedImages list
                addImageToFlowPane(image);
                // Call addImageToFlowPane() to display the Image
            }
        }
    }
    @FXML
    public void clear() {
        clearFields();
    }
    private void clearFields() {
        clubNameField.clear();
        descriptionArea.clear();
        startHourComboBox.getSelectionModel().clearSelection();
        startMinuteComboBox.getSelectionModel().clearSelection();
        endHourComboBox.getSelectionModel().clearSelection();
        endMinuteComboBox.getSelectionModel().clearSelection();
        // Clear ComboBox selections
        heightField.getSelectionModel().clearSelection();
        widthField.getSelectionModel().clearSelection();
        uploadedImages.clear();
        imageFlowPane.getChildren().clear();
        latField.clear();
        longField.clear();
    }
    @FXML
    public void cancel() {
        cancelButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
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
    private Button createRemoveImageButton(Image image, HBox imageBox) {
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            uploadedImages.remove(image);
            imageFlowPane.getChildren().remove(imageBox);

            // Determine the type of image (e.g., "club" or "stadium")
            String type = image.getType();

            // Call the appropriate DAO to delete the image from the database
            if(titleLabel.getText().equals("Change your Club"))
            imageDAO.delete(image);

        });
        return removeButton;
    }

    private void addImageToFlowPane(Image image) {
        ImageView imageView = new ImageView(image.getUrl());
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        HBox imageBox = new HBox(imageView);
        imageBox.setSpacing(10);

        Button removeButton = createRemoveImageButton(image, imageBox);
        removeButton.getStyleClass().add("remove-button");

        imageBox.getChildren().add(removeButton);
        imageFlowPane.getChildren().add(imageBox); // Add to the VBox

        // Set the VBox containing the FlowPane as the content of the ScrollPane
        imageScrollPane.setContent(imageFlowPane);

        // Ensure the ScrollPane is visible
        if (imageFlowPane.getChildren().isEmpty()) {
            // If no images, hide the ScrollPane
            imageScrollPane.setVisible(false);
        } else {
            // If images are present, show the ScrollPane
            imageScrollPane.setVisible(true);
        }
    }

    private void redirectToViewClub() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) saveButton.getScene().getWindow(); // Assuming saveButton is present in NewClub.fxml
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //MAP
    public void initMapAndControls() {

        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));

        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mapView.setZoom(ZOOM_DEFAULT);
                mapView.setCenter(centerCoordiante);
            }
        });


        setupEventHandlers();


        // finally initialize the map view
        logger.trace("start map initialization");
        mapView.initialize(Configuration.builder()
                .projection(Projection.WEB_MERCATOR)
                .showZoomControls(false)
                .build());
        logger.debug("initialization finished");

    }


    /**
     * initializes the event handlers.
     */

    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            newPosition.getLatitude();

            if (markerClick.getVisible()) {
                final Coordinate oldPosition = markerClick.getPosition();
                if (oldPosition != null) {
                    animateClickMarker(oldPosition, newPosition);
                } else {
                    markerClick.setPosition(newPosition);
                    // adding can only be done after coordinate is set
                    mapView.addMarker(markerClick);
                }
            }
        });

        // add an event handler for MapViewEvent#MAP_EXTENT and set the extent in the map
        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
            mapView.setExtent(event.getExtent());
        });

        // add an event handler for extent changes and display them in the status label
        mapView.addEventHandler(MapViewEvent.MAP_BOUNDING_EXTENT, event -> {
            event.consume();
        });

        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            System.out.println(event.getCoordinate());
            latField.setText(event.getCoordinate().getLatitude().toString());
            longField.setText(event.getCoordinate().getLongitude().toString());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
        });
        mapView.addEventHandler(MarkerEvent.MARKER_RIGHTCLICKED, event -> {
            event.consume();
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_CLICKED, event -> {
            event.consume();
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_RIGHTCLICKED, event -> {
            event.consume();
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, event -> {
            logger.debug("pointer moved to " + event.getCoordinate());
        });

        logger.trace("map handlers initialized");
    }

    private void animateClickMarker(Coordinate oldPosition, Coordinate newPosition) {
        // animate the marker to the new position
        final Transition transition = new Transition() {
            private final Double oldPositionLongitude = oldPosition.getLongitude();
            private final Double oldPositionLatitude = oldPosition.getLatitude();
            private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
            private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

            {
                setCycleDuration(Duration.seconds(1.0));
                setOnFinished(evt -> markerClick.setPosition(newPosition));
            }

            @Override
            protected void interpolate(double v) {
                final double latitude = oldPosition.getLatitude() + v * deltaLatitude;
                final double longitude = oldPosition.getLongitude() + v * deltaLongitude;
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        };
        transition.play();
    }




}