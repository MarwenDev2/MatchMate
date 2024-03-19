package controllers;

import entities.Club;
import entities.Image;
import entities.User;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ClubDAO;
import services.ImageDAO;
import services.ImageStadiumDAO;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class NewClubController {

    @FXML
    private Label titleLabel;
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
    private TextField heightField;
    @FXML
    private FlowPane imageFlowPane;
    @FXML
    private TextField widthField;

    private ClubDAO clubDAO;
    private ImageDAO imageDAO;
    private Club C;
    private List<Image> uploadedImages; // Keep track of uploaded images



    public NewClubController() {
        clubDAO = new ClubDAO();
        imageDAO = new ImageDAO();
        uploadedImages = new ArrayList<>();
    }
    public void initialize() {
        // Fill the ComboBoxes with options for hours (0-23) and minutes (0-59)
        ObservableList<Integer> hoursList = FXCollections.observableArrayList();
        ObservableList<Integer> minutesList = FXCollections.observableArrayList();

        for (int i = 0; i < 24; i++) {
            hoursList.add(i);
        }

        for (int i = 0; i < 60; i++) {
            minutesList.add(i);
        }

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


    }

    public void populateFieldsWithClubData(Club club) {
        int idC = club.getId();
        C = club;
        clubNameField.setText(club.getName());
        heightField.setText(String.valueOf(club.getHeight()));
        widthField.setText(String.valueOf(club.getWidth()));
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

        List<Image> images = imageDAO.findByObjectId(idC,"club");
        for (Image image : images) {
            addImageToFlowPane(image);
        }
        titleLabel.setText("Change your Club");
        saveButton.setText("Update");


    }
    @FXML
    public void saveClub() {

        String name = clubNameField.getText();
        String description = descriptionArea.getText();
        String heightText = heightField.getText();
        String widthText = widthField.getText();
        Integer startHour = startHourComboBox.getValue();
        Integer startMinute = startMinuteComboBox.getValue();
        Integer endHour = endHourComboBox.getValue();
        Integer endMinute = endMinuteComboBox.getValue();


        // 1. Empty Field Check
        if (name.isEmpty() || description.isEmpty() || heightText.isEmpty() || widthText.isEmpty()) {
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

        float height, width;
        try {
            // 2. Numeric Value Check
            height = Float.parseFloat(heightText);
            width = Float.parseFloat(widthText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Height and width must be numeric values.", Alert.AlertType.ERROR);
            return;
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
        Club3.setHeight(height);
        Club3.setWidth(width);
        User u1 = new User();
        u1.setId(5);
        Club3.setUser(u1);

        if (titleLabel.getText().equals("Change your Club")) {
            int nbr = C.getStadiumNbr();
            Club3.setId(C.getId());
            Club3.setStadiumNbr(nbr);
            System.out.println(C.getId());
            if (clubDAO.update(Club3)) {
                showAlert("Success", "Club updated successfully.", Alert.AlertType.INFORMATION);
                saveImagesToDatabase(C.getId()); // Save uploaded images to the database
                redirectToViewClub();
            } else {
                showAlert("Error", "Failed to update club.", Alert.AlertType.ERROR);
            }
        } else {
            // Save new club
            Club3.setStadiumNbr(0);
            int idClub = clubDAO.save(Club3);
            if (idClub != 0) {
                showAlert("Success", "Club added successfully with ID: " + idClub, Alert.AlertType.INFORMATION);
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
                addImageToFlowPane(image); // Call addImageToFlowPane() to display the Image
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
        heightField.clear();
        widthField.clear();
        uploadedImages.clear();
        imageFlowPane.getChildren().clear();
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
        imageBox.getStyleClass().add("image-box");

        Button removeButton = createRemoveImageButton(image, imageBox); // Pass the HBox to the method
        removeButton.getStyleClass().add("remove-button");

        imageBox.getChildren().add(removeButton); // Add the remove button to the HBox
        imageFlowPane.getChildren().add(imageBox); // Add the HBox to the imageFlowPane
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


}