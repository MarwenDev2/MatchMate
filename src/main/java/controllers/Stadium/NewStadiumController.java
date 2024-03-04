package controllers.Stadium;

import controllers.SharedData;
import entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.Club.ClubDAO;
import services.Image.ImageStadiumDAO;
import services.Notification.NotificationDAO;
import services.Stadium.StadiumDAO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewStadiumController {

    @FXML
    private Label titleLabel;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField stadiumNameField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField widthField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField rateField;
    @FXML
    private FlowPane imageFlowPane;
    @FXML
    private Label viewClubsButton;
    private ImageStadiumDAO imageS;
    private StadiumDAO stadiumDAO;
    private ImageStadiumDAO imageDAO;
    private ClubDAO cl;
    private Club c;
    private Stadium s;
    private int idClub;
    private String stadiumRef;

    private List<Image> uploadedImages;

    @FXML
    private Button maintenanceButton;

    private Stadium currentStadium;
    private NotificationDAO NotificationDAO;



    @FXML
    public void initialize() throws IOException {
        imageS = new ImageStadiumDAO();
        stadiumDAO = new StadiumDAO();
        NotificationDAO = new NotificationDAO();
        cl = new ClubDAO();
        uploadedImages = new ArrayList<>();

        maintenanceButton.setVisible(false);

        idClub= SharedData.getClubId();
        c=cl.findById(idClub);
        stadiumRef = c.getName().substring(0, Math.min(c.getName().length(), 3)).toUpperCase();
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        stadiumRef = stadiumRef + randomNumber;
        stadiumNameField.setText(stadiumRef);

        // Set event handler for back button
        backButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewStadium/ViewStadium.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    }
    public void hello(){
        return;
    }

    public void populateFieldsWithStadium(String ref) {
        ImageStadiumDAO imo = new ImageStadiumDAO();
        s = stadiumDAO.findById(ref);
        stadiumNameField.setText(s.getReference());
        heightField.setText(String.valueOf(s.getHeight()));
        widthField.setText(String.valueOf(s.getWidth()));
        priceField.setText((String.valueOf(s.getPrice())));
        Stadium currentStadium = stadiumDAO.findById(ref);

        if (currentStadium.getMaintenance()==1) {
            maintenanceButton.setVisible(true);
        }

        List<Image> images = imo.findByIDStadium(s.getReference(),"stadium");
        for (Image image : images) {
            addImageToFlowPane(image);
        }
        titleLabel.setText("Change your Stadium");
        saveButton.setText("Update");
    }


    @FXML
    public void saveStadium() {

        String reference = stadiumNameField.getText();
        String heightText = heightField.getText();
        String widthText = widthField.getText();
        String priceText = priceField.getText();


        if ( heightText.isEmpty() || widthText.isEmpty() || priceText.isEmpty() ) {
            showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        float height, width;
        int price,rate;

        try {
            height = Float.parseFloat(heightText);
            width = Float.parseFloat(widthText);
            price = Integer.parseInt(priceText);

        } catch (NumberFormatException e) {
            showAlert("Error", "Height, width, price, and rate must be numeric values.", Alert.AlertType.ERROR);
            return;
        }

        Stadium stadium = new Stadium(reference,c, height, width, price);
        if (titleLabel.getText().equals("Change your Stadium")) {
            if (stadiumDAO.update(stadium)) {
                showAlert("Success", "Stadium updated successfully.", Alert.AlertType.INFORMATION);
                saveImagesToDatabase(stadiumRef); // Save uploaded images to the database
                redirectToViewStadium();
            } else {
                showAlert("Error", "Failed to update club.", Alert.AlertType.ERROR);
            }
        } else {
            // Save new club
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

// Format the current date and time
            String formattedDateTime = LocalDateTime.now().format(formatter);

// Create the notification with the formatted date and time
            Notification notification = new Notification(
                    cl.findById(idClub).getUser(),
                    "A stadium was successfully created for Club Name: " + cl.findById(idClub).getName() +
                            " on " + formattedDateTime);
            NotificationDAO.save(notification);
            stadium.setMaintenance(0);
            String m = stadiumDAO.save(stadium);
            if (m!=null) {
                showAlert("Success", "Stadium added successfully for Club Name: " + cl.findById(idClub).getName(), Alert.AlertType.INFORMATION);
                saveImagesToDatabase(reference); // Save uploaded images to the database
                redirectToViewStadium();
            } else {
                showAlert("Error", "Failed to add club.", Alert.AlertType.ERROR);
            }
        }

    }
    private void saveImagesToDatabase(String ref) {
        for (Image image : uploadedImages) {
            imageS.save(image,ref);
        }
    }
    @FXML
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
                String type = "stadium";

                // Create an instance of your Image class
                Image image = new Image(name, url, type);
                uploadedImages.add(image); // Add Image to uploadedImages list
                addImageToFlowPane(image); // Call addImageToFlowPane() to display the Image
            }
        }
    }

    private void redirectToViewStadium() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ViewStadium/ViewStadium.fxml"));
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) saveButton.getScene().getWindow(); // Assuming saveButton is present in NewClub.fxml
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clear() {
        clearFields();
    }
    private void clearFields() {
        stadiumNameField.clear();
        heightField.clear();
        widthField.clear();
        priceField.clear();
        rateField.clear();
        uploadedImages.clear();
        imageFlowPane.getChildren().clear();
    }

    @FXML
    public void cancel() {
        // Implement cancel action
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
            // Remove the HBox containing both the image and the remove button
            imageFlowPane.getChildren().remove(imageBox);

            // Check if there are no images left

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

    @FXML
    private void handleMaintenanceButtonClick() {
        // Set maintenance status back to true


        s.setMaintenance(0);

        // Hide maintenance button
        maintenanceButton.setVisible(false);

        // Update the stadium in the database
        stadiumDAO.update(s);
    }
}
