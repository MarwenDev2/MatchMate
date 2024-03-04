package services.Notification;

import database.Connexion;
import entities.Notification;
import entities.User;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private Connection connection;

    public NotificationDAO() {
        this.connection = Connexion.getInstance(); // Assuming Connexion.getInstance() returns the connection
    }

    // Method to save a new notification
    public boolean save(Notification notification) {
        String query = "INSERT INTO notification (idUser, text) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, notification.getUser().getId());
            statement.setString(2, notification.getText());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to retrieve all notifications for a user
    public List<Notification> findByUser(User user) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                Notification notification = new Notification(user, text);
                notification.setId(id);
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // Method to delete a notification
    public boolean delete(int notificationId) {
        String query = "DELETE FROM notification WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, notificationId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showNotification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(5))
                .showInformation();
    }
    public List<Notification> getNotificationsForUser(User user) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                // Create Notification object and add it to the list
                Notification notification = new Notification(id, user, text);
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }




    // Other methods...
}
