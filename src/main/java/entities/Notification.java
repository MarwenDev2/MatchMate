package entities;

public class Notification {
    private int id;
    private User user;
    private String text;

    // Constructors
    public Notification() {
    }

    public Notification(User user, String text) {
        this.user = user;
        this.text = text;
    }

    public Notification(int id, User user, String text) {
        this.id = id;
        this.user = user;
        this.text = text;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // toString method (for debugging purposes)
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }
}
