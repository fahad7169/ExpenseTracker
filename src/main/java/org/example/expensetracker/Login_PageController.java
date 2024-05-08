package org.example.expensetracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Login_PageController implements Initializable {
    @FXML private AnchorPane AnchorRoot;
    @FXML private ImageView eye;
    @FXML private PasswordField passField;
    @FXML private Button showHideButton;
    boolean showPass=false;
    private int count;
    TextField textField;
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

        Image show=new Image("/icons8-show-password-90.png");
        Image hide=new Image("/icons8-hide-100.png");
        if (showPass){
            eye.setImage(show);
            String temp=passField.getText();
            textField=new TextField(temp);
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
        }
        else{
            if (count>=1){
                eye.setImage(hide);
                Platform.runLater(()->{
                    passField.setText(textField.getText());
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

}

