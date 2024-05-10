package org.example.expensetracker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardPageController implements Initializable {
    @FXML private PieChart pieStastics;
    @FXML private DatePicker startDate,endDate;
    @FXML private Label pieChartDate,incomeLabel,expenseLabel,balanceLabel,transactionsLabel;



    private int userID;
    private int incomeAmount,expenseAmount,balanceAmount,transactions;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startDate.setValue(LocalDate.of(2023,1,1));
        endDate.setValue(LocalDate.of(2023,12,12));

        startDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            setPieChart();
            calculateIncomeAmount();
            calculateExpenseAmount();
            calculateTranscations();
        });
        endDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            setPieChart();
            calculateIncomeAmount();
            calculateExpenseAmount();
            calculateTranscations();
        });
        setPieChart();
        calculateIncomeAmount();
        calculateExpenseAmount();
        calculateTranscations();










    }
    public void setUserID(int userID){
        this.userID=userID;
    }
    private void setPieChart(){


        LocalDate startDate1=startDate.getValue();
        LocalDate endDate1=endDate.getValue();

        String formattedStartDate=startDate1.format(DateTimeFormatter.ISO_DATE);
        String formattedEndDate=endDate1.format(DateTimeFormatter.ISO_DATE);

        String formattedStartDate1=startDate1.format(DateTimeFormatter.ofPattern("MMM dd"));
        String formattedEndDate1=endDate1.format(DateTimeFormatter.ofPattern("MMM dd"));

        Platform.runLater(()->{
               pieChartDate.setText(formattedStartDate1+" - "+formattedEndDate1);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String pass="";
                Connection connection=DriverManager.getConnection(url,username,pass);


                String query="SELECT category, SUM(amount) AS total_amount FROM transactions "
                        +"WHERE transaction_date BETWEEN ? AND ? "+ "GROUP BY category";

                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,formattedStartDate);
                preparedStatement.setString(2,formattedEndDate);

                ResultSet resultSet=preparedStatement.executeQuery();




                ObservableList<PieChart.Data> pieChartData=FXCollections.observableArrayList();

                while (resultSet.next()){
                    String category=resultSet.getString("category");
                    double totalAmount=resultSet.getDouble("total_amount");
                    pieChartData.add(new PieChart.Data(category, totalAmount));
                }
                preparedStatement.close();
                connection.close();
                pieStastics.setData(pieChartData);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void calculateIncomeAmount(){

        Platform.runLater(()->{
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String password="";

                Connection connection=DriverManager.getConnection(url,username,password);


                String query="SELECT SUM(amount) AS income_amount FROM transactions "+
                        "WHERE cashflow='Income' AND transaction_date BETWEEN ? AND ? ";


                LocalDate startDate1=startDate.getValue();
                LocalDate endDate1=endDate.getValue();

                String formattedStartDate=startDate1.format(DateTimeFormatter.ISO_DATE);
                String formattedEndDate=endDate1.format(DateTimeFormatter.ISO_DATE);

                PreparedStatement preparedStatement= connection.prepareStatement(query);
                preparedStatement.setString(1,formattedStartDate);
                preparedStatement.setString(2,formattedEndDate);




                ResultSet resultSet=preparedStatement.executeQuery();


                while (resultSet.next()){
                    incomeAmount=resultSet.getInt("income_amount");
                }

                preparedStatement.close();
                connection.close();


            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        Platform.runLater(()->{
//            System.out.println(incomeAmount);
            incomeLabel.setText("$"+incomeAmount);
        });




    }
    private void calculateExpenseAmount(){
        Platform.runLater(()->{
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String password="";

                Connection connection=DriverManager.getConnection(url,username,password);

                String query="SELECT SUM(amount) AS expense_amount FROM transactions "+
                        "WHERE cashflow='Expense' AND transaction_date BETWEEN ? AND ? ";

                LocalDate startDate1=startDate.getValue();
                LocalDate endDate1=endDate.getValue();

                String formattedStartDate=startDate1.format(DateTimeFormatter.ISO_DATE);
                String formattedEndDate=endDate1.format(DateTimeFormatter.ISO_DATE);

                PreparedStatement preparedStatement= connection.prepareStatement(query);
                preparedStatement.setString(1,formattedStartDate);
                preparedStatement.setString(2,formattedEndDate);

                ResultSet resultSet=preparedStatement.executeQuery();


                while (resultSet.next()){
                    expenseAmount=resultSet.getInt("expense_amount");
                }

              preparedStatement.close();
                connection.close();


            }
            catch (Exception e){
                 e.printStackTrace();
            }
        });
     Platform.runLater(()->{
         balanceAmount=incomeAmount-expenseAmount;
         balanceLabel.setText("$"+balanceAmount);
         expenseLabel.setText("$"+expenseAmount);
     });

    }

    private void calculateTranscations(){
        Platform.runLater(()->{
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String password="";

                Connection connection=DriverManager.getConnection(url,username,password);

                String query="SELECT COUNT(DISTINCT transaction_id) as transactionCount FROM transactions "+
                        "WHERE transaction_date BETWEEN ? AND ? ";
                LocalDate startDate1=startDate.getValue();
                LocalDate endDate1=endDate.getValue();

                String formattedStartDate=startDate1.format(DateTimeFormatter.ISO_DATE);
                String formattedEndDate=endDate1.format(DateTimeFormatter.ISO_DATE);

                PreparedStatement preparedStatement= connection.prepareStatement(query);
                preparedStatement.setString(1,formattedStartDate);
                preparedStatement.setString(2,formattedEndDate);

                ResultSet resultSet=preparedStatement.executeQuery();


                while (resultSet.next()){
                    transactions=resultSet.getInt("transactionCount");
                }

                preparedStatement.close();
                connection.close();


            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
        Platform.runLater(()->{
//            System.out.println(transactions);
            transactionsLabel.setText("$"+transactions);
        });

    }
}
