package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class BaseController {
    protected final SceneBuilder sceneBuilder = new SceneBuilder();
    protected final ElementsBuilder elementsBuilder = new ElementsBuilder();
    protected final MainController mainController;
    protected final ServiceFacade serviceFacade;

    public BaseController(ServiceFacade serviceFacade) {
        this.mainController = serviceFacade.getMainController();
        this.serviceFacade = serviceFacade;
    }

    public void showMainScreen(Stage stage) {
        mainController.showMainScreen(stage);
    }

    public HBox createTopLayout(Button backButton, Label titleLabel, Button createButton) {
        return elementsBuilder.createTopLayout(backButton, titleLabel, createButton);
    }

    public HBox createTopLayout(Button backButton, Label titleLabel) {
        return elementsBuilder.createTopLayout(backButton, titleLabel);
    }

    public abstract void showScreen(Stage stage);
    public abstract void showAddScreen(Stage stage);
    public abstract void createScene(VBox layout, Stage stage);
}