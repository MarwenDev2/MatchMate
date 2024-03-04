package entities;

public class Product {
    private int id;
    private String reference;
    private String name;
    private float price;
    private int quantity;
    private String size;
    private String type;

    public Product() {
    }

    public Product(int id, String reference, String name, float price, int quantity, String size, String type) {
        this.id = id;
        this.reference = reference;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.type = type;
    }

    public Product(String reference, String name, float price, int quantity, String size, String type) {
        this.reference = reference;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
