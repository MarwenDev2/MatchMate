package service;

import entities.Command;
import entities.Product;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandService implements IServiceCommand<Command> {
    private final Connection conn;
    private Statement ste;
    private PreparedStatement pst;

    public CommandService() {
        conn= DataSource.getInstance().getCnx();
    }




    @Override
    public int save(Command c) {
        if (c == null)
            return -1;

        int n = 0;
        PreparedStatement pst = null;
        ResultSet generatedKeys = null;
        try {
            pst = conn.prepareStatement(
                    "insert into command (reference,dateC,priceTotal,idUser) values (?,?,?,?)"
                    ,Statement.RETURN_GENERATED_KEYS);
            pst.setString(1,c.getReference());
            pst.setDate(2,c.getDateC());
            pst.setFloat(3,c.getPriceTotal());
            pst.setInt(4,c.getIdUser());

            pst.executeUpdate();
            n = pst.executeUpdate();
            pst.close();
            if (n == 1) {
                generatedKeys = pst.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int productId = generatedKeys.getInt(1); // Retrieve the auto-generated club ID
                    System.out.println("The Command has been added with ID: " + productId);
                    return productId; // Return the generated club ID
                } else {
                    System.out.println("Failed to retrieve command ID");
                    return -1; // Return -1 if club ID retrieval fails
                }
            } else {
                System.out.println("NO command has been added");
            }

        } catch (SQLException e1) {
            System.out.println(c.getReference() + "  addition was failed"+ e1.getMessage());
        }
        return -1;
    }

    @Override
    public void add(Command c) {
        String redquete="insert into command (reference,dateC,priceTotal,idUser) values (?,?,?,?)";
        try {
            pst=conn.prepareStatement(redquete);
            pst.setString(1,c.getReference());
            pst.setDate(2,c.getDateC());
            pst.setFloat(3,c.getPriceTotal());
            pst.setInt(4,c.getIdUser());


            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(Command c) {
        try
        {
            String qry="DELETE FROM `command` WHERE id=?";
            PreparedStatement smt = conn.prepareStatement(qry);
            smt.setInt(1, c.getId());
            smt.executeUpdate();
            System.out.println("Suppression Effectué");
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void Update(Command c) {
        try
        {
            String qry="UPDATE `command` SET `reference`=?,`date`=?,`pricetotal`=?,`idUser`=? WHERE `id`=?";
            PreparedStatement pst = conn.prepareStatement(qry);
            pst.setString(1,c.getReference());
            pst.setDate(2,c.getDateC());
            pst.setFloat(3,c.getPriceTotal());
            pst.setInt(4,c.getIdUser());
            pst.executeUpdate();
            System.out.println("Modification effectué");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public List<Command> getAll() {
        ArrayList<Command> commandes = new ArrayList();
        String qry = "SELECT * FROM `command`";
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                Command c = new Command();
                c.setId(rs.getInt("id"));
                c.setReference(rs.getString("reference"));
                c.setDateC(rs.getDate("date"));
                c.setPriceTotal(rs.getFloat("price"));
                c.setIdUser(rs.getInt("idUser"));


                commandes.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return commandes;
    }

    @Override
    public Command readById(int id) {
        return null;
    }
}
