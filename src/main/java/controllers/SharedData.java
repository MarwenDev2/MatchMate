package controllers;

public class SharedData {
    private static int clubId;
    private static int userId;
    private static String refStd;

    public static String getRefStd() {
        return refStd;
    }

    public static void setRefStd(String refStd) {
        SharedData.refStd = refStd;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int user) {
        SharedData.userId = user;
    }

    public static int getClubId() {
        return clubId;
    }

    public static void setClubId(int id) {
        clubId = id;
    }
}
