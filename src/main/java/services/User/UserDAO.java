package services.User;

import entities.*;
import entities.User;
import database.Connexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO implements IUserDAO<User> {

    private Connection con;
    private Statement ste;
    private PreparedStatement pst;

    public UserDAO() {
        con = Connexion.getInstance();
    }

    ResultSet generatedKeys = null;
    @Override
    public int addUser(User user) {
        // Validate user input
       if (!validate_User_Input(user).isEmpty()) return 0;

        // Encrypt the password
        String encryptedPassword = PasswordEncryption.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword); // Update the user object with encrypted password

        // check if the user already exists in the database based on their email
        if (userExists(user.getEmail())) {
            System.out.println("can't add the User :" + user.getFirstName() + " " + user.getLastName() + " already exists in the application . ");
            return 0;
        }
        String sql = "INSERT INTO user (firstName, lastName, phoneNumber, email, password, role, image) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
        try {
            pst = con.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getPhoneNumber());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getPassword());
            pst.setString(6, user.getRole().name()); // Assuming role is stored as string
            pst.setString(7, user.getImage());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 1) {
                // Retrieve the auto-generated keys
                generatedKeys = pst.getGeneratedKeys();
                System.out.println("user : " + user.getFirstName() + " " + user.getLastName() + " is added successfuly ");
                int userId = 0;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                    System.out.println("The user has been added with ID: " + userId);
                }
                return userId;
            } else {
                System.out.println("no user was added ");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public List<String> validate_User_Input(User user) {
        List<String> errorMessages = validateUser(user);
        if (!errorMessages.isEmpty()) {
            System.out.println("Invalid user data:");
            for (String errorMessage : errorMessages) {
                System.out.println("- " + errorMessage);
            }
        }
        return errorMessages;
    }


        private List<String> validateUser (User user){
            List<String> errorMessages = new ArrayList<>();

            // Validate user attributes
            if (user == null) {
                errorMessages.add("User object is null.");
            } else {
                if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
                    errorMessages.add("First name is required.");
                }
                if (user.getLastName() == null || user.getLastName().isEmpty()) {
                    errorMessages.add("Last name is required.");
                }
                if (user.getPhoneNumber() == null || !isValidPhoneNumber(user.getPhoneNumber())) {
                    errorMessages.add("Phone number must be exactly 8 digits.");
                }
                if (user.getEmail() == null || !isValidEmail(user.getEmail())) {
                    errorMessages.add("Invalid email format.");
                }
                if (user.getPassword() == null) {
                    errorMessages.add("Password must not be empty");
                }
                if (user.getRole() == null) {
                    errorMessages.add("Role is required.");
                }
                if (user.getImage() == null || user.getImage().isEmpty()) {
                    errorMessages.add("Image is required.");
                }
            }
            return errorMessages;
        }

    // Method to validate email format
    public boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
    }

    // Method to validate phone number format
    public boolean isValidPhoneNumber(String phoneNumber) {
        // Phone number must be exactly 8 digits
        return phoneNumber.matches("\\d{8}");
    }

    // Method to check if the user already exists in the database based on their email
    public boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // If count > 0, user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if an exception occurs
    }


    @Override
    public boolean updateUser(User user) {

        // Validate user input
        if (!validate_User_Input(user).isEmpty() ){
            return false;
        }
        // Check if the user exists in the database based on their ID
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            System.out.println("User : "+ user.getFirstName() +"  " + user.getLastName() +"  does not exist in the database.");
            return false;
        }
        String sql = "UPDATE user SET firstName=?, lastName=?, phoneNumber=?, email=?, password=?, role=?, image=? WHERE id=?";
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, user.getPhoneNumber());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getPassword()); // Assuming password is updated
            pst.setString(6, user.getRole().name()); // Assuming role is stored as string
            pst.setString(7, user.getImage());
            pst.setInt(8, user.getId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("User :"+ user.getFirstName() + user.getLastName() +" is updated successfully.");
                return true;
            } else {
                System.out.println("user: "+ user.getFirstName() + user.getLastName() +" was NOT updated.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }



    @Override
    public boolean deleteUserById(int userId) {
        // Validate input: Ensure userId is valid
        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return false;
        }

        // Check user existence
        if (getUserById(userId).getId()!= userId) {
            System.out.println("User with ID " + userId + " does not exist.");
            return false;
        }

        String sql = "DELETE FROM user WHERE id = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("User with ID " + userId + " deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete user with ID " + userId + ".");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1,userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                // Assuming role is stored as string in the database and mapped to an enum in the User class
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setImage(rs.getString("image"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        String sql = "SELECT * FROM user";
        try {
            pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setImage(rs.getString("image"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }


    public User getUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM user WHERE email = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password")); // Assuming password is stored hashed
                // Assuming role is stored as string in the database and mapped to an enum in the User class
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setImage(rs.getString("image"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User authenticateUser(String email, String password) {

        if (!isValidEmail(email)) {
            return null ;
        }
        // Check if the user exists in the database based on the provided email
        User user = getUserByEmail(email);
        if (user == null) {
            return null ;
        }
        // Verify the password
        String hashedPasswordFromDatabase = user.getPassword(); // Retrieve hashed password from the database
        // Hash the provided password using the same algorithm and parameters used to hash the password in the database
        String hashedPasswordToCompare = PasswordEncryption.encryptPassword(password); // You need to implement this method
        // Compare the hashed passwords
        if (hashedPasswordToCompare.equals(hashedPasswordFromDatabase)) {
            return user;
        } else {
            return null;
        }

    }

    public boolean updateUserPassword(String newHashedPassword, User currentUser) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try
        {
            PreparedStatement pst = con.prepareStatement(sql);

            // Set the new hashed password and the user's ID
            pst.setString(1, newHashedPassword);
            pst.setInt(2, currentUser.getId());

            // Execute the update statement
            int rowsAffected = pst.executeUpdate();

            // Check if the password was successfully updated
            if (rowsAffected >  0) {
                return true;
            } else {
                System.out.println("No rows were updated.");
            }
        } catch (SQLException e) {
            // Log the exception and return false
            e.printStackTrace();
            return false;
        }
        return false;
    }

}



























































    /* @Override
    public void add( user user) {
        String query = "INSERT INTO user (FirstName, LastName, PhoneNumber, Email, Password, Role) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            // Specify Statement.RETURN_GENERATED_KEYS when preparing the statement
            pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(3, String.valueOf(user.getPhoneNumber()));
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getPassword()); // Ensure password is hashed before storing!
            pst.setString(6, user.getRole());
            int rowsAffected = pst.executeUpdate();
            // Retrieve the generated keys
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    // Set the generated ID to your user object or use it as needed
                    user.setId(generatedId);
                } else {
                    System.out.println("No generated keys returned.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(user user) {
        String query  = "DELETE FROM user WHERE id = ?";
        try ( PreparedStatement pst = con.prepareStatement(query) ) {
            pst.setInt(1, user.getId());
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("No user found with the provided ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while deleting user: " + e.getMessage());
        }
    }


    @Override
    public void update(user user) {
        String query = "UPDATE user SET FirstName=?, LastName=?, PhoneNumb=? + Email=?, Password=?, Role=? WHERE id=?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query))
              {
                  pst.setString(1, user.getFirstName());

                  pst.setString(2, user.getLastName());
                  pst.setInt(3, user.getPhoneNumber());
                  pst.setString(4, user.getEmail());
                  pst.setString(5, user.getPassword());
                  pst.setString(6, user.getRole());
                  pst.setInt(7, user.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user found with the provided ID or update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }

    @Override
    public List<user> readAll() {
        List<user> userList = new ArrayList<>();
        String query = "SELECT * FROM user";
        try ( PreparedStatement preparedStatement = con.prepareStatement(query) ){
            ResultSet resultSet = preparedStatement.executeQuery() ;
            while (resultSet.next()) {
            user user = new user(
                    resultSet.getInt("id"),
                    resultSet.getString("FirstName"),
                    resultSet.getString("LastName"),
                    resultSet.getInt("PhoneNumber"),
                    resultSet.getString("Email"),
                    resultSet.getString("Password"),
                    resultSet.getString("Role")
            );
            userList.add(user);
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle the exception appropriately in your application
    }
        return userList;
}

    @Override
    public user readById(int id) {
        String query = "SELECT * FROM user  WHERE id = ?";
        try {
            pst = con.prepareStatement(query);
            pst.setInt(1, id);
            try (ResultSet resultSet = pst.executeQuery()) {
                if (resultSet.next()) {
                    // Assuming User class has a constructor that accepts the necessary parameters
                    return new user(
                            resultSet.getInt("id"),
                            resultSet.getString("FirstName"),
                            resultSet.getString("LastName"),
                            resultSet.getInt("PhoneNumber"),
                            resultSet.getString("Email"),
                            resultSet.getString("Password"),
                            resultSet.getString("Role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
        return null; // Return null if no user is found with the provided ID
} }
*/

