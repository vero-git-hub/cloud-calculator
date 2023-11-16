package com.example.cloudcalc.builder.text.fields;

import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class PrizeFieldManager implements PrizeFieldsUpdatable {

    private TextField nameField;
    private TextField badgeCountField;

    public PrizeFieldManager(ResourceBundle bundle) {
        nameField = new TextField();
        badgeCountField = new TextField();

        updateNameFieldPlaceholder(bundle.getString("addPrizeNameField"));
        updateBadgeCountFieldPlaceholder(bundle.getString("addPrizeCountField"));
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getBadgeCountField() {
        return badgeCountField;
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        nameField.setPromptText(placeholder);
    }

    @Override
    public void updateBadgeCountFieldPlaceholder(String placeholder) {
        badgeCountField.setPromptText(placeholder);
    }

}
