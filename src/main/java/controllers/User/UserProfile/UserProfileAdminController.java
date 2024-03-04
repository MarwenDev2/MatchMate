package controllers.User.UserProfile;
import entities.PasswordEncryption;
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
import services.User.UserDAO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserProfileAdminController {

    @FXML
    private TextField new_mdp;

    @FXML
    private Label new_mdp_txt;

    @FXML
    private TextField old_mdp;

    @FXML
    private Label old_mdp_txt;

    @FXML
    private ImageView profile_Newimg_upload;


    @FXML
    private Button profile_change_password_confirm_btn;



    @FXML
    private TextField profile_email;

    @FXML
    private TextField profile_firstName;

    @FXML
    private TextField profile_lastName;



    @FXML
    private TextField profile_phone;


    @FXML
    private Label username;

    @FXML
    private TextField profile_roles;



    private UserDAO US = new UserDAO();

    public String imagePath = "";

    private User currentUser= SessionManager.getInstance().getCurrentUser();
    // Method to initialize the UI elements
    public void initialize() {
        System.out.println("logged admin user is :  " + currentUser.toString());
        // Initially hide the text fields of the password ( old + new )
        new_mdp.setVisible(false);
        old_mdp.setVisible(false);
        profile_change_password_confirm_btn.setVisible(false);
        new_mdp_txt.setVisible(false);
        old_mdp_txt.setVisible(false);

        // Populate the UI elements with the user information
        if (currentUser != null) {
            String currentUserName = currentUser.getFirstName() + " " + currentUser.getLastName();
            username.setText(currentUserName);

            //set profile fields automatically
            setUserFields(currentUser) ;

        }



    }

    private void setUserFields(User currentUser) {
        profile_email.setText(currentUser.getEmail());
        profile_firstName.setText(currentUser.getFirstName());
        profile_lastName.setText(currentUser.getLastName());
        profile_phone.setText(currentUser.getPhoneNumber());
        // Set ComboBox items with enum values
        ObservableList<Role> roles = FXCollections.observableArrayList(Role.values());
        // Set selected value to the one returned by getRole()
        profile_roles.setEditable(false);
        profile_roles.setText(currentUser.getRole().toString());
        profile_roles.setEditable(false);


        // Load and display profile image when reclicking the profile button after the update
        if (!imagePath.isEmpty()) {
            Image updatedImage = new Image(new File(imagePath).toURI().toString());
            profile_Newimg_upload.setImage(updatedImage);
        } else {
            File imageFile = new File(currentUser.getImage());
            Image img = new Image(imageFile.toURI().toString());
            profile_Newimg_upload.setImage(img);
        }

    }

    @FXML
    void changePassword(ActionEvent event) {
        // When the button is clicked, make the text fields visible
        new_mdp.setVisible(true);
        old_mdp.setVisible(true);
        new_mdp_txt.setVisible(true);
        old_mdp_txt.setVisible(true);
        profile_change_password_confirm_btn.setVisible(true);
    }

    @FXML
    void confirmPasswordChange(ActionEvent event) {
        String newPassword = new_mdp.getText();
        String oldPassword = old_mdp.getText();

        // Hash the old password entered by the user
        String hashedOldPassword = PasswordEncryption.encryptPassword(oldPassword);

        // Compare the hashed old password with the one in the database
        if (hashedOldPassword.equals(currentUser.getPassword())) {
            // Hash the new password and update it in the database
            String newHashedPassword = PasswordEncryption.encryptPassword(newPassword);

            if (US.updateUserPassword(newHashedPassword , currentUser)){
                // If the password update was successful, load the new page
                loadPage("/Login/login1.fxml", event);
            }
        } else {
            // Show an error message if the old password is incorrect
            System.out.println("Old password is incorrect.");
        }

    }

    @FXML
    void deleteUser(ActionEvent event) {
        // Get the selected user from the table view
        User current = currentUser ;
        System.out.println("the deleted user is : " +currentUser);

        //Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your user?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed deletion, call the service method to delete the user
            boolean deleted = US.deleteUserById(currentUser.getId());
            if (deleted) {
                showAlert("User deleted successfully.");
                // User deleted successfully, refresh the table view
                loadPage("/SignUp/signup.fxml", event);

            } else {
                showAlert("Failed to delete user.");
            }
        }

    }

    @FXML
    String handleLoadImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            profile_Newimg_upload.setImage(image); // Set the image to the ImageView
            imagePath = file.getAbsolutePath();
        }
        return  imagePath;
    }


    @FXML
    void logout(ActionEvent event) {
        // Invalidate the current session
        SessionManager.getInstance().invalidateSession(SessionManager.getInstance().getCurrentSessionId());
        loadPage("/Login/login1.fxml", event);
        System.out.println(currentUser.toString());

    }

    @FXML
    void seeProfile(ActionEvent event) {
        loadPage("/UserProfileAdmin/user_profile_admin.fxml", event);
    }
    public void seeUsers(ActionEvent actionEvent) {
        loadPage("/AdminDashboard/admin_dashboard.fxml", actionEvent);
    }

    @FXML
    void updateUser(ActionEvent event) {
        // Get data from the text fields and combo box
        String firstName =   profile_firstName.getText();
        String lastName = profile_lastName.getText();
        String phoneNumber = profile_phone.getText();
        String email = profile_email.getText();
        Role selectedRole = Role.fromString(profile_roles.getText());
        int id = currentUser.getId();
        String password = currentUser.getPassword();

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        if (!imagePath.isEmpty()) {
            // Create a new user object with the updated data
            User updatedUser = new User(id, firstName, lastName, phoneNumber, email, password, selectedRole, imagePath);
            List<String> validationErrors = US.validate_User_Input(updatedUser);
            if (!validationErrors.isEmpty()) {
                // Display validation errors to the user
                showAlertInput(validationErrors);

            }

            boolean success = US.updateUser(updatedUser);
            // Call the updateCurrentUser method
            SessionManager.getInstance().updateCurrentUser(updatedUser);
            if (success) {
                showAlert("User updated successfully.");
                loadPage("/UserProfileAdmin/user_profile_admin.fxml", event);
            }
        } else {
            // Create a new user object with the old data
            User updatedUser = new User(id, firstName, lastName, phoneNumber, email, password, selectedRole, currentUser.getImage());
            List<String> validationErrors = US.validate_User_Input(updatedUser);
            if (!validationErrors.isEmpty()) {
                // Display validation errors to the user
                showAlertInput(validationErrors);

            }
            boolean success = US.updateUser(updatedUser);
            // Call the updateCurrentUser method
            SessionManager.getInstance().updateCurrentUser(updatedUser);

            if (success) {
                showAlert("User updated successfully.");
                loadPage("/UserProfileAdmin/user_profile_admin.fxml", event);
            }
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
    // Method to show an alert message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    public void load_users_page(ActionEvent actionEvent) {
        loadPage("/AdminDashboard/admin_dashboard.fxml", actionEvent);
    }
}
