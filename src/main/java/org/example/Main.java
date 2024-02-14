package org.example;

import database.Connexion;
import entities.*;
import services.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
    Connexion cnx = new Connexion();
        System.out.println(cnx);

        ClubDAO clubService = new ClubDAO();
        StadiumDAO sDAO = new StadiumDAO();
        ReservationDAO rDAO = new ReservationDAO();

        int userId = 1; // Assuming user ID 1 is the owner

        // Create a new club
       Club newClub = new Club();
        User u1 = new User();
        u1.setId(userId);
        newClub.setUser(u1); // Set the user ID
        //newClub.setLocation("Location");
        newClub.setName("Clyubyj");
        newClub.setStartTime(new Time(10,00,00));
        newClub.setEndTime(new Time(15,00,00));


        Stadium newStadium = new Stadium();
        newStadium.setReference("CLY317");
        newStadium.setHeight(15.5f);
        newStadium.setWidth(2.3f);
        newStadium.setPrice(10);
        newStadium.setRate(1);
        Club club = new Club(); // Assuming you have a Club class
        club.setId(12);
        club.setName("Clyubyj");// Assuming the ID of the club is 1
        newStadium.setClub(club);
        //sDAO.save(newStadium);

        //System.out.println(clubService.findByRef("CLU18"));

        List<Stadium> c = new ArrayList<Stadium>();

        //System.out.println(c=sDAO.findAllByCLub(12));

        Reservation r = new Reservation(u1,newStadium,Date.valueOf("2001-04-04"),new Time(14,30,00),new Time(15,30,00),"assigned");

       rDAO.delete(1);
       // System.out.println(rDAO.findAllByDate(Date.valueOf("2001-04-04")));








    }
}