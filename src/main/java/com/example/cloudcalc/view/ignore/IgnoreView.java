package com.example.cloudcalc.view.ignore;

import com.example.cloudcalc.controller.IgnoreController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class IgnoreView implements Localizable {

    private final IgnoreController ignoreController;
    private String title = "IGNORE";

    public IgnoreView(IgnoreController ignoreController) {
        this.ignoreController = ignoreController;

        LanguageManager.registerLocalizable(this);
    }

    public String getTitle() {
        return title;
    }

    public void showScreen(Stage stage) {
        ignoreController.initVariablesForTable();
        ignoreController.buildScreen(stage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("ignoreTitle");
    }
}
