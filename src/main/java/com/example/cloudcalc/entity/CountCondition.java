package com.example.cloudcalc.entity;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CountCondition {
    DatePicker startDatePicker;
    TextField countField;
    TextField ignoreField;

    public CountCondition(DatePicker startDatePicker, TextField countField, TextField ignoreField) {
        this.startDatePicker = startDatePicker;
        this.countField = countField;
        this.ignoreField = ignoreField;
    }
}
