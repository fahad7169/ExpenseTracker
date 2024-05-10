package org.example.expensetracker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DashboardPageController implements Initializable {
    @FXML private PieChart pieStastics;


    private int userID;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url="jdbc:mysql:localhost:3306/user"+userID;
                String username="root";
                String pass="";
                Connection connection= DriverManager.getConnection(url,username,pass);

                Statement statement=connection.createStatement();
                ResultSet resultSet=statement.executeQuery("SELECT category, SUM(amount) AS total_amount FROM transactions GROUP BY category");
                ObservableList<PieChart.Data> pieChartData= FXCollections.observableArrayList();

                while(resultSet.next()){
                    String category=resultSet.getString("category");;
                    double totalAmount=resultSet.getDouble("total_amount");
                    pieChartData.add(new PieChart.Data(category,totalAmount));
                }
                pieStastics.setData(pieChartData);

            }
            catch (Exception e){
                e.printStackTrace();
            }
        });





    }
    public void getUserId(int userID){
        this.userID=userID;
    }
}
