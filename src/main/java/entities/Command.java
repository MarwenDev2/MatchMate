package entities;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Command {
    private int id;
    private String reference;
    private Date dateC;
    private float priceTotal;
    private int idUser;

    public Command() {
    }

    public Command(int id, String reference, Date dateC, float priceTotal, int idUser) {
        this.id = id;
        this.reference = reference;
        this.dateC = dateC;
        this.priceTotal = priceTotal;
        this.idUser = idUser;
    }

    public Command(String text, LocalDate value, String text1, int i) {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public java.sql.Date getDateC() {
        return (java.sql.Date) dateC;
    }

    public void setDateC(Date dateC) {
        this.dateC = dateC;
    }

    public float getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(float priceTotal) {
        this.priceTotal = priceTotal;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return id == command.id && Float.compare(priceTotal, command.priceTotal) == 0 && idUser == command.idUser && Objects.equals(reference, command.reference) && Objects.equals(dateC, command.dateC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, dateC, priceTotal, idUser);
    }

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", dateC=" + dateC +
                ", priceTotal=" + priceTotal +
                ", idUser=" + idUser +
                '}';
    }
}
