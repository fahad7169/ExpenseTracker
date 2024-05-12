package org.example.expensetracker;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DialogBoxController {

    @FXML private RadioButton incomeRadioButton,expenseRadioButton,cashRadioButton,creditCardRadioButton,debitCardRadioButton;
    @FXML private DatePicker dateButton;
    @FXML private ComboBox<String> selectCategory;
    @FXML private TextField amountField,descriptionField;
    @FXML private Button addButton,cancelButton;
    private int userID;




    public RadioButton getIncomeRadioButton(){
        return incomeRadioButton;
    }
    public RadioButton getExpenseRadioButton(){
        return  expenseRadioButton;
    }
    public RadioButton getCashRadioButton(){
        return cashRadioButton;
    }
    public RadioButton getDebitCardRadioButton(){
        return debitCardRadioButton;
    }
    public RadioButton getCreditCardRadioButton(){
        return creditCardRadioButton;
    }
    public DatePicker getDateButton(){
        return dateButton;
    }
    public ComboBox<String> getSelectCategory(){
        return selectCategory;
    }
    public TextField getAmountField(){
        return amountField;
    }
    public TextField getDescriptionField(){
        return descriptionField;
    }
    public Button getAddButton(){
        return addButton;
    }
    public Button getCancelButton(){
        return cancelButton;
    }
    public void setSelectCategory(ObservableList<String> list){
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
        amountField.setTextFormatter(formatter);
        selectCategory.setItems(list);
    }
    public void setUseriD(int userID){
        this.userID=userID;
    }
    public void closeWindow(){
        Stage stage=(Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    public void addTransaction(){

        Platform.runLater(()->{
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url="jdbc:mysql://localhost:3306/user"+userID;
                String username="root";
                String password="";

                Connection connection= DriverManager.getConnection(url,username,password);

                LocalDate transactionDate=dateButton.getValue();
                String formattedDate=transactionDate.format(DateTimeFormatter.ISO_DATE);
                String category=selectCategory.getValue();
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
                String description=descriptionField.getText();
                int amount=Integer.parseInt(amountField.getText());


                String query=
                """
                        INSERT INTO transactions(category,cashflow,payment_mode,description,amount,transaction_date)
                        VALUES(?,?,?,?,?,?)
                        """
                                ;
                PreparedStatement preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,category);
                preparedStatement.setString(2,cashflow);
                preparedStatement.setString(3,paymentMode);
                preparedStatement.setString(4,description);
                preparedStatement.setInt(5,amount);
                preparedStatement.setString(6,formattedDate);
                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();


                Stage stage=(Stage) addButton.getScene().getWindow();
                stage.close();



            }
            catch (Exception e){
                  e.printStackTrace();
            }
        });

    }

}

