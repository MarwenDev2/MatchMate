package entities;

public class Participants {
    private int id_user;
    private Evenement evenement;

    public Participants(int id_user, Evenement evenement) {
        this.id_user = id_user;
        this.evenement = evenement;
    }

    public Participants() {
    }

    @Override
    public String toString() {
        return "Participants{" +
                "id_user=" + id_user +
                ", Event" + evenement.toString() +
                '}';
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public Evenement getEvent() {
        return evenement;
    }

    public void setEvent(Evenement evenement) {
        this.evenement = evenement;
    }
}
