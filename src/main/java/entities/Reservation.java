package entities;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

public class Reservation {
    private int id;
    private User player;
    private Stadium stadium;
    private Date date;
    private Time startTime,endTime;
    private String type;

    public Reservation() {
    }

    public Reservation(int id, User player, Stadium stadium, Date date, Time startTime, Time endTime, String type) {
        this.id = id;
        this.player = player;
        this.stadium = stadium;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public Reservation(User player, Stadium stadium, Date date, Time startTime, Time endTime, String type) {
        this.player = player;
        this.stadium = stadium;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id == that.id;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", player=" + player +
                ", stadium=" + stadium +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type='" + type + '\'' +
                '}';
    }
}
