package com.example.cloudcalc.model;

import javafx.beans.property.SimpleStringProperty;

public class CountConditionModel {
    private final SimpleStringProperty conditionType;
    private final SimpleStringProperty conditionValue;

    public CountConditionModel(String type, String value) {
        this.conditionType = new SimpleStringProperty(type);
        this.conditionValue = new SimpleStringProperty(value);
    }

    public String getConditionType() {
        return conditionType.get();
    }

    public String getConditionValue() {
        return conditionValue.get();
    }
}