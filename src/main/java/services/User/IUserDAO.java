package services.User;

import entities.User;

import java.util.List;

public interface IUserDAO<T> {
    // Method to add a new user
    int addUser(User user);

    // Method to update an existing user
    boolean updateUser(User user);

    // Method to delete a user by ID
    boolean deleteUserById(int userId);

    // Method to get a user by ID
    User getUserById(int userId);

    // Method to get all users
    List<User> getAllUsers();

    // Method to authenticate a user
    User authenticateUser(String email, String password);

}
