package com.example.cloudcalc.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CountConditionModel {
    private final SimpleStringProperty conditionType;
    private final SimpleStringProperty conditionValue;

    public CountConditionModel() {
        this.conditionType = new SimpleStringProperty();
        this.conditionValue = new SimpleStringProperty();
    }

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

    public StringProperty conditionTypeProperty() {
        return conditionType;
    }

    public StringProperty conditionValueProperty() {
        return conditionValue;
    }

    public void setType(String type) {
        this.conditionType.set(type);
    }

    public void setValue(String value) {
        this.conditionValue.set(value);
    }

    @Override
    public String toString() {
        return "CountConditionModel{" +
                "type='" + getConditionType() + '\'' +
                ", value='" + getConditionValue() + '\'' +
                '}';
    }
}