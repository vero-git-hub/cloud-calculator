package com.example.cloudcalc.badge.ignored;

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

public class AddIgnoreScreen implements ScreenDisplayable {
    private final IgnoreManager ignoreManager;

    public AddIgnoreScreen(IgnoreManager ignoreManager) {
        this.ignoreManager = ignoreManager;
    }

    public void showScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> ignoreManager.getIgnoreScreen().showScreen(primaryStage));
        Label titleLabel = ignoreManager.getUiCallbacks().createLabel("Add Ignore Badge");

        Button saveIgnoreBadgeButton = ButtonFactory.createSaveIgnoreBadgeButton(
                () -> {
                    List<String> ignoredBadges = ignoreManager.getFileOperationManager().loadBadgesFromFile(Constants.IGNORE_FILE);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");

                    String badgeName = nameField.getText().trim();
                    if (badgeName.isEmpty()) {
                        Notification.showAlert("Input Error", "Empty Badge Name", "Please enter a valid badge name.");
                        return;
                    }

                    ignoredBadges.add(nameField.getText());
                    ignoreManager.getFileOperationManager().saveBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                    ignoreManager.getIgnoreScreen().showScreen(primaryStage);
                },
                () -> (TextField) layout.getScene().lookup("#nameField")
        );

        HBox topLayout = ignoreManager.getUiCallbacks().createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                ignoreManager.getUiCallbacks().createNameTextField(),
                saveIgnoreBadgeButton
        );

        ignoreManager.getUiCallbacks().createScene(layout, primaryStage);
    }
}
