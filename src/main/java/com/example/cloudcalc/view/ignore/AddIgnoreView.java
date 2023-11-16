package com.example.cloudcalc.view.ignore;

import com.example.cloudcalc.builder.TextFieldManager;
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

        TextFieldManager textFieldManager = LanguageManager.getTextFieldManager();
        nameTextField = textFieldManager.getNameTextField();

        layout.getChildren().addAll(
                topLayout,
                textFieldManager.getNameTextField(),
                saveButton
        );

        ignoreController.createScene(layout, primaryStage);
    }
}
