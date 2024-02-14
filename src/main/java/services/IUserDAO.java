package services;

import java.util.List;

public interface IUserDAO <T> {
    boolean save(T t);

    boolean update(T t);

    T findById(int id);

    boolean delete(T user);

    List<T> findAll();
}
