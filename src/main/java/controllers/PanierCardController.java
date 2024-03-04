package controllers;

import entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import services.ImageService;
import services.PanierService;

import java.io.IOException;
import java.util.List;

public class PanierCardController {
    @FXML
    private Label referenceLabel;
    @FXML
    private TextField quantityLabel;
    @FXML
    private Label idUserLabel;

    @FXML
    private Label totalLabel;
    @FXML
    private ImageView imagepanier;

    private ImageService imageService;
    private Panier panier;
    private Product p=new Product();

    private User currentUser= SessionManager.getInstance().getCurrentUser();

    public PanierCardController() {
        imageService = new ImageService();
    }

    public void setData(Panier panier) {
        this.panier = panier;
        referenceLabel.setText(panier.getIdProuct().getReference());
        idUserLabel.setText(String.valueOf(panier.getIdUser()));
        quantityLabel.setText(String.valueOf(panier.getQuantity()));

        totalLabel.setText(String.valueOf(panier.getQuantity()*panier.getIdProuct().getPrice()));
        System.out.println(panier.getIdProuct());

        List<Image> images = imageService.findByObjectId(panier.getIdProuct().getId(), "product");
        System.out.println(images);
        if (!images.isEmpty()) {
            javafx.scene.image.Image image = new javafx.scene.image.Image(images.get(0).getUrl());
            imagepanier.setImage(image);
        }
    }
public Panier getdata()
{
   return panier;

}



    public void handleDelete(javafx.event.ActionEvent actionEvent) throws IOException {
        PanierService panierService = new PanierService();
        if (panier != null) {
            System.out.println(panier);
            panierService.delete(panier);
            HBox parent = (HBox) imagepanier.getParent().getParent();
            parent.getChildren().remove(imagepanier.getParent());
        } else {
            System.out.println("Error: Panier object is null");
        }
    }


    public void handleModify(javafx.event.ActionEvent actionEvent) {
        int quant = Integer.valueOf(quantityLabel.getText());
        PanierService panierService = new PanierService();
        Panier updatedPanier = new Panier(panier.getId(), currentUser, panier.getIdProuct(), quant);
        panierService.Update(updatedPanier);

        // Update the data in the card
        setData(updatedPanier);
    }

}
