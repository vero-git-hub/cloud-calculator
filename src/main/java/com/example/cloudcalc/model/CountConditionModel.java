package com.example.cloudcalc.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class CountConditionModel {
    private final SimpleStringProperty type;
    private final ObservableList<String> values;

    public CountConditionModel() {
        this.type = new SimpleStringProperty();
        this.values = FXCollections.observableArrayList();
    }

    public CountConditionModel(String type, List<String> values) {
        this.type = new SimpleStringProperty(type);
        this.values = FXCollections.observableArrayList(values);
    }

    public String getType() {
        return type.get();
    }

    public ObservableList<String> getValues() {
        return values;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setValues(List<String> values) {
        this.values.setAll(values);
    }

    @Override
    public String toString() {
        return "CountConditionModel{" +
                "type='" + getType() + '\'' +
                ", values='" + values + '\'' +
                '}';
    }
}