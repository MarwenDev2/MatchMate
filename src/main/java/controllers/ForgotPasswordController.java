package controllers;



import entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import services.UserDAO;

import java.io.IOException;


public class ForgotPasswordController {
    @FXML
    private Button SendPasswordbtn;

    @FXML
    private TextField emailtxt;

    UserDAO userDAO = new UserDAO();


    public void SendPassword(ActionEvent event) {
        String email = emailtxt.getText();

        // Check if the email exists in the database
        User user = UserDAO.getUserByEmail(email);
        if (user != null) {
           /* // Generate a new password for the user
            String newPassword = PasswordGenerator.generateRandomPassword(10); // Implement your password generation logic here
            // Hash the new genrated password
            String hashedNewPassword = PasswordEncryption.encryptPassword(newPassword);
            // Update user's password in the database
            boolean passwordUpdated = UserDAO.updateUserPassword(hashedNewPassword, user);
            if (passwordUpdated) {*/
            UserEmailHolder.setUserEmail(email);
            String userEmail = email;
            String VerificationCode = PasswordGenerator.generateRandomPassword(10); // Implement your password generation logic here
            VerificationCodeHolder.setExpectedCode(VerificationCode); //store the code
                String subject = "MatchMate Verification Code  ";
                String messageBody = "Here are the details of your request :\n\n"
                        + "Verification Code : "+ VerificationCode +" \n\n"
                        + "Your verification code has been generated successfully.\n\n You can now proceed to the MatchMate application and use this code to RESET YOUR PASSWORD .";
                EmailSender.sendEmail(userEmail, subject, messageBody);
            loadPage("/Login/enter_code.fxml", event);

        }
         else {
            // Display an error message if the email does not exist in the database
            showAlert("Email address not found. Please enter a valid email.");
        }
        
    }

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


    public void load_code_page(MouseEvent mouseEvent) {
        loadPage("/login/login1.fxml", mouseEvent);
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
}
