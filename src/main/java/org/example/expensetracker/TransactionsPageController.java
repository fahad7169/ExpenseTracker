package org.example.expensetracker;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class TransactionsPageController implements Initializable {

    private int userID;
    private ObservableList<Transaction> alltransactions;
    private final int itemsPerPage = 13;

    private final ObservableList<String> incomeCategoryList=FXCollections.observableArrayList
            ("Salary","Interests","Business","Extra income");
    private final ObservableList<String> expenseCategoryList=FXCollections.observableArrayList("Rent","Food","Bills",
            "Utilities","Transportation","Insurance","Shopping"
            ,"Entertainment","Health Care","Housing","Taxes","Clothing","Education","Miscellaneous","Personal Care");
    private int balanceAmount;
    @FXML private Button dashboardButton,addTransactionButton;
    @FXML private AnchorPane AnchorRoot;
    @FXML private Label sideBalanceLabel;
    @FXML private TableView<Transaction> transactionContent;
    @FXML private TableColumn<Transaction,Boolean>  checkboxColumn;
    @FXML private TableColumn<Transaction,String> categoryColumn,dateColumn,paymentModeColumn,descriptionColumn;
    @FXML private TableColumn<Transaction,String> amountColumn;
    @FXML private Pagination pagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Platform.runLater(()->{
            AnchorRoot.getScene().getRoot().requestFocus();
            AnchorRoot.setOnMouseClicked(event -> {
                dashboardButton.getParent().requestFocus();
            });
        });
        dashboardButton.setOnAction(this::DashboardScene);
        addTransactionButton.setOnAction(this::showAddTransactionDialog);
        Platform.runLater(()->{
            sideBalanceLabel.setText("$"+balanceAmount);
            String style= """
                    -fx-font-size: 20px;
                    -fx-font-family: Arial;
                    -fx-background-color:  #f2f2f2""";
            categoryColumn.setStyle(style);
            dateColumn.setStyle(style);
            paymentModeColumn.setStyle(style);
            descriptionColumn.setStyle(style);
            amountColumn.setStyle(style);


        });

        Platform.runLater(()->{
            checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
            checkboxColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            categoryColumn.setCellValueFactory(cellData-> cellData.getValue().categoryProperty());
            dateColumn.setCellValueFactory(cellData-> cellData.getValue().dateProperty());
            paymentModeColumn.setCellValueFactory(cellData-> cellData.getValue().paymentModeProperty());
            descriptionColumn.setCellValueFactory(cellData->cellData.getValue().descriptionProperty());
            amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());

            String style= """
                    -fx-background-color: #ffffff;
                    -fx-text-fill: #615c5c;
                     -fx-font-size: 17px;
                    """;

            checkboxColumn.setStyle(style);
            categoryColumn.setStyle(style);
            dateColumn.setStyle(style);
            paymentModeColumn.setStyle(style);
            descriptionColumn.setStyle(style);
            String style1= """
                    -fx-background-color: #ffffff;
                    -fx-text-fill: #228B22;
                     -fx-font-size: 17px;
                    """;
            amountColumn.setStyle(style1);

            setTheTransactions();
            setPagination();
        });






    }

    public void setValues(int balanceAmount, int userID){
        this.balanceAmount=balanceAmount;
        this.userID=userID;
    }
    private void DashboardScene(ActionEvent event){

            try {

                FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("DashboardPage.fxml"));
                Parent root=fxmlLoader.load();

               DashboardPageController dashboardPageController=fxmlLoader.getController();
               dashboardPageController.setUserID(userID);

                Scene scene=new Scene(root);
                Stage stage=(Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            }
            catch (Exception e){
                e.printStackTrace();
            }

    }

    public void setTheTransactions(){
        alltransactions=FXCollections.observableArrayList();


        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url="jdbc:mysql://localhost:3306/user"+userID;
            String username="root";
            String password="";

            Connection connection=DriverManager.getConnection(url,username,password);

            String query="SELECT * FROM transactions ORDER BY transaction_date DESC";

            PreparedStatement preparedStatement=connection.prepareStatement(query);
            ResultSet resultSet=preparedStatement.executeQuery();

            while (resultSet.next()){
                boolean selected=false;
                String category=resultSet.getString("category");
                String Date=resultSet.getString("transaction_date");
                String paymentMode=resultSet.getString("payment_mode");
                String description=resultSet.getString("description");
                int temp=resultSet.getInt("amount");
                String amount="$"+temp;
//                System.out.println("Category: "+category+" date: "+Date+" Payment mode: "+paymentMode+" description: "+description+" Amount: "+amount);
                alltransactions.add(new Transaction(selected,category,Date,paymentMode,description,amount));
            }
             preparedStatement.close();
            connection.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
//        transactionContent.setItems(alltransactions);



    }

    public void setPagination(){

        int pageCount=(int) Math.ceil((double) alltransactions.size()/ itemsPerPage);


        pagination.setPageCount(pageCount);
        pagination.setPrefSize(848,542);
        pagination.setLayoutX(1);
        pagination.setLayoutY(57);

        pagination.setPageFactory(createPageFactory());

    }
    private Callback<Integer,Node> createPageFactory(){
        return param -> {
            int fromIndex=param*itemsPerPage;
            int toIndex=Math.min(fromIndex+itemsPerPage,alltransactions.size());



            transactionContent.setItems(FXCollections.observableArrayList(alltransactions.subList(fromIndex,toIndex)));


            return transactionContent;

        };
    }
    private void showAddTransactionDialog(ActionEvent event){


       try {
           FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("DailogeBox.fxml"));
           Parent root=fxmlLoader.load();
           DialogBoxController dialogBoxController=fxmlLoader.getController();

           Dialog<Void> dialog=new Dialog<>();
           dialog.setTitle("Add a Transaction");
           dialog.initModality(Modality.APPLICATION_MODAL);
           dialog.getDialogPane().setContent(root);

           dialogBoxController.setSelectCategory(incomeCategoryList);
           dialogBoxController.getIncomeRadioButton().setOnAction(event1 -> {
               dialogBoxController.setSelectCategory(incomeCategoryList);
           });
           dialogBoxController.getExpenseRadioButton().setOnAction(event1 -> {
               dialogBoxController.setSelectCategory(expenseCategoryList);
           });
           dialogBoxController.setUseriD(userID);


           Stage stage=(Stage) dialog.getDialogPane().getScene().getWindow();
           stage.setResizable(false);
           stage.setX(580);
           stage.setY(150);
           stage.setOnCloseRequest(event1-> stage.close());
           stage.showAndWait();





       }
       catch (Exception e){
           e.printStackTrace();
       }


    }
}


class Transaction {
    private final BooleanProperty selected;
    private final StringProperty category;
    private final StringProperty date;
    private final StringProperty paymentMode;
    private final StringProperty description;
    private final StringProperty amount;

    public Transaction(boolean selected, String category, String date, String paymentMode, String description, String amount) {
        this.selected = new SimpleBooleanProperty(selected);
        this.category = new SimpleStringProperty(category);
        this.date = new SimpleStringProperty(date);
        this.paymentMode = new SimpleStringProperty(paymentMode);
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleStringProperty(amount);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty paymentModeProperty() {
        return paymentMode;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty amountProperty() {
        return amount;
    }
}