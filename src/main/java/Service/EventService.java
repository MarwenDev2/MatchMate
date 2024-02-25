package Service;

import entities.Club;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entities.Evenement;

public class EventService implements Iservice<Evenement> {
    private Connection cnx;
    private PreparedStatement ste;

    public EventService() {
        cnx = DataSource.getInstance().getCnx();
    }

    @Override

    public int add(Evenement evenement) {
        String req = "INSERT INTO event (name, dateDeb, dateFin, nbrParticipants, idClub,price) VALUES (?, ?, ?, ?, ?,?)";
        int eventId = 0;
        try {
            ste = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ste.setString(1, evenement.getName());
            ste.setDate(2, new java.sql.Date(evenement.getDateDeb().getTime()));
            ste.setDate(3, new java.sql.Date(evenement.getDateFin().getTime()));
            ste.setInt(4, evenement.getNbrParticipant());
            ste.setInt(5, evenement.getIdClub().getId());
            ste.setFloat(6, evenement.getPrice());// Set parameter 5 with the value of idClub
            int i = ste.executeUpdate();
            ResultSet rs = ste.getGeneratedKeys();
            if (rs.next()) {
                eventId = rs.getInt(1);
                evenement.setId(eventId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return eventId;
    }


    @Override
    public void Delete(Evenement evenement) {
        String req = "DELETE FROM event WHERE name = ?";
        try {
            ste = cnx.prepareStatement(req);
            ste.setString(1, evenement.getName());
            ste.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Update(Evenement evenement, Evenement evenement1) {
        String req = "UPDATE event SET name=?, dateDeb=?, dateFin=?, nbrParticipants=?, price=? WHERE id=?";
        try {
            ste = cnx.prepareStatement(req);
            ste.setString(1, evenement1.getName());
            ste.setDate(2, new java.sql.Date(evenement1.getDateDeb().getTime()));
            ste.setDate(3, new java.sql.Date(evenement1.getDateFin().getTime()));
            ste.setInt(4, evenement1.getNbrParticipant());
            // Check if the price attribute of event1 is not null before accessing it
            if (evenement1.getPrice() != null) {
                ste.setFloat(5, evenement1.getPrice()); // Assuming getPrice() returns a Float directly
            } else {
                ste.setNull(5, Types.FLOAT);
            }
            ste.setInt(6, evenement.getId());
            ste.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());        }
    }





    public ObservableList<Evenement> ReadAll(int Id) {
        List<Evenement> evenements = new ArrayList<>();
        String req = "SELECT * FROM event where `idClub`= ? ";
        try {
            PreparedStatement ste = cnx.prepareStatement(req);
            ste.setInt(1, Id);

            ResultSet rs = ste.executeQuery();
            while (rs.next()) {
                ClubDAO dao = new ClubDAO();
                int id = rs.getInt("id");
                String name = rs.getString("Name");
                Date dateDeb = rs.getDate("dateDeb");
                Date dateFin = rs.getDate("dateFin");
                int nbrParticipant = rs.getInt("nbrParticipants");
                int idClub = rs.getInt("idClub");
                Float price = rs.getFloat("Price");

                // Récupérer l'objet Club correspondant à idClub
                Club club = dao.findById(idClub);

                // Debugging statements
                System.out.println("Club ID: " + idClub);
                System.out.println("Club object: " + club);
                System.out.println("Club name: " + (club != null ? club.getName() : "null"));

                // Handle NULL clubname
                String clubname = (club != null) ? club.getName() : "Unknown Club";

                // Créer l'objet Event avec les données récupérées de la base de données
                evenements.add(new Evenement(id, name, dateDeb, dateFin, clubname, nbrParticipant, price));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return FXCollections.observableArrayList(evenements);
    }

    @Override
    public List<Evenement> ReadAll() {
        List<Evenement> evenements = new ArrayList<>();
        String req = "SELECT * FROM event  ";
        try {
            PreparedStatement ste = cnx.prepareStatement(req);


            ResultSet rs = ste.executeQuery();
            while (rs.next()) {
                ClubDAO dao = new ClubDAO();
                int id = rs.getInt("id");
                String name = rs.getString("Name");
                Date dateDeb = rs.getDate("dateDeb");
                Date dateFin = rs.getDate("dateFin");
                int nbrParticipant = rs.getInt("nbrParticipants");
                int idClub = rs.getInt("idClub");
                Float price = rs.getFloat("Price");

                // Récupérer l'objet Club correspondant à idClub
                Club club = dao.findById(idClub);

                // Debugging statements
                System.out.println("Club ID: " + idClub);
                System.out.println("Club object: " + club);
                System.out.println("Club name: " + (club != null ? club.getName() : "null"));

                // Handle NULL clubname
                String clubname = (club != null) ? club.getName() : "Unknown Club";

                // Créer l'objet Event avec les données récupérées de la base de données
                evenements.add(new Evenement(id, name, dateDeb, dateFin, clubname, nbrParticipant, price));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return FXCollections.observableArrayList(evenements);

    }


    @Override
    public Evenement readByName(String name) {
        String req = "SELECT * FROM event WHERE name= ?";
        ResultSet res;
        Evenement evenement=new Evenement();
        ObservableList<Evenement> evenements = FXCollections.observableArrayList(); // Create an empty list to store events
        try {
            ste = cnx.prepareStatement(req);
            ste.setString(1, name);
            res = ste.executeQuery();
            while (res.next()) {
                ClubDAO clubDAO=new ClubDAO();
                int clubid=res.getInt(5);

                // Create an Event object for each row in the result set and add it to the list
              evenement = new Evenement(res.getInt("id"), res.getString("name"), res.getDate("dateDeb"), res.getDate("dateFin"), res.getInt("nbrParticipants"), res.getInt("idClub"), res.getFloat("Price"));
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return evenement; // Return the list of events
    }
    public Evenement readById(int id) {
        String req = "SELECT * FROM event WHERE id= ?";
        Evenement e = new Evenement();
        ResultSet res;
        ObservableList<Evenement> evenements = FXCollections.observableArrayList(); // Create an empty list to store events

        try {
            PreparedStatement ste = cnx.prepareStatement(req);
            ste.setInt(1, id);
            res = ste.executeQuery();

            while (res.next()) {
                int clubId = res.getInt("idClub");

                // Maintenant, vous devez obtenir l'objet Club correspondant à clubId
                ClubDAO clubService = new ClubDAO(); // Vous devez ajuster cela selon la manière dont vous obtenez un Club
                Club club = clubService.findById(clubId);

                // Créer l'objet Event avec les données récupérées de la base de données
                e = new Evenement(res.getInt("id"), res.getString("name"), res.getDate("dateDeb"), res.getDate("dateFin"), club, res.getInt("nbrParticipants"), res.getFloat("Price"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return e;
    }



    @Override
    public void addPst(Evenement evenement) {
        // Implementation not provided in original code
    }

    public int addImage(int eventId, int imageId) {
        String req = "INSERT INTO `imageevent`(`idEvent`, `idImage`) VALUES (?, ?)";
        int imageEventId = 0;
        try {
            ste = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ste.setInt(1, eventId);
            ste.setInt(2, imageId);
            ste.executeUpdate();
            ResultSet rs = ste.getGeneratedKeys();
            if (rs.next()) {
                imageEventId = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding image to event: " + e.getMessage());
        }
        return imageEventId;
    }
}
