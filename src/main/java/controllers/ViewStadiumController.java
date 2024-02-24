package controllers;

import entities.Club;
import entities.Stadium;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.StadiumDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewStadiumController {

    @FXML
    private TableView<Stadium> stadiumTableView;
    @FXML
    private TableColumn<Stadium, String> referenceColumn;
    @FXML
    private TableColumn<Stadium, Float> heightColumn;
    @FXML
    private TableColumn<Stadium, Float> widthColumn;
    @FXML
    private TableColumn<Stadium, Float> priceColumn;
    @FXML
    private TableColumn<Stadium, Integer> rateColumn;
    @FXML
    private TableColumn<Stadium, Void> actionsColumn;
    @FXML
    private Button backButton;
    @FXML
    private Button addButton;
    @FXML
    private Label viewClubsButton;

    private StadiumDAO stadiumDAO;
    List<Stadium> stadiums;
    private int clubId;
    public int getClubId() {
        return clubId;
    }
    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public ViewStadiumController() {
        stadiumDAO = new StadiumDAO();
        List<Stadium> stadiums = new ArrayList<>();
    }



    @FXML
    public void initialize() {
        clubId = SharedData.getClubId();
        // Load stadiums data
        loadStadiums();

        // Initialize table columns
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        widthColumn.setCellValueFactory(new PropertyValueFactory<>("width"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));

        actionsColumn.setCellFactory(createActionCellFactory());

        viewClubsButton.setOnMouseClicked(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) viewClubsButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/NewStadium/NewStadium.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Set event handler for back button
        backButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 1180.0, 655.0);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
    private void loadStadiums() {
        stadiums = stadiumDAO.findAllByClub(clubId);
        stadiumTableView.getItems().addAll(stadiums);
        System.out.println(stadiums);
    }

    private Callback<TableColumn<Stadium, Void>, TableCell<Stadium, Void>> createActionCellFactory() {
        return new Callback<TableColumn<Stadium, Void>, TableCell<Stadium, Void>>() {
            @Override
            public TableCell<Stadium, Void> call(TableColumn<Stadium, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.setOnAction(event -> {
                            Stadium s = getTableView().getItems().get(getIndex());
                            editStadium(s);
                        });

                        deleteButton.setOnAction(event -> {
                            Stadium s = getTableView().getItems().get(getIndex());
                            deleteStadium(s);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(editButton, deleteButton));
                        }
                    }
                };
            }
        };
    }

    private void deleteStadium(Stadium stadium){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirm Delete");
        confirmationAlert.setContentText("Are you sure you want to delete the stadium ?");

        // Add OK and Cancel buttons to the confirmation dialog
        confirmationAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the confirmation dialog and wait for user input
        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // User clicked OK, proceed with deletion
                boolean isDeleted = stadiumDAO.delete(stadium);
                if (isDeleted) {
                    // Remove the deleted club from the table view
                    stadiumTableView.getItems().remove(stadium);
                    showAlert("Success", "Stadium deleted successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to delete stadium.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    private void editStadium(Stadium stadium) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewStadium/NewStadium.fxml"));
            Parent root = loader.load();
            NewStadiumController newStadiumController = loader.getController();
            newStadiumController.populateFieldsWithStadium(stadium.getReference());
            Scene scene = new Scene(root, 1180.0, 655.0);
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
