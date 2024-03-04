package services;

import entities.Panier;
import entities.Product;
import database.Connexion;
import entities.SessionManager;
import entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierService {
    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;
    private ProductService po = new ProductService() ;

    private User currentUser= SessionManager.getInstance().getCurrentUser();
    private UserDAO UserService = new UserDAO();

    public PanierService() {
        conn= Connexion.getInstance();
    }

    public int save(Panier p){
        if (p == null)
            return -1;

        int n = 0;
        PreparedStatement pst = null;
        ResultSet generatedKeys = null;
        try {
            pst = conn.prepareStatement(
                    "insert into panier (idProduct,idUser,quantity,total) values (?,?,?,?)"
                    ,Statement.RETURN_GENERATED_KEYS);
            Float totall= p.getQuantity()*p.getIdProuct().getPrice();
            pst.setInt(1,p.getIdProuct().getId());

            pst.setInt(2,p.getIdUser().getId());
            pst.setInt(3,p.getQuantity());
            pst.setFloat(4,totall);
            pst.executeUpdate();
            n = pst.executeUpdate();
            pst.close();
            if (n == 1) {
                generatedKeys = pst.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int productId = generatedKeys.getInt(1); // Retrieve the auto-generated club ID
                    System.out.println("The Club has been added with ID: " + productId);
                    return productId; // Return the generated club ID
                } else {
                    System.out.println("Failed to retrieve club ID");
                    return -1; // Return -1 if club ID retrieval fails
                }
            } else {
                System.out.println("NO product has been added");
            }

        } catch (SQLException e1) {
            System.out.println(p.getIdProuct().getName() + "  addition was failed"+ e1.getMessage());
        }
        return -1;
    }
    public int add(Product p){

        ResultSet generatedKeys = null;
        String redquete="insert into product (reference,name,price,quantity,size,type) values (?,?,?,?,?,?)";
        try {
            pst=conn.prepareStatement(redquete,Statement.RETURN_GENERATED_KEYS);

            pst.setString(1,p.getReference());
            pst.setString(2,p.getName());
            pst.setFloat(3,p.getPrice());
            pst.setInt(4,p.getQuantity());
            pst.setString(5,p.getSize());
            pst.setString(6,p.getType());
            int n= pst.executeUpdate();

            if (n == 1) {
                // Retrieve the auto-generated keys
                generatedKeys = pst.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int pId = generatedKeys.getInt(1); // Retrieve the auto-generated club ID
                    System.out.println("The Club has been added with ID: " + pId);
                    return pId; // Return the generated club ID
                } else {
                    System.out.println("Failed to retrieve club ID");
                    return -1; // Return -1 if club ID retrieval fails
                }
            } else {
                System.out.println("No club has been added");
                return -1; // Return -1 to indicate failure
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public  void delete(Panier p) {
        try
        {
            Connection cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/matchmate", "root", "");
            String qry="DELETE FROM `panier` WHERE id=?";
            PreparedStatement smt = cnx.prepareStatement(qry);
            smt.setInt(1, p.getId());
            smt.executeUpdate();
            System.out.println("Suppression Effectué");
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

    }


    public int Somme(Panier p) {
        int rs = 0;
        try {
            Connection cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/matchmate", "root", "");
            String qry = "SELECT SUM(total) AS totalSum FROM `panier` WHERE idUser=?;";
            PreparedStatement smt = cnx.prepareStatement(qry);
            smt.setInt(1, p.getId());
            ResultSet resultSet = smt.executeQuery();
            if (resultSet.next()) {
                rs = resultSet.getInt("totalSum");
            }
            System.out.println("Somme Effectué");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rs;
    }




    public void Update(Panier p) {
        try
        {             Connection cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/matchmate", "root", "");

            String qry="UPDATE `panier` SET `quantity`=? ,`total`=? WHERE `id`=?";
            PreparedStatement stm = cnx.prepareStatement(qry);
            stm.setInt(1, p.getQuantity());
            Float tot=p.getQuantity()*p.getIdProuct().getPrice();
            stm.setFloat(2, tot);
            stm.setInt(3,p.getId());

            stm.executeUpdate();
            System.out.println("Modification effectué");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /*@Override
    public List<Product> readAll() {
        List<Product> all = new ArrayList<>();
        return all;
    }

    @Override
    public Product readById(int id) {
        return null;
    }*/






    public List<Panier> getAll() {
        ArrayList<Panier> paniers = new ArrayList();
        String qry = "SELECT * FROM `panier`";
        ProductService productService=new ProductService();
        Product prod=new Product();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
User u=UserService.getUserById(rs.getInt(4));
                prod=productService.readById(rs.getInt("idProduct"));
                Panier p = new Panier();
                p.setId(rs.getInt("id"));
                p.setIdUser (u);
                p.setIdProuct(prod);
                p.setTotal(rs.getFloat("total"));
                p.setQuantity(rs.getInt("quantity"));


                paniers.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return paniers;
    }
    public List<Panier> getPanierByUserId(int userId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Panier> paniers = new ArrayList<>();

        try { Connection cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/matchmate", "root", "");
            // Prepare SQL statement
            String query = "SELECT * FROM panier WHERE idUser=?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, userId);
            Product p =null;

            // Execute query
            resultSet = statement.executeQuery();

            // Process result set
            while (resultSet.next()) {
                Panier panier = new Panier();
                User u=UserService.getUserById(resultSet.getInt(4));

                panier.setId(resultSet.getInt("id"));
                panier.setQuantity(resultSet.getInt("quantity"));
                panier.setTotal(resultSet.getFloat("total"));
                panier.setIdUser(u);
                p=po.readById(resultSet.getInt("idProduct"));
                System.out.println("fffffff"+p);
                panier.setIdProuct(p);

                // Add panier to the list
                paniers.add(panier);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace(); // Print the stack trace for debugging
            // Optionally, log the exception or handle it in a different way
        }


        return paniers;
    }



    public Panier readById(int id) {
        return null;
    }
}
