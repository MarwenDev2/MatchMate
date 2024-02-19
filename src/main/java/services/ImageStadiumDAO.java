package services;

import database.Connexion;
import entities.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageStadiumDAO implements IImageStadiumDAO<Image>{
    Connection cnx = Connexion.getInstance();

    public boolean save(Image image, String ref) {
        if (image == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            pstmt = cnx.prepareStatement(
                    "INSERT INTO image(name, url, type) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, image.getName());
            pstmt.setString(2, image.getUrl());
            pstmt.setString(3, image.getType());
            n = pstmt.executeUpdate();
            if (n == 1) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int lastInsertedId = generatedKeys.getInt(1);
                    if (insertIntoStadiumTable(ref,lastInsertedId)) {
                        System.out.println("Image has been added");
                        return true;
                    }
                }
            }
            System.out.println("No Image has been added");
        } catch (SQLException e) {
            System.out.println("Image addition failed: " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean insertIntoStadiumTable(String ref, int imageId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement(
                    "INSERT INTO imagestadium(refStadium, idImage) VALUES (?, ?)");
            pstmt.setString(1, ref);
            pstmt.setInt(2, imageId);
            int result = pstmt.executeUpdate();
            return result == 1;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    public List<Image> findByIDStadium(String ref, String type) {
        List<Image> images = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            String typeTableName = "image" + type.toLowerCase();
            String typeLower = type.toLowerCase();
            String firstLetterCapitalized = Character.toUpperCase(typeLower.charAt(0)) + typeLower.substring(1);
            String idObject = "id" + firstLetterCapitalized;
            String query = "SELECT i.* FROM image i JOIN " + typeTableName + " it ON i.id = it.idImage WHERE it.refStadium = ?";
            pstmt = cnx.prepareStatement(query);
            pstmt.setString(1, ref);
            res = pstmt.executeQuery();
            while (res.next()) {
                images.add(new Image(res.getInt("id"),res.getString("name"),res.getString("url"),res.getString("type")));
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching image by id and type: " + e.getMessage());
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return images;
    }
}
