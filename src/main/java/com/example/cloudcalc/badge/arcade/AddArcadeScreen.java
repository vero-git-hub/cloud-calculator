package com.example.cloudcalc.badge.arcade;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.util.Notification;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AddArcadeScreen implements ScreenDisplayable {

    private final ArcadeManager arcadeManager;

    public AddArcadeScreen(ArcadeManager arcadeManager) {
        this.arcadeManager = arcadeManager;
    }

    public void showScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> arcadeManager.getArcadeScreen().showScreen(primaryStage));
        Label titleLabel = arcadeManager.getUiCallbacks().createLabel("Add Arcade Badge");

        Button saveButton = ButtonFactory.createSaveArcadeButton(
                () -> {
                    List<String> badges = arcadeManager.getFileOperationManager().loadBadgesFromFile(Constants.ARCADE_FILE);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");
                    String badgeName = nameField.getText().trim();

                    if (badgeName.isEmpty()) {
                        Notification.showAlert("Input Error", "Empty Badge Name", "Please enter a valid badge name.");
                        return;
                    }

                    badges.add(nameField.getText());
                    arcadeManager.getFileOperationManager().saveBadgesToFile(badges, Constants.ARCADE_FILE);
                    arcadeManager.getArcadeScreen().showScreen(primaryStage);
                },
                () -> (TextField) layout.getScene().lookup("#nameField")
        );

        HBox topLayout = arcadeManager.getUiCallbacks().createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                arcadeManager.getUiCallbacks().createNameTextField(),
                saveButton
        );

        arcadeManager.getUiCallbacks().createScene(layout, primaryStage);
    }
}
