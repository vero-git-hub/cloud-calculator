package com.example.cloudcalc.view.arcade;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.builder.NameTextFieldUpdatable;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.controller.ArcadeController;
import com.example.cloudcalc.controller.StatsController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ArcadeView implements Localizable {
    private final ArcadeController arcadeController;
    private String title = "ARCADE";

    public ArcadeView(ArcadeController arcadeController) {
        this.arcadeController = arcadeController;

        LanguageManager.registerLocalizable(this);
    }

    public String getTitle() {
        return title;
    }

    public void showArcadeScreen(Stage primaryStage) {
        arcadeController.initVariablesForTable();
        arcadeController.buildScreen(primaryStage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("arcadeTitle");

        //this.updateElements(title, addArcadeTitle, bundle);
    }

}