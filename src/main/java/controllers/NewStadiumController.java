package controllers;

import entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import services.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewStadiumController {

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
    private TextArea descriptionArea;
    @FXML
    private FlowPane imageFlowPane;

    private ImageDAO imageDAO;
    private StadiumDAO stadiumDAO;
    private ClubDAO cl;
    private List<Image> uploadedImages;

    public NewStadiumController() {
        imageDAO = new ImageDAO();
        stadiumDAO = new StadiumDAO();
        cl = new ClubDAO();
        uploadedImages = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        // Initialize the form
    }

    @FXML
    public void saveStadium() {
        String name = stadiumNameField.getText();
        String heightText = heightField.getText();
        String widthText = widthField.getText();
        String priceText = priceField.getText();
        String rateText = rateField.getText();

        if (name.isEmpty() || heightText.isEmpty() || widthText.isEmpty() || priceText.isEmpty() || rateText.isEmpty()) {
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

        Club c = cl.findById(18);

        Stadium stadium = new Stadium(c, height, width, price, rate);

        int stadiumId = stadiumDAO.save(stadium);
        if (stadiumId != 0) {
            showAlert("Success", "Stadium added successfully with ID: " + stadiumId, Alert.AlertType.INFORMATION);
            saveImagesToDatabase(stadiumId); // Save uploaded images to the database
            clearFields();
        } else {
            showAlert("Error", "Failed to add stadium.", Alert.AlertType.ERROR);
        }
    }
    private void saveImagesToDatabase(int clubId) {
        for (Image image : uploadedImages) {
            imageDAO.save(image, clubId);
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
                String type = "club";

                // Create an instance of your Image class
                Image image = new Image(name, url, type);
                uploadedImages.add(image); // Add Image to uploadedImages list
                addImageToFlowPane(image); // Call addImageToFlowPane() to display the Image
            }
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
