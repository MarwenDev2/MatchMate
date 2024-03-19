package services;

import entities.Reservation;

import java.sql.Date;
import java.util.List;

public interface IReservationDAO <T> {

    public boolean save(T t);
    public boolean update(T t);
    public T findById(int id);
    public List<T> findFilteredReservations(String stadium, int idPlayer, String type, Date date);
    public boolean delete(int id);

}
