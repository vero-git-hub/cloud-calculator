package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.model.TypeBadgeModel;
import com.example.cloudcalc.view.TypeBadgeView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TypeBadgeController {

    private TypeBadgeView typeBadgeView = new TypeBadgeView(this);
    private TypeBadgeModel typeBadgeModel = new TypeBadgeModel(this);
    private ServiceFacade serviceFacade;
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final SceneBuilder sceneBuilder = new SceneBuilder();

    public TypeBadgeController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    public TypeBadgeModel getTypeBadgeModel() {
        return typeBadgeModel;
    }

    public void showScreen(Stage stage) {
        typeBadgeView.showScreen(stage);
    }

    public void handleTypeBadgeSave(Stage primaryStage, String
            textFromNameField, String textFromDateField) {
        if(textFromNameField != null && !textFromNameField.isEmpty() &&
                textFromDateField != null && !textFromDateField.isEmpty()) {
            typeBadgeModel.handleTypeBadgeSave(primaryStage, textFromNameField, textFromDateField);
        }

    };

    public void showAddPrizesScreen(Stage stage) {
        serviceFacade.getPrizeController().showAddPrizesScreen(stage);
    }

    public HBox createTopLayout(Button backButton, Label titleLabel) {
        return elementsBuilder.createTopLayout(backButton, titleLabel);
    }

    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }
}
