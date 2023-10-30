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
        TableBuilder tableBuilder = new TableBuilder(ignoreManager.getUiCallbacks(), ignoreManager.getFileOperationManager(), ignoreManager.getAddIgnoreScreen(), this);
        tableBuilder.buildScreen(primaryStage, "IGNORE", Constants.IGNORE_FILE);
    }

}
