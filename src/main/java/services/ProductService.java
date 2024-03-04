package services;

import entities.Product;
import database.Connexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService implements IService<Product> {

    private Connection conn;
    private Statement ste;
    private PreparedStatement pst;

    public ProductService() {
        conn= Connexion.getInstance()
        ;
    }

    public int save(Product p){
        if (p == null)
            return -1;

        int n = 0;
        PreparedStatement pst = null;
        ResultSet generatedKeys = null;
        try {
            pst = conn.prepareStatement(
                    "insert into product (reference,name,price,quantity,size,type,image) values (?,?,?,?,?,?,?)"
                    ,Statement.RETURN_GENERATED_KEYS);
            pst.setString(1,p.getReference());
            pst.setString(2,p.getName());
            pst.setFloat(3,p.getPrice());
            pst.setInt(4,p.getQuantity());
            pst.setString(5,p.getSize());
            pst.setString(6,p.getType());
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
            System.out.println(p.getName() + "  addition was failed"+ e1.getMessage());
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

    @Override
    public  void delete(Product p) {
        try
        {
            String qry="DELETE FROM `product` WHERE id=?";
            PreparedStatement smt = conn.prepareStatement(qry);
            smt.setInt(1, p.getId());
            smt.executeUpdate();
            System.out.println("Suppression Effectué");
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    @Override

    public void Update(Product p) {
            try
            {
                String qry="UPDATE `product` SET `reference`=?,`name`=?,`price`=?,`quantity`=?,`size`=?, `type`=?  WHERE `id`=?";
                PreparedStatement stm = conn.prepareStatement(qry);
                stm.setString(1, p.getReference());
                stm.setString(2, p.getName());
                stm.setFloat(3, p.getPrice());
                stm.setInt(4, p.getQuantity());
                stm.setString(5, p.getSize());
                stm.setString(6, p.getType());
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

    public void ajouter(Product product) {
    }



    @Override
    public ArrayList<Product> getAll() {
        ArrayList<Product> postes = new ArrayList();
        String qry = "SELECT * FROM `product`";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setReference(rs.getString("reference"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getFloat("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setSize(rs.getString("size"));
                p.setType(rs.getString("type"));

                postes.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return postes;
    }

    @Override
    public Product readById(int id) {
        String req = "SELECT * FROM product WHERE id=?";
        try {
            // Check if the connection is closed and reopen it if necessary
            PreparedStatement ps = conn.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product(rs.getInt("id"),rs.getString("reference"),rs.getString("name")
                        ,rs.getFloat("price"),rs.getInt("quantity"),rs.getString("size"),rs.getString("type"));
                return  p;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading product by ID", e);
        }
        // If no product with the given ID is found, return null
        return null;
    }


    @Override
    public List<Product> rechercheParRef(String reference) {

        return getAll().stream()
                .filter(sport -> sport.getReference().equalsIgnoreCase(reference))
                .collect(Collectors.toList());
    }
    @Override
    public List<Product> triProductByReference() {
        return getAll().stream().sorted((o1, o2) -> o1.getReference().compareTo(o2.getReference())).collect(Collectors.toList());
    }


}