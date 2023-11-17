package com.example.cloudcalc.builder.fields.badge;

import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class BadgeFieldManager implements BadgeNameFieldUpdatable {
    private TextField nameField;

    public BadgeFieldManager(ResourceBundle bundle) {
        nameField = new TextField();
        updateNameFieldPlaceholder(bundle.getString("addScreenNameField"));
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        nameField.setPromptText(placeholder);
    }

    @Override
    public TextField getNameField() {
        return nameField;
    }

}
