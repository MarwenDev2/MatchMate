package entities;

import java.sql.Date;

public class Subscription {
    private int id;
    private User userId;
    private Offer offerId;
    private Date startDate;
    private Date endDate;

    public Subscription(int id, User userId, Offer offerId, Date startDate, Date endDate) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Subscription(User userId, Offer offerId, Date startDate, Date endDate) {
        this.userId = userId;
        this.offerId = offerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Offer getOfferId() {
        return offerId;
    }

    public void setOfferId(Offer offerId) {
        this.offerId = offerId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", userId=" + userId +
                ", offerId=" + offerId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
