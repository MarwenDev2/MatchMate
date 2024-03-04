package controllers.User.Authentification;

import entities.SessionManager;
import entities.Role;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.User.UserDAO;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupController {


    @FXML
    private TextField Emailtxt;

    @FXML
    private TextField FirstNametxt;

    @FXML
    private TextField LastNametxt;

    @FXML
    private TextField Passwordtxt;

    @FXML
    private Button btnImg;

    @FXML
    private Button btnLinkGoogle;

    @FXML
    private Button btnSignUp;

    @FXML
    private RadioButton fieldownerbtn;

    @FXML
    private ImageView imagepath;

    @FXML
    private Label linkHomePage;

    @FXML
    private Label linkSignIn;

    @FXML
    private TextField phoneNumbertxt;

    @FXML
    private RadioButton playerbtn;

    private ToggleGroup toggleGroup;
    public UserDAO userDAO = new UserDAO();

    // Initialize the SessionManager
    private SessionManager sessionManager = SessionManager.getInstance();

    public String imagePath = "";
    @FXML
    private Label errorMessageLabel;


    @FXML
    void initialize() {
        // Initialize the toggle group
        toggleGroup = new ToggleGroup();
        // Add radio buttons to the toggle group
        fieldownerbtn.setToggleGroup(toggleGroup);
        playerbtn.setToggleGroup(toggleGroup);

        // Set a listener to handle when no radio button is selected
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // Display an error message using your showAlert method
                showAlert("Please select one of the options.");
            }
        });

        //handle the already sign in option

            linkSignIn.setOnMouseClicked(this::handleSignUpLinkClick);


    }
    @FXML
    private void handleSignUpLinkClick(MouseEvent event) {
        // Load the sign-up page
        loadPageMouseEvent("/Login/login1.fxml", event);
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


    @FXML
    void handleLoadImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imagepath.setImage(image); // Set the image to the ImageView
        }
        imagePath = file.getAbsolutePath();

    }

    @FXML
    void signUp(ActionEvent event) {

        // Retrieve user input from text fields
        String firstName = FirstNametxt.getText();
        String lastName = LastNametxt.getText();
        String email = Emailtxt.getText();
        String password = Passwordtxt.getText();
        String phoneNumber = phoneNumbertxt.getText();
        boolean isFieldOwner = fieldownerbtn.isSelected();
        boolean isPlayer = playerbtn.isSelected();


        // Determine the role based on the selected radio button
        Role rolee ;
        if (fieldownerbtn.isSelected()) {
            rolee = Role.fieldOwner;
        } else if (playerbtn.isSelected()) {
            rolee = Role.player;
        } else {
            // Handle the case where no role is selected
            rolee = Role.player; // Set a default role baed lezem nbadlou ki nkamel el controel de saisie
        }
        User newUser = new User(firstName,lastName,phoneNumber,email,password,rolee,imagePath);


        // Validate user input
        Map<String, String> validationErrors = validate_User_Input(newUser);
        if (!validationErrors.isEmpty()) {
            // Display validation errors next to their corresponding text fields
            displayValidationErrors(validationErrors);
            return;
        }
        if(userDAO.userExists(email)){
            showAlert("Sorry This Email already exists ! ");
            Emailtxt.setText("");
        }else {
            // Use the UserService to authenticate the user
            int userAdd = userDAO.addUser(newUser);

            // Check if user addition was successful
            if (userAdd!=0) {
                // Generate a session ID for the user
                String sessionId = SessionManager.generateSessionId();

                // Get the SessionManager instance
                SessionManager sessionManager = SessionManager.getInstance();

                // Add the session ID and user to the SessionManager
                SessionManager.addSession(sessionId, newUser , userAdd);

                // Store the session ID in SessionManager
                sessionManager.setCurrentSessionId(sessionId);
                // Print the current user's information
                System.out.println(sessionManager.getCurrentUser());

                //// if i want to retreive the user later on
                //user currentUser = sessionManager.getCurrentUser();
                //if (currentUser != null) {
                //    // Now you have access to the current user object

                // Check if user addition was successful
                // Call the loadPage() method based on the user's role
                switch (rolee) {
                    case player:
                        loadPage("/UserProfilePlayer/user_profile_player.fxml", event);
                        break;
                    case fieldOwner:
                        loadPage("/UserProfileFiledOwner/user_profile_fieldOwner.fxml", event);
                        break;
                    case Admin:
                        loadPage("/AdminDashboard/admin_dashboard.fxml", event);
                        break;
                }

            }

        }


    }


    public Map<String, String> validate_User_Input(User user) {
        Map<String, String> errorMessages = new HashMap<>();
        validateAndAddError(user.getFirstName(), "FirstNametxt", "First name", errorMessages);
        validateAndAddError(user.getLastName(), "LastNametxt", "Last name", errorMessages);
        validateAndAddError(user.getPhoneNumber(), "phoneNumbertxt", "Phone number", errorMessages);
        validateAndAddError(user.getEmail(), "Emailtxt", "Email", errorMessages);
        validateAndAddError(user.getPassword(), "Passwordtxt", "Password", errorMessages);
        // You may need to convert role to string for validation, assuming it's stored as a string
        validateAndAddError(user.getImage(), "imagepath","image", errorMessages);
        return errorMessages;
    }
    private void validateAndAddError(String fieldValue, String fieldId, String fieldName, Map<String, String> errorMessages) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            errorMessages.put(fieldId, fieldName + " is required.");
        } else if ("Email".equals(fieldName) && !userDAO.isValidEmail(fieldValue)) {
            errorMessages.put(fieldId, "Invalid email format.");
        } else if ("Phone number".equals(fieldName) && !userDAO.isValidPhoneNumber(fieldValue)) {
            errorMessages.put(fieldId, "Phone number must be exactly 8 digits.");
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
                case "FirstNametxt":
                    displayErrorMessage(FirstNametxt, errorMessage);
                    break;
                case "LastNametxt":
                    displayErrorMessage(LastNametxt, errorMessage);
                    break;
                case "phoneNumbertxt":
                    displayErrorMessage(phoneNumbertxt, errorMessage);
                    break;
                case "Emailtxt":
                    displayErrorMessage(Emailtxt, errorMessage);
                    break;
                case "Passwordtxt":
                    displayErrorMessage(Passwordtxt, errorMessage);
                    break;
                case "imagepath":
                    displayErrorMessage(imagepath, errorMessage);
                    break;

            }
        }
    }
    private void displayErrorMessage(Node node, String errorMessage) {
        if (node instanceof TextField) {
            TextField textField = (TextField) node;
            Label errorLabel = getErrorLabelForTextField(textField);
            errorLabel.setText(errorMessage);
        } else if (node instanceof ImageView) {
            ImageView imageView = (ImageView) node;
            Label errorLabel = getErrorLabelForImageView(imageView);
            errorLabel.setText(errorMessage);
        } else {
            // Handle unknown node type or throw an exception
        }
    }

    private Label getErrorLabelForImageView(ImageView imageView) {
        // Construct the ID of the error label based on the ID of the image view
        String errorLabelId = imageView.getId() + "ErrorLabel";
        // Find the error label by its ID in the scene graph
        return (Label) imageView.getScene().lookup("#" + errorLabelId);
    }

    private Label getErrorLabelForTextField(TextField textField) {
        // Construct the ID of the error label based on the ID of the text field
        String errorLabelId = textField.getId() + "ErrorLabel";
        // Find the error label by its ID in the scene graph
        return (Label) textField.getScene().lookup("#" + errorLabelId);
    }


    private void clearErrorMessages() {
        // Clear error labels next to text fields

        // Clear error label next to First Name text field
        clearErrorLabel(FirstNametxt);
        // Clear error label next to Last Name text field
        clearErrorLabel(LastNametxt);
        // Clear error label next to Email text field
        clearErrorLabel(Emailtxt);
        // Clear error label next to Password text field
        clearErrorLabel(Passwordtxt);
        // Clear error label next to Phone Number text field
        clearErrorLabel(phoneNumbertxt);
        // Clear error label next to other text fields as needed
    }

    private void clearErrorLabel(TextField textField) {
        // Retrieve the error label associated with the text field
        Label errorLabel = getErrorLabelForTextField(textField);
        // Clear the error message text
        errorLabel.setText("");
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


    // Utility method to show alert messages
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }





}



