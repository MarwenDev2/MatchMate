package controllers.Authentification;

import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dom4j.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class SuccessController {


    @FXML
    private Label payment_txt;
    @FXML
    private Button pdf_btn;
    @FXML
    private Button back_btn;
    private Reservation reservation;
    public static final String ACCOUNT_SID = "ACe1969f27c9ebaba39c1c2e19532653e5";
    public static final String AUTH_TOKEN = "53b72d6c70636292563f45e93c8725f3";

    public void setData(Reservation r) {
        String value;
        this.reservation = r;
        value = "This confirms that we've just received your online payment for your Reservation of the Staduim : " + r.getStadium().getReference() + " on the "+r.getDate()+" ;";
        payment_txt.setText(value);
        // send_sms_to_Client(50190957); // this will be replaced with the client number
    }
    public void export_pdf(String html, String fileName) throws IOException, DocumentException {
        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        // Create a document
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();

        // Export to PDF
        try (FileOutputStream os = new FileOutputStream(file)) {
            renderer.createPDF(os);
        } catch (com.lowagie.text.DocumentException e) {
            throw new RuntimeException(e);
        }
        // Open the PDF file
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }


    @FXML
    private void getReceiptPdf(ActionEvent event) {
        float total = reservation.getStadium().getPrice();
        DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date_res = formatter_date.format(reservation.getDate().toLocalDate());
        String start_time = formatter_time.format(reservation.getStartTime().toLocalTime());
        String end_time = formatter_time.format(reservation.getEndTime().toLocalTime());
        String html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "    <title>Receipt</title>\n"
                + "    <style>\n"
                + "        body {\n"
                + "            font-family: Arial, sans-serif;\n"
                + "            font-size: 17px;\n"
                + "        }\n"
                + "        .header {\n"
                + "            text-align: center;\n"
                + "        }\n"
                + "        .header h1 {\n"
                + "            margin: 0;\n"
                + "            font-size: 24px;\n"
                + "        }\n"
                + "        .info {\n"
                + "            margin-top: 20px;\n"
                + "            margin-bottom: 20px;\n"
                + "            border: 1px solid #ee1e46;\n"
                + "            padding: 10px;\n"
                + "        }\n"
                + "        .items {\n"
                + "            margin-top: 20px;\n"
                + "            margin-bottom: 20px;\n"
                + "            border-collapse: collapse;\n"
                + "            width: 100%;\n"
                + "            font-size: 16px;\n"
                + "        }\n"
                + "        .items th, .items td {\n"
                + "            border: 1px solid #ccc;\n"
                + "            padding: 10px;\n"
                + "            text-align: left;\n"
                + "        }\n"
                + "        .items th {\n"
                + "            background-color: #eee;\n"
                + "            font-weight: bold;\n"
                + "        }\n"
                + "        .items td {\n"
                + "            vertical-align: middle;\n"
                + "        }\n"
                + "        .items tr:hover {\n"
                + "            background-color: #f5f5f5;\n"
                + "        }\n"
                + "        .total {\n"
                + "            text-align: right;\n"
                + "            font-weight: bold;\n"
                + "            font-size: 18px;\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "<div class=\"header\">\n"
                + " <img src=\"..resources/Images/logo.png\" alt=\"Company Logo\" width=\"240\" height=\"180\" />\n"
                + " <h1>Receipt</h1>\n"
                + "</div>\n"
                + "<div class=\"info\">\n"
                + "    <p><strong>Client Information:</strong></p>\n"
                + "    <p><strong>Firstname:</strong> " + reservation.getPlayer().getFirstName() + "</p>\n"
                + "    <p><strong>Lastname:</strong> " + reservation.getPlayer().getLastName() + "</p>\n"
                + "    <p><strong>Email:</strong> " + reservation.getPlayer().getEmail() + "</p>\n"
                + "    <p><strong>Telephone:</strong> " + reservation.getPlayer().getPhoneNumber() + "</p>\n"
                + "</div>\n"
                + "<table class=\"items\">\n"
                + "    <thead>\n"
                + "        <tr>\n"
                + "            <th>Club</th>\n"
                + "            <th>Stadium</th>\n"
                + "            <th>Localisation</th>\n"
                + "            <th>Date</th>\n"
                + "            <th>Start Time</th>\n"
                + "            <th>End Time</th>\n"
                + "            <th>Player</th>\n"
                + "            <th>Total</th>\n"
                + "        </tr>\n"
                + "    </thead>\n"
                + "    <tbody>\n"
                + "        <tr>\n"
                + "            <td>" + reservation.getStadium().getClub().getName() + "</td>\n"
                + "            <td>" + reservation.getStadium().getReference() + "</td>\n"
                + "            <td>" + reservation.getStadium().getClub().getHeight() + "</td>\n"
                + "            <td>" + date_res + "</td>\n"
                + "            <td>" + start_time + "</td>\n"
                + "            <td>" + end_time + "</td>\n"
                + "            <td>" + reservation.getPlayer().getFirstName() + " " + reservation.getPlayer().getLastName() + "</td>\n"
                + "            <td>" + String.valueOf(total) + ".DT</td>\n"
                + "        </tr>\n"
                + "    </tbody>\n"
                + "</table>\n"
                + "<div>\n"
                + "    <p>\n"
                + "        © All rights reserved | This template is made with ♥ by Creative Crew \n"
                + "    </p>\n"
                + "</div>\n"
                + "</body>\n"
                + "</html>";

        String txt = reservation.getStadium()+ ".pdf";
        String fileName = txt;
        try {
            export_pdf(html, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void redirectToListReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/ViewReservationPlayer.fxml"));
            Parent root = loader.load();
            //UPDATE The Controller with Data :
            Scene scene = new Scene(root);
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
