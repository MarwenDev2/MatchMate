package controller;

import Service.EventService;
import Service.ImageService;
import entities.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import entities.Evenement;

import static controller.SharedData.clubId;

public class UpdateEvent  implements Initializable {
    ObservableList<Evenement> list;
    ;
    EventService eventService;
    @FXML
    private Button DeleteButton;
    @FXML
    private Button EventButton;
    private Image selectedImage;
    Evenement event;
    @FXML
    private TableColumn<Evenement, Float> PriceColumn;

    @FXML
    private TextField PriceField;
    @FXML
    private ImageView imageView;
    @FXML
    private TableColumn<Evenement, Integer> ParticipantColumn;
    @FXML
    private TableColumn<Evenement, Date> EndDateColumn;
    @FXML
    private TableColumn<Evenement, String> ClubColumn;

    @FXML
    private DatePicker EndDateSelector;

    @FXML
    private Button HomeButton;

    @FXML
    private TableColumn<Evenement, Integer> IdColumn;

    @FXML
    private TableColumn<Evenement, Float> ImageColumn;

    @FXML
    private TableColumn<Evenement, String> NameColumn;

    @FXML
    private TextField NameField;

    @FXML
    private TableColumn<Evenement, Date> StartDateColumn;

    @FXML
    private DatePicker StartDateSelector;

    @FXML
    private TableView<Evenement> tableView;

    @FXML
    private TextField ParticpantField;
    @FXML
    private Label ParticipatorsLabel;

    @FXML
    private Label NameLabel;

    @FXML
    private Label PriceLabel;

    @FXML
    private Label StartLabel;

    @FXML
    private Label EndLabel;

    @FXML
    private Button SearchButton;

    @FXML
    private TextField SearchField;

    @FXML
    private Button ChosePictureButton;
    @FXML
    private Button UpdateButton;

    private static Club club;
    public static void getClub(Club c) {
        club = c;
    }
    public boolean getErrors() {
        clearErrorLabels(); // Clear any previous error messages

        boolean hasErrors = false;

        // Validate NameField
        if (NameField.getText().isBlank()) {
            setNameError("Invalid name");
            hasErrors = true;
        }

        // Validate Start Date and End Date
        LocalDate startDate = StartDateSelector.getValue();
        LocalDate endDate = EndDateSelector.getValue();

        if (startDate == null || endDate == null) {
            setStartDateError("Select Start and End Date");
            hasErrors = true;
        } else {
            LocalDate currentDate = LocalDate.now();
            if (startDate.isAfter(endDate)) {
                setEndDateError("End Date need to be Upper or equals  StartDate");
                hasErrors = true;
            }
            if (startDate.isBefore(currentDate)) {
                setStartDateError("Start Date need to be after current date or equals currentdate");
                hasErrors = true;
            }
        }

        // Validate Nombre Participant
        if (!Pattern.matches("\\d{1,3}", ParticpantField.getText())) {
            setParticipantError("invalid participators number");
            hasErrors = true;
        }
        if (!Pattern.matches("\\d{1,4}(\\.\\d{1,3})?", PriceField.getText())) {
            setPriceError("invlid price form XXXX.XXX");
            hasErrors = true;
        }



        return hasErrors;
    }


    private void clearErrorLabels() {
        EndLabel.setText("");
        StartLabel.setText("");
        NameLabel.setText("");
        ParticipatorsLabel.setText("");
        PriceLabel.setText("");
    }

    private void setNameError(String message) {
        NameLabel.setTextFill(Color.RED);
        NameLabel.setText(message);
    }
    private void setPriceError(String message) {
        PriceLabel.setTextFill(Color.RED);
        PriceLabel.setText(message);
    }
    private void setStartDateError(String message) {
        StartLabel.setTextFill(Color.RED);
        StartLabel.setText(message);
    }

    private void setEndDateError(String message) {
        EndLabel.setTextFill(Color.RED);
        EndLabel.setText(message);
    }

    private void setParticipantError(String message) {
        ParticipatorsLabel.setTextFill(Color.RED);
        ParticipatorsLabel.setText(message);
    }




    @FXML
    void RowClicked(MouseEvent event) {
        Evenement clickedEvenement = tableView.getSelectionModel().getSelectedItem();
        NameField.setText(String.valueOf(clickedEvenement.getName()));

        // Convert java.sql.Date to java.util.Date (or java.time.LocalDate if you prefer)
        java.util.Date startDate = new java.util.Date(clickedEvenement.getDateDeb().getTime());
        java.util.Date endDate = new java.util.Date(clickedEvenement.getDateFin().getTime());

        // Set the values to the DatePickers
        StartDateSelector.setValue(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        EndDateSelector.setValue(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        ParticpantField.setText(String.valueOf(clickedEvenement.getNbrParticipant()));

        // Check if the price is not null before setting it in the PriceField
        if (clickedEvenement.getPrice() != null) {
            PriceField.setText(clickedEvenement.getPrice().toString());
        } else {
            PriceField.clear(); // Clear the PriceField if the price is null
        }
    }


    @FXML
    void HandleDelete(ActionEvent event) {

            Evenement SelectedItem = tableView.getSelectionModel().getSelectedItem();
        ObservableList<Evenement> currentTableData = tableView.getItems();
        Evenement selectedEvenement = tableView.getSelectionModel().getSelectedItem();
        if (selectedEvenement == null) {
            selectedEvenement = eventService.readByName(SearchField.getText());
        }
        else
            selectedEvenement=eventService.readById(SelectedItem.getId());
            for (Evenement e : currentTableData) {
                if (e.getId() == selectedEvenement.getId()) {
                    System.out.println(selectedEvenement.getId());
                    ImageService imageservice = new ImageService();
                    Image image = imageservice.readById(imageservice.getImageIdByEventId(selectedEvenement.getId()));
                    if (image != null) {
                        imageservice.delete(image);
                    } else {
                        System.out.println("No image found for the current event ID.");
                    }

                    eventService.Delete(e);
                    break;
                }
            }

            // Refresh the table data
            list = eventService.ReadAll(SharedData.getClubId());
            tableView.setItems(list);
            tableView.refresh();

        }


    private Date convertToDate(DatePicker datePicker) {
        if (datePicker.getValue() != null) {
            LocalDate localDate = datePicker.getValue();
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            return null;
        }
    }





    @FXML
    void HandleUpdate(ActionEvent event) {
        if (!getErrors()) {


            System.out.println("HandleUpdate method called");
            ObservableList<Evenement> currentTableData = tableView.getItems();
            Evenement selectedEvenement = tableView.getSelectionModel().getSelectedItem();
            if (selectedEvenement == null) {
                selectedEvenement = eventService.readById(SharedEventData.getEventId());
            }

            int currentEventId = selectedEvenement.getId();
            Date d1 = convertToDate(StartDateSelector);
            Date d2 = convertToDate(EndDateSelector);
            int Part = Integer.parseInt(ParticpantField.getText());

            // Check if PriceField is empty or null
            Float price;
            try {
                price = Float.parseFloat(PriceField.getText());
            } catch (NumberFormatException e) {
                // Handle the case where the price field is not a valid float
                // You may display an error message or take appropriate action
                return;
            }

            Evenement updatedEvenement = new Evenement(NameField.getText(), d1, d2, Part, price);

            // Update the selected event
            System.out.println("Selected Event before update: " + selectedEvenement);
            eventService.Update(selectedEvenement, updatedEvenement);
            System.out.println("Selected Event after update: " + selectedEvenement);

            // Update the selected image
            if (selectedImage != null) {
                ImageService ims = new ImageService();
                int im = ims.getImageIdByEventId(currentEventId);
                ims.updateImageByEventId(im, selectedImage);
            } else {
                System.out.println("No image selected for update.");
            }

            // Refresh the table data after the update
            list = eventService.ReadAll(club.getId());
            System.out.println("Updated Event List: " + list);
            tableView.setItems(list);
            tableView.refresh();

        }
        else
            System.out.println("error");
        GoToEventScene(event);
    }



    @FXML
    void ShowInterface(ActionEvent event) {

    }
    void RedirctToHome(ActionEvent event) {
        try {
            // Load the Event.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event.fxml"));
            Parent root = loader.load();

            // Show the Event.fxml scene
            Stage stage = (Stage) UpdateButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFields();
    System.out.println(club);
        eventService = new EventService();
        Image image=new Image();
        ImageService imageService=new ImageService();
        int imageid=imageService.getImageIdByEventId(event.getId());
        image=imageService.readById(imageid);
        list = eventService.ReadAll(club.getId());
        System.out.println(list);
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        StartDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDeb"));
        EndDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        ParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("nbrParticipant"));
        ClubColumn.setCellValueFactory(new PropertyValueFactory<>("idClub"));
        PriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        tableView.setItems(list);
    javafx.scene.image.Image image1=new javafx.scene.image.Image("File:"+image.getUrl(),70,190,false,true);
    imageView.setImage(image1);


    }



    public void setFields()  {
        eventService = new EventService();

        event = eventService.readById(SharedEventData.getEventId());


        NameField.setText(event.getName());

        java.util.Date startDate = new java.util.Date(event.getDateDeb().getTime());
        java.util.Date endDate = new java.util.Date(event.getDateFin().getTime());

        // Set the values to the DatePickers
        StartDateSelector.setValue(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        EndDateSelector.setValue(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        ParticpantField.setText(String.valueOf(event.getNbrParticipant()));

        // Check if the price is not null before setting it in the PriceField
        if (event.getPrice() != null) {
            PriceField.setText(event.getPrice().toString());
        } else {
            PriceField.clear(); // Clear the PriceField if the price is null
        }


    }





    @FXML
    void goToEventScene(MouseEvent event) {
        try {
            UpdateEvent.getClub(club);
            // Load the Event.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event.fxml"));
            Parent root = loader.load();

            // Show the Event.fxml scene
            Stage stage = (Stage) HomeButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void HandleSearch(ActionEvent actionEvent) {
        String eventName = SearchField.getText();
        if (!eventName.isEmpty()) {

            ObservableList<Evenement> searchResult = (ObservableList<Evenement>) eventService.readByName(eventName);
            tableView.setItems(searchResult);
        } else {
            // If the search field is empty, display all events
            tableView.setItems(list);
        }

    }
    @FXML
    void handleLoadImageButton(ActionEvent event) {
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setTitle("Open Image File");
        File file = fileChooser1.showOpenDialog(null);
        if (file != null) {
            selectedImage = new Image(file.getName(), file.getAbsolutePath(), "event");
            javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }


    public void GoToEventScene(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Event.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root) ;
            Stage stage = (Stage) UpdateButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }







