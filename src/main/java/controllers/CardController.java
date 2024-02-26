package controllers;

import entities.ImageProduct;
import entities.Product;
import entities.ProductClient;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import javax.swing.text.html.ImageView;
import java.awt.*;

public class CardController {

        @FXML
        private ImageView imageCard;

        @FXML
        private Label nameCard;

        @FXML
        private Label priceCard;

        @FXML
        private Label refCard;

        @FXML
        private Label typeCard;

        public  void setData(ProductClient pc)
        {
                Image image=new Image(getClass().getResourceAsStream(ProductClient.getImage()));
                pc.setImage(String.valueOf(image));
                refCard.setText(pc.getRef());
                nameCard.setText(pc.getName());
                //priceCard.setText(String.valueOf(Float.parseFloat(pc.getPrice())));
                typeCard.setText(pc.getType());
        }

    }
