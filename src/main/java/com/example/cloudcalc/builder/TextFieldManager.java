package com.example.cloudcalc.builder;

import com.example.cloudcalc.constant.FieldNames;
import javafx.scene.control.TextField;

public class TextFieldManager implements NameTextFieldUpdatable {
    private TextField nameTextField;

    public TextFieldManager() {
        nameTextField = new TextField();
        updateNameTextFieldPlaceholder(FieldNames.NAME.getLabel());
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
