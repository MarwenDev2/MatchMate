package services.Subscription;

import database.Connexion;
import entities.Offer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class OfferDAO {
    private Connection connection;



    public OfferDAO() {
        this.connection = Connexion.getInstance(); // Assuming Connexion.getInstance() returns the connection
    }

    public Offer findById(int id) {
        Offer offer = null;
        String query = "SELECT * FROM Offer WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String text = resultSet.getString("text");
                int nbrClub = resultSet.getInt("nbrClub");
                offer = new Offer(id, name, price, text, nbrClub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offer;
    }

    public Offer findByName(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Offer offer = null;

        try {
            connection = Connexion.getInstance(); // Get connection
            String query = "SELECT * FROM offer WHERE name = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Extract offer data from the result set
                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");
                String text = resultSet.getString("text");
                int nbrClub = resultSet.getInt("nbrClub");
                // You can extract other offer attributes as well

                // Create a new Offer object
                offer = new Offer(id, name, price, text, nbrClub);
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

        return offer;
    }


}


    // Other methods...

