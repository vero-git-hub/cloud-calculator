package com.example.cloudcalc.builder.fields.profile;

import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class ProfileFieldManager implements ProfileFieldUpdatable {

    private TextField nameField;
    private TextField dateField;
    private TextField linkField;

    public ProfileFieldManager(ResourceBundle bundle) {
        this.nameField = new TextField();
        this.dateField = new TextField();
        this.linkField = new TextField();

        updateNameFieldPlaceholder(bundle.getString("createProfileNameField"));
        updateDateFieldPlaceholder(bundle.getString("createProfileDateField"));
        updateLinkFieldPlaceholder(bundle.getString("createProfileLinkField"));
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getDateField() {
        return dateField;
    }

    public TextField getLinkField() {
        return linkField;
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        nameField.setPromptText(placeholder);
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        dateField.setPromptText(placeholder);
    }

    @Override
    public void updateLinkFieldPlaceholder(String placeholder) {
        linkField.setPromptText(placeholder);
    }
}
