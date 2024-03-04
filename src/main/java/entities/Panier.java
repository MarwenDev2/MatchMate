package entities;

public class Panier {
    private int id;
    private User idUser;
    private Product idProuct;
    private int quantity;
    private float total;

    public Panier() {
    }

    public Panier(int id, User idUser, Product idProuct, int quantity, float total) {
        this.id = id;
        this.idUser = idUser;
        this.idProuct = idProuct;
        this.quantity = quantity;
        this.total = total;
    }

    public Panier(int id, User idUser, Product idProuct, int quantity) {
        this.id = id;
        this.idUser = idUser;
        this.idProuct = idProuct;
        this.quantity = quantity;
    }

    public Panier(User idUser, Product idProuct, int quantity, float total) {
        this.idUser = idUser;
        this.idProuct = idProuct;
        this.quantity = quantity;
        this.total = total;
    }
    public Panier(User idUser, Product idProuct, int quantity ) {
        this.idUser = idUser;
        this.idProuct = idProuct;
        this.quantity = quantity;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    public Product getIdProuct() {
        return idProuct;
    }

    public void setIdProuct(Product idProuct) {
        this.idProuct = idProuct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idProuct=" + idProuct +
                ", quantity=" + quantity +
                ", total=" + total +
                '}';
    }
}

