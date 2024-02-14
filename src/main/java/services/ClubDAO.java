package services;

import database.Connexion;
import entities.*;

import java.sql.*;
import java.util.*;

public class ClubDAO implements IClubDAO<Club>{

    Connection cnx = Connexion.getInstance();
    UserDAO userDAO = new UserDAO();
    public boolean save(Club c) {
        if (c == null)
            return false;

        int n = 0;
        PreparedStatement pstmt = null;

            try {
                pstmt = cnx.prepareStatement(
                        "insert into club(name,height,widht,startTime,endTime,stadiumNbr,idUser) values (?,?,?,?,?,?,?,?)");
                pstmt.setString(1, c.getName());
                pstmt.setFloat(2, c.getHeight());
                pstmt.setFloat(3, c.getWidth());
                pstmt.setTime(4, c.getStartTime());
                pstmt.setTime(5, c.getEndTime());
                pstmt.setInt(6, 0);
                pstmt.setString(7, c.getDescription());
                pstmt.setInt(8, c.getUser().getId());
                n = pstmt.executeUpdate();
                pstmt.close();
                if (n == 1) {
                    System.out.println("the Club has been added");
                    return true;
                } else {
                    System.out.println("NO club has been added");
                }

            } catch (SQLException e1) {
                System.out.println("the" + c.getName() + " club addition was failed"+ e1.getMessage());
            }
        return false;
    }

    public boolean update(Club c) {
        if (c == null)
            return false;
        Connection cnx = Connexion.getInstance();
        int n = 0;
        int idUser=c.getId();
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "update Club set name= ?, height= ?, width=?, startTime= ?,endTime= ?,stadiumNbr= ?, description=?, idUser= ? where id=?");
            pstmt.setString(1, c.getName());
            pstmt.setFloat(2, c.getHeight());
            pstmt.setFloat(3, c.getWidth());
            pstmt.setTime(4, c.getStartTime());
            pstmt.setTime(5, c.getEndTime());
            pstmt.setInt(6, 0);
            pstmt.setString(7, c.getDescription());
            pstmt.setInt(8, c.getUser().getId());
            pstmt.setInt(9, c.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Updated with success valid");
                return true;
            } else
                System.out.println("No update has been done");
        } catch (SQLException e) {
            System.out.println( c.getName() + " Club didn't updated successfully"+e.getMessage());
        }
        return false;
    }

    public Club findById(int id) {
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        Club c = null;
        try {
            pstmt = cnx.prepareStatement("select * from club where id=?");
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                User us1 = userDAO.findById(res.getInt(9));
                c = new Club(res.getInt(1),us1,res.getString(2), res.getFloat(3),res.getFloat(4), res.getTime(5),res.getTime(6),res.getInt(7),res.getString(8));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;
    }


    public boolean findByName(String name) {
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement("select * from club where name=?");
            pstmt.setString(1, name);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                return true;
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return false;
    }

    public Club findByRef(String ref) {
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        Club c = null;
        try {
            pstmt = cnx.prepareStatement("select * from club i join stadium s on i.id = s.idClub where reference=?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                User us1 = userDAO.findById(res.getInt(9));
                c = new Club(res.getInt(1),us1,res.getString(2), res.getFloat(3),res.getFloat(4), res.getTime(5),res.getTime(6),res.getInt(7),res.getString(8));            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;
    }

    public int findNbrClub() {
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int nbr=0;
        try {
            pstmt = cnx.prepareStatement("select sum(stadiumNbr) from club");
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                nbr=res.getInt(1);
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return nbr;
    }

    public List<Club> findAll() {
        Connection cnx = Connexion.getInstance();
        List<Club> c = new ArrayList<Club>();
        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from club");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                User us1 = userDAO.findById(res.getInt(9));
                c.add(new Club(res.getInt(1),us1,res.getString(2), res.getFloat(3),res.getFloat(4), res.getTime(5),res.getTime(6),res.getInt(7),res.getString(8)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;

    }
    public boolean delete(Club c) {
        if (c == null)
            return false;
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("delete from club where id =?");
            pstmt.setInt(1, c.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("club removale was succeded good");
                return true;
            } else {
                System.out.println("no club was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("wrong compilation of the reqeute");
            e.printStackTrace();
            return false;
        }
    }

}
