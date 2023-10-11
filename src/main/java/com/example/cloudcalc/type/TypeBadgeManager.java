package com.example.cloudcalc.type;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.prize.PrizeManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TypeBadgeManager {

    private final UICallbacks uiCallbacks;
    private final TypeBadgeDataManager typeBadgeDataManager;
    private final PrizeManager prizeManager;

    public TypeBadgeManager(UICallbacks uiCallbacks, TypeBadgeDataManager typeBadgeDataManager, PrizeManager prizeManager) {
        this.uiCallbacks = uiCallbacks;
        this.typeBadgeDataManager = typeBadgeDataManager;
        this.prizeManager = prizeManager;
    }

    public void showAddTypeBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        TypeBadge typeBadge = new TypeBadge();

        TextField nameField = uiCallbacks.createTextField("Name");
        TextField dateField = uiCallbacks.createTextField("Start Date (e.g., 26.09.2023)");

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            handleTypeBadgeSave(primaryStage, typeBadge, nameField.getText(), dateField.getText());
        });

        Button backButton = ButtonFactory.createBackButton(e -> prizeManager.showAddPrizesScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("Create Badge Type");

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                saveButton
        );

        uiCallbacks.createScene(layout, primaryStage);
    }

    private void handleTypeBadgeSave(Stage primaryStage, TypeBadge typeBadge, String name, String startDate) {
        if(name != null && !name.isEmpty() &&
                startDate != null && !startDate.isEmpty()) {

            typeBadge.setName(name);
            typeBadge.setStartDate(startDate);

            typeBadgeDataManager.saveTypeBadgeToFile(typeBadge, Constants.TYPES_BADGE_FILE);
            prizeManager.showAddPrizesScreen(primaryStage);
        }
    }
}
