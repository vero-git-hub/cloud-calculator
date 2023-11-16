package com.example.cloudcalc.view;

import com.example.cloudcalc.builder.text.fields.TypeBadgeFieldsManager;
import com.example.cloudcalc.builder.text.fields.TypeBadgeFieldsUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.TypeBadgeController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddTypeBadgeView implements Localizable, TypeBadgeFieldsUpdatable {
    private TypeBadgeController typeBadgeController;
    private String title = "CREATE BADGE TYPE";
    private TextField nameField;
    private TextField dateField;

    public AddTypeBadgeView(TypeBadgeController typeBadgeController) {
        this.typeBadgeController = typeBadgeController;

        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

        TypeBadgeFieldsManager typeBadgeFieldsManager = LanguageManager.getTypeBadgeTextFieldsManager();
        nameField = typeBadgeFieldsManager.getNameField();
        dateField = typeBadgeFieldsManager.getDateField();
//        nameField.setPromptText(FieldNames.NAME.getLabel());
//        dateField.setPromptText(FieldNames.START_DATE.getLabel());

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            typeBadgeController.handleTypeBadgeSave(stage, nameField.getText(), dateField.getText());
        });

        Button backButton = ButtonFactory.createBackButton(e -> typeBadgeController.showAddPrizesScreen(stage));

        HBox topLayout = typeBadgeController.createTopLayout(backButton, new Label(title));

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                saveButton
        );

        typeBadgeController.createScene(layout, stage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addBadgeTypeTitle");

        updateNameFieldPlaceholder(bundle.getString("addBadgeTypeNameField"));
        updateDateFieldPlaceholder(bundle.getString("addBadgeTypeDateField"));
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        if (nameField != null) {
            nameField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        if (dateField != null) {
            dateField.setPromptText(placeholder);
        }
    }
}
