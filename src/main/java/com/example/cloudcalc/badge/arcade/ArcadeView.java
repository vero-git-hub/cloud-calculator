package com.example.cloudcalc.badge.arcade;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.builder.NameTextFieldUpdatable;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

//public class ArcadeView implements ScreenDisplayable, Localizable, NameTextFieldUpdatable {
//
//    private ServiceFacade serviceFacade;
//    private final ArcadeManager controller;
//
//
//    private String title = "ARCADE";
//    private String addArcadeTitle = "ADD ARCADE BADGE";
//
//    private TextField nameTextField = new TextField();
//
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
//    public void showArcadeScreen(Stage primaryStage) {
////        TableBuilder.initVariables(FileName.ARCADE_FILE, controller.getUiCallbacks(), controller.getFileOperationManager(), this, addArcadeTitle, this);
//
//        TableBuilder.fileName = fileName;
//        TableBuilder.uiCallbacks = uiCallbacks;
//        TableBuilder.fileOperationManager = fileOperationManager;
//        TableBuilder.screenDisplayable = screenDisplayable;
//        TableBuilder.addScreenLabel = addScreenLabel;
//        TableBuilder.nameTextFieldUpdatable = nameTextFieldUpdatable;
//
//
//
//        TableBuilder.buildScreen(primaryStage, title);
//    }
//
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
//}
