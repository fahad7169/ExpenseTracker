package org.example.expensetracker;

import com.mysql.cj.jdbc.Driver;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class Login_PageController implements Initializable {
    @FXML private AnchorPane AnchorRoot;
    @FXML private ImageView eye;
    @FXML private PasswordField passField;
    @FXML private Button showHideButton;
    @FXML private TextField emailField;
    @FXML private Label checkLabel;
    boolean showPass=false;
    private int count;
    private TextField textField;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            AnchorRoot.getScene().getRoot().requestFocus();
            AnchorRoot.getScene().setOnMouseClicked(event -> {
                eye.getParent().requestFocus();
            });
        });
        showHideIcon();
        showHideButton.setOnAction(event -> {
            showHideIcon();
        });


    }
    private void showHideIcon(){

        Image show=new Image("/eye (3).png");
        Image hide=new Image("/eye (4).png");
        if (showPass){
            eye.setImage(hide);
            String temp=null;
            try {
                temp=passField.getText();
            }
            catch (Exception ignored){

            }

            textField=new TextField();
            if (temp!=null){
                textField.setText(temp);
            }
            AnchorRoot.getChildren().remove(passField);
            textField.setPromptText("Password");
            textField.setOpacity(0.48);
            textField.setLayoutX(153);
            textField.setLayoutY(312);
            textField.setPrefSize(381,51);
            textField.setStyle(" -fx-background-radius: 30px;");
            textField.setFont(Font.font("Arial",24));
            Platform.runLater(textField::toBack);
            AnchorRoot.getChildren().add(textField);
//            System.out.println(count);
        }
        else{
            if (count>=1){
                eye.setImage(show);
                Platform.runLater(()->{
                    String t=null;
                    try {
                        t=textField.getText();
                    }
                    catch (Exception ignored) {

                    }
                    if (t!=null){
                        passField.setText(t);
                    }

                    AnchorRoot.getChildren().remove(textField);
                    AnchorRoot.getChildren().add(passField);
                });
                Platform.runLater(passField::toBack);
            }

        }
        count++;
        showPass=!showPass;






    }
    public void signupScene(ActionEvent event) throws Exception{

        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("SignupPage.fxml"));

        Parent root=fxmlLoader.load();
        Scene scene=new Scene(root);
        Stage stage=(Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }
    public void Login(ActionEvent event){

        String email=null;
        String pass=null;
        try {
            email=emailField.getText();
            if (showPass){
                pass=passField.getText();
//                System.out.println(pass);

            }
            else {

                pass=textField.getText();
//                System.out.println(pass);
            }
        }
        catch (Exception ignored){

        }
        if (email!=null && pass!=null){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                String url="jdbc:mysql://localhost:3306/users";
                String username="root";
                String password="";
                Connection connection= DriverManager.getConnection(url,username,password);

                String query="SELECT * FROM userslogininfo " + "WHERE email='"+email+"'";
                Statement statement= connection.createStatement();
                ResultSet resultSet=statement.executeQuery(query);
                if (resultSet.next()){
                    int userId=resultSet.getInt(1);
                    String databaseEmail=resultSet.getString(2);
                    String databasePass=resultSet.getString(3);
                    if (databaseEmail.equals(email) && databasePass.equals(pass)){
                        checkLabel.setVisible(false);
                        openMainPage(userId,event);
                    }
                    else {
                        checkLabel.setText("*Your email and password doesn't match");
                    }
                }
                else{
                   checkLabel.setText("*Your email doesn't exist in our database");
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }





    }
    private void openMainPage(int userId, ActionEvent ev) throws Exception{


        FadeTransition fadeout=new FadeTransition(Duration.seconds(0.2),AnchorRoot);
        fadeout.setFromValue(1.0);
        fadeout.setToValue(0.0);

        fadeout.setOnFinished(event -> {

            try {
                FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("DashboardPage.fxml"));
                Parent root=fxmlLoader.load();

                DashboardPageController dashboardPageController=fxmlLoader.getController();
                dashboardPageController.getUserId(userId);

                Scene scene=new Scene(root);
                Stage stage=(Stage) ((Node) ev.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }



        });
        fadeout.play();


    }

}

