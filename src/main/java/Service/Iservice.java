package Service;

import entities.Evenement;

import java.util.List;

public interface Iservice <T>{
   int add(T t);
    void Delete(T t);
    void Update (T t,T t4);
    List<T> ReadAll(int t);
 List<T> ReadAll();



 Evenement readByName(String name);

 void addPst(T t);

}
