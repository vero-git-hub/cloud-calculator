package com.example.cloudcalc.entity.program;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;

public class CountCondition {
    private final StringProperty type;
    private final ObservableList<ValueWithPoints> values;

    public CountCondition() {
        this.type = new SimpleStringProperty();
        this.values = FXCollections.observableArrayList();
    }

    public CountCondition(String type, List<ValueWithPoints> values) {
        this.type = new SimpleStringProperty(type);
        this.values = FXCollections.observableArrayList(values);
    }

    public String getType() {
        return type.get();
    }

    public ObservableList<ValueWithPoints> getValues() {
        return values;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setValues(List<ValueWithPoints> values) {
        this.values.setAll(values);
    }

    @Override
    public String toString() {
        return "CountConditionModel{" +
                "type='" + getType() + '\'' +
                ", values='" + values + '\'' +
                '}';
    }

    public static class ValueWithPoints {
        private final StringProperty title;
        private final IntegerProperty points;

        public ValueWithPoints(String title, int points) {
            this.title = new SimpleStringProperty(title);
            this.points = new SimpleIntegerProperty(points);
        }

        public String getTitle() {
            return title.get();
        }

        public int getPoints() {
            return points.get();
        }

        public StringProperty titleProperty() {
            return title;
        }

        public void setTitle(String title) {
            this.title.set(title);
        }

        public IntegerProperty pointsProperty() {
            return points;
        }

        public void setPoints(int points) {
            this.points.set(points);
        }

        @Override
        public String toString() {
            return "ValueWithPoints{" +
                    "title='" + getTitle() + '\'' +
                    ", points=" + getPoints() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValueWithPoints that = (ValueWithPoints) o;
            return getPoints() == that.getPoints() && Objects.equals(getTitle(), that.getTitle());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTitle(), getPoints());
        }
    }
}