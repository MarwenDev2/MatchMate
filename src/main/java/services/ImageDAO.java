package services;

import database.Connexion;
import entities.*;
import entities.User;

import java.sql.*;
import java.util.*;

public class ImageDAO implements IImageDAO<Image> {

    Connection cnx = Connexion.getInstance();

    public boolean save(Image image,int id) {
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
                    if (insertIntoTypeTable(id,lastInsertedId, image.getType())) {
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

    public boolean insertIntoTypeTable(int relatedId, int imageId, String type) throws SQLException {
        String typeTableName = "image" + type.toLowerCase();
        String typeLower = type.toLowerCase();
        String firstLetterCapitalized = Character.toUpperCase(typeLower.charAt(0)) + typeLower.substring(1);
        String idObject = "id" + firstLetterCapitalized;
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement(
                    "INSERT INTO " + typeTableName + "(" + idObject + ", idImage) VALUES (?, ?)");
            pstmt.setInt(1, relatedId);
            pstmt.setInt(2, imageId);
            int result = pstmt.executeUpdate();
            return result == 1;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    public Image findByIdImage(int id) {
        Image image = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            String query = "SELECT * FROM image  WHERE id = ?";
            pstmt = cnx.prepareStatement(query);
            pstmt.setInt(1, id);
            res = pstmt.executeQuery();
            while (res.next()) {
                image = new Image(res.getInt("id"),res.getString("name"),res.getString("url"),res.getString("type"));
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
        return image;
    }

    public List<Image> findByObjectId(int id, String type) {
        List<Image> images = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            String typeTableName = "image" + type.toLowerCase();
            String typeLower = type.toLowerCase();
            String firstLetterCapitalized = Character.toUpperCase(typeLower.charAt(0)) + typeLower.substring(1);
            String idObject = "id" + firstLetterCapitalized;
            String query = "SELECT * FROM image i JOIN " + typeTableName + " it ON i.id = it.idImage WHERE it." + idObject + " = ?";
            pstmt = cnx.prepareStatement(query);
            pstmt.setInt(1, id);
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

    public boolean update(Image image) {
        if (image == null)
            return false;
        Connection cnx = Connexion.getInstance();
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "update Image set name= ?, url= ?, type=? where id=?");
            pstmt.setString(1, image.getName());
            pstmt.setString(2, image.getUrl());
            pstmt.setString(3, image.getType());
            pstmt.setInt(4, image.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Updated with success valid");
                return true;
            } else
                System.out.println("No update has been done");
        } catch (SQLException e) {
            System.out.println( image.getName() + " Image didn't updated successfully"+e.getMessage());
        }
        return false;
    }

    public boolean delete(Image i) {
        if (i == null)
            return false;
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("delete from image where id =?");
            pstmt.setInt(1, i.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("image removale was succeded good");
                return true;
            } else {
                System.out.println("no image was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("wrong compilation of the reqeute");
            e.printStackTrace();
            return false;
        }
    }

}
