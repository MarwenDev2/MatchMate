package controllers;

import entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import services.ImageService;
import services.OrderService;
import services.PanierService;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanierViewController {
    @FXML
    private GridPane gridpane;

    @FXML
    private ScrollPane scrollPane;
    private List<Panier> paniers; // Déclarer la liste en tant qu'attribut de classe

    private int productId;
    private PanierService panierService;
    private Product product;
    @FXML
    private HBox stadiumCardsContainer;
    @FXML
    private Button purchase;

    private Panier panier;


    private PanierViewController panierViewController;
    private User currentUser= SessionManager.getInstance().getCurrentUser();

    public PanierViewController() {
        panierService = new PanierService();
        paniers = new ArrayList<>();
    }

    public List<Panier> returnPanier() {
        return paniers;
    }

    public void initialize() {
        try {
            // Fetch paniers for user with id 2
            List<Panier> paniers = panierService.getPanierByUserId(currentUser.getId());

            System.out.println("4" + paniers);

            // Load PanierCard components for each panier and add them to the stadiumCardsContainer
            for (Panier panier : paniers) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PanierCard.fxml"));
                AnchorPane panierCard = loader.load();
                PanierCardController panierCardController = loader.getController();
                panierCardController.setData(panier);
                stadiumCardsContainer.getChildren().add(panierCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handlepdf(ActionEvent actionEvent) {
        try {
            String pdfFilePath = "PanierInformation.pdf"; // File path for the PDF

            // Create the PDF
            createPDF(pdfFilePath);

            // Print the PDF
            printPDF(pdfFilePath);
        } catch (IOException | PrinterException e) {
            e.printStackTrace();
        }

    }

    private void createPDF(String pdfFilePath) throws IOException, MalformedURLException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);


            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set color for table header background
                contentStream.setNonStrokingColor(0.8f, 0.8f, 0.8f); // Light gray

                // Draw table header background
                contentStream.fillRect(50, 690, 500, 20);

                // Set color for table header text
                contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f); // Black

                // Set font and font size for table header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Add table headers
                contentStream.beginText();
                contentStream.newLineAtOffset(60, 700);
                contentStream.showText("IdUser");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Reference");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Quantity");
                contentStream.newLineAtOffset(70, 0); // Adjusted offset to create space between "End Date" and "Price"
                contentStream.showText("Total");
                contentStream.newLineAtOffset(50, 0); // Adjusted offset to create more space between "Price" and "Participant Number"
                contentStream.showText("Date");
                contentStream.newLineAtOffset(80, 0); // Adjusted offset to create more space between "Participant Number" and "Image"
                contentStream.showText("Image");
                contentStream.endText();

                // Fetch event data from the PanierService
                PanierService panierService = new PanierService();
                List<Panier> paniers = panierService.getPanierByUserId(currentUser.getId());
                System.out.println(paniers);

                // Set font and font size for table content
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                // Set text color (black) for table content
                contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f); // Black

                float y = 670;
                for (Panier panier : paniers) {
                    System.out.println("bechir"+panier);
                    // Draw table row borders
                    contentStream.setStrokingColor(0.0f, 0.0f, 0.0f); // Black
                    contentStream.setLineWidth(0.5f);
                    contentStream.moveTo(50, y);
                    contentStream.lineTo(550, y);
                    contentStream.stroke();

                    // Draw panier data
                    contentStream.beginText();
                    contentStream.newLineAtOffset(60, y - 30);
                    contentStream.showText(String.valueOf(panier.getIdUser()));
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(panier.getIdProuct().getReference());
                    // Assuming getIdProduct() returns the product
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(String.valueOf(panier.getQuantity()));
                    System.out.println(String.valueOf(panier.getTotal()));// Assuming sum() calculates the total
                    contentStream.newLineAtOffset(60, 0);
                    contentStream.showText(String.valueOf(panier.getQuantity()*panier.getIdProuct().getPrice()));
                    contentStream.showText(String.valueOf(LocalDate.now())); // Current date
                    contentStream.newLineAtOffset(150, 0);

                    // Draw event image
                    try {
                        ImageService imageService=new ImageService();
                        List<Image> images=imageService.findByObjectId(panier.getIdProuct().getId(),"product");
                        System.out.println("pppp"+images);
                        String imageId =images.get(0).getUrl() ;
                        if (images.get(0) != null) {
                            File file = new File(images.get(0).getUrl());
                            URL imageUrl = file.toURI().toURL();

                            try (InputStream inputStream = imageUrl.openStream()) {
                                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(inputStream), "image");
                                // Draw the image
                                contentStream.drawImage(pdImage, 450, y - 60, 100, 60); // Adjust the coordinates and size as needed
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (MalformedURLException e) {
                        // Handle malformed URL
                        e.printStackTrace();
                    }

                    y -= 80; // Decrease the vertical spacing between rows
                }
            }

            document.save(pdfFilePath);
        }
    }





    private void printPDF(String pdfFilePath) throws IOException, PrinterException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new PDFPrintable(document));
            if (job.printDialog()) {
                job.print();
            }
        }
    }

    @FXML
    void handlePurchase(ActionEvent event) {
        OrderService orderService = new OrderService();
        PanierCardController panierCardController=new PanierCardController();
        List<Panier> p=panierService.getPanierByUserId(currentUser.getId());

        Order ordre=new Order( p.get(0),Date.valueOf(LocalDate.now()));
        int n=orderService.save(ordre);




    }
}
