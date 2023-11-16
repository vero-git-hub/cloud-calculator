package com.example.cloudcalc.builder.text.fields;

import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class TypeBadgeFieldsManager implements TypeBadgeFieldsUpdatable {

    private TextField nameField;
    private TextField dateField;

    public TypeBadgeFieldsManager(ResourceBundle bundle) {
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
