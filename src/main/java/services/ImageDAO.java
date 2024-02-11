package services;

import database.Connexion;
import entities.*;
import entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImageDAO {

    Connection cnx = Connexion.getInstance();

    public boolean save(Image i) {
        if (i == null)
            return false;

        int n = 0;
        PreparedStatement pstmt = null;
        String type = i.getType();
        try {
            pstmt = cnx.prepareStatement(
                    "insert into image(name,url) values (?,?)");
            pstmt.setString(1, i.getName());
            pstmt.setString(2, i.getUrl());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("the User has been added");
                return true;
            } else {
                System.out.println("NO User has been added");
            }

        } catch (SQLException e1) {
            System.out.println(i.getName() + "  addition was failed"+ e1.getMessage());
        }
        return false;
    }
}
