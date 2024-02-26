package service;

import java.util.List;

public interface IServiceCommand <T>{
    public int save(T t);
    void add(T t);
    void delete(T t);



    void Update(T t);

    List<T> getAll();

    T readById(int id);
}
