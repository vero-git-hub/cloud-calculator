package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.DailyStatController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.program.Program;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.event.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_code
 **/
public class DailyStatView {

    private final DailyStatController dailyStatController;
    private static final String DAILY_STAT_TITLE = "DAILY STAT";
    private static final String DAILY_STAT_TITLE_CHECKBOX = "Choose the profiles";
    private static final String DAILY_STAT_TITLE_TEMPLATE = "Result template for copy";
    private String textAreaPromptText = "Enter text here...";
    private String programsTitle = "Choose the programs";

    public DailyStatView(DailyStatController dailyStatController) {
        this.dailyStatController = dailyStatController;
    }

    public void showScreen(Stage stage) {
        VBox rootLayout = new VBox(10);

        SplitPane splitPane = new SplitPane();

        Button backButton = ButtonFactory.createBackButton(e -> dailyStatController.showMainScreen(stage));
        Label mainLabel = new Label(DAILY_STAT_TITLE);
        HBox headerBox = dailyStatController.createTopLayout(backButton, mainLabel);

        VBox leftLayout = new VBox(10);
        List<Profile> profiles = dailyStatController.getProfiles();
        Label profilesLabel = new Label(DAILY_STAT_TITLE_CHECKBOX);
        leftLayout.getChildren().add(profilesLabel);

        List<CheckBox> profileCheckBoxes = createCheckBoxes(profiles, leftLayout, Profile::getName);
        List<String> savedProfiles = dailyStatController.loadSelectedProfiles();
        setSelectedItems(profileCheckBoxes, savedProfiles);

        VBox rightLayout = new VBox(10);
        List<Program> programs = dailyStatController.getPrograms();
        Label programsLabel = new Label(programsTitle);
        rightLayout.getChildren().add(programsLabel);

        List<CheckBox> programsCheckBoxes = createCheckBoxes(programs, rightLayout, Program::getName);
        List<String> savedPrograms = dailyStatController.loadSelectedPrograms();
        setSelectedItems(programsCheckBoxes, savedPrograms);

        splitPane.getItems().addAll(leftLayout, rightLayout);
        splitPane.setDividerPositions(0.5);

        VBox bottomLayout = new VBox(10);

        TextArea textArea = new TextArea();
        setTemplate(textArea);
        textArea.setPromptText(textAreaPromptText);

        Button saveButton = createSaveButton(profileCheckBoxes, programsCheckBoxes);
        Button scanButton = createScanButton(profileCheckBoxes, programsCheckBoxes);
        HBox buttonsBox = new HBox(10, saveButton, scanButton);

        Button saveTemplateButton = createSaveTemplateButton(textArea);
        Button copyButton = createCopyButton(textArea);
        HBox templateButtonsBox = new HBox(10, saveTemplateButton, copyButton);

        Label templateLabel = new Label(DAILY_STAT_TITLE_TEMPLATE);
        bottomLayout.getChildren().addAll(buttonsBox, templateLabel, textArea, templateButtonsBox);

        rootLayout.getChildren().addAll(headerBox, splitPane, bottomLayout);

        dailyStatController.createScene(rootLayout, stage);
    }

    private void setTemplate(TextArea textArea) {
        String template = dailyStatController.loadTemplate();
        if (template != null) {
            textArea.setText(template);
        }
    }

    private void setSelectedItems(List<CheckBox> checkBoxes, List<String> savedItems) {
        for (CheckBox checkBox : checkBoxes) {
            if (savedItems.contains(checkBox.getText())) {
                checkBox.setSelected(true);
            }
        }
    }

    private Button createSaveTemplateButton(TextArea textArea) {
        EventHandler<ActionEvent> action = e -> {
            String text = textArea.getText();
            dailyStatController.saveTemplate(text);
        };
        return ButtonFactory.createSaveButton(action);
    }

    private Button createCopyButton(TextArea textArea) {
        EventHandler<ActionEvent> copyAction = e -> {
            String text = textArea.getText();
            copyToClipboard(text);
        };

        return ButtonFactory.createCopyButton(copyAction);
    }

    private void copyToClipboard(String text) {
        final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        final javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private Button createScanButton(List<CheckBox> checkBoxes, List<CheckBox> programsCheckBoxes) {
        // TODO: scan selected profiles (from checkBoxes)
        EventHandler<ActionEvent> action = e -> {
            List<String> selectedProfiles = getSelectedProfiles(checkBoxes);
            List<String> selectedPrograms = getSelectedPrograms(programsCheckBoxes);
            dailyStatController.scanProfiles(selectedProfiles, selectedPrograms);
        };
        return ButtonFactory.createScanButton(action);
    }

    private List<String> getSelectedPrograms(List<CheckBox> checkBoxes) {
        return checkBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    private Button createSaveButton(List<CheckBox> checkBoxes, List<CheckBox> programsCheckBoxes) {
        EventHandler<ActionEvent> action = e -> {
            List<String> selectedProfiles = getSelectedProfiles(checkBoxes);
            List<String> selectedPrograms = getSelectedPrograms(programsCheckBoxes);
            dailyStatController.saveSelectedProfiles(selectedProfiles);
            dailyStatController.saveSelectedPrograms(selectedPrograms);
        };
        return ButtonFactory.createSaveButton(action);
    }

    private List<String> getSelectedProfiles(List<CheckBox> checkBoxes) {
        return checkBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    private <T> List<CheckBox> createCheckBoxes(List<T> items, VBox layout, Function<T, String> nameExtractor) {
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (T item : items) {
            CheckBox checkBox = new CheckBox(nameExtractor.apply(item));
            checkBoxes.add(checkBox);
            layout.getChildren().add(checkBox);
        }
        return checkBoxes;
    }
}