package services.Subscription;

import database.Connexion;
import entities.Subscription;
import entities.User;
import entities.Offer;
import services.User.UserDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {
    private Connection connection;

    private OfferDAO offerDAO = new OfferDAO();
    private UserDAO userDAO = new UserDAO();

    public SubscriptionDAO() {
        this.connection = Connexion.getInstance(); // Assuming Connexion.getInstance() returns the connection
    }

    public List<Subscription> findByUser(User user) {
        List<Subscription> subscriptions = new ArrayList<>();
        String query = "SELECT * FROM Subscription WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int offerId = resultSet.getInt("idOffer");
                Offer offer = new OfferDAO().findById(offerId);
                Date startdate = resultSet.getDate("startdate");
                Date enddate = resultSet.getDate("enddate");
                Subscription subscription = new Subscription(id, user, offer, startdate,enddate);
                subscriptions.add(subscription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscriptions;
    }

    // Method to find subscription by user ID
    public Subscription findByUserId(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Subscription subscription = null;
        Offer offers = null;
        User user = null;
        try {
            connection = Connexion.getInstance(); // Get connection
            String query = "SELECT * FROM subscription WHERE idUser = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Extract subscription data from the result set
                int id = resultSet.getInt("id");
                int offerId = resultSet.getInt("idOffer");
                // You can extract other subscription attributes as well
                offers = offerDAO.findById(offerId);
                user = userDAO.getUserById(userId);
                Date startdate = resultSet.getDate("startdate");
                Date enddate = resultSet.getDate("enddate");


                // Create a new Subscription object
                subscription = new Subscription(id, user, offers,startdate,enddate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return subscription;
    }


    public boolean save(Subscription subscription) {
        String query = "INSERT INTO subscription (idUser, idOffer, startDate, endDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, subscription.getUserId().getId());
            statement.setInt(2, subscription.getOfferId().getId());
            statement.setDate(3, new java.sql.Date(subscription.getStartDate().getTime()));
            statement.setDate(4, new java.sql.Date(subscription.getEndDate().getTime()));
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int subscriptionId) {
        String query = "DELETE FROM subscription WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, subscriptionId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean userExistsInSubscriptionTable(int userId) {
        try {
            String query = "SELECT id FROM subscription WHERE idUser = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next(); // Returns true if the user exists in the Subscription table
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of any exception
        }
    }

    // Other methods...
}
