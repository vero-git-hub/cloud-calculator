package com.example.cloudcalc.view.ignore;

import com.example.cloudcalc.controller.IgnoreController;
import javafx.stage.Stage;

public class IgnoreView {

    private final IgnoreController ignoreController;
    private String title = "IGNORE";

    public IgnoreView(IgnoreController ignoreController) {
        this.ignoreController = ignoreController;
    }

    public String getTitle() {
        return title;
    }

    public void showScreen(Stage stage) {
        ignoreController.initVariablesForTable();
        ignoreController.buildScreen(stage);
    }
}
