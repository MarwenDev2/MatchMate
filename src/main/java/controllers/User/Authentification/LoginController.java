package controllers.User.Authentification;


import entities.SessionManager;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import services.User.UserDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML
    private Label linkSignUp;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField textemail;
    @FXML
    private Label EmailtxtErrorLabel;
    @FXML
    private Label PasswordtxtErrorLabel;

    private final UserDAO userDAO = new UserDAO();

    // Initialize the SessionManager
    private SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    void initialize() {
        linkSignUp.setOnMouseClicked(this::handleSignUpLinkClick);
    }
    @FXML
    private void handleSignUpLinkClick(MouseEvent event) {
        // Load the sign-up page
        loadPageMouseEvent("/SignUp/signup.fxml", event);
    }

    @FXML
    void authenticate(ActionEvent event) {
        String email = textemail.getText();
        String password = txtPassword.getText();

        // Validate user input
        Map<String, String> validationErrors = validate_User_Input(email , password);
        if (!validationErrors.isEmpty()) {
            // Display validation errors next to their corresponding text fields
            displayValidationErrors(validationErrors);
        }

        if (!userDAO.userExists(email)) {
            showAlert("Sorry This Email does not exists ! ");
            textemail.setText("");
        } else {
            // Use the UserService to authenticate the user
            User authenticated_user = userDAO.authenticateUser(email, password);
            if (authenticated_user != null) {
                redirectToHomePage();
                // Generate session ID (You can use UUID.randomUUID() for this purpose)
                String sessionId = SessionManager.generateSessionId();
                // Store user information in session manager
                SessionManager.addSession(sessionId, authenticated_user, authenticated_user.getId());
                // Set the current session ID
                SessionManager.getInstance().setCurrentSessionId(sessionId);

                // Retrieve user object from session manager using session ID
                // SessionManager sessionManager = SessionManager.getInstance();
                //String sessionId = sessionManager.getCurrentSessionId();
                //user currentUser = sessionManager.getUserFromSession(sessionId);
                // Redirect user based on role
                switch (authenticated_user.getRole()) {
                    case player:
                        loadPage("/UserProfilePlayer/user_profile_player.fxml", event);
                        break;
                    case Admin:
                        loadPage("/AdminDashboard/admin_dashboard.fxml", event);
                        break;
                    case fieldOwner:
                        loadPage("/UserProfileFiledOwner/user_profile_fieldOwner.fxml", event);
                        break;
                    default:
                        // Handle unknown role
                        showAlert("Unknown user role.");
                        break;
                }
                //return sessionId; // Return session ID to the caller
            } else {
                // Authentication failed, display an error message
                showAlert("Invalid email or password. Such user doesn't exist.Please try again.");
            }


        }
    }
    public Map<String, String> validate_User_Input(String email , String password) {
        Map<String, String> errorMessages = new HashMap<>();
        validateAndAddError(email, "textemail", "Email", errorMessages);
        validateAndAddError(password, "txtPassword", "Password", errorMessages);
        return errorMessages;
    }
    private void validateAndAddError(String fieldValue, String fieldId, String fieldName, Map<String, String> errorMessages) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            errorMessages.put(fieldId, fieldName + " is required.");
        } else if ("Email".equals(fieldName) && !userDAO.isValidEmail(fieldValue)) {
            errorMessages.put(fieldId, "Invalid email format.");
        }
    }
    private void displayValidationErrors(Map<String, String> validationErrors) {
        // Clear any previous error messages
        clearErrorMessages();
        // Display validation errors next to their corresponding text fields
        for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
            String fieldName = entry.getKey();
            String errorMessage = entry.getValue();
            switch (fieldName) {
                case "textemail":
                    displayErrorMessage(textemail , errorMessage);
                    break;
                case "txtPassword":
                    displayErrorMessage(txtPassword, errorMessage);
                    break;

            }
        }
    }
    private void displayErrorMessage(TextField node, String errorMessage) {
            TextField textField =  node;
            Label errorLabel = getErrorLabelForTextField(textField);
            errorLabel.setText(errorMessage);
    }
    private Label getErrorLabelForTextField(TextField textField) {
        // Construct the ID of the error label based on the ID of the text field
        String errorLabelId = textField.getId() + "ErrorLabel";
        // Find the error label by its ID in the scene graph
        return (Label) textField.getScene().lookup("#" + errorLabelId);
    }


    private void clearErrorMessages() {
        // Clear error labels next to text fields
        // Clear error label next to Email text field
        clearErrorLabel(textemail);
        // Clear error label next to Password text field
        clearErrorLabel(txtPassword);

    }
    private void clearErrorLabel(TextField textField) {
        // Retrieve the error label associated with the text field
        Label errorLabel = getErrorLabelForTextField(textField);
        // Clear the error message text
        errorLabel.setText("");
    }


    private void loadPageMouseEvent(String fxmlFileName , MouseEvent event) {
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


    // Method to redirect the user to the home page
    private void redirectToHomePage() {
        // Perform redirection logic here (e.g., load a new FXML file for the home page)
        // For demonstration purposes, let's just print a message
        System.out.println("Redirecting to home page...");
    }

    // Method to show an alert message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Authentication Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
    }


