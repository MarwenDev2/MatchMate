package entities;
import java.security.SecureRandom;

public class PasswordGenerator {

    static String generated_code = "";

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";


    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER ;

    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Password length must be at least 1 character");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(PASSWORD_ALLOW_BASE.length());
            password.append(PASSWORD_ALLOW_BASE.charAt(randomIndex));
        }
        generated_code = password.toString();

        return password.toString();
    }

    public String getGenerated_code(){
        return generated_code;
    }

    public static void main(String[] args) {
        // Generate a random password of length 10
        String newPassword = generateRandomPassword(10);
        System.out.println("Generated Password: " + newPassword);
    }
}
