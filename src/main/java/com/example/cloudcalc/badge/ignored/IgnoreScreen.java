package com.example.cloudcalc.badge.ignored;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.badge.TableBuilder;
import javafx.stage.Stage;

public class IgnoreScreen implements ScreenDisplayable {

    private final IgnoreManager ignoreManager;

    public IgnoreScreen(IgnoreManager ignoreManager) {
        this.ignoreManager = ignoreManager;
    }

    @Override
    public void showScreen(Stage primaryStage) {
        TableBuilder.initVariables(Constants.IGNORE_FILE, ignoreManager.getUiCallbacks(), ignoreManager.getFileOperationManager(), this, "Add Ignore Badge");
        TableBuilder.buildScreen(primaryStage, "IGNORE");
    }

}
