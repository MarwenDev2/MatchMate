package entities;

import java.sql.Time;
import java.util.Objects;

public class Club {
    private int id;
    private User user;
    private String name;
    private Float height,width;
    private Time startTime;
    private Time endTime;
    private int stadiumNbr;

    private String description;

    public Club() {
    }

    public Club(int id, User user, String name, Float height, Float width, Time startTime, Time endTime, int stadiumNbr, String description) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.height = height;
        this.width = width;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
        this.description = description;
    }

    public Club(User user, String name, Float height, Float width, Time startTime, Time endTime, int stadiumNbr, String description) {
        this.user = user;
        this.name = name;
        this.height = height;
        this.width = width;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getStadiumNbr() {
        return stadiumNbr;
    }

    public void setStadiumNbr(int stadiumNbr) {
        this.stadiumNbr = stadiumNbr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Club club = (Club) o;
        return id == club.id;
    }

    @Override
    public String toString() {
        return "Club{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stadiumNbr=" + stadiumNbr +
                ", description='" + description + '\'' +
                '}';
    }
}
