package controllers;

import entities.EmailSender;
import entities.PasswordEncryption;
import entities.User;
import entities.UserEmailHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.UserDAO;

import java.io.IOException;

public class ResetPasswordController {

    @FXML
    private Text go_backLink;

    @FXML
    private Button loginbtn;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField newPasswordComfirmed;

    UserDAO US = new UserDAO() ;
    String userEmail = UserEmailHolder.getUserEmail();
    User user = UserDAO.getUserByEmail(userEmail) ;
    public void load_code_page(MouseEvent mouseEvent) {
        loadPage("/login/login1.fxml", mouseEvent);

    }



    public void login(ActionEvent event) {
        String newPwd = newPassword.getText();
        String confirmedPwd = newPasswordComfirmed.getText();
        if (!newPwd.isEmpty() && !confirmedPwd.isEmpty()) {
            if (newPwd.equals(confirmedPwd)) {

                // Hash the new password entered by the user
                String newpassword = PasswordEncryption.encryptPassword(newPwd);
                // Assuming 'user' is an instance of the User class representing the current user
                boolean passwordUpdated = UserDAO.updateUserPassword(newpassword, user);
                // Passwords match
                if (passwordUpdated) {
                    showAlert("Password updated successfully. Please login with your new password.");
                    loadPage("/login/login1.fxml" , event);
                } else {
                    // Failed to update password
                    showAlert("Failed to update password. Please try again later.");
                }
            } else {
                // Passwords do not match
                showAlert("Passwords do not match.");
            }
        } else {
            // Password fields are empty
            showAlert("Please enter both new password and confirm password.");
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

    private void loadPage(String fxmlFileName, MouseEvent event) {
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
