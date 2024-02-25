package controller;

import Service.EventService;
import entities.Evenement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuCardEvent implements Initializable {
    @FXML
    private AnchorPane Menu_Form;

    @FXML
    private GridPane Menu_Grid;

    @FXML
    private ScrollPane Menu_Scroll;
    EventService eventService=new EventService();
    public java.util.List<Evenement> getlist() throws SQLException {

        return  eventService.ReadAll();
    }
    private List<Evenement> events=new ArrayList<>();

    private List<Evenement> getData() throws SQLException {

         List <Evenement>ev = getlist();
        return ev;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int column = 0;
        int row = 1;
        try {
            events.addAll(getData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            for(int i=0;i<events.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CardViewEvent.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                    CardViewEvent cardController = fxmlLoader.getController();
                cardController.setdata(events.get(i));

                if (column == 2){
                    column=0;
                    row++;
                }

                Menu_Grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane,new Insets(10));



            }} catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    }

