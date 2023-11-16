package com.example.cloudcalc.view.arcade;

import com.example.cloudcalc.builder.NameTextFieldUpdatable;
import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ArcadeController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddArcadeView implements Localizable, NameTextFieldUpdatable {

    private final ArcadeController arcadeController;
    private String title = "ADD ARCADE";
    private TextField nameTextField;

    public AddArcadeView(ArcadeController arcadeController) {
        this.arcadeController = arcadeController;

        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> arcadeController.showScreen(primaryStage));
        Label titleAddScreenLabel = new Label(title);

        Button saveButton = ButtonFactory.createSaveButton(event -> {
            arcadeController.handleSave(primaryStage);
        });

        HBox topLayout = arcadeController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);

        TextFieldManager textFieldManager = LanguageManager.getTextFieldManager();
        nameTextField = textFieldManager.getNameTextField();

        layout.getChildren().addAll(
                topLayout,
                nameTextField,
                saveButton
        );

        arcadeController.createScene(layout, primaryStage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addArcadeTitle");

        updateNameTextFieldPlaceholder(bundle.getString("addScreenNameField"));
    }

    @Override
    public void updateNameTextFieldPlaceholder(String placeholder) {
        if (nameTextField != null) {
            nameTextField.setPromptText(placeholder);
        }
    }

    @Override
    public TextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new TextField();
            nameTextField.setPromptText("Lab name");
            nameTextField.setId("nameField");
        }
        return nameTextField;
    }
}
