package Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DataSource;
import entities.Image;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageService implements ImageInterface {
    private Connection cnx;
    private PreparedStatement ste;

    public ImageService() {
        cnx = DataSource.getInstance().getCnx();
    }

    @Override
    public int add(Image image) {
        String req = "INSERT INTO image (name, url, type) VALUES (?, ?, ?)";
        ResultSet generatedKeys = null;
        int id = 0;
        try {
            ste = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ste.setString(1, image.getName());
            ste.setString(2, image.getUrl());
            ste.setString(3, image.getType());
            ste.executeUpdate();
            generatedKeys = ste.getGeneratedKeys();
            if (generatedKeys.next()) { // Move cursor to the first row
                id = generatedKeys.getInt(1);
                System.out.println("Image added successfully with ID: " + id);
            } else {
                System.out.println("No generated keys found for the image.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding image: " + e.getMessage());
        } finally {
            // Close the ResultSet, PreparedStatement, and any other resources here
        }
        return id;
    }


    @Override
    public void delete(Image image) {
        String req = "DELETE FROM image WHERE id = ?";
        try {
            ste = cnx.prepareStatement(req);
            ste.setInt(1, image.getId());
            ste.executeUpdate();
            System.out.println("Image deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting image: " + e.getMessage());
        }
    }




    @Override
    public void update(Image eventImage, Image newImage) {
        if (eventImage != null) {
            String updateImageQuery = "UPDATE `image` SET `name`=?,`url`=?,`type`=? WHERE id=?";
            try {
                ste = cnx.prepareStatement(updateImageQuery);
                ste.setString(1, newImage.getName());
                ste.setString(2, newImage.getUrl());
                ste.setString(3, newImage.getType());
                ste.setInt(4, eventImage.getId());

                ste.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Cannot update image because eventImage is null.");
        }
    }

    public int getImageIdByEventId(int eventId) {
        String query = "SELECT idImage FROM imageevent WHERE idEvent = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(query);
            statement.setInt(1, eventId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching image ID by event ID: " + e.getMessage());
        }
        return -1; // Return -1 if no image ID found for the event ID
    }


    @Override
    public ObservableList<Image> readAll() {
        List<Image> images = new ArrayList<>();
        String req = "SELECT * FROM image";
        try {
            Statement statement = cnx.createStatement();
            ResultSet rs = statement.executeQuery(req);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String url = rs.getString("url");
                String type = rs.getString("type");
                images.add(new Image(id, name, url, type));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching images: " + e.getMessage());
        }
        return FXCollections.observableArrayList(images);
    }

    @Override
    public Image readById(int id) {
        String req = "SELECT * FROM image WHERE id = ?";
        try {
            ste = cnx.prepareStatement(req);
            ste.setInt(1, id);
            ResultSet rs = ste.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String url = rs.getString("url");
                String type = rs.getString("type");
                return new Image(id, name, url, type);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching image by ID: " + e.getMessage());
        }
        return null;
    }
    public void updateImageByEventId(int eventId, Image newImage) {
        if (eventId != -1) {
            String query = "UPDATE image SET name = ?, url = ?, type = ? WHERE id = ?";
            try {
                PreparedStatement statement = cnx.prepareStatement(query);
                statement.setString(1, newImage.getName());
                statement.setString(2, newImage.getUrl());
                statement.setString(3, newImage.getType());
                statement.setInt(4, eventId);
                statement.executeUpdate();
                System.out.println("Image updated successfully for the event with ID: " + eventId);
            } catch (SQLException e) {
                System.out.println("Error updating image for the event: " + e.getMessage());
            }
        } else {
            System.out.println("No image found for the event with ID: " + eventId);
        }
    }

}
