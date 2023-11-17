package com.example.cloudcalc.view.ignore;

import com.example.cloudcalc.builder.fields.badge.BadgeFieldManager;
import com.example.cloudcalc.builder.fields.badge.BadgeNameFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.IgnoreController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddIgnoreView implements Localizable, BadgeNameFieldUpdatable {

    private final IgnoreController ignoreController;
    private String title = "ADD IGNORE";
    private TextField nameField;

    public AddIgnoreView(IgnoreController ignoreController) {
        this.ignoreController = ignoreController;

        LanguageManager.registerLocalizable(this);
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
        nameField = textFieldManager.getNameField();

        layout.getChildren().addAll(
                topLayout,
                textFieldManager.getNameField(),
                saveButton
        );

        ignoreController.createScene(layout, primaryStage);
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        if (nameField != null) {
            nameField.setPromptText(placeholder);
        }
    }

    @Override
    public TextField getNameField() {
        if (nameField == null) {
            nameField = new TextField();
            nameField.setPromptText("Lab name");
            nameField.setId("nameField");
        }
        return nameField;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addIgnoreTitle");

        updateNameFieldPlaceholder(bundle.getString("addScreenNameField"));
    }
}
