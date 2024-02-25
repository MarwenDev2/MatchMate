package controller;

public class SharedEventData {
    private static int eventId;

    public static int getEventId() {
        return eventId;
    }

        public static void setEventId(int id) {
        eventId = id;
    }
}
