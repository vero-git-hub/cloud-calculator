package com.example.cloudcalc.view.program;

import com.example.cloudcalc.builder.fields.program.ProgramFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private String title = "ADD PROGRAM";
    private final CheckBox countCheckBox = new CheckBox("Specify badges for counting");
    private final CheckBox ignoreCheckBox = new CheckBox("Exclude badges");
    private final CheckBox pdfCheckBox = new CheckBox("Should PDF be taken into account?");
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
        Label titleAddScreenLabel = new Label(title);
        return programController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);
    }

    private GridPane createFormLayout() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        Label label = new Label("Program name: ");
        TextField programNameField = new TextField();
        programNameField.setPromptText("Enter your name");

        Label labelDate = new Label("Counting start date: ");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Enter date");

        gridPane.add(label, 0, 0);
        gridPane.add(programNameField, 1, 0);
        gridPane.add(labelDate, 0, 1);
        gridPane.add(startDatePicker, 1, 1);

        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setHalignment(labelDate, HPos.LEFT);

        return gridPane;
    }

    private VBox createCheckBoxSection() {
        Label labelCheckBox = new Label("Set up the counting condition: ");
        createBox();
        HBox checkBoxes = new HBox(10, countBox, ignoreBox, pdfBox);
        return new VBox(10, labelCheckBox, checkBoxes);
    }

    private VBox createButtonsSection() {
        Button addConditionButton = new Button("Add a counting condition");
        addConditionButton.setOnAction(e -> addCountCondition(layout));

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

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