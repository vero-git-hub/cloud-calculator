package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.DailyStatController;
import com.example.cloudcalc.entity.Profile;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_code
 **/
public class DailyStatView {

    private final DailyStatController dailyStatController;
    private static final String DAILY_STAT_TITLE = "DAILY STAT";
    private static final String DAILY_STAT_TITLE_CHECKBOX = "Choose the profiles";
    private static final String DAILY_STAT_TITLE_TEMPLATE = "Result template for copy";

    public DailyStatView(DailyStatController dailyStatController) {
        this.dailyStatController = dailyStatController;
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);
        List<Profile> profiles = dailyStatController.getProfiles();

        Button backButton = ButtonFactory.createBackButton(e -> dailyStatController.showMainScreen(stage));
        Label label = new Label(DAILY_STAT_TITLE);
        Label labelCheckbox = new Label(DAILY_STAT_TITLE_CHECKBOX);
        Label labelTemplate = new Label(DAILY_STAT_TITLE_TEMPLATE);
        layout.getChildren().addAll(
                dailyStatController.createTopLayout(backButton, label),
                labelCheckbox
        );

        List<CheckBox> checkBoxes = createCheckBoxes(profiles, layout);

        Button saveButton = createSaveButton();
        Button scanButton = createScanButton();
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(saveButton, scanButton);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter text here...");

        Button saveTemplateButton = createSaveTemplateButton();
        Button copyButton = createCopyButton(textArea);
        HBox hBoxTemplate = new HBox(10);
        hBoxTemplate.getChildren().addAll(saveTemplateButton, copyButton);

        layout.getChildren().addAll(hBox, labelTemplate, textArea, hBoxTemplate);
        dailyStatController.createScene(layout, stage);
    }

    private Button createSaveTemplateButton() {
        // TODO: save template (from textArea)
        return new Button();
    }

    private Button createCopyButton(TextArea textArea) {
        // TODO: add icon to button
        Button copyButton = new Button("Copy text");
        copyButton.setOnAction(e -> {
            String text = textArea.getText();
            copyToClipboard(text);
        });
        return copyButton;
    }

    private void copyToClipboard(String text) {
        final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        final javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private Button createScanButton() {
        // TODO: scan selected profiles (from checkBoxes)
        return new Button();
    }

    private Button createSaveButton() {
        // TODO: save checkBoxes (for next time)
        return new Button();
    }

    private List<CheckBox> createCheckBoxes(List<Profile> profiles, VBox layout) {
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Profile profile : profiles) {
            CheckBox checkBox = new CheckBox(profile.getName());
            checkBoxes.add(checkBox);
            layout.getChildren().add(checkBox);
        }
        return checkBoxes;
    }
}