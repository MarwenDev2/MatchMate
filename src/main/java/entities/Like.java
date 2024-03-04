package entities;

public class Like {
    private Stadium stadium;
    private User user;

    public Like(Stadium stadium, User user) {
        this.stadium = stadium;
        this.user = user;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
