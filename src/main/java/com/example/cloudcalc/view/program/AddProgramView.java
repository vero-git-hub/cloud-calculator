package com.example.cloudcalc.view.program;

import com.example.cloudcalc.builder.GridPaneBuilder;
import com.example.cloudcalc.builder.fields.program.ProgramFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
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
    private CheckBox countCheckBox = new CheckBox();
    private CheckBox ignoreCheckBox = new CheckBox();
    private CheckBox pdfCheckBox = new CheckBox();
    private VBox layout;
    private HBox countBox;
    private HBox ignoreBox;
    private HBox pdfBox;
    TextField countField = new TextField();
    TextField ignoreField = new TextField();
    Button uploadPdfButton = new Button();
    String countTooltip;
    String ignoreTooltip;
    String pdfTooltip;
    GridPane gridPane;
    Label labelName = new Label();
    TextField programNameField = new TextField();
    Label labelDate = new Label();
    DatePicker startDatePicker = new DatePicker();
    Label labelCheckBox = new Label();
    Label titleAddScreenLabel = new Label();
    Button addConditionButton = new Button();
    Button saveButton = new Button();
    Button cancelButton = new Button();

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

        return programController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);
    }

    private GridPane createFormLayout() {
        gridPane = new GridPane();
        GridPaneBuilder gridPaneBuilder = new GridPaneBuilder(labelName, labelDate);
        gridPaneBuilder.setGridPaneSizes();

        gridPaneBuilder.settingGridPane(programNameField, startDatePicker);
        gridPaneBuilder.setHAlignmentGridPane(labelName, labelDate);

        return gridPaneBuilder.getGridPane();
    }

    private VBox createCheckBoxSection() {
        createBox();
        HBox checkBoxes = new HBox(10, countBox, ignoreBox, pdfBox);
        return new VBox(10, labelCheckBox, checkBoxes);
    }

    private VBox createButtonsSection() {
        addConditionButton.setOnAction(e -> addCountCondition(layout));

        return new VBox(10, addConditionButton, saveButton, cancelButton);
    }

    private void createBox() {
        countBox = new HBox(5, countCheckBox, createInfoIcon(countTooltip));

        ignoreBox = new HBox(5, ignoreCheckBox, createInfoIcon(ignoreTooltip));

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
        titleAddScreenLabel.setText(bundle.getString("addProgramTitle"));

        labelName.setText(bundle.getString("programNameLabel"));
//        programNameField.setPromptText(TranslateUtils.getTranslate("programNameField"));
        labelDate.setText(bundle.getString("labelDate"));

//        startDatePicker.setPromptText(TranslateUtils.getTranslate("startDatePicker"));
        labelCheckBox.setText(bundle.getString("labelCheckBox"));


        addConditionButton.setText(bundle.getString("addConditionButton"));
        countCheckBox.setText(bundle.getString("countCheckBox"));
        ignoreCheckBox.setText(bundle.getString("ignoreCheckBox"));
        pdfCheckBox.setText(bundle.getString("pdfCheckBox"));


        countTooltip = bundle.getString("countTooltip");
        ignoreTooltip = bundle.getString("ignoreTooltip");
        pdfTooltip = bundle.getString("pdfTooltip");

        uploadPdfButton.setText(bundle.getString("pdfCheckBox"));

        saveButton.setText(bundle.getString("saveButton"));
        cancelButton.setText(bundle.getString("cancelButton"));

        updateCountFieldPlaceholder(bundle.getString("countField"));
        updateIgnoreFieldPlaceholder(bundle.getString("ignoreField"));
        updateProgramNameFieldPlaceholder(bundle.getString("programNameField"));
        updateDateFieldPlaceholder(bundle.getString("startDatePicker"));
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

    @Override
    public void updateProgramNameFieldPlaceholder(String placeholder) {
        if (programNameField != null) {
            programNameField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        if (startDatePicker != null) {
            startDatePicker.setPromptText(placeholder);
        }
    }
}