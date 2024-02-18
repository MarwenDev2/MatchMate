package controllers;

import entities.Club;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ClubDAO;

import javafx.util.Callback;
import java.io.IOException;
import java.sql.Time;
import java.util.List;


public class ViewClubController {
    @FXML
    private TableView<Club> clubTableView;
    @FXML
    private TableColumn<Club, String> nameColumn;
    @FXML
    private TableColumn<Club, Float> heightColumn;
    @FXML
    private TableColumn<Club, Float> widthColumn;
    @FXML
    private TableColumn<Club, Time> startTimeColumn;
    @FXML
    private TableColumn<Club, Time> endTimeColumn;
    @FXML
    private TableColumn<Club, Integer> stadiumNbrColumn;
    @FXML
    private TableColumn<Club, String> descriptionColumn;
    @FXML
    private TableColumn<Club, Void> actionsColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button viewStadiumsButton;

    private ClubDAO clubDAO;
    private int userId=5;

    public ViewClubController() {
        clubDAO = new ClubDAO();
    }
    @FXML
    public void initialize() {

        // Fetch clubs by user ID and populate TableView
        populateTableView();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        widthColumn.setCellValueFactory(new PropertyValueFactory<>("width"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        stadiumNbrColumn.setCellValueFactory(new PropertyValueFactory<>("stadiumNbr"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        addButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/NewClub/NewClub.fxml"));
                Scene scene = new Scene(root, 900, 700);
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        actionsColumn.setCellFactory(createActionCellFactory());

        // Add selection listener to the table view
        clubTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // A club is selected, show the "View stadiums" button
                viewStadiumsButton.setVisible(true);
            } else {
                // No club is selected, hide the "View stadiums" button
                viewStadiumsButton.setVisible(false);
            }
        });

        // Set action for the "View stadiums" button
        viewStadiumsButton.setOnAction(event -> {
            Club selectedClub = clubTableView.getSelectionModel().getSelectedItem();
            if (selectedClub != null) {
                openViewStadiums(selectedClub.getId());
            }
        });
    }
    private Callback<TableColumn<Club, Void>, TableCell<Club, Void>> createActionCellFactory() {
        return new Callback<TableColumn<Club, Void>, TableCell<Club, Void>>() {
            @Override
            public TableCell<Club, Void> call(TableColumn<Club, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.setOnAction(event -> {
                            Club club = getTableView().getItems().get(getIndex());
                            editClub(club);
                        });

                        deleteButton.setOnAction(event -> {
                            Club club = getTableView().getItems().get(getIndex());
                            deleteClub(club);
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

    private void deleteClub(Club club) {
        // Implement delete functionality here
    }

    private void populateTableView() {
        List<Club> userClubs = clubDAO.findByUser(userId);
        clubTableView.getItems().addAll(userClubs);
    }

    private void editClub(Club club) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewClub/NewClub.fxml"));
            Parent root = loader.load();
            NewClubController newClubController = loader.getController();
            newClubController.populateFieldsWithClubData(club);
            Scene scene = new Scene(root, 900, 700);
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openViewStadiums(int clubId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStadium/ViewStadium.fxml"));
            Parent root = loader.load();
            ViewStadiumController viewStadiumController = loader.getController();
            viewStadiumController.setClubId(clubId); // Pass the selected club's ID
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Callback<TableColumn<Club, Void>, TableCell<Club, Void>> createActionsCellFactory() {
        return new Callback<TableColumn<Club, Void>, TableCell<Club, Void>>() {
            @Override
            public TableCell<Club, Void> call(TableColumn<Club, Void> param) {
                return new TableCell<Club, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        // Handle edit button action
                        editButton.setOnAction(event -> {
                            // Get the Club object associated with this row
                            Club club = getTableView().getItems().get(getIndex());
                            // Implement edit action here...
                        });

                        // Handle delete button action
                        deleteButton.setOnAction(event -> {
                            // Get the Club object associated with this row
                            Club club = getTableView().getItems().get(getIndex());
                            // Implement delete action here...
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Set buttons as graphic if the row is not empty
                            setGraphic(new HBox(editButton, deleteButton));
                        }
                    }
                };
            }
        };
    }
}
