package controllers;

import entities.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import services.ImageService;
import services.ProductService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CardsView implements Initializable {

    @FXML
    private VBox CardsContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button addcartbutton;
    @FXML
    private VBox cardProduct;
    @FXML
    private ComboBox<?> comboL;

    @FXML
    private ImageView imageL;

    @FXML
    private Label priceL;

    @FXML
    private Label refL;
    @FXML
    private AnchorPane AnchorPane1;
    @FXML
    private TextField SearchLabel;
    private ProductService productService;
    @FXML
    private ScrollPane Menu_Scroll;
    @FXML
    private GridPane Menu_Grid;
    @FXML
    private AnchorPane rootPane;
    private ImageService imageService;
    @FXML
    private Label ReferenceLabel;
    @FXML
    private Label NameLabel;
    @FXML
    private Label PriceLabel;
    @FXML
    private Button commanderButton;
    @FXML
    private ImageView productImageView;

    private Product product;
    private CardsView cardsView;
    private List<Product> products = new ArrayList<>(); // Déclarer la liste en tant qu'attribut de classe

    private int productId;


    public int getProductIdId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public List<Product> getList() throws SQLException {
        return productService.getAll();
    }

    public CardsView() {
        productService = new ProductService();
        // Ne pas réinitialiser une nouvelle liste ici, utiliser directement l'attribut de classe
        // List<Product> products = new ArrayList<>();
    }

    private List<Product> getData() throws SQLException {
        return getList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int column = 0;
        int row = 1;
        try {
            products.addAll(getData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Menu_Grid.setPadding(new Insets(20)); // Increase padding for the entire grid
        Menu_Grid.setHgap(150); // Set horizontal gap between grid cells
        Menu_Grid.setVgap(20); // Set vertical gap between grid cells

        try {
            for (int i = 0; i < products.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                CardController cardController = fxmlLoader.getController();
                cardController.setData(products.get(i));

                if (column == 2) {
                    column = 0;
                    row++;
                }

                Menu_Grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
                Menu_Grid.setHgap(180); // Espacement horizontal de 10 pixels entre les colonnes
                Menu_Grid.setVgap(10); // Espacement vertical de 10 pixels entre les lignes

                // Set margin for each pane
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



/*    public void setData(Product product) {
        this.product = product;
        ReferenceLabel.setText(product.getReference());
        NameLabel.setText(String.valueOf(product.getName()));
        PriceLabel.setText(String.valueOf(product.getPrice()));

        List<Image> images = imageService.findByObjectId(product.getId(), "product");
        if (!images.isEmpty()) {
            String imageUrl = images.get(0).getUrl();
            javafx.scene.image.Image image = new javafx.scene.image.Image(imageUrl);
            productImageView.setImage(image);
        }
    } */

    @FXML
    private void handleCommander() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardOrder.fxml"));
            VBox content = loader.load();

            // Pass any data needed to the controller of the new content
            // e.g., loader.<CardOrderController>getController().setData(...);

            addContentToScrollPane(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addContentToScrollPane(Node content) {
        VBox container = new VBox();
        container.getChildren().add(content);
        scrollPane.setContent(container);
    }
    public void refreshGrid() {
        Menu_Grid.getChildren().clear(); // Clear existing cards
        int column = 0; // Declare column variable
        int row = 1; // Declare row variable
        try {
            products.clear();
            products.addAll(getData());
            for (int i = 0; i < products.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(products.get(i));
                if (column == 2) {
                    column = 0;
                    row++;
                }
                Menu_Grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void HandleSearch(KeyEvent event) {
        String searchText = SearchLabel.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            refreshGrid();
        } else {
            ProductService productService = new ProductService();
            // Clear existing cards
            Menu_Grid.getChildren().clear();

            // Retrieve events by name using EventService
            List<Product> filteredProducts = productService.rechercheParRef(searchText);
            // Display filtered events
            int column = 0;
            int row = 1;
            try {
                for (int i = 0; i < filteredProducts.size(); i++) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/Card.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    CardController cardController = fxmlLoader.getController();
                    cardController.setData(filteredProducts.get(i));

                    if (column == 2) {
                        column = 0;
                        row++;
                    }

                    Menu_Grid.add(anchorPane, column++, row);
                    GridPane.setMargin(anchorPane, new Insets(10)); // Set margin for each pane
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private Button checkcartbutt;

    @FXML
    void checkcart(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/PanierView.fxml"));
            checkcartbutt.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            // Handle the exception appropriately (e.g., show an error message)
        }
    }


}




