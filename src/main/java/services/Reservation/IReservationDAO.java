package services.Reservation;

import entities.Reservation;

import java.sql.Date;
import java.util.List;

public interface IReservationDAO <T> {

    public boolean save(T t);
    public boolean update(T t);
    public List<T> findAllByIdPlayer(int playerId);
    public List<T> findAllByDate(Date date);
    public boolean delete(int id);
    public List<T> findAllByStadium(String ref);
    public List<T> findAllByStadiumDate(String ref,Date d);
    public List<T> findAllByType(String t);
}
