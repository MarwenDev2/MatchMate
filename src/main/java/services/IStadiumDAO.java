package services;


import java.util.List;

public interface IStadiumDAO <T> {
    public int save(T t);

    public boolean update(T t);

    public T findById(String reference);

    public boolean delete(T t);

    public List<T> findAll();

    public List<T> findAllByClub(int clubId);
}
