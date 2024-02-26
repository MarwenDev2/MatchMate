package controllers;

import entities.ProductClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import service.ProductClientService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherBoutiqueController {
        private  final ProductClientService ps=new ProductClientService();

        @FXML
        private HBox cardLayout;

        public void initialize(URL location, ResourceBundle resources) {
            List<ProductClient> sports = new ArrayList<>(ps.getAll());
            try {
                for (int i=0;i<sports.size();i++)
                {
                    FXMLLoader loader=new FXMLLoader();
                    loader.setLocation((getClass().getResource("/card.fxml")));

                    HBox cardBox=loader.load();
                    CardController cardController=loader.getController();
                    cardController.setData(sports.get(i));
                    cardLayout.getChildren().add(cardBox);


                }} catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
}
