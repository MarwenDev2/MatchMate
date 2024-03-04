package services.Stadium;

import database.Connexion;
import entities.*;
import services.Club.ClubDAO;
import services.User.UserDAO;

import java.sql.*;
import java.util.*;

public class StadiumDAO implements IStadiumDAO<Stadium>{

    public static Connection cnx = Connexion.getInstance();
    public ClubDAO clubDAO = new ClubDAO();
    public UserDAO us = new UserDAO();

    private static final String DISTINCT_RATES_QUERY = "SELECT DISTINCT rate FROM stadium";

    private static final String DISTINCT_PRIZES_QUERY = "SELECT DISTINCT price FROM stadium";



    public String save(Stadium s) {
        if (s == null)
            return null;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "insert into stadium(reference,idClub,height,width,price,rate, maintenance) values (?,?,?,?,?,?,?)");
            pstmt.setString(1, s.getReference());
            pstmt.setInt(2, s.getClub().getId());
            pstmt.setFloat(3, s.getHeight());
            pstmt.setFloat(4, s.getWidth());
            pstmt.setInt(5, s.getPrice());
            pstmt.setInt(6, s.getRate());
            pstmt.setInt(7, s.getMaintenance());
            Club c = clubDAO.findById(s.getClub().getId());
            c.setStadiumNbr(c.getStadiumNbr() + 1);
            n = pstmt.executeUpdate();
            if (n == 1) {
                System.out.println("The stadium has been added with Reference: " + s.getReference());
                clubDAO.update(c);
                return s.getReference();
            } else {
                System.out.println("No stadium has been added");
                return null;
            }
        } catch (SQLException e1) {
            System.out.println("The " + s.getReference() + " stadium addition failed: " + e1.getMessage());
            return null;
        } finally {
            // Close resources
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean update(Stadium s) {
        if (s == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "update Stadium set idClub= ?, height= ?,width= ?,price= ?, rate= ?, maintenance = ? where reference=?");
            pstmt.setInt(1, s.getClub().getId());
            pstmt.setFloat(2, s.getHeight());
            pstmt.setFloat(3, s.getWidth());
            pstmt.setInt(4, s.getPrice());
            pstmt.setInt(5, s.getRate());
            pstmt.setInt(6, s.getMaintenance());
            pstmt.setString(7, s.getReference());
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
                cl.setId(res.getInt("idClub"));
                s = new Stadium(ref, cl, res.getFloat("height"), res.getFloat("width"),
                        res.getInt("price"), res.getInt("rate"), res.getInt("maintenance"));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
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

    public boolean saveLike(Like l) {
        if (l == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement(
                    "insert into liked(refStadium,idUser) values (?,?)");
            pstmt.setString(1, l.getStadium().getReference());
            pstmt.setInt(2, l.getUser().getId());

            n = pstmt.executeUpdate();
            if (n == 1) {
                System.out.println("The stadium has been added with Reference: ");
                return true;
            } else {
                System.out.println("No stadium has been added");
                return false;
            }
        } catch (SQLException e1) {
            System.out.println("The "  + " stadium addition failed: " + e1.getMessage());
            return false;
        } finally {
            // Close resources
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Like findLikes(String ref) {
        PreparedStatement pstmt = null;
        Like l = null;
        try {
            pstmt = cnx.prepareStatement("select * from liked where refStadium=?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                User u =  us.getUserById(res.getInt("idUser"));
                Stadium s = findById(ref);
                l = new Like(s,u);
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return l;
    }
    public int getStadiumsLikes(String ref) {
        PreparedStatement pstmt = null;
        int l = -1;
        try {
            pstmt = cnx.prepareStatement("SELECT COUNT(refStadium) FROM liked WHERE refStadium = ?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            if (res.next()){
                return res.getInt(1);
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return l;
    }

    public Like findAllLikes(String ref,int user) {
        Like c = null;
        try {
            PreparedStatement pstmt = cnx.prepareStatement("SELECT * FROM liked WHERE refStadium=? AND idUser =?");
            pstmt.setString(1, ref);
            pstmt.setInt(2, user);
            ResultSet res = pstmt.executeQuery();
            if (res.next())
                c = new Like(findById(res.getString(1)),us.getUserById(res.getInt(2)));
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;

    }


    public List<Stadium> findAll() {
        List<Stadium> c = new ArrayList<Stadium>();
        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from stadium");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                Club cl  = clubDAO.findById(res.getInt(2));
                c.add(new Stadium(res.getString(1), cl, res.getFloat(3), res.getFloat(4), res.getInt(5), res.getInt(6), res.getInt(7)));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("La requ�te n'a pas pu �tre ex�cut�e");
            e.printStackTrace();
        }
        return c;

    }

    public List<Stadium> findAllByClub(int idClub) {
        List<Stadium> stadiums = new ArrayList<>();
        try {
            PreparedStatement pstmt = cnx.prepareStatement("SELECT * FROM stadium WHERE idClub = ?");
            pstmt.setInt(1, idClub);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                Club club = clubDAO.findById(res.getInt("idClub"));
                Stadium stadium = new Stadium(res.getString("reference"), club, res.getFloat("height"),
                        res.getFloat("width"), res.getInt("price"),
                        res.getInt("rate"), res.getInt("maintenance"));
                stadiums.add(stadium);
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return stadiums;
    }

    @Override
    public int countStadiumsByClub(int clubId) {
        int count = 0;
        String query = "SELECT COUNT(*) AS stadium_count FROM stadium WHERE idClub = ?";
        try (
                PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, clubId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("idClub");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;
    }

    public List<Integer> getDistinctRates() {
        List<Integer> distinctRates = new ArrayList<>();
        try (PreparedStatement statement = cnx.prepareStatement(DISTINCT_RATES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                distinctRates.add(resultSet.getInt("rate"));
            }
        } catch (SQLException e) {
            // Handle the SQL exception here, e.g., logging or throwing a custom DAOException
            e.printStackTrace();
        }
        return distinctRates;
    }

    public List<Integer> getDistinctPrizes() {
        List<Integer> distinctPrizes = new ArrayList<>();
        try (PreparedStatement statement = cnx.prepareStatement(DISTINCT_PRIZES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                distinctPrizes.add(resultSet.getInt("price"));
            }
        } catch (SQLException e) {
            // Handle the SQL exception here, e.g., logging or throwing a custom DAOException
            e.printStackTrace();
        }
        return distinctPrizes;
    }

    public void removeLike(Like like) {
        // Establish a connection to your database (Assuming you have a connection established)
        PreparedStatement preparedStatement = null;

        try {
            // Your SQL DELETE statement to remove the like record
            String sql = "DELETE FROM liked WHERE refStadium = ? AND idUser = ?";

            // Create a PreparedStatement with the SQL query
            preparedStatement = cnx.prepareStatement(sql);

            // Set the parameter values
            preparedStatement.setString(1, like.getStadium().getReference());
            preparedStatement.setInt(2, like.getUser().getId());

            // Execute the query
            preparedStatement.executeUpdate();

            // Optionally, commit the transaction if you are using transactions

        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                // Note: Do not close the connection here as it might be used by other methods
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
