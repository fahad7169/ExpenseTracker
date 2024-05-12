package org.example.expensetracker;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DialogBoxController {

    @FXML private RadioButton incomeRadioButton,expenseRadioButton,cashRadioButton,creditCardRadioButton,debitCardRadioButton;
    @FXML private DatePicker dateButton;
    @FXML private ComboBox<String> selectCategory;
    @FXML private TextField amountField,descriptionField;
    @FXML private Button addButton,cancelButton;




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
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
        amountField.setTextFormatter(formatter);
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
        selectCategory.setItems(list);
    }

}

