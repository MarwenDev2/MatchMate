package services.Image;

import java.sql.SQLException;
import java.util.List;

public interface IImageStadiumDAO <T>{
    boolean save(T t,String ref);
    boolean insertIntoStadiumTable(String ref ,int imageId) throws SQLException;
    public List<T> findByIDStadium(String ref,String type);

}
