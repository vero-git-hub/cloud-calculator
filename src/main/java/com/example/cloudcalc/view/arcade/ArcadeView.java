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

public class ArcadeView {
    private final ArcadeController arcadeController;
    private String title = "ARCADE";

    public ArcadeView(ArcadeController arcadeController) {
        this.arcadeController = arcadeController;
    }

    public String getTitle() {
        return title;
    }


//    public ArcadeView() {
//        this.serviceFacade = ServiceFacade.getInstance();
//        this.controller = createController();
//
//        LanguageManager.registerLocalizable(this);
//    }
//
//    private ArcadeManager createController() {
//        return new ArcadeManager(serviceFacade);
//    }
//
    public void showArcadeScreen(Stage primaryStage) {
        arcadeController.initVariablesForTable();
        arcadeController.buildScreen(primaryStage);
    }

//    @Override
//    public void updateLocalization(ResourceBundle bundle) {
//        title = bundle.getString("arcadeTitle");
//        addArcadeTitle = bundle.getString("addArcadeTitle");
//
//        this.updateElements(title, addArcadeTitle, bundle);
//
//        updateNameTextFieldPlaceholder(bundle.getString("addScreenNameField"));
//    }
//
//    @Override
//    public void updateNameTextFieldPlaceholder(String placeholder) {
//        if (nameTextField != null) {
//            nameTextField.setPromptText(placeholder);
//        }
//    }
//
//    @Override
//    public TextField getNameTextField() {
//        if (nameTextField == null) {
//            nameTextField = new TextField();
//            nameTextField.setPromptText("Lab name");
//            nameTextField.setId("nameField");
//        }
//        return nameTextField;
//    }

}