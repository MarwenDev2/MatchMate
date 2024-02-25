package Service;
import javafx.collections.ObservableList;
import entities.Image;
public interface ImageInterface {
    int add(Image image);
    void delete(Image image);
    void update(Image oldImage, Image newImage);
    ObservableList<Image> readAll();
    Image readById(int id);

}
