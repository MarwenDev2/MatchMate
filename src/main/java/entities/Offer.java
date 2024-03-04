package entities;

public class Offer {
    private int id;
    private String name;
    private double price;
    private String text;
    private int nbrClub; // Number of clubs allowed for this offer

    public Offer(int id, String name, double price, String text, int nbrClub) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.text = text;
        this.nbrClub = nbrClub;
    }

    public Offer(int id) {
        this.id = id;
    }
    // Getters and setters

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNbrClub() {
        return nbrClub;
    }

    public void setNbrClub(int nbrClub) {
        this.nbrClub = nbrClub;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", text='" + text + '\'' +
                ", nbrClub=" + nbrClub +
                '}';
    }
}
