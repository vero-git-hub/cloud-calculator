package com.example.cloudcalc.builder.fields.type;

import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class TypeBadgeFieldManager implements TypeBadgeFieldUpdatable {

    private TextField nameField;
    private TextField dateField;

    public TypeBadgeFieldManager(ResourceBundle bundle) {
        this.nameField = new TextField();
        this.dateField = new TextField();

        updateNameFieldPlaceholder(bundle.getString("addBadgeTypeNameField"));
        updateDateFieldPlaceholder(bundle.getString("addBadgeTypeDateField"));
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getDateField() {
        return dateField;
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        nameField.setPromptText(placeholder);
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        dateField.setPromptText(placeholder);
    }
}
