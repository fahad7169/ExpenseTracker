package org.example.expensetracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Signup_PageController implements Initializable {
    @FXML private AnchorPane AnchorRoot;
    @FXML private Button loginButton;
    @FXML private TextField emailField,passField,confrimPassField;
    @FXML private Label checkLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            AnchorRoot.getScene().getRoot().requestFocus();
            AnchorRoot.getScene().setOnMouseClicked(event -> {
                  loginButton.getParent().requestFocus();
            });
        });
    }
    public void loginScene(ActionEvent event) throws Exception{
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("LoginPage.fxml"));
        Parent root=fxmlLoader.load();
        Scene scene=new Scene(root);
        Stage stage=(Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public void createAccount(){

            if (!emailField.getText().isEmpty() && !passField.getText().isEmpty() && !confrimPassField.getText().isEmpty()){
                String email=emailField.getText();
                String pass=passField.getText();
                String confrimPass=confrimPassField.getText();
                int countUsers=0;
                if (!pass.equals(confrimPass)){
                    checkLabel.setText("*You Password doesn't match with confirm Password");
                }
                else {
                    checkLabel.setVisible(false);
                    String url ="jdbc:mysql://localhost:3306/users";
                    String username="root";
                    String password="";
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection= DriverManager.getConnection(url,username,password);

                        Statement statement=connection.createStatement();
                        int rowsAffected = statement.executeUpdate
                                ("INSERT INTO userslogininfo(email, password) VALUES('" + email + "', '" + pass + "')");

                        // Check if any rows were affected
                        if (rowsAffected > 0) {
                            System.out.println("Data inserted successfully.");
                        } else {
                            System.out.println("Failed to insert data.");
                        }
                        String countQuery="SELECT COUNT(*) AS userid FROM userslogininfo";
                        ResultSet resultSet=statement.executeQuery(countQuery);
                        if (resultSet.next()){
                            countUsers=resultSet.getInt("userId");
                            System.out.println("Number of users");
                        }
                        statement.close();
                        connection.close();
                        try {

                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Connection connection1=DriverManager.getConnection(url,username,password);

                            Statement statement1=connection1.createStatement();
                            String query="CREATE DATABASE IF NOT EXISTS user"+ countUsers;
                            System.out.println(query);
                            statement1.executeUpdate(query);
                            statement1.close();
                            connection1.close();
                        }
                        catch (Exception e){
                             e.printStackTrace();
                        }

                    }
                    catch (SQLException e){
                        System.out.println("Cannot open database");
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        System.out.println("Other problems occured");
                        e.printStackTrace();
                    }
                    try{
                        String url1="jdbc:mysql://localhost:3306/user"+countUsers;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection=DriverManager.getConnection(url1,username,password);

                        Statement statement=connection.createStatement();
                        String query= """
                                CREATE TABLE IF NOT EXISTS transactions (
                                    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                                    category VARCHAR(50),
                                    cashflow ENUM('Income', 'Expense'),
                                    payment_mode VARCHAR(20),
                                    description VARCHAR(255),
                                    amount DECIMAL(10, 2),
                                    transaction_date DATE
                                );
                                """;
                        statement.executeUpdate(query);
                        statement.close();
                        connection.close();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    Alert alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setResizable(false);
                    alert.setHeaderText(null);
                    alert.setContentText("✓ Account successfully created! Now Go back to login page");

                    alert.showAndWait();


                }
            }
            else {
                checkLabel.setText("*You can't leave any field empty");
            }









    }
}
