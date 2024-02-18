package entities;

import java.util.Objects;

public class Stadium {
    private String reference;
    private Club club;
    private float height,width;
    private int price,rate;

    public Stadium() {
    }

    public Stadium(Club club, float height, float width, int price, int rate) {
        this.club = club;
        this.height = height;
        this.width = width;
        this.price = price;
        this.rate = rate;
    }

    public Stadium(String reference, Club club, float height, float width, int price, int rate) {
        this.reference = reference;
        this.club = club;
        this.height = height;
        this.width = width;
        this.price = price;
        this.rate = rate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stadium stadium = (Stadium) o;
        return Objects.equals(reference, stadium.reference);
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
                '}';
    }
}
