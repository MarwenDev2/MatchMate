package controllers;

import entities.PasswordGenerator;
import entities.VerificationCodeHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;



public class EnterCodeController {
    @FXML
    private Button Verify;

    @FXML
    private TextField codetxt;
    @FXML
    private Text go_backLink;

    String expectedCode = VerificationCodeHolder.getExpectedCode();




    public void VerifyCode(ActionEvent event) {

        // Get the verification code entered by the user
        String enteredCode = codetxt.getText().trim();


        // Check if the entered code matches the expected code
        if (enteredCode.equals(expectedCode)) {
            loadPage("/login/reset_password.fxml", event);

        } else {
            clearFields(new ActionEvent());
           showAlert("Wrong Verification Code ! ");

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
        codetxt.setText("");

    }


    public void load_code_page(MouseEvent mouseEvent) {
        loadPage("/login/forgot_password.fxml", mouseEvent);

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
