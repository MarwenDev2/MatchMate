package entities;

import java.util.Date;

public class Evenement {
    private int id;
    private String name;
    private int ClubName;

    private Date dateDeb;
    private Date dateFin;
    private Club idClub;
    String clubname;
    private int nbrParticipant;
    private Float price;

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Evenement() {
    }

    public Evenement(String name, Date dateDeb, Date dateFin, Club idClub, int nbrParticipant, Float price) {
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.idClub = idClub;
        this.nbrParticipant=nbrParticipant;
        this.price=price;
    }

    public Evenement(int id, String name, Date dateDeb, Date dateFin, int nbrParticipantClub, Club idClub, Float price) {
        this.id = id;
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.idClub = idClub;
        this.nbrParticipant=nbrParticipant;
        this.price=price;
    }
    public Evenement(int id, String name, Date dateDeb, Date dateFin, Club idClub, int nbrParticipant, Float price) {
        this.id = id;
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.idClub = idClub;
        this.nbrParticipant = nbrParticipant;
        this.price = price;
    }
    public Evenement(int id, String name, Date dateDeb, Date dateFin, String clubname, int nbrParticipant, Float price) {
        this.id = id;
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.clubname = clubname; // Fix here
        this.nbrParticipant = nbrParticipant;
        this.price = price;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateDeb() {
        return dateDeb;
    }

    public void setDateDeb(Date dateDeb) {
        this.dateDeb = dateDeb;
    }

    public Club getIdClub() {
        return idClub;
    }

    public void setIdClub(Club idClub) {
        this.idClub = idClub;
    }

    public int getNbrParticipant() {
        return nbrParticipant;
    }

    public void setNbrParticipant(int nbrParticipant) {
        this.nbrParticipant = nbrParticipant;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", dateDeb=" + dateDeb +
                ", dateFin=" + dateFin +
                ", nbrParticipant=" + nbrParticipant +
                ", idClub=" + idClub +
                ", price=" + price +
                '}';
    }


    public Evenement(String name, Date dateDeb, Date dateFin, int nbrParticipant, Float price) {
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.nbrParticipant=nbrParticipant;
        this.price=price;
    }

    public Evenement(int id, String name, Date dateDeb, Date dateFin, int s, int nbrParticipant, Float price) {
        this.id=id;
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        this.nbrParticipant=nbrParticipant;
        this.ClubName=s;
    }


    public int getClubName() {
        return ClubName;
    }

    public void setClubName(int clubName) {
        ClubName = clubName;
    }
}
