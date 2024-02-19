package controllers;

public class SharedData {
    private static int clubId;

    public static int getClubId() {
        return clubId;
    }

    public static void setClubId(int id) {
        clubId = id;
    }
}
