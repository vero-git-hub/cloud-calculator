package com.example.cloudcalc.view.ignore;

import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.IgnoreController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddIgnoreView {

    private final IgnoreController ignoreController;
    private String title = "ADD IGNORE";

    public AddIgnoreView(IgnoreController ignoreController) {
        this.ignoreController = ignoreController;
    }

    public void showScreen(Stage primaryStage, TextFieldManager textFieldManager) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> ignoreController.showScreen(primaryStage));
        Label titleAddScreenLabel = new Label(title);

        Button saveButton = ButtonFactory.createSaveButton(event -> {
            ignoreController.handleSave(primaryStage);
        });

        HBox topLayout = ignoreController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);

        layout.getChildren().addAll(
                topLayout,
                textFieldManager.getNameTextField(),
                saveButton
        );

        ignoreController.createScene(layout, primaryStage);
    }
}
