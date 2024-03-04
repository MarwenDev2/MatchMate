package controllers;

import entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import services.ImageService;
import services.PanierService;

import java.util.List;

public class CardController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label ReferenceLabel;
    @FXML
    private Label NameLabel;
    @FXML
    private Label PriceLabel;
    @FXML
    private Button addcartbutton;
    @FXML
    private ImageView productImageView;
    private CardsView cardsViewController;
    @FXML
    private TextField quantityLabel;
    private Product product;
    private ImageService imageService;

    private User currentUser= SessionManager.getInstance().getCurrentUser();

    public CardController() {
        imageService = new ImageService();
    }

    @FXML
    public void initialize(){
        Product p = new Product();
    }

    public void setData(Product product) {
        this.product = product;
        ReferenceLabel.setText(product.getReference());
        NameLabel.setText(product.getName());
        PriceLabel.setText(String.valueOf(product.getPrice()));
        List<Image>images=imageService.findByObjectId(product.getId(),"product");
        javafx.scene.image.Image image=new javafx.scene.image.Image(images.get(0).getUrl());
        productImageView.setImage(image);
    }



    // Reference to CardsView controller

    public void setCardsViewController(CardsView cardsViewController) {
        this.cardsViewController = cardsViewController;
    }



    public void addtocart(javafx.event.ActionEvent actionEvent) {
        try {
            PanierService panierService = new PanierService();
            Panier panier = new Panier(currentUser, product, Integer.parseInt(quantityLabel.getText()));
            panierService.save(panier);

            // Create and configure the alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Product was successfully added !");

            // Display the alert
            alert.showAndWait();
        } catch (NumberFormatException e) {
            // Handle the case where quantityLabel.getText() is not a valid integer
            e.printStackTrace(); // You might want to log this error for debugging purposes
            // Show an error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Quantity");
            alert.setContentText("Please enter a valid quantity.");

            alert.showAndWait();
        }

    }
}