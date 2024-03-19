package services;

import entities.Payment;

import java.util.List;

public interface IPaymentDAO <T>{

    public boolean save(T t, int id);
    public boolean insertIntoTypeTable(int relatedId,int paymentId, String type);
    public List<T> findByUserId(int userId);
    public boolean delete(T t);
}
