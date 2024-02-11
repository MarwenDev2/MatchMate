package entities;

import java.sql.Time;
import java.util.Objects;

public class Club {
    private int id;
    private User user;
    private String name;
    private String location;
    private Time startTime;
    private Time endTime;
    private int stadiumNbr;

    public Club() {
    }

    public Club(int id, User user, String name, String location, Time startTime, Time endTime, int stadiumNbr) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
    }

    public Club(User user, String name, String location, Time startTime, Time endTime, int stadiumNbr) {
        this.user = user;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stadiumNbr=" + stadiumNbr +
                '}';
    }
}
