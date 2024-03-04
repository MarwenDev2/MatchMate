package entities;

import java.util.Objects;

public class Stadium {
    private String reference;
    private Club club;
    private float height, width;
    private int price, rate;

    private int maintenance;

    public Stadium(String reference, Club club, float height, float width, int price, int rate, int maintenance) {
        this.reference = reference;
        this.club = club;
        this.height = height;
        this.width = width;
        this.price = price;
        this.rate = rate;
        this.maintenance = maintenance;
    }

    public Stadium(String reference, Club club, float height, float width, int price) {
        this.reference = reference;
        this.club = club;
        this.height = height;
        this.width = width;
        this.price = price;
    }

    public Stadium() {
    }

    public Stadium(String reference, Club club, float height, float width, int price, int maintenance) {
        this.reference = reference;
        this.club = club;
        this.height = height;
        this.width = width;
        this.price = price;
        this.maintenance = maintenance;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(int maintenance) {
        this.maintenance = maintenance;
    }

    @Override
    public String toString() {
        return "Stadium{" +
                "reference='" + reference + '\'' +
                ", club=" + club +
                ", height=" + height +
                ", width=" + width +
                ", price=" + price +
                ", rate=" + rate +
                ", maintenance=" + maintenance +
                '}';
    }
}