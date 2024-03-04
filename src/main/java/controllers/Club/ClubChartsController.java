package controllers.Club;

import entities.Club;
import entities.Role;
import entities.SessionManager;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;

import javafx.stage.Stage;
import services.Club.ClubDAO;

import java.util.List;
import java.util.Map;

public class ClubChartsController {

    @FXML
    private BarChart<String, Number> clubBarChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private PieChart clubPieChart;

    @FXML
    private LineChart<String, Number> stadiumLineChart;

    @FXML
    private NumberAxis yAxis;

    private User currentUser = SessionManager.getInstance().getCurrentUser();

    private ClubDAO clubDAO;

    private List<Club> clubs;

    public void setClubs(List<Club> clubs) {
        this.clubs = clubs;
    }

    @FXML
    public void initialize() {
        clubDAO = new ClubDAO();

        if (currentUser.getRole() == Role.fieldOwner) {
            clubs = clubDAO.findByUser(currentUser.getId());
        } else {
            clubs = clubDAO.findAll();
        }

        // Display statistics on the bar chart
        displayBarChartStatistics();

        // Display statistics on the pie chart
        displayPieChartStatistics();

        // Display statistics on the line chart
        displayLineChartStatistics();



    }

    private void displayBarChartStatistics() {
        // Count clubs by city
        Map<String, Integer> cityCount = clubDAO.countClubsByCity();

        // Clear previous data
        clubBarChart.getData().clear();

        // Add new data to the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        cityCount.forEach((city, count) -> series.getData().add(new XYChart.Data<>(city, count)));
        clubBarChart.getData().add(series);
    }

    private void displayPieChartStatistics() {
        // Count clubs by governorate
        Map<String, Integer> governorateCount = clubDAO.countClubsByGovernorate();

        // Clear previous data
        clubPieChart.getData().clear();

        // Add new data to the pie chart
        governorateCount.forEach((governorate, count) ->
                clubPieChart.getData().add(new PieChart.Data(governorate, count))
        );
    }

    private void displayLineChartStatistics() {
        // Count stadiums by club and display in the line chart
        Map<String, Integer> stadiumCountByClub = clubDAO.countStadiumsByClub();

        // Clear previous data
        stadiumLineChart.getData().clear();

        // Add new data to the line chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : stadiumCountByClub.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        stadiumLineChart.getData().add(series);
    }

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        // Get the current stage and close it
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
