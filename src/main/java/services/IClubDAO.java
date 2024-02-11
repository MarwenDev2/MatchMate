package services;

import entities.Club;

import java.util.List;

public interface IClubDAO <T>{
    public boolean save(T t);
    public boolean update(T t);
    public T findById(int id);
    public boolean findByName(String name);
    public T findByRef(String ref);
    public int findNbrClub();
    public List<T> findAll();
    public boolean delete(T t);

}
