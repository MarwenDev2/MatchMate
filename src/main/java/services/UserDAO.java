package services;

import database.Connexion;
import entities.*;

import java.sql.*;
import java.util.*;

public class UserDAO implements IUserDAO<User>{

    Connection cnx = Connexion.getInstance();

    public boolean save(User u){
        if (u == null)
            return false;

        int n = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement(
                    "insert into user(firstName,lastName,phoneNumber,email,password,role,image) values (?,?,?,?,?,?,?)");
            pstmt.setString(1, u.getFirstName());
            pstmt.setString(2, u.getLastName());
            pstmt.setInt(3, u.getPhoneNumber());
            pstmt.setString(4, u.getEmail());
            pstmt.setString(5, u.getPassword());
            pstmt.setString(6, u.getRole());
            pstmt.setString(7,u.getImage());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("the User has been added");
                return true;
            } else {
                System.out.println("NO User has been added");
            }

        } catch (SQLException e1) {
            System.out.println(u.getFirstName() + "  addition was failed"+ e1.getMessage());
        }
        return false;
    }

    public boolean update(User u) {
        if (u == null)
            return false;

        int n = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement(
                    "update User set firstName= ?, lastName= ?, phoneNumber= ?,email= ?,password= ?,image=? where id=?");
            pstmt.setString(1, u.getFirstName());
            pstmt.setString(2, u.getLastName());
            pstmt.setInt(3, u.getPhoneNumber());
            pstmt.setString(4, u.getEmail());
            pstmt.setString(5, u.getRole());
            pstmt.setInt(6, u.getId());
            pstmt.setString(7,u.getImage());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("the User has been updated");
                return true;
            } else {
                System.out.println("NO User has been update");
            }

        } catch (SQLException e1) {
            System.out.println(u.getFirstName() + "  udaptation was failed"+ e1.getMessage());
        }
        return false;
    }

    public User findById(int id) {
        PreparedStatement pstmt = null;
        User u = null;
        try {
            pstmt = cnx.prepareStatement("select * from user where id=?");
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                User u1 = new User(id,res.getString(2),res.getString(3),res.getInt(4),res.getString(5),res.getString(6),res.getString(7),res.getString(8));
                u=u1;
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return u;
    }

    public boolean delete(User u) {
        if (u == null)
            return false;
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("delete from user where id =?");
            pstmt.setInt(1, u.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("user removale was succeded");
                return true;
            } else {
                System.out.println("no user was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("wrong compilation of the reqeute");
            e.printStackTrace();
            return false;
        }
    }

    public List<User> findAll() {
        List<User> cl = new ArrayList<User>();
        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from user");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                cl.add(new User(res.getInt(1),res.getString(2),res.getString(3),res.getInt(4),res.getString(5),res.getString(6),res.getString(7),res.getString(8)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return cl;

    }
}
