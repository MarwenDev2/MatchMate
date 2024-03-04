package services.Club;

import database.Connexion;
import entities.Club;
import entities.SessionManager;
import entities.User;
import services.User.UserDAO;

import javax.persistence.EntityManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubDAO implements IClubDAO<Club> {

    private final Connection connection = Connexion.getInstance();
    private final UserDAO userDAO = new UserDAO();
    private static final String SELECT_CLUBS_BY_GOVERNORATE = "SELECT * FROM club WHERE governorate = ?";
    private static final String SELECT_CLUBS_BY_GOVERNORATE_AND_CITY = "SELECT * FROM club WHERE governorate = ? AND city = ?";

    private User currentUser = SessionManager.getInstance().getCurrentUser();

    @Override
    public int save(Club c) {
        if (c == null)
            return -1; // Return -1 to indicate failure
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            pstmt = connection.prepareStatement(
                    "insert into club(name,governorate,city,startTime,endTime,stadiumNbr,description,idUser,latitude,longitude) values (?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getGovernorate());
            pstmt.setString(3, c.getCity());
            pstmt.setTime(4, c.getStartTime());
            pstmt.setTime(5, c.getEndTime());
            pstmt.setInt(6, c.getStadiumNbr());
            pstmt.setString(7, c.getDescription());
            pstmt.setInt(8, c.getUser().getId());
            pstmt.setDouble(9, c.getLatitude()); // New attribute
            pstmt.setDouble(10, c.getLongitude()); // New attribute



            int n = pstmt.executeUpdate();

            if (n == 1) {
                // Retrieve the auto-generated keys
                generatedKeys = pstmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int clubId = generatedKeys.getInt(1); // Retrieve the auto-generated club ID
                    System.out.println("The Club has been added with ID: " + clubId);
                    return clubId; // Return the generated club ID
                } else {
                    System.out.println("Failed to retrieve club ID");
                    return -1; // Return -1 if club ID retrieval fails
                }
            } else {
                System.out.println("No club has been added");
                return -1; // Return -1 to indicate failure
            }

        } catch (SQLException e1) {
            System.out.println("The " + c.getName() + " club addition failed: " + e1.getMessage());
            return -1; // Return -1 to indicate failure
        } finally {
            // Close resources
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean update(Club c) {
        if (c == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(
                    "update Club set name= ?, governorate= ?, city=?, startTime= ?,endTime= ?,stadiumNbr= ?, description=?, idUser= ?, latitude= ?, longitude= ? where id=?");
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getGovernorate());
            pstmt.setString(3, c.getCity());
            pstmt.setTime(4, c.getStartTime());
            pstmt.setTime(5, c.getEndTime());
            pstmt.setInt(6, c.getStadiumNbr());
            pstmt.setString(7, c.getDescription());
            pstmt.setInt(8, c.getUser().getId());
            pstmt.setInt(11 , c.getId());
            pstmt.setDouble(9, c.getLatitude()); // New attribute
            pstmt.setDouble(10, c.getLongitude()); // New attribute

            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Updated with success valid");
                return true;
            } else
                System.out.println("No update has been done");
        } catch (SQLException e) {
            System.out.println(c.getName() + " Club didn't updated successfully" + e.getMessage());
        }
        return false;
    }

    @Override
    public Club findById(int id) {
        Club c = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("select * from club where id=?");
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                User user = userDAO.getUserById(res.getInt("idUser"));
                c = new Club(
                        res.getInt("id"),
                        user,
                        res.getString("name"),
                        res.getString("governorate"),
                        res.getString("city"),
                        res.getTime("startTime"),
                        res.getTime("endTime"),
                        res.getInt("stadiumNbr"),
                        res.getString("description"),
                        res.getDouble("latitude"), // New attribute
                        res.getDouble("longitude") // New attribute

                );
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public boolean findByName(String name) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("select * from club where name=?");
            pstmt.setString(1, name);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                return true;
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return false;
    }

    public List<Club> findByUser(int idUser) {
        List<Club> clubs = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("select * from club where idUser =?");
            pstmt.setInt(1, idUser);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                User user = userDAO.getUserById(idUser);
                clubs.add(new Club(
                        res.getInt("id"),
                        user,
                        res.getString("name"),
                        res.getString("governorate"),
                        res.getString("city"),
                        res.getTime("startTime"),
                        res.getTime("endTime"),
                        res.getInt("stadiumNbr"),
                        res.getString("description"),
                        res.getDouble("latitude"), // Added latitude
                        res.getDouble("longitude") // Added longitude
                ));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return clubs;
    }


    @Override
    public Club findByRef(String ref) {
        Club c = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("select * from club i join stadium s on i.id = s.idClub where reference=?");
            pstmt.setString(1, ref);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                User user = userDAO.getUserById(res.getInt("idUser"));
                c = new Club(
                        res.getInt("id"),
                        user,
                        res.getString("name"),
                        res.getString("governorate"),
                        res.getString("city"),
                        res.getTime("startTime"),
                        res.getTime("endTime"),
                        res.getInt("stadiumNbr"),
                        res.getString("description"),
                        res.getDouble("latitude"), // Added latitude
                        res.getDouble("longitude") // Added longitude
                );
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return c;
    }


    @Override
    public int findNbrClub() {
        int nbr = 0;
        try {
            PreparedStatement pstmt = connection.prepareStatement("select sum(stadiumNbr) from club");
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                nbr = res.getInt(1);
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return nbr;
    }

    @Override
    public List<Club> findAll() {
        List<Club> clubs = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("select * from club");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                User user = userDAO.getUserById(res.getInt("idUser"));
                clubs.add(new Club(
                        res.getInt("id"),
                        user,
                        res.getString("name"),
                        res.getString("governorate"),
                        res.getString("city"),
                        res.getTime("startTime"),
                        res.getTime("endTime"),
                        res.getInt("stadiumNbr"),
                        res.getString("description"),
                        res.getDouble("latitude"), // Added latitude
                        res.getDouble("longitude") // Added longitude
                ));
            }
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
        }
        return clubs;
    }

    @Override
    public boolean delete(Club c) {
        if (c == null)
            return false;
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = connection.prepareStatement("delete from club where id =?");
            pstmt.setInt(1, c.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Club removal was successful");
                return true;
            } else {
                System.out.println("No club was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Failed to execute the query");
            e.printStackTrace();
            return false;
        }
    }

    public List<Club> searchByName(String name) {
        List<Club> result = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM club WHERE name LIKE ?");
            pstmt.setString(1, "%" + name + "%");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                User user = userDAO.getUserById(res.getInt("idUser"));
                Club club = new Club(
                        res.getInt("id"),
                        user,
                        res.getString("name"),
                        res.getString("governorate"),
                        res.getString("city"),
                        res.getTime("startTime"),
                        res.getTime("endTime"),
                        res.getInt("stadiumNbr"),
                        res.getString("description")
                );
                result.add(club);
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getClubCountForUser(int userId) {
        int clubCount = 0;
        String query = "SELECT COUNT(*) AS clubCount FROM club WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clubCount = resultSet.getInt("clubCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubCount;
    }



    private List<String> getDistinctGovernorates() {
        List<String> governorates = new ArrayList<>();

        String query = "SELECT DISTINCT governorate FROM club";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String governorate = resultSet.getString("governorate");
                governorates.add(governorate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return governorates;
    }

    public Map<String, Integer> countClubsByGovernorate() {
        Map<String, Integer> governorateCounts = new HashMap<>();
        String query = "SELECT governorate, COUNT(*) AS count FROM club GROUP BY governorate";
        try (
                     PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String governorate = resultSet.getString("governorate");
                int count = resultSet.getInt("count");
                governorateCounts.put(governorate, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception...
        }
        return governorateCounts;
    }

    // Method to count clubs by city
    public Map<String, Integer> countClubsByCity() {
        Map<String, Integer> cityCounts = new HashMap<>();
        String query = "SELECT city, COUNT(*) AS count FROM club GROUP BY city";
        try (
                     PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String city = resultSet.getString("city");
                int count = resultSet.getInt("count");
                cityCounts.put(city, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception...
        }
        return cityCounts;
    }

    public Map<String, Integer> countStadiumsByClub() {
        Map<String, Integer> stadiumCountByClub = new HashMap<>();
        String query = "SELECT name, stadiumNbr FROM club";

        try (
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String clubName = resultSet.getString("name");
                int stadiumCount = resultSet.getInt("stadiumNbr");
                stadiumCountByClub.put(clubName, stadiumCount);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return stadiumCountByClub;
    }

    public List<Club> findFilteredClubs(String selectedGovernorate, String selectedCity) {
        List<Club> clubs = new ArrayList<>();
        try {
            PreparedStatement statement;
            if (selectedCity == null || selectedCity.isEmpty()) {
                statement = connection.prepareStatement(SELECT_CLUBS_BY_GOVERNORATE);
                statement.setString(1, selectedGovernorate);
            } else {
                statement = connection.prepareStatement(SELECT_CLUBS_BY_GOVERNORATE_AND_CITY);
                statement.setString(1, selectedGovernorate);
                statement.setString(2, selectedCity);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Club club = new Club();
                    // Set club properties from resultSet
                    club.setId(resultSet.getInt("id"));
                    club.setName(resultSet.getString("name"));
                    club.setGovernorate(resultSet.getString("governorate"));
                    club.setCity(resultSet.getString("city"));
                    club.setStadiumNbr(resultSet.getInt("stadiumNbr"));
                    club.setStartTime(resultSet.getTime("startTime")); // Assuming startTime is of type TIME in the database
                    club.setEndTime(resultSet.getTime("endTime")); // Assuming endTime is of type TIME in the database
                    club.setDescription(resultSet.getString("description"));
                    club.setUser(currentUser);
                    clubs.add(club);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }


}
