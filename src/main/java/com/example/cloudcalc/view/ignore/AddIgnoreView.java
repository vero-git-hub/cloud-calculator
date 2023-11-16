package com.example.cloudcalc.view.ignore;

import com.example.cloudcalc.builder.text.fields.BadgeFieldManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.IgnoreController;
import com.example.cloudcalc.language.LanguageManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddIgnoreView {

    private final IgnoreController ignoreController;
    private String title = "ADD IGNORE";
    private TextField nameTextField;

    public AddIgnoreView(IgnoreController ignoreController) {
        this.ignoreController = ignoreController;
    }

    public void showScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> ignoreController.showScreen(primaryStage));
        Label titleAddScreenLabel = new Label(title);

        Button saveButton = ButtonFactory.createSaveButton(event -> {
            ignoreController.handleSave(primaryStage);
        });

        HBox topLayout = ignoreController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);

        BadgeFieldManager textFieldManager = LanguageManager.getTextFieldManager();
        nameTextField = textFieldManager.getNameField();

        layout.getChildren().addAll(
                topLayout,
                textFieldManager.getNameField(),
                saveButton
        );

        ignoreController.createScene(layout, primaryStage);
    }
}
