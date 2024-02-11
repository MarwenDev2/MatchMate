package services;

import database.Connexion;
import entities.*;

import java.sql.*;
import java.util.*;

public class StadiumDAO {

    static Connection cnx = Connexion.getInstance();
    ClubDAO clubDAO = new ClubDAO();

    public boolean save(Stadium s) {
        if (s == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "insert into stadium(reference,idClub,height,width,price,rate) values (?,?,?,?,?,?)");
            String stadiumRef = s.getClub().getName().substring(0, Math.min(s.getClub().getName().length(), 3)).toUpperCase();
            Random random = new Random();
            int randomNumber = random.nextInt(1000);
            stadiumRef = stadiumRef + randomNumber;
            pstmt.setString(1, stadiumRef);
            pstmt.setInt(2, s.getClub().getId());
            pstmt.setFloat(3, s.getHeight());
            pstmt.setFloat(4, s.getWidth());
            pstmt.setInt(5, s.getPrice());
            pstmt.setInt(6, s.getRate());
            Club c =clubDAO.findById(s.getClub().getId());
            c.setStadiumNbr(c.getStadiumNbr()+1);
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("the Stadium has been added");
                clubDAO.update(c);
                return true;
            } else {
                System.out.println("NO Stadium has been added");
            }

        } catch (SQLException e1) {
            System.out.println("the" + s.getReference() + "stadium addition was failed"+ e1.getMessage());
        }
        return false;
    }

    public boolean update(Stadium s) {
        if (s == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "update Stadium set idClub= ?, height= ?,width= ?,price= ?, rate= ? where reference=?");
            pstmt.setInt(1, s.getClub().getId());
            pstmt.setFloat(2, s.getHeight());
            pstmt.setFloat(3, s.getWidth());
            pstmt.setInt(4, s.getPrice());
            pstmt.setInt(5, s.getRate());
            pstmt.setString(6, s.getReference());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Updated with success");
                return true;
            } else
                System.out.println("No update has been done");
        } catch (SQLException e) {
            System.out.println( s.getReference() + " Club didn't updated successfully");
        }
        return false;
    }


    public Stadium findById(String ref) {

        PreparedStatement pstmt = null;
        Stadium s = null;
        try {
            pstmt = cnx.prepareStatement("select * from stadium where reference=?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                Club cl  = new Club();
                cl.setId(res.getInt(2));
                s = new Stadium(ref,cl,res.getFloat(3),res.getFloat(4),res.getInt(5),res.getInt(6));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return s;
    }

    public boolean delete(Stadium s) {
        if (s == null)
            return false;
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("delete from stadium where reference =?");
            pstmt.setString(1, s.getReference());
            n = pstmt.executeUpdate();
            pstmt.close();
            Club c =clubDAO.findById(s.getClub().getId());
            c.setStadiumNbr(c.getStadiumNbr()-1);
            if (n == 1) {
                clubDAO.update(c);
                System.out.println("stadium removale was succeded");
                return true;
            } else {
                System.out.println("no stadium was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("wrong compilation of the reqeute");
            e.printStackTrace();
            return false;
        }
    }

    public List<Stadium> findAll() {
        List<Stadium> c = new ArrayList<Stadium>();
        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from stadium");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                Club cl  = clubDAO.findById(res.getInt(2));
                c.add(new Stadium(res.getString(1),cl,res.getFloat(3),res.getFloat(4),res.getInt(5),res.getInt(6)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;

    }

    public List<Stadium> findAllByCLub(int idClub) {
        List<Stadium> c = new ArrayList<Stadium>();
        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from club c join stadium s on c.id = s.idClub where id=?");
            pstmt.setInt(1, idClub);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                Club cl  = clubDAO.findById(res.getInt(1));
                c.add(new Stadium(res.getString(1),cl,res.getFloat(10),res.getFloat(11),res.getInt(12),res.getInt(13)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;

    }


}
