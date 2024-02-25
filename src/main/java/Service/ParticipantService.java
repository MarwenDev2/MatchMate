package Service;

import entities.Evenement;
import entities.Participants;
import javafx.collections.ObservableList;
import utils.DataSource;

import java.sql.*;
import java.util.List;

public class ParticipantService implements ParticipantInt<Participants> {
    private Connection cnx;
    private PreparedStatement ste;

    public ParticipantService() {
        cnx = DataSource.getInstance().getCnx();
    }

    @Override
    public void add_Participant(Participants participants) {
        String req = "INSERT INTO participation (idEvent, idUser) VALUES (?,?)";
        try {
            ste = cnx.prepareStatement(req);
            ste.setInt(1, participants.getEvent().getId());
            ste.setInt(2, participants.getId_user());
            int i = ste.executeUpdate();

    } catch (SQLException e) {
            throw new RuntimeException(e);
        } ;



    }

    @Override
    public void Delete_Participant(Participants participants) {
        String req = "DELETE FROM participation WHERE idUser = ?";
        try {
            ste = cnx.prepareStatement(req);
            ste.setInt(1, participants.getId_user());
            ste.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void Update_Participant(Participants participants, Participants t4) {

    }

    @Override
    public List<Participants> ReadAllParticipant() {
        return null;
    }

    @Override
    public ObservableList<Evenement> readParticipantByName(String name) {
        return null;
    }

    @Override
    public void addPst(Participants participants) {

    }
}
