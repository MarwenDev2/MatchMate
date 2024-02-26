package controllers;


import entities.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import service.ProductService;



import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterProductController implements Initializable
{

    private final ProductService ps = new ProductService();

    @FXML
    private Button afficher;

    @FXML
    private Button ajouter;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField priceTF;

    @FXML
    private TextField quantityTF;

    @FXML
    private TextField referenceTF;

    @FXML
    private ChoiceBox<String> sizeTF;

    @FXML
    private ChoiceBox<String> typeTF;

    private final String[] size = {"xS" , "S" , "M" ,"L","xL","xxL","xxxL","39","40","41","42","43","44","45","46","47" };
    private final String[] type = {"SHIRT" , "T-SHIRT" , "SNEAKERS" };

    @FXML
    void ajouter(ActionEvent event) {
        String reference = referenceTF.getText();
        String name = nameTF.getText();
        String priceText = priceTF.getText();
        String quantityText = quantityTF.getText();
        String size = sizeTF.getValue();
        String type = typeTF.getValue();

        // Vérification de la référence
        if (reference.isEmpty() || !reference.matches("[A-Z0-9]+")) {
            afficherErreur("La référence doit être non vide et ne contenir que des lettres majuscules et des chiffres !");
            return;
        }

        // Vérification du nom
        if (name.isEmpty()) {
            afficherErreur("Le nom ne peut pas être vide !");
            return;
        }

        // Vérification de la quantité
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity > 100) {
                afficherErreur("La quantité ne peut pas dépasser 100 !");
                return;
            }
        } catch (NumberFormatException e) {
            afficherErreur("La quantité doit être un nombre entier !");
            return;
        }

        try {
            float price = Float.parseFloat(priceText);
            int quantity = Integer.parseInt(quantityText);
            Product p = new Product(reference, name, price, quantity, size, type);
            ps.add(p);
            System.out.println(p);
            afficherConfirmation("Produit ajouté avec succès !");
        } catch (NumberFormatException e) {
            afficherErreur("Veuillez entrer un prix valide !");
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }







    @FXML
    void Afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherProduct.fxml"));
            referenceTF.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }



    public void getSize (ActionEvent event){
        String Size = sizeTF.getValue();
        // genreTF.setText(Genre);
    }

    public void getType (ActionEvent event){
        String Type = typeTF.getValue();
        // genreTF.setText(Genre);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sizeTF.getItems().addAll(size);
        sizeTF.setOnAction(this::getSize);

        typeTF.getItems().addAll(type);
        typeTF.setOnAction(this::getType);
    }
}


