package controllers;

import entities.SessionManager;
import entities.Role;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import services.UserDAO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardController {
    public Button users_add_updatez;
    public Button users_delete;
    public Button users_clear;
    public Button add_user_btn;
    public TextField password_user_txtfield;
    public javafx.scene.chart.PieChart PieChart;
    @FXML
    private ComboBox<Role> roleComboBox;


    @FXML
    private Label username;

    @FXML
    private TextField users_add_email;

    @FXML
    private TextField users_add_firstname;
    @FXML
    private TextField users_add_phone;

    @FXML
    private ImageView users_add_img;

    @FXML
    private TextField users_add_lastname;

    @FXML
    private TextField users_add_role;
    @FXML
    private TextField users_search;

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, Integer> users_tableView_id;

    @FXML
    private TableColumn<User, String> users_tableView_firstname;

    @FXML
    private TableColumn<User, String> users_tableView_lastname;

    @FXML
    private TableColumn<User, String> users_tableView_email;

    @FXML
    private TableColumn<User, Integer> users_tableView_phone;

    @FXML
    private TableColumn<User, Role> users_tableView_role;

    @FXML
    private TableColumn<User, String> users_tableView_img;
    @FXML
    private TableColumn<User, String> users_tableView_passwoard;

   public String imagePath = "";


    UserDAO userDAO = new UserDAO();

    // Retrieve the current user from the session manager
    User currentUser= SessionManager.getInstance().getCurrentUser();

    public User selectedUser ;

    public String selected_user_password ;
    public int selected_user_id ;

    UserDAO US = new UserDAO();


    private FilteredList<User> filteredUsers;
    @FXML
    public void initialize() {


        // Retrieve all users
        List<User> users = US.getAllUsers();
        // Count users by role
        Map<String, Integer> roleCounts = new HashMap<>();
        for (User user : users) {
            String role = user.getRole().toString();
            roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
        }

        // Prepare data for the pie chart
        for (Map.Entry<String, Integer> entry : roleCounts.entrySet()) {
            String role = entry.getKey();
            int count = entry.getValue();
            PieChart.Data slice = new PieChart.Data(role + " (" + count + ")", count);
            PieChart.getData().add(slice);
        }



        // Initialize the filteredUsers with the complete user list
        List<User> userList = userDAO.getAllUsers();
        // Filter out the current user
        userList = userList.stream()
                .filter(u -> !u.equals(currentUser))
                .collect(Collectors.toList());
        filteredUsers = new FilteredList<>(FXCollections.observableArrayList(userList), p -> true);
        // Set up the TableView with the filteredUsers
        usersTableView.setItems(filteredUsers);


        // Set up the ComboBox with roles
        ObservableList<Role> roles = FXCollections.observableArrayList(Role.values());
// Add "All" option as a separate element
        roles.add(0, null);

// Convert null option to "All" string
        roleComboBox.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role == null ? "All" : role.toString();
            }

            @Override
            public Role fromString(String string) {
                return null; // Not needed for our example
            }
        });

// Set the items of the ComboBox
        roleComboBox.setItems(roles);



        // Call the function to set the label value
        setLabelValue();
        // Initialize the TableView columns
        users_tableView_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        users_tableView_firstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        users_tableView_lastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        users_tableView_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        users_tableView_phone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        users_tableView_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        users_tableView_img.setCellValueFactory(new PropertyValueFactory<>("image"));
        users_tableView_passwoard.setCellValueFactory(new PropertyValueFactory<>("Password"));

       // Initialize the TableView with data
        //populateTableView();


        // Listen for changes in the selected role and update the filter accordingly
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Filter the TableView based on the selected role
                filteredUsers.setPredicate(user -> user.getRole() == newValue);
                usersTableView.setItems(filteredUsers);
            }
            else {
                // Display all users when "All" is selected
                filteredUsers.setPredicate(user -> true);
            }
        });




        // Add listener to TableView selection model
        usersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Set selected user's data to fields
                setUserFields(newSelection);
                selectedUser = newSelection;
            }
        });



    }


    private void setLabelValue() {
        // Call your function to get the value
        String firstname = currentUser.getFirstName();
        String lastname = currentUser.getLastName();
        // Set the value to the label
        username.setText(firstname + " " + lastname);
    }



    private void setUserFields(User selectedUser) {
       // users_add_id.setText(String.valueOf(selectedUser.getId()));
        users_add_email.setText(selectedUser.getEmail());
        users_add_firstname.setText(selectedUser.getFirstName());
        users_add_lastname.setText(selectedUser.getLastName());
        users_add_phone.setText(selectedUser.getPhoneNumber());
        users_add_role.setEditable(false);
        users_add_role.setText(selectedUser.getRole().toString());
        users_add_role.setEditable(false);
        // You may need to handle image loading separately
        // Load and display image
        File imageFile = new File(selectedUser.getImage());
        Image image = new Image(imageFile.toURI().toString());
        users_add_img.setImage(image);
        selected_user_password = selectedUser.getPassword();
        selected_user_id = selectedUser.getId();
    }


    @FXML
    void handleLoadImageButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            users_add_img.setImage(image); // Set the image to the ImageView
        }
        imagePath = file.getAbsolutePath();

    }

    @FXML

    private void updateUser(ActionEvent event) {
        // Get data from the text fields and combo box
        String firstName = users_add_firstname.getText();
        String lastName = users_add_lastname.getText();
        String phoneNumber = users_add_phone.getText();
        String email = users_add_email.getText();
        Role selectedRole = Role.fromString(users_add_role.getText());
        int id = selected_user_id;
        String password = selected_user_password ;


        if (!imagePath.isEmpty()) {
            // Create a new user object with the updated data
            User updatedUser = new User(id, firstName, lastName, phoneNumber, email, password, selectedRole, imagePath);
            List<String> validationErrors = userDAO.validate_User_Input(updatedUser);
            if (!validationErrors.isEmpty()) {
                // Display validation errors to the user
                showAlertInput(validationErrors);

            }

            boolean success = userDAO.updateUser(updatedUser);

            if (success) {
                // Refresh the table view
                //populateTableView();
                initialize();
                showAlert("User updated successfully.");

            }
        } else {
            // Create a new user object with the old data
            User updatedUser = new User(id, firstName, lastName, phoneNumber, email, password, selectedRole, selectedUser.getImage());

            List<String> validationErrors = userDAO.validate_User_Input(updatedUser);
            if (!validationErrors.isEmpty()) {
                // Display validation errors to the user
                showAlertInput(validationErrors);

            }

            boolean success = userDAO.updateUser(updatedUser);

            if (success) {
               // handleResetFilterButton();
                // Refresh the table view
                //populateTableView();
                initialize();
                showAlert("User updated successfully.");
            }
        }

    }
    private void showAlertInput(List<String> validationErrors) {
        // Create an alert dialog
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please correct the following errors:");

        // Concatenate the validation errors into a single string
        String errorMessage = String.join("\n", validationErrors);
        alert.setContentText(errorMessage);

        // Show the alert dialog and wait for the user to close it
        alert.showAndWait();
    }



    // Method to show an alert message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearFields(ActionEvent event) {
        // Clear the text fields
        users_add_email.setText("");
        users_add_firstname.setText("");
        users_add_lastname.setText("");
        users_add_phone.setText("");

        users_add_img.setImage(null); // Clear the image
        users_add_role.setText(""); // Clear the selection in the combo box
    }


    // This method will be called when the users_delete button is clicked
    @FXML
    private void deleteUser(ActionEvent event) {
        // Get the selected user from the table view
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();

        // Check if a user is selected
        if (selectedUser == null) {
            showAlert("Please select a user to delete.");
            return;
        }

        // Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed deletion, call the service method to delete the user
            boolean deleted = userDAO.deleteUserById(selectedUser.getId());

            if (deleted) {

                // User deleted successfully, refresh the table view
               initialize();
                showAlert("User deleted successfully.");
            } else {
                showAlert("Failed to delete user.");
            }
        }

    }
    /*
    // Adjusted usersSearch method to accept a TextField parameter
    private void usersSearch(String searchText) {
        // Get the data currently displayed in the TableView
        ObservableList<user> allUsers = usersTableView.getItems();

        // Create a filtered list to hold the filtered data
        FilteredList<user> filteredData = new FilteredList<>(allUsers, user -> true);



        // If the search text is not empty, apply the filter
        if (!searchText.isEmpty()) {
            // Use the searchText to filter the data
            filteredData.setPredicate(user -> {
                // Convert the user properties to lowercase for case-insensitive search
                String lowerCaseSearchText = searchText.toLowerCase();
                String firstName = user.getFirstName().toLowerCase();
                String lastName = user.getLastName().toLowerCase();
                String email = user.getEmail().toLowerCase();
                String phoneNumber = user.getPhoneNumber().toLowerCase();
                String role = user.getRole().toString().toLowerCase(); // Assuming role is an enum

                // Check if any user property contains the search text
                return firstName.contains(lowerCaseSearchText)
                        || lastName.contains(lowerCaseSearchText)
                        || email.contains(lowerCaseSearchText)
                        || phoneNumber.contains(lowerCaseSearchText)
                        || role.contains(lowerCaseSearchText);
            });


        // Set the TableView's items to the filtered list
        usersTableView.setItems(filteredData);

        // Handle the case when the search text is empty
        if (searchText.isEmpty()) {
            ObservableList<user> users = usersTableView.getItems();
            // Set the TableView's items back to the original list of all users
            usersTableView.setItems(users);
        }


    }*/


    @FXML
    void logout(ActionEvent event) throws IOException {
        System.out.println(currentUser.toString());
        // Invalidate the current session
        SessionManager.getInstance().invalidateSession(SessionManager.getInstance().getCurrentSessionId());
        loadPage("/Login/login1.fxml", event);


    }
    public void addUser(ActionEvent actionEvent) {
        loadPage("/AddUserByAdmin/Add_User_ByAdmin.fxml", actionEvent);
    }
    private void loadPage(String fxmlFileName , ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the case where the FXML file cannot be loaded
        }
    }


    public void seeProfile(ActionEvent actionEvent) {
       loadPage("/UserProfileAdmin/user_profile_admin.fxml", actionEvent);
    }
    public void seeUsers(ActionEvent actionEvent) {
        loadPage("/AdminDashboard/admin_dashboard.fxml", actionEvent);
    }

}


