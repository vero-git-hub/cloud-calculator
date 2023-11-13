package com.example.cloudcalc.view.arcade;

import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ArcadeController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddArcadeView {

    private final ArcadeController arcadeController;
    private String title = "ADD ARCADE";

    public AddArcadeView(ArcadeController arcadeController) {
        this.arcadeController = arcadeController;
    }

    public void showScreen(Stage primaryStage, TextFieldManager textFieldManager) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> arcadeController.showScreen(primaryStage));
        Label titleAddScreenLabel = new Label(title);

        Button saveButton = ButtonFactory.createSaveButton(event -> {
            arcadeController.handleSave(primaryStage);
        });

        HBox topLayout = arcadeController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);

        layout.getChildren().addAll(
                topLayout,
                textFieldManager.getNameTextField(),
                saveButton
        );

        arcadeController.createScene(layout, primaryStage);
    }
}
