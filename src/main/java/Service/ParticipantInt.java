package Service;

import entities.Evenement;
import javafx.collections.ObservableList;

import java.util.List;

public interface ParticipantInt <T> {
    void add_Participant(T t);
    void Delete_Participant(T t);
    void Update_Participant (T t,T t4);
    List<T> ReadAllParticipant();




    ObservableList<Evenement> readParticipantByName(String name);

    void addPst(T t);
}
