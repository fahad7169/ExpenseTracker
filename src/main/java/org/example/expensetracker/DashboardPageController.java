package org.example.expensetracker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardPageController implements Initializable {
    @FXML private PieChart pieStastics;
    @FXML private DatePicker startDate,endDate;
    @FXML private Label pieChartDate,incomeLabel,expenseLabel,balanceLabel,transactionsLabel,
            sideBalanceLabel;
    @FXML private BarChart<String, Number> incomeExpenseChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private AreaChart<String, Number> balanceChart;




    private int count;
    private int userID;
    private int incomeAmount,expenseAmount,balanceAmount,transactions;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startDate.setValue(LocalDate.of(2023,4,21));
        endDate.setValue(LocalDate.of(2023,6,12));

        startDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            setPieChart();
            calculateIncomeAmount();
            calculateExpenseAmount();
            calculateTranscations();
            setBarChart();
            setAreaChart();
        });
        endDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            setPieChart();
            calculateIncomeAmount();
            calculateExpenseAmount();
            calculateTranscations();
            setBarChart();
            setAreaChart();
        });
        setPieChart();
        calculateIncomeAmount();
        calculateExpenseAmount();
        calculateTranscations();
        setBarChart();
        setAreaChart();










    }
    public void setUserID(int userID){
        this.userID=userID;
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
         sideBalanceLabel.setText("$"+balanceAmount);
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
            transactionsLabel.setText(String.valueOf(transactions));

        });

    }




    private void setBarChart(){



         Platform.runLater(()->{
             try{
                 Class.forName("com.mysql.cj.jdbc.Driver");
                 String url="jdbc:mysql://localhost:3306/user"+userID;
                 String username="root";
                 String password="";

                 Connection connection=DriverManager.getConnection(url,username,password);

                 String incomeQuery = "SELECT DATE_FORMAT(transaction_date, '%Y-%m') AS month, SUM(amount) AS total_income " +
                         "FROM transactions " +
                         "WHERE transaction_date BETWEEN ? AND ? AND cashflow='Income' " +
                         "GROUP BY DATE_FORMAT(transaction_date, '%Y-%m')";

                 LocalDate startDate1=startDate.getValue();
                 LocalDate endDate1=endDate.getValue();
                 if (count>=1){
                     incomeExpenseChart.getData().clear();
                 }

                 String formattedStartDate=startDate1.format(DateTimeFormatter.ISO_DATE);
                 String formattedEndDate=endDate1.format(DateTimeFormatter.ISO_DATE);

                 PreparedStatement incomeStatement=connection.prepareStatement(incomeQuery);
                 incomeStatement.setString(1,formattedStartDate);
                 incomeStatement.setString(2,formattedEndDate);

                 ResultSet incomeResultset=incomeStatement.executeQuery();

                 XYChart.Series<String,Number> incomeSeries=new XYChart.Series<>();
                 incomeSeries.setName("Income");
                 while (incomeResultset.next()){
                     String month=incomeResultset.getString("month");
                     int amount=incomeResultset.getInt("total_income");
                     incomeSeries.getData().add(new XYChart.Data<>(month,amount));
//                     System.out.println("month: "+month+" total: "+amount);
                 }
                 incomeExpenseChart.getData().add(incomeSeries);

                 String expenseQuery = "SELECT DATE_FORMAT(transaction_date, '%Y-%m') AS month, SUM(amount) AS total_expense " +
                         "FROM transactions " +
                         "WHERE transaction_date BETWEEN ? AND ? AND cashflow = 'Expense' " +
                         "GROUP BY DATE_FORMAT(transaction_date, '%Y-%m')";

                 PreparedStatement expenseStatement=connection.prepareStatement(expenseQuery);
                 expenseStatement.setString(1,formattedStartDate);
                 expenseStatement.setString(2,formattedEndDate);
                 ResultSet expenseResultSet=expenseStatement.executeQuery();

                 XYChart.Series<String, Number> expenseSeries=new XYChart.Series<>();
                 expenseSeries.setName("Expense");
                 while (expenseResultSet.next()){
                     String month=expenseResultSet.getString("month");
                     int amount=expenseResultSet.getInt("total_expense");

//                     System.out.println(month+" "+ amount);
                     expenseSeries.getData().add(new XYChart.Data<>(month,amount));
                 }
                 incomeExpenseChart.getData().add(expenseSeries);

                 incomeExpenseChart.setCategoryGap(50);
                 count++;
                 incomeStatement.close();
                 expenseStatement.close();
                 connection.close();


             }
             catch (Exception e){

             }
         });




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
            xAxis.setLabel("Month");
            yAxis.setLabel("Amount");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String pass="";
                Connection connection=DriverManager.getConnection(url,username,pass);


                String query="SELECT category, SUM(amount) AS total_amount FROM transactions "
                        +"WHERE cashflow= 'Expense' AND transaction_date BETWEEN ? AND ? "+ "GROUP BY category";

                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,formattedStartDate);
                preparedStatement.setString(2,formattedEndDate);

                ResultSet resultSet=preparedStatement.executeQuery();




                ObservableList<PieChart.Data> pieChartData=FXCollections.observableArrayList();

                while (resultSet.next()){
                    String category=resultSet.getString("category");
                    double totalAmount=resultSet.getDouble("total_amount");
//                    System.out.println(totalAmount);
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

    private void setAreaChart(){
              Platform.runLater(()->{
                  try {

                      Class.forName("com.mysql.cj.jdbc.Driver");
                      String url="jdbc:mysql://localhost:3306/user"+userID;
                      String username="root";
                      String password="";

                      Connection connection=DriverManager.getConnection(url,username,password);

                      if (count>=1){
                          balanceChart.getData().clear();
                      }


                      String incomeQuery = "SELECT DATE_FORMAT(transaction_date, '%Y-%m') AS month, SUM(amount) AS total_income " +
                              "FROM transactions " +
                              "WHERE transaction_date BETWEEN ? AND ? AND cashflow='Income' " +
                              "GROUP BY DATE_FORMAT(transaction_date, '%Y-%m')";

                      LocalDate startDate1=startDate.getValue();
                      LocalDate endDate1=endDate.getValue();

                      String formattedStartDate=startDate1.format(DateTimeFormatter.ISO_DATE);
                      String formattedEndDate=endDate1.format(DateTimeFormatter.ISO_DATE);

                      PreparedStatement incomeStatement=connection.prepareStatement(incomeQuery);
                      incomeStatement.setString(1,formattedStartDate);
                      incomeStatement.setString(2,formattedEndDate);

                      ResultSet incomeResultset=incomeStatement.executeQuery();


                      String expenseQuery = "SELECT DATE_FORMAT(transaction_date, '%Y-%m') AS month, SUM(amount) AS total_expense " +
                              "FROM transactions " +
                              "WHERE transaction_date BETWEEN ? AND ? AND cashflow = 'Expense' " +
                              "GROUP BY DATE_FORMAT(transaction_date, '%Y-%m')";

                      PreparedStatement expenseStatement=connection.prepareStatement(expenseQuery);
                      expenseStatement.setString(1,formattedStartDate);
                      expenseStatement.setString(2,formattedEndDate);
                      ResultSet expenseResultSet=expenseStatement.executeQuery();

                      XYChart.Series<String,Number> amountSeries= new XYChart.Series<>();
                      amountSeries.setName("Amount");
                      while (incomeResultset.next() && expenseResultSet.next()){
                          String month=incomeResultset.getString("month");
                          double incomeAmount=incomeResultset.getDouble("total_income");
                          double expenseAmount=expenseResultSet.getDouble("total_expense");
                          double balance=incomeAmount-expenseAmount;
                          amountSeries.getData().add(new XYChart.Data<>(month,balance));
                      }
                      balanceChart.getData().add(amountSeries);


                  }
                  catch (Exception e){

                  }
              });
    }

}
