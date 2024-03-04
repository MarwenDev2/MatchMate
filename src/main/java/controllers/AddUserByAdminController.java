package controllers;

import entities.SessionManager;
import entities.Role;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.UserDAO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddUserByAdminController {
    @FXML
    private Button CLUBS_btn;

    @FXML
    private Button EVENTS_btn;

    @FXML
    private Button FIELDS_btn;

    @FXML
    private Button FILEDOWNERS_btn;

    @FXML
    private Button PLAYERS_btn;

    @FXML
    private Button RECLAMATIONS_btn4;

    @FXML
    private Button STORE_btn;

    @FXML
    private Button add_user_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private Button profile_btn;

    @FXML
    private Label username;

    @FXML
    private TextField users_add_email;

    @FXML
    private TextField users_add_firstname;

    @FXML
    private ImageView users_add_img;

    @FXML
    private Button users_add_img_upload_btn;

    @FXML
    private TextField users_add_lastname;

    @FXML
    private TextField users_add_password;

    @FXML
    private TextField users_add_phone;

    @FXML
    private ComboBox<Role> users_add_role;

    @FXML
    private Button users_btn;

    @FXML
    private Button users_clear;


    public String imagePath = "";


    UserDAO userDAO = new UserDAO();
    // Retrieve the current user from the session manager
    User currentUser= SessionManager.getInstance().getCurrentUser();


    @FXML
    public void initialize() {
        // Call the function to set the label value
        setLabelValue();

        // Populate the ComboBox with enum values
        ObservableList<Role> roles = FXCollections.observableArrayList(Role.values());
        users_add_role.setItems(roles);

    }

    private void setLabelValue() {
        // Call your function to get the value
        String value = getCurentUserFirstName();

        // Set the value to the label
        username.setText("Admin : "+value);
    }
    private String getCurentUserFirstName() {

        // Check if the current user is not null
        if (currentUser != null) {
            // Return the first name of the user
            return currentUser.getFirstName();
        } else {
            // Handle the case where the current user is null
            return "User Not Found"; // Or any default value you want to set
        }
    }

    @FXML
    void addUser(ActionEvent event) {
        // Retrieve user input from text fields
        String firstName = users_add_firstname.getText();
        String lastName = users_add_lastname.getText();
        String phoneNumber = users_add_phone.getText();
        String email = users_add_email.getText();
        String password = users_add_password.getText();

        Role role = users_add_role.getValue();
        String image = imagePath; // Get the image path from the ImageView

        // Create a new user object
        User newUser = new User(firstName, lastName, phoneNumber, email, password, role, image);


        List<String> validationErrors = userDAO.validate_User_Input(newUser);
        if (!validationErrors.isEmpty()) {
            // Display validation errors to the user
            showAlertInput(validationErrors);

        }

        int userAdded = userDAO.addUser(newUser);

        // Check if user addition was successful
        if (userAdded!=0) {
            // Show a success message
            showAlert("User added successfully.");
            loadPage("/AdminDashboard/admin_dashboard.fxml",event);
        }

    }

    private void showAlertInput(List<String> validationErrors) {
        // Create an alert dialog
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please correct the following errors:");

        // Concatenate the validation errors into a single string
        String errorMessage = String.join("\n", validationErrors);
        alert.setContentText(errorMessage);

        // Show the alert dialog and wait for the user to close it
        alert.showAndWait();
    }
    @FXML
    void logout(ActionEvent event) {
        System.out.println(currentUser.toString());
        // Invalidate the current session
        SessionManager.getInstance().invalidateSession(SessionManager.getInstance().getCurrentSessionId());
        loadPage("/Login/login1.fxml", event);

    }

    private void loadPage(String fxmlFileName , ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the case where the FXML file cannot be loaded
        }
    }

    @FXML
    void seeProfile(ActionEvent event) {
        loadPage("/UserProfileAdmin/user_profile_admin.fxml", event);
    }
    public void seeUsers(ActionEvent actionEvent) {
        loadPage("/AdminDashboard/admin_dashboard.fxml", actionEvent);
    }



    @FXML
    void handleLoadImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            users_add_img.setImage(image); // Set the image to the ImageView
        }
        imagePath = file.getAbsolutePath();
    }

    // Method to show an alert message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearFields(ActionEvent event) {
        // Clear the text fields
        users_add_email.setText("");
        users_add_firstname.setText("");
        users_add_lastname.setText("");
        users_add_phone.setText("");

        users_add_img.setImage(null); // Clear the image
        users_add_role.getSelectionModel().clearSelection(); // Clear the selection in the combo box
    }



    @FXML
    void updateUser(ActionEvent event) {


    }
}
