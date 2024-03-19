package services;

import database.Connexion;
import entities.Payment;
import entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO implements IPaymentDAO<Payment> {

    Connection cnx = Connexion.getInstance();

    public boolean save(Payment payment,int id) {
        if (payment == null)
            return false;
        int n = 0;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            pstmt = cnx.prepareStatement(
                    "INSERT INTO payment(type, idUser) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, payment.getType());
            pstmt.setInt(2, payment.getUser().getId());
            n = pstmt.executeUpdate();
            if (n == 1) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int lastInsertedId = generatedKeys.getInt(1);
                    if(insertIntoTypeTable(id,lastInsertedId,payment.getType())) {
                        System.out.println("Payment has been added with ID: " + lastInsertedId);
                        return true;
                    }
                }
            }
            System.out.println("No Payment has been added");
        } catch (SQLException e) {
            System.out.println("Payment addition failed: " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean insertIntoTypeTable(int relatedId,int paymentId, String type) {
        String typeTableName = "payment" + type.toLowerCase();
        String typeLower = type.toLowerCase();
        String firstLetterCapitalized = Character.toUpperCase(typeLower.charAt(0)) + typeLower.substring(1);
        String idObject = "id" + firstLetterCapitalized;
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement(
                    "INSERT INTO " + typeTableName + "(" + idObject + ", idPayment) VALUES (?,?)");
            pstmt.setInt(1, paymentId);
            pstmt.setInt(2,relatedId);
            int result = pstmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Payment> findByUserId(int userId) {
        List<Payment> payments = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            pstmt = cnx.prepareStatement("SELECT * FROM payment WHERE idUser = ?");
            pstmt.setInt(1, userId);
            res = pstmt.executeQuery();
            while (res.next()) {
                UserDAO us = new UserDAO();
                User u = us.findById(res.getInt("idUser"));
                Payment payment = new Payment(res.getInt("id"), res.getString("type"), u);
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching payments by user ID: " + e.getMessage());
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return payments;
    }

    public boolean delete(Payment payment) {
        if (payment == null)
            return false;
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("DELETE FROM payment WHERE id = ?");
            pstmt.setInt(1, payment.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Payment removal was successful");
                return true;
            } else {
                System.out.println("No payment was deleted");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error during payment deletion");
            e.printStackTrace();
            return false;
        }
    }
}
