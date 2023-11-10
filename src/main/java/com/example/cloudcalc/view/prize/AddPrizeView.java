package com.example.cloudcalc.view.prize;

import com.example.cloudcalc.badge.type.TypeBadge;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AddPrizeView {
    private final PrizeController prizeController;
    private String title = "ADD PRIZE";
    private ComboBox<String> badgeTypeComboBox = new ComboBox<>();

    public AddPrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;
        badgeTypeComboBox.setPromptText("Select badge type");
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

        HBox typeLayout = new HBox();
        typeLayout.setAlignment(Pos.CENTER);

        List<TypeBadge> typeBadgeList = prizeController.loadTypesBadgeFromFile();
        typeBadgeList.forEach(typeBadge -> badgeTypeComboBox.getItems().add(typeBadge.getName()));

        Button addButton = ButtonFactory.createAddButton(e -> prizeController.showAddTypeBadgeScreen(stage));

        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        typeLayout.getChildren().addAll(badgeTypeComboBox, addButton, rightSpacer);

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            prizeController.savePrize(stage, badgeTypeComboBox.getValue());
        });

        Button backButton = ButtonFactory.createBackButton(e -> prizeController.showScreen(stage));

        HBox topLayout = prizeController.createTopLayoutForAddScreen(backButton, new Label(title));

        layout.getChildren().addAll(
                topLayout,
                prizeController.getNamePrizeField(),
                prizeController.getBadgeCountField(),
                typeLayout,
                saveButton
        );

        prizeController.createScene(layout, stage);
    }
}
