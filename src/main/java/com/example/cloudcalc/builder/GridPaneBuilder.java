package com.example.cloudcalc.builder;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class GridPaneBuilder {

    private GridPane gridPane = new GridPane();
    private Label programNameLabel;
    private  Label labelDate;

    public GridPaneBuilder(Label programNameLabel, Label labelDate) {
        this.programNameLabel = programNameLabel;
        this.labelDate = labelDate;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Label getProgramNameLabel() {
        return programNameLabel;
    }

    public void setProgramNameLabel(Label programNameLabel) {
        this.programNameLabel = programNameLabel;
    }

    public Label getLabelDate() {
        return labelDate;
    }

    public void setLabelDate(Label labelDate) {
        this.labelDate = labelDate;
    }

    public void setGridPaneSizes() {
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
    }

    public void settingGridPane(TextField programNameField, DatePicker startDatePicker) {
        gridPane.add(programNameLabel, 0, 0);
        gridPane.add(programNameField, 1, 0);
        gridPane.add(labelDate, 0, 1);
        gridPane.add(startDatePicker, 1, 1);
    }

    public void setHAlignmentGridPane(Label programNameLabel, Label labelDate) {
        GridPane.setHalignment(programNameLabel, HPos.LEFT);
        GridPane.setHalignment(labelDate, HPos.LEFT);
    }
}
