package service;

import entities.Product;

import java.util.List;

public interface IService <T>{

    public int save(T t);
    void add(T t);
    void delete(T t);



    void Update(T t);

    List<T> getAll();

    T readById(int id);

    List<Product> triProductByReference();

    List<Product> rechercheParRef(String s);
}
