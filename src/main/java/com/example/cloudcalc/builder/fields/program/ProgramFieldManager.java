package com.example.cloudcalc.builder.fields.program;

import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class ProgramFieldManager implements ProgramFieldUpdatable {
    private TextField countField;
    private TextField ignoreField;

    public ProgramFieldManager(ResourceBundle bundle) {
        this.countField = new TextField();
        this.ignoreField = new TextField();

        updateCountFieldPlaceholder(bundle.getString("countField"));
        updateIgnoreFieldPlaceholder(bundle.getString("ignoreField"));
    }

    public TextField getCountField() {
        return countField;
    }

    public TextField getIgnoreField() {
        return ignoreField;
    }

    @Override
    public void updateCountFieldPlaceholder(String placeholder) {
        countField.setPromptText(placeholder);
    }

    @Override
    public void updateIgnoreFieldPlaceholder(String placeholder) {
        ignoreField.setPromptText(placeholder);
    }
}