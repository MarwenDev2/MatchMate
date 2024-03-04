package services.User;

import static entities.PasswordEncryption.encryptPassword;

public class passwoardEncryptionAdmin {
    String password;
       public static void passwoardEncryption(String pswd){

            // Encrypt the password using the encryptPassword function
            String hashedPassword = encryptPassword(pswd);

            // Print the hashed password to the console
            System.out.println("Hashed password: " + hashedPassword);
        }


    }

