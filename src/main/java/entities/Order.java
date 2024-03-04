package entities;

import java.sql.Date;

public class Order {
    private int id;
    private Panier idPanier;
    private Date dateOrder;

    public Order() {
    }

    public Order(Panier idPanier, Date dateOrder) {
        this.idPanier = idPanier;
        this.dateOrder = dateOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Panier getIdPanier() {
        return idPanier;
    }

    public void setIdPanier(Panier idPanier) {
        this.idPanier = idPanier;
    }

    public Date getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", idPanier=" + idPanier +
                ", dateOrder=" + dateOrder +
                '}';
    }
}
