package org.example;
import Service.*;
import entities.Evenement;

public class Main {
    public static void main(String[] args) {
        EventService e = new EventService();
        ClubDAO c=new ClubDAO();
        String name = c.findById(19).getName();
        System.out.println(name);


    }
}