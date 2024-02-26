package service;

import entities.Product;
import entities.ProductClient;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductClientService implements IServiceClient<ProductClient> {

    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;

    public ProductClientService() {
        conn = DataSource.getInstance().getCnx();
    }

    public int save(ProductClient p) {
        if (p == null)
            return -1;

        int n = 0;
        PreparedStatement pst = null;
        ResultSet generatedKeys = null;
        try {
            pst = conn.prepareStatement(
                    "insert into productClient (ref,name,price,type,image) values (?,?,?,?,?,?)"
                    , Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, p.getRef());
            pst.setString(2, p.getName());
            pst.setFloat(3, p.getPrice());
            pst.setString(4, p.getType());
            pst.setString(5, p.getImage());
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
            System.out.println(p.getName() + "  addition was failed" + e1.getMessage());
        }
        return -1;
    }

    public void add(ProductClient p) {
        String redquete = "insert into productClient (ref,name,price,type,image) values (?,?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(redquete);
            pst.setString(1, p.getRef());
            pst.setString(2, p.getName());
            pst.setFloat(3, p.getPrice());
            pst.setString(4, p.getType());
            pst.setString(5, p.getImage());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(ProductClient p) {
        try {
            String qry = "DELETE FROM `productClient` WHERE id=?";
            PreparedStatement smt = conn.prepareStatement(qry);
            smt.setInt(1, p.getId());
            smt.executeUpdate();
            System.out.println("Suppression Effectué");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override

    public void Update(ProductClient p) {
        try {
            String qry = "UPDATE `productClient` SET `ref`=?,`name`=?,`price`=?,`type`,`image`=?  WHERE `id`=?";
            PreparedStatement stm = conn.prepareStatement(qry);
            stm.setString(1, p.getRef());
            stm.setString(2, p.getName());
            stm.setFloat(3, p.getPrice());
            stm.setString(6, p.getType());
            stm.setString(5, p.getImage());
            stm.executeUpdate();
            System.out.println("Modification effectué");
        } catch (Exception ex) {
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

    public void ajouter(Product product) {
    }


    @Override
    public ArrayList<ProductClient> getAll() {
        ArrayList<ProductClient> postes = new ArrayList();
        String qry = "SELECT * FROM `productClient`";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                ProductClient p = new ProductClient();
                p.setId(rs.getInt("id"));
                p.setRef(rs.getString("reference"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getFloat("price"));
                p.setType(rs.getString("type"));
                p.setImage(rs.getString("image"));

                postes.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return postes;
    }

    @Override
    public ProductClient readById(int id) {
        return null;
    }
}





