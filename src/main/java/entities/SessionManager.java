package entities;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static SessionManager instance;
    private static Map<String, User> activeSessions = new HashMap<>(); // Store active sessions
    private String currentSessionId; // Store the current session ID

    // Private constructor to prevent instantiation
    private SessionManager() {
    }

    // Singleton instance method
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Add a new session
    public static void addSession(String sessionId, User user , int userId) {

        activeSessions.put(sessionId, user);
        setCurrentUserId(sessionId, userId);
    }

    public static void setCurrentUserId(String sessionId, int userId) {
        User currentUser = activeSessions.get(sessionId);
        if (currentUser != null) {
            currentUser.setId(userId);
        }
    }

    // Retrieve user object based on session ID
    public User getUserFromSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public String getCurrentSessionId() {
        return this.currentSessionId;
    }

    // Invalidate a session
    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    // Generate a new session ID
    public static String generateSessionId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return new BigInteger(1, token).toString(16); // Convert to hexadecimal representation
    }

    // Setter for current session ID
    public void setCurrentSessionId(String sessionId) {

        this.currentSessionId = sessionId;
    }

    // Getter for current user based on current session ID
    public User getCurrentUser() {
        return getUserFromSession(currentSessionId);
    }

    // Update current user's information
    public void updateCurrentUser(User updatedUser) {
        String currentSessionId = getCurrentSessionId();
        if (currentSessionId != null && activeSessions.containsKey(currentSessionId)) {
            activeSessions.put(currentSessionId, updatedUser);
        }
    }
}
