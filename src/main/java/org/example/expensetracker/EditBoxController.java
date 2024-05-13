package org.example.expensetracker;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditBoxController implements Initializable {
    @FXML private RadioButton incomeRadioButton,expenseRadioButton,cashRadioButton,creditCardRadioButton,debitCardRadioButton;
    @FXML private DatePicker dateButton;
    @FXML private ComboBox<String> selectCategory;
    @FXML private TextField amountField,descriptionField;
    @FXML private Button saveButton,cancelButton;
    @FXML private AnchorPane AnchorRoot;
    private int userID;
    String category, date,paymentMode,description,amount;
    ObservableList<String> incomeCategoryList,expenseCategoryList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Platform.runLater(()->{
            AnchorRoot.getScene().getRoot().requestFocus();
            saveButton.getParent().requestFocus();
        });

    }

    public RadioButton getIncomeRadioButton(){
        return incomeRadioButton;
    }
    public RadioButton getExpenseRadioButton(){
        return  expenseRadioButton;
    }
    public void setSelectCategory(ObservableList<String> list){

        selectCategory.setItems(list);
    }
    public void setUseriD(int userID){
        this.userID=userID;
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
        amountField.setTextFormatter(formatter);
    }
    public void closeWindow(){
        Stage stage=(Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    public void setVariables(String category,String date,String paymentMode,String description,String amount
            ,ObservableList<String> incomeCategoryList,ObservableList<String> expenseCategoryList){
        this.category=category;
        this.date=date;
        this.paymentMode=paymentMode;
        this.description=description;
        this.amount=amount;
        this.incomeCategoryList=incomeCategoryList;
        this.expenseCategoryList=expenseCategoryList;
    }
    public void setData(){
        Platform.runLater(()->{
            selectCategory.setValue(category);


            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateButton.setValue(localDate);
            for (String list: incomeCategoryList){
                if (list.equals(category)){
                    incomeRadioButton.setSelected(true);
                    break;
                }
            }
            for (String list:expenseCategoryList){
                if (list.equals(category)){
                    expenseRadioButton.setSelected(true);
                    break;
                }
            }



            if (paymentMode.equals("Cash")){
                cashRadioButton.setSelected(true);
            }
            else if (paymentMode.equals("Debit card")){
                debitCardRadioButton.setSelected(true);
            }
            else {
                creditCardRadioButton.setSelected(true);
            }


            descriptionField.setText(description);
            amountField.setText(amount);

        });


    }
    public void saveTransaction(){
        Platform.runLater(()->{
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String password="";

                Connection connection= DriverManager.getConnection(url,username,password);

                LocalDate transactionDate=dateButton.getValue();
                String formattedDate=null;
                if (transactionDate!=null){
                    formattedDate=transactionDate.format(DateTimeFormatter.ISO_DATE);
                }
                String category=null;
                if (selectCategory.getValue()!=null){
                    category=selectCategory.getValue();
                }
                String paymentMode="";
                if (cashRadioButton.isSelected()){
                    paymentMode="Cash";
                }
                else if (debitCardRadioButton.isSelected()){
                    paymentMode="Debit card";
                }
                else if(creditCardRadioButton.isSelected()){
                    paymentMode="Credit card";
                }
                String cashflow="";
                if (incomeRadioButton.isSelected()){
                    cashflow="Income";
                }
                else if (expenseRadioButton.isSelected()){
                    cashflow="Expense";
                }
                String description="";
                if (descriptionField.getText()!=null){
                    description=descriptionField.getText();
                }

                String amount=amountField.getText();
                int amount1=0;
                if (!amount.isEmpty()){
                    amount1=Integer.parseInt(amount);
                }

                String query = """
                       UPDATE transactions
                       SET category=?, cashflow=?, payment_mode=?, description=?, amount=?, transaction_date=?
                       WHERE description=? AND category=? AND payment_mode=?
                       """;
                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,category);
                preparedStatement.setString(2,cashflow);
                preparedStatement.setString(3,paymentMode);
                preparedStatement.setString(4,description);
                preparedStatement.setInt(5,amount1);
                preparedStatement.setString(6,formattedDate);
                preparedStatement.setString(7,this.description);
                preparedStatement.setString(8,this.category);
                preparedStatement.setString(9,this.paymentMode);


                if (category!=null && transactionDate!=null && amount1>0){
                    preparedStatement.executeUpdate();
                    Stage stage=(Stage) saveButton.getScene().getWindow();
                    stage.close();

                }

                preparedStatement.close();
                connection.close();



            }
            catch (Exception e){
                e.printStackTrace();
            }
        });


    }


}
