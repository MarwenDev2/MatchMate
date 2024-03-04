package entities;

import java.sql.Time;
import java.util.Objects;

public class Club {
    private int id;
    private User user;
    private String name;
    private String governorate;
    private String city;
    private Time startTime;
    private Time endTime;
    private int stadiumNbr;

    private String description;
    private double latitude; // Added latitude attribute
    private double longitude; // Added longitude attribute


    public Club() {
    }


    public Club(User user, String name, String governorate, String city, Time startTime, Time endTime, int stadiumNbr, String description, double latitude, double longitude) {
        this.user = user;
        this.name = name;
        this.governorate = governorate;
        this.city = city;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
        this.description = description;
        this.latitude = latitude; // Initialize latitude
        this.longitude = longitude; // Initialize longitude
    }

    public Club(User user, String name, String governorate, String city, Time startTime, Time endTime, int stadiumNbr, String description) {
        this.user = user;
        this.name = name;
        this.governorate = governorate;
        this.city = city;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
        this.description = description;
    }

    public Club(int id, User user, String name, String governorate, String city, Time startTime, Time endTime, int stadiumNbr, String description) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.governorate = governorate;
        this.city = city;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
        this.description = description;
    }

    public Club(int id, User user, String name, String governorate, String city, Time startTime, Time endTime, int stadiumNbr, String description, double latitude, double longitude) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.governorate = governorate;
        this.city = city;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stadiumNbr = stadiumNbr;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Club{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", governorate='" + governorate + '\'' +
                ", city='" + city + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stadiumNbr=" + stadiumNbr +
                ", description='" + description + '\'' +
                ", latitude=" + latitude + // Include latitude in toString()
                ", longitude=" + longitude + // Include longitude in toString()
                '}';
    }
}