package org.example.expensetracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

public class Signup_PageController implements Initializable {
    @FXML private AnchorPane AnchorRoot;
    @FXML private Button loginButton;
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
}
