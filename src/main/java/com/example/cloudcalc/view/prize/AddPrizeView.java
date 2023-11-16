package com.example.cloudcalc.view.prize;

import com.example.cloudcalc.builder.text.fields.PrizeFieldManager;
import com.example.cloudcalc.builder.text.fields.PrizeFieldsUpdatable;
import com.example.cloudcalc.entity.badge.TypeBadge;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class AddPrizeView implements Localizable, PrizeFieldsUpdatable {
    private final PrizeController prizeController;
    private String title = "ADD PRIZE";
    private ComboBox<String> badgeTypeComboBox;
    private TextField nameTextField;
    private TextField badgeCountTextField;

    public AddPrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;

        badgeTypeComboBox = new ComboBox<>();
        badgeTypeComboBox.setPromptText("Select badge type");

        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

        HBox typeLayout = new HBox();
        typeLayout.setAlignment(Pos.CENTER);

        badgeTypeComboBox.getItems().clear();
        List<TypeBadge> typeBadgeList = prizeController.loadTypesBadgeFromFile();
        typeBadgeList.forEach(typeBadge -> badgeTypeComboBox.getItems().add(typeBadge.getName()));

        Button addTypeBadgeButton = ButtonFactory.createAddButton(e -> prizeController.showAddTypeBadgeScreen(stage));

        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        typeLayout.getChildren().addAll(badgeTypeComboBox, addTypeBadgeButton, rightSpacer);

        PrizeFieldManager prizeTextFieldManager = LanguageManager.getTextFieldPrizeManager();
        nameTextField = prizeTextFieldManager.getNameField();
        badgeCountTextField = prizeTextFieldManager.getBadgeCountField();

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            prizeController.savePrize(stage, badgeTypeComboBox.getValue(), nameTextField, badgeCountTextField);
        });

        Button backButton = ButtonFactory.createBackButton(e -> prizeController.showScreen(stage));

        HBox topLayout = prizeController.createTopLayoutForAddScreen(backButton, new Label(title));

        layout.getChildren().addAll(
                topLayout,
                nameTextField,
                badgeCountTextField,
                typeLayout,
                saveButton
        );

        prizeController.createScene(layout, stage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addPrizeTitle");
        badgeTypeComboBox.setPromptText(bundle.getString("addPrizeTypeComboBox"));

        updateNameFieldPlaceholder(bundle.getString("addPrizeNameField"));
        updateBadgeCountFieldPlaceholder(bundle.getString("addPrizeCountField"));
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        if (nameTextField != null) {
            nameTextField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateBadgeCountFieldPlaceholder(String placeholder) {
        if (badgeCountTextField != null) {
            badgeCountTextField.setPromptText(placeholder);
        }
    }

//    public TextField getNameTextField() {
//        if (nameTextField == null) {
//            nameTextField = new TextField();
//            nameTextField.setPromptText("Enter name prize");
//            nameTextField.setId("nameField");
//        }
//        return nameTextField;
//    }
}
