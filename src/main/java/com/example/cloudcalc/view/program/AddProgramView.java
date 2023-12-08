package com.example.cloudcalc.view.program;

import com.example.cloudcalc.builder.GridPaneBuilder;
import com.example.cloudcalc.builder.fields.program.ProgramFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.util.TranslateUtils;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddProgramView implements Localizable, ProgramFieldUpdatable {
    private final ProgramController programController;
    private String title;
    private CheckBox countCheckBox;
    private CheckBox ignoreCheckBox;
    private CheckBox pdfCheckBox;
    private VBox layout;
    private HBox countBox;
    private HBox ignoreBox;
    private HBox pdfBox;
    TextField countField = new TextField();
    TextField ignoreField = new TextField();
    Button uploadPdfButton;
    String countTooltip;
    String ignoreTooltip;
    String pdfTooltip;
    GridPane gridPane;
    Label programNameLabel;
    TextField programNameField;
    Label labelDate;
    DatePicker startDatePicker;
    Label labelCheckBox;

    public AddProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        layout = new VBox(10);
        layout.getChildren().addAll(
                createTopLayout(stage),
                createFormLayout(),
                createCheckBoxSection(),
                createButtonsSection()
        );

        programController.createScene(layout, stage);
    }

    private HBox createTopLayout(Stage stage) {
        Button backButton = ButtonFactory.createBackButton(e -> programController.showScreen(stage));
        Label titleAddScreenLabel = new Label(TranslateUtils.getTranslate("addProgramTitle"));
        return programController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);
    }

    private GridPane createFormLayout() {
        programNameLabel = new Label(TranslateUtils.getTranslate("programNameLabel"));
        programNameField = new TextField();
        programNameField.setPromptText(TranslateUtils.getTranslate("programNameField"));

        labelDate = new Label(TranslateUtils.getTranslate("labelDate"));
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText(TranslateUtils.getTranslate("startDatePicker"));

        gridPane = new GridPane();
        GridPaneBuilder gridPaneBuilder = new GridPaneBuilder(programNameLabel, labelDate);
        gridPaneBuilder.setGridPaneSizes();

        gridPaneBuilder.settingGridPane(programNameField, startDatePicker);
        gridPaneBuilder.setHAlignmentGridPane(programNameLabel, labelDate);

        return gridPaneBuilder.getGridPane();
    }

    private VBox createCheckBoxSection() {
        labelCheckBox = new Label(TranslateUtils.getTranslate("labelCheckBox"));
        createBox();
        HBox checkBoxes = new HBox(10, countBox, ignoreBox, pdfBox);
        return new VBox(10, labelCheckBox, checkBoxes);
    }

    private VBox createButtonsSection() {
        Button addConditionButton = new Button(TranslateUtils.getTranslate("addConditionButton"));
        addConditionButton.setOnAction(e -> addCountCondition(layout));

        Button saveButton = new Button(TranslateUtils.getTranslate("saveButton"));
        Button cancelButton = new Button(TranslateUtils.getTranslate("cancelButton"));

        return new VBox(10, addConditionButton, saveButton, cancelButton);
    }

    private void createBox() {
        countCheckBox = new CheckBox(TranslateUtils.getTranslate("countCheckBox"));
        countBox = new HBox(5, countCheckBox, createInfoIcon(countTooltip));

        ignoreCheckBox = new CheckBox(TranslateUtils.getTranslate("ignoreCheckBox"));
        ignoreBox = new HBox(5, ignoreCheckBox, createInfoIcon(ignoreTooltip));

        pdfCheckBox = new CheckBox(TranslateUtils.getTranslate("pdfCheckBox"));
        pdfBox = new HBox(5, pdfCheckBox, createInfoIcon(pdfTooltip));
    }

    private HBox createInfoIcon(String tooltipText) {
        ImageView infoIcon = new ImageView(new Image(ButtonFactory.class.getResourceAsStream("/images/info-48.png")));
        infoIcon.setFitHeight(20);
        infoIcon.setFitWidth(20);
        infoIcon.setCursor(Cursor.HAND);

        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(infoIcon, tooltip);

        return new HBox(infoIcon);
    }

    private void addCountCondition(VBox layout) {
        HBox conditionBox = new HBox(10);

        if (countCheckBox.isSelected()) {
            conditionBox.getChildren().add(countField);
        }

        if (ignoreCheckBox.isSelected()) {
            conditionBox.getChildren().add(ignoreField);
        }

        if (pdfCheckBox.isSelected()) {
            conditionBox.getChildren().add(uploadPdfButton);
        }

        layout.getChildren().add(layout.getChildren().size() - 2, conditionBox);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addProgramTitle");

        countTooltip = bundle.getString("countTooltip");
        ignoreTooltip = bundle.getString("ignoreTooltip");
        pdfTooltip = bundle.getString("pdfTooltip");

        updateCountFieldPlaceholder(bundle.getString("countField"));
        updateIgnoreFieldPlaceholder(bundle.getString("ignoreField"));
    }

    @Override
    public void updateCountFieldPlaceholder(String placeholder) {
        if(countField != null) {
            countField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateIgnoreFieldPlaceholder(String placeholder) {
        if (ignoreField != null) {
            ignoreField.setPromptText(placeholder);
        }
    }
}