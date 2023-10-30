package com.example.cloudcalc.badge.arcade;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.badge.TableBuilder;
import javafx.stage.Stage;

public class ArcadeScreen implements ScreenDisplayable {

    private final ArcadeManager arcadeManager;

    public ArcadeScreen(ArcadeManager arcadeManager) {
        this.arcadeManager = arcadeManager;
    }

    public void showScreen(Stage primaryStage) {
        TableBuilder tableBuilder = new TableBuilder(arcadeManager.getUiCallbacks(), arcadeManager.getFileOperationManager(), arcadeManager.getAddArcadeScreen(), this);
        tableBuilder.buildScreen(primaryStage, "ARCADE", Constants.ARCADE_FILE);
    }

}
