package controllers;


import entities.Image;
import entities.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ImageService;
import services.ProductService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class AjouterProductController implements Initializable
{

    private final ProductService ps = new ProductService();

    @FXML
    private Button afficher;
    @FXML
    private ImageView imageTF;
    @FXML
    private Button ajouter;
    @FXML
    private Button upload;
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
    @FXML
    private FlowPane imageFlowPane;

    @FXML
    private ScrollPane imageScrollPane;

    private List<Image> uploadedImages;
    private final String[] size = {"xS" , "S" , "M" ,"L","xL","xxL","xxxL","39","40","41","42","43","44","45","46","47" };
    private final String[] type = {"SHIRT" , "T-SHIRT" , "SNEAKERS" };

    private ImageService imageService;
    private List <Image> uploadImages;


    public AjouterProductController(){
        uploadImages = new ArrayList<>();
        imageService = new ImageService();


    }


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

            int idProduct = ps.add(p);
            saveImagesToDatabase(idProduct);
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
        uploadedImages = new ArrayList<>();
        sizeTF.getItems().addAll(size);
        sizeTF.setOnAction(this::getSize);

        typeTF.getItems().addAll(type);
        typeTF.setOnAction(this::getType);
    }

    @FXML
    private File selectedImageFile;

    // Méthode pour gérer le téléchargement d'une image depuis un bouton
    public void upload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers images", "*.png", "*.jpg", "*.gif")
        );

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            System.out.println("Image sélectionnée : " + selectedImageFile.getAbsolutePath());
        } else {
            System.out.println("Aucun fichier sélectionné.");
        }
    }

    public void uploadImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Images");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(imageFlowPane.getScene().getWindow());
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                String name = file.getName(); // Get the file name
                String url = file.toURI().toString(); // Get the file URL
                //String[] splitName = name.split("\\."); // Split the file name to get the extension
                //String type = splitName[splitName.length - 1]; // Get the file extension
                String type = "product";

                // Create an instance of your Image class
                Image image = new Image(name, url, type);
                uploadedImages.add(image); // Add Image to uploadedImages list
                addImageToFlowPane(image); // Call addImageToFlowPane() to display the Image
            }
        }
    }
    private void addImageToFlowPane(Image image) {
        ImageView imageView = new ImageView(image.getUrl());
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        HBox imageBox = new HBox(imageView);
        imageBox.setSpacing(10);

        Button removeButton = createRemoveImageButton(image, imageBox);
        removeButton.getStyleClass().add("remove-button");

        imageBox.getChildren().add(removeButton);
        imageFlowPane.getChildren().add(imageBox); // Add to the VBox

        // Set the VBox containing the FlowPane as the content of the ScrollPane
        imageScrollPane.setContent(imageFlowPane);

        // Ensure the ScrollPane is visible
        if (imageFlowPane.getChildren().isEmpty()) {
            // If no images, hide the ScrollPane
            imageScrollPane.setVisible(false);
        } else {
            // If images are present, show the ScrollPane
            imageScrollPane.setVisible(true);
        }
    }
    private Button createRemoveImageButton(Image image, HBox imageBox) {
    Button removeButton = new Button("Remove");
    removeButton.setOnAction(event -> {
        uploadedImages.remove(image);
        imageFlowPane.getChildren().remove(imageBox);

        // Determine the type of image (e.g., "club" or "stadium")
        String type = image.getType();

        // Call the appropriate DAO to delete the image from the database

            imageService.delete(image);

    });
    return removeButton;
}


    private void saveImagesToDatabase(int idProduct) {
        for (Image image : uploadedImages) {
            imageService.save(image, idProduct);
        }


    }}



