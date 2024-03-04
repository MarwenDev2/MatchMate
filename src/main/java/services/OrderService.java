package services;

import entities.Order;
import database.Connexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;

    public OrderService() {
        conn = Connexion.getInstance();
    }

    public int save(Order order) {
        String query = "INSERT INTO `order` (idPanier, dateOrder) VALUES (?, ?)";
        int affectedRows;
        try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, order.getIdPanier().getId());
            pst.setDate(2, order.getDateOrder()); // Set the current date as the order date
            affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Creating order failed, no rows affected.");
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
            return -1;
        }
        return affectedRows;
    }


    public List<Order> getAll() {
        ArrayList<Order> orders = new ArrayList<>();
        String qry = "SELECT * FROM `order`";
        PanierService panierService = new PanierService();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setIdPanier(panierService.readById(rs.getInt("idPanier")));
                o.setDateOrder(rs.getDate("datOrder"));
                orders.add(o);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return orders;
    }

    private void readById(int idPanier) {
        // Implement this method as needed
    }
}
