package controllers;

import entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewStadiumController {

    @FXML
    private Label titleLabel;
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
    public void initialize() throws IOException {
        imageS = new ImageStadiumDAO();
        stadiumDAO = new StadiumDAO();
        cl = new ClubDAO();
        uploadedImages = new ArrayList<>();


        idClub=SharedData.getClubId();
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
                Scene scene = new Scene(root, 1000, 600);
                Stage stage = (Stage) backButton.getScene().getWindow();
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
        rateField.setText(String.valueOf(s.getRate()));


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
        String rateText = rateField.getText();

        if ( heightText.isEmpty() || widthText.isEmpty() || priceText.isEmpty() || rateText.isEmpty()) {
            showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
            return;
        }

        float height, width;
        int price,rate;

        try {
            height = Float.parseFloat(heightText);
            width = Float.parseFloat(widthText);
            price = Integer.parseInt(priceText);
            rate = Integer.parseInt(rateText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Height, width, price, and rate must be numeric values.", Alert.AlertType.ERROR);
            return;
        }

        Stadium stadium = new Stadium(reference,c, height, width, price, rate);
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

            String m = stadiumDAO.save(stadium);
            if (m!=null) {
                showAlert("Success", "Club added successfully with ID: " + idClub, Alert.AlertType.INFORMATION);
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
            Scene scene = new Scene(root, 1100, 600);
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
            imageFlowPane.getChildren().remove(imageBox);

            // Determine the type of image (e.g., "club" or "stadium")
            String type = image.getType();

            // Call the appropriate DAO to delete the image from the database
            if(titleLabel.getText().equals("Change your Club"))
                imageS.delete(image);

        });
        return removeButton;
    }
    private void addImageToFlowPane(Image image) {
        ImageView imageView = new ImageView(image.getUrl());
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        HBox imageBox = new HBox(imageView);
        imageBox.setSpacing(10);
        imageBox.getStyleClass().add("image-box");

        Button removeButton = createRemoveImageButton(image, imageBox); // Pass the HBox to the method
        removeButton.getStyleClass().add("remove-button");

        imageBox.getChildren().add(removeButton); // Add the remove button to the HBox
        imageFlowPane.getChildren().add(imageBox); // Add the HBox to the imageFlowPane
    }
}
