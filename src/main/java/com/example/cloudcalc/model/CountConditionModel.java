package com.example.cloudcalc.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CountConditionModel {
    private final SimpleStringProperty type;
    private final SimpleStringProperty value;

    public CountConditionModel() {
        this.type = new SimpleStringProperty();
        this.value = new SimpleStringProperty();
    }

    public CountConditionModel(String type, String value) {
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleStringProperty(value);
    }

    public String getType() {
        return type.get();
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    @Override
    public String toString() {
        return "CountConditionModel{" +
                "type='" + getType() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}