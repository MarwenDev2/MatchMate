package entities;
import org.apache.commons.codec.digest.DigestUtils;

public class PasswordEncryption {
    public static String encryptPassword(String password) {
        // Hash the password using SHA-256 algorithm
        return DigestUtils.sha256Hex(password);
    }
}
