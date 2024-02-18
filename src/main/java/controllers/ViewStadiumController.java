package controllers;

import entities.Stadium;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.StadiumDAO;

import java.io.IOException;
import java.util.List;

public class ViewStadiumController {

    @FXML
    private TableView<Stadium> stadiumTableView;
    @FXML
    private TableColumn<Stadium, String> referenceColumn;
    @FXML
    private TableColumn<Stadium, Double> heightColumn;
    @FXML
    private TableColumn<Stadium, Double> widthColumn;
    @FXML
    private TableColumn<Stadium, Double> priceColumn;
    @FXML
    private TableColumn<Stadium, Integer> rateColumn;
    @FXML
    private Button backButton;
    @FXML
    private Button addButton;

    private StadiumDAO stadiumDAO;
    int clubId;
    public int getClubId() {
        return clubId;
    }
    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public ViewStadiumController() {
        stadiumDAO = new StadiumDAO();
    }

    public void initialize() {
        // Load stadiums data
        loadStadiums();

        // Initialize table columns
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        widthColumn.setCellValueFactory(new PropertyValueFactory<>("width"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));

        // Set event handler for back button
        backButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ViewClub/ViewClub.fxml"));
                Scene scene = new Scene(root, 900, 700);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadStadiums() {
        List<Stadium> stadiums = stadiumDAO.findAllByClub(clubId);
        stadiumTableView.getItems().addAll(stadiums);
    }
}
