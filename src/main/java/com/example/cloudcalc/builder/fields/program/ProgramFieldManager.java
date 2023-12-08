package com.example.cloudcalc.builder.fields.program;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class ProgramFieldManager implements ProgramFieldUpdatable {
    private TextField countField;
    private TextField ignoreField;
    private TextField programNameField;
    private DatePicker dateField;

    public ProgramFieldManager(ResourceBundle bundle) {
        this.countField = new TextField();
        this.ignoreField = new TextField();
        this.programNameField = new TextField();
        this.dateField = new DatePicker();

        updateCountFieldPlaceholder(bundle.getString("countField"));
        updateIgnoreFieldPlaceholder(bundle.getString("ignoreField"));
        updateProgramNameFieldPlaceholder(bundle.getString("programNameField"));
        updateDateFieldPlaceholder(bundle.getString("startDatePicker"));
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

    @Override
    public void updateProgramNameFieldPlaceholder(String placeholder) {
        programNameField.setPromptText(placeholder);
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        dateField.setPromptText(placeholder);
    }
}