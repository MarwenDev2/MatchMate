package controllers;

import entities.Image;
import entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import services.ImageService;

import java.awt.event.ActionEvent;
import java.util.List;

public class CardOrderController {
    @FXML
    private Button addcartbutton;

    @FXML
    private ComboBox<String> comboL;

    @FXML
    private ImageView imageP;

    @FXML
    private Label priceL;

    @FXML
    private Label refL;
    private Product product;

    public void setData(Product product) {
        this.product = product;
        refL.setText(product.getReference());
        priceL.setText(String.valueOf(product.getPrice()));
        ImageService imageService1 = new ImageService();

        List<Image> images1 = imageService1.findByObjectId(product.getId(), "product");
        if(!images1.isEmpty())
        {
            String imageUrl=images1.get(0).getUrl();
            javafx.scene.image.Image image=new javafx.scene.image.Image(imageUrl);
            imageP.setImage(image);
        }
        // Load and display images




    }

    @FXML
    void addtocart(ActionEvent event) {

    }

}
