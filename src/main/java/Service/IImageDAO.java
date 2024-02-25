package Service;

import entities.Image;

import java.sql.SQLException;
import java.util.List;

public interface IImageDAO <T>{

    public boolean save(T t,int id);
    public boolean insertIntoTypeTable(int relatedId, int imageId, String type) throws SQLException;
    public T findByIdImage(int id);
    public List<T> findByObjectId(int id, String type);
    public boolean update(T t);
    public boolean delete(T t);

}
