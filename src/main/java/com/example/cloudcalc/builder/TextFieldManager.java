package com.example.cloudcalc.builder;

import com.example.cloudcalc.constant.FieldNames;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class TextFieldManager implements NameTextFieldUpdatable {
    private TextField nameTextField;

    public TextFieldManager(ResourceBundle bundle) {
        nameTextField = new TextField();
        updateNameTextFieldPlaceholder(bundle.getString("addScreenNameField"));
    }

    @Override
    public void updateNameTextFieldPlaceholder(String placeholder) {
        nameTextField.setPromptText(placeholder);
    }

    @Override
    public TextField getNameTextField() {
        return nameTextField;
    }

}
