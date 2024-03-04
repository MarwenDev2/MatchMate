package services.Reservation;

import database.Connexion;
import entities.Reservation;
import entities.Stadium;
import entities.User;
import services.Stadium.StadiumDAO;
import services.User.UserDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO implements IReservationDAO<Reservation>{
    Connection cnx = Connexion.getInstance();
    UserDAO ud = new UserDAO();
    StadiumDAO sd = new StadiumDAO();
    public boolean save(Reservation r) {
        if (r == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "insert into reservation(idPlayer,refStadium,date,startTime,endTime,type) values (?,?,?,?,?,?)");
            pstmt.setInt(1, r.getPlayer().getId());
            pstmt.setString(2, r.getStadium().getReference());
            pstmt.setDate(3, r.getDate());
            pstmt.setTime(4, r.getStartTime());
            pstmt.setTime(5, r.getEndTime());
            pstmt.setString(6, r.getType());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("the reservation has been added");
                return true;
            } else {
                System.out.println("NO reservation has been added");
            }

        } catch (SQLException e1) {
            System.out.println("your reservation addition was failed"+ e1.getMessage());
        }
        return false;
    }
    public boolean update(Reservation r) {
        if (r == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "update reservation set idPlayer= ?, idStadium= ?,date= ?,startTime= ?, endTime= ?endTime= ? where id=?");
            pstmt.setInt(1, r.getPlayer().getId());
            pstmt.setString(2, r.getStadium().getReference());
            pstmt.setDate(3, r.getDate());
            pstmt.setTime(4, r.getStartTime());
            pstmt.setTime(5, r.getEndTime());
            pstmt.setString(6, r.getType());
            pstmt.setInt(7, r.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Updated with success");
                return true;
            } else
                System.out.println("No update has been done");
        } catch (SQLException e) {
            System.out.println("your reservation addition was failed");
        }
        return false;
    }

    public List<Reservation> findAllByIdPlayer(int id) {

        PreparedStatement pstmt = null;
        List<Reservation> list = new ArrayList<>();

        try {
            pstmt = cnx.prepareStatement("select * from reservation where idPlayer=?");
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            while(res.next()){
                User u1 = ud.getUserById(id);
                Stadium s = sd.findById(res.getString(3));
                list.add(new Reservation(res.getInt(1),u1,s,res.getDate(4),res.getTime(5),res.getTime(6),res.getString(7)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> findAllByDate(Date d) {

        PreparedStatement pstmt = null;
        List<Reservation> list = new ArrayList<>();

        try {
            pstmt = cnx.prepareStatement("select * from reservation where Date=?");
            pstmt.setDate(1, d);
            ResultSet res = pstmt.executeQuery();
            while(res.next()){
                User u1 = ud.getUserById(res.getInt(2));
                Stadium s = sd.findById(res.getString(3));
                list.add(new Reservation(res.getInt(1),u1,s,d,res.getTime(5),res.getTime(6),res.getString(7)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> findAllByStadium(String ref) {

        PreparedStatement pstmt = null;
        List<Reservation> list = new ArrayList<>();

        try {
            pstmt = cnx.prepareStatement("select * from reservation where refStadium=?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            while(res.next()){
                User u1 = ud.getUserById(res.getInt(2));
                Stadium s = sd.findById(ref);
                list.add(new Reservation(res.getInt(1),u1,s,res.getDate(4),res.getTime(5),res.getTime(6),res.getString(7)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> findAllByStadiumDate(String ref,Date d) {

        PreparedStatement pstmt = null;
        List<Reservation> list = new ArrayList<>();

        try {
            pstmt = cnx.prepareStatement("select * from reservation where refStadium=? and date=?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            while(res.next()){
                User u1 = ud.getUserById(res.getInt(2));
                Stadium s = sd.findById(ref);
                list.add(new Reservation(res.getInt(1),u1,s,d,res.getTime(5),res.getTime(6),res.getString(7)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> findAllByType(String t) {

        PreparedStatement pstmt = null;
        List<Reservation> list = new ArrayList<>();

        try {
            pstmt = cnx.prepareStatement("select * from reservation where type=?");
            pstmt.setString(1, t);
            ResultSet res = pstmt.executeQuery();
            while(res.next()){
                User u1 = ud.getUserById(res.getInt(2));
                Stadium s = sd.findById(res.getString(3));
                list.add(new Reservation(res.getInt(1),u1,s,res.getDate(4),res.getTime(5),res.getTime(6),t));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int id) {
        if (id == -1)
            return false;
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("delete from reservation where id =?");
            pstmt.setInt(1, id);
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("reservation removale was succeded");
                return true;
            } else {
                System.out.println("no reservation was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("wrong compilation of the reqeute");
            e.printStackTrace();
            return false;
        }
    }

    // Method to count reservations by stadium reference
    public int countReservationsByStadium(String stadiumReference) {
        int reservationCount = 0;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Prepare SQL query to count reservations by stadium reference
            String query = "SELECT COUNT(*) AS reservation_count FROM reservation WHERE refStadium = ?";
            preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setString(1, stadiumReference);

            // Execute query and retrieve result set
            resultSet = preparedStatement.executeQuery();

            // Check if result set has a row
            if (resultSet.next()) {
                reservationCount = resultSet.getInt("reservation_count");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception as per your application's error handling strategy
        } finally {
            // Close resources in finally block
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle or log the exception as per your application's error handling strategy
            }
        }

        return reservationCount;
    }

}
