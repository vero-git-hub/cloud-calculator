package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FieldNames;
import com.example.cloudcalc.controller.TypeBadgeController;
import com.example.cloudcalc.entity.TypeBadge;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TypeBadgeView {

    private TypeBadgeController typeBadgeController;

    TextField nameField = new TextField();
    TextField dateField = new TextField();

    Label titleLabel = new Label("CREATE BADGE TYPE");

//    private final UICallbacks uiCallbacks;
//    private final TypeBadgeDataManager typeBadgeDataManager;
//    private final PrizeManager prizeManager;
//
//    public TypeBadgeManager(UICallbacks uiCallbacks, TypeBadgeDataManager typeBadgeDataManager, PrizeManager prizeManager) {
//        this.uiCallbacks = uiCallbacks;
//        this.typeBadgeDataManager = typeBadgeDataManager;
//        this.prizeManager = prizeManager;
//    }

    public TypeBadgeView(TypeBadgeController typeBadgeController) {
        this.typeBadgeController = typeBadgeController;

        nameField.setPromptText(FieldNames.NAME.getLabel());
        dateField.setPromptText(FieldNames.START_DATE.getLabel());
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            typeBadgeController.handleTypeBadgeSave(stage, nameField.getText(), dateField.getText());
        });

        Button backButton = ButtonFactory.createBackButton(e -> typeBadgeController.showAddPrizesScreen(stage));

        HBox topLayout = typeBadgeController.createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                saveButton
        );

        typeBadgeController.createScene(layout, stage);
    }


}
