package controllers;

import entities.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.ProductService;



import java.util.List;

public  class AfficherProductController implements initialize {

    private final ProductService ps = new ProductService();
    @FXML
    private TableColumn<Product, Integer> Cid;

    @FXML
    private Button Cmodif;

    @FXML
    private TableColumn<Product, String> Cname;

    @FXML
    private TableColumn<Product, Float> Cprice;

    @FXML
    private TableColumn<Product, Integer> Cquantity;

    @FXML
    private Button Crecherche;

    @FXML
    private TableColumn<Product, String> Cref;

    @FXML
    private TableColumn<Product, String> Csize;

    @FXML
    private Button Csupp;

    @FXML
    private TableView<Product> Ctable;

    @FXML
    private Button Ctrier;

    @FXML
    private TableColumn<Product, String> Ctype;

    @FXML
    private TextField nameT;

    @FXML
    private TextField priceT;

    @FXML
    private TextField quantityT;

    @FXML
    private TextField recherche;

    @FXML
    private TextField refT;

    @FXML
    private ChoiceBox<String> sizeT;

    @FXML
    private ChoiceBox<String> typeT;


    private final String[] size = {"xS" , "S" , "M" ,"L","xL","xxL","xxxL","39","40","41","42","43","44","45","46","47" };
    private final String[] type = {"SHIRT" , "T-SHIRT" , "SNEAKERS" };





    @FXML
    void initialize() {
        List<Product> poste = ps.getAll();
        ObservableList<Product> observableList = FXCollections.observableList(poste);
        Ctable.setItems(observableList);

        Cid.setCellValueFactory(new PropertyValueFactory<>("id"));
        Cref.setCellValueFactory(new PropertyValueFactory<>("reference"));
        Cname.setCellValueFactory(new PropertyValueFactory<>("name"));
        Cprice.setCellValueFactory(new PropertyValueFactory<>("price"));
        Cquantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        Csize.setCellValueFactory(new PropertyValueFactory<>("size"));
        Ctype.setCellValueFactory(new PropertyValueFactory<>("type"));

        typeT.getItems().addAll(type);
        typeT.setOnAction(this::getType);

        sizeT.getItems().addAll(size);
        sizeT.setOnAction(this::getSize);
    }

    public void getType (ActionEvent event){
        String Type = typeT.getValue();
        // genreTF.setText(Genre);
    }

    public void getSize (ActionEvent event){
        String Size = sizeT.getValue();
        // genreTF.setText(Genre);
    }



    @FXML
    public void selection(javafx.scene.input.MouseEvent mouseEvent) {
        Product selectedProduct = Ctable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {

            refT.setText(selectedProduct.getReference());
            nameT.setText(selectedProduct.getName());
            priceT.setText(String.valueOf(selectedProduct.getPrice()));
            quantityT.setText(String.valueOf(selectedProduct.getQuantity()));
            sizeT.setValue(selectedProduct.getSize());
            typeT.setValue(selectedProduct.getType());
        }
    }
    @FXML
    void supprimerLigne(Product product) {
        Ctable.getItems().remove(product);
        ps.delete(product);
    }

    public void supprimer(ActionEvent actionEvent) {
        Product product = Ctable.getSelectionModel().getSelectedItem();

        if (product != null) {
            supprimerLigne(product);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une ligne à supprimer.");
            alert.showAndWait();
        }

    }

    public void modifier(ActionEvent actionEvent) {
        // Vérifier si un élément est sélectionné dans la TableView
        Product productSelectionne = Ctable.getSelectionModel().getSelectedItem();
        if (productSelectionne != null) {
            // Récupérer les valeurs des champs de texte
            String reference = refT.getText();
            String name = nameT.getText();
            Float price = Float.parseFloat(priceT.getText());
            Integer quantity = Integer.parseInt(quantityT.getText());
            String size = sizeT.getValue();
            String type = typeT.getValue();



            // Mettre à jour les informations du tournoi sélectionné
            productSelectionne.setReference(reference);
            productSelectionne.setName(name);
            productSelectionne.setPrice(price);
            productSelectionne.setQuantity(quantity);
            productSelectionne.setQuantity(quantity);
            productSelectionne.setSize(size);
            productSelectionne.setType(type);




            // Mettre à jour la TableView avec les modifications
            Ctable.refresh();

            // Vous pouvez également appeler votre méthode de mise à jour dans le service ici
            ProductService ps = new ProductService();
            ps.Update(productSelectionne);

            // Réinitialiser les champs de texte après la modification

            refT.clear();
            nameT.clear();
            priceT.clear();
            quantityT.clear();
            sizeT.getItems().clear();
            typeT.getItems().clear();



        } else {
            // Afficher un message d'erreur si aucun élément n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un élément à modifier.");
            alert.showAndWait();
        }
    }
    @FXML
    public void trier (ActionEvent actionEvent){
        List<Product> product = ps.triProductByReference();
        ObservableList<Product> observableList = FXCollections.observableList(product);
        Ctable.setItems(observableList);

        Cref.setCellValueFactory(new PropertyValueFactory<>("reference"));
        Cname.setCellValueFactory(new PropertyValueFactory<>("name"));
        Cprice.setCellValueFactory(new PropertyValueFactory<>("price"));
        Cquantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        Csize.setCellValueFactory(new PropertyValueFactory<>("size"));
        Ctype.setCellValueFactory(new PropertyValueFactory<>("type"));
    }


    public void recherche(ActionEvent actionEvent) {
        String refRecherche = recherche.getText(); // Obtenir la ref à rechercher depuis le champ de texte
        List<Product> resultats = ps.rechercheParRef(String.valueOf(refRecherche)); // Appel de la méthode rechercheParRef
        ObservableList<Product> observableResultats = FXCollections.observableList(resultats);
        Ctable.setItems(observableResultats);

    }
}
