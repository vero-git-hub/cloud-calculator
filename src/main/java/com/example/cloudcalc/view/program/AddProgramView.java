package com.example.cloudcalc.view.program;

import com.example.cloudcalc.builder.GridPaneBuilder;
import com.example.cloudcalc.builder.fields.program.ProgramFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.model.CountConditionModel;
import com.example.cloudcalc.util.Notification;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    Label labelAddCondition = new Label();
    Button saveButton = new Button();
    Button cancelButton = new Button();
    private TableView<CountConditionModel> conditionsTable;
    Stage modalStage;
    Button modalUploadPdfButton = new Button();
    Button saveConditionButton = new Button();
    Button closeConditionButton = new Button();
    private Label countLabel = new Label("What to count (" +
            "separated by commas):");
    private Label ignoreLabel = new Label("What not to count (" +
            "separated by commas):");
    private Label pdfLabel = new Label("Download PDF:");
    Button createButton = new Button();

    public AddProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);
        initializeConditionsTable();
    }

    public void showScreen(Stage stage) {
        resetForm();
        layout = new VBox(10);
        layout.getChildren().addAll(
                createTopLayout(stage),
                createFormLayout(),
                createCheckBoxSection(),
                createAddConditionButton(),
                conditionsTable,
                createButtonsSection()
        );

        programController.createScene(layout, stage);
    }

    private void resetForm() {
        programNameField.clear();
        startDatePicker.setValue(null);
        conditionsTable.getItems().clear();
        countCheckBox.setSelected(false);
        ignoreCheckBox.setSelected(false);
        pdfCheckBox.setSelected(false);
    }

    private HBox createTopLayout(Stage stage) {
        Button backButton = ButtonFactory.createBackButton(e -> programController.showScreen(stage));
        return programController.createTopLayoutForAddScreen(backButton, titleAddScreenLabel);
    }

    private HBox createForm() {
        return new HBox(10, labelName, programNameField, labelDate, startDatePicker);
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

    private HBox createAddConditionButton() {
        createButton = ButtonFactory.createAddButton(e -> showAddConditionModal());
        labelAddCondition.setAlignment(Pos.CENTER);
        HBox hbox = new HBox(10, labelAddCondition, createButton);
        hbox.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(labelAddCondition, Priority.ALWAYS);
        return hbox;
    }

    private HBox createButtonsSection() {
        saveButton.setOnAction(event -> {
            String programName = programNameField.getText();
            LocalDate selectedDate = startDatePicker.getValue();

            if (programName == null || programName.trim().isEmpty()) {
                Notification.showAlert("Empty field", "The program name field must not be empty", "Please enter the program name");
            } else if (selectedDate == null) {
                Notification.showAlert("Empty field", "No date selected.", "Please select a date.");
            } else if (isConditionsTableEmpty()) {
                Notification.showAlert("Empty table", "No conditions.", "Please add a condition.");
            }
            else {
                Program program = new Program();
                program.setName(programName);
                program.setDate(selectedDate);
                List<CountConditionModel> list = new ArrayList<>();

                for (CountConditionModel condition : conditionsTable.getItems()) {
                    list.add(condition);
                }

                program.setConditions(list);
                saveProgram(program);
            }
        });
        return new HBox(10, saveButton, cancelButton);
    }

    private boolean isConditionsTableEmpty() {
        return conditionsTable.getItems().isEmpty();
    }

    private void saveProgram(Program program) {
        programController.saveProgram(program);
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

    private void showAddConditionModal() {
        if (countCheckBox.isSelected() || ignoreCheckBox.isSelected() || pdfCheckBox.isSelected()) {
            modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Add condition");

            VBox modalLayout = new VBox(10);
            modalLayout.setPadding(new Insets(10));

            TextField modalCountField = new TextField();
            TextField modalIgnoreField = new TextField();
            modalUploadPdfButton = new Button("Download PDF");

            if (countCheckBox.isSelected()) {
                modalLayout.getChildren().add(new HBox(countLabel, modalCountField));
            }
            if (ignoreCheckBox.isSelected()) {
                modalLayout.getChildren().add(new HBox(ignoreLabel, modalIgnoreField));
            }
            if (pdfCheckBox.isSelected()) {
                modalLayout.getChildren().add(new HBox(pdfLabel, modalUploadPdfButton));
            }

            saveConditionButton.setText("Save");
            saveConditionButton.setOnAction(e -> {
                if (countCheckBox.isSelected()) {
                    String value = modalCountField.getText();
                    CountConditionModel newCondition = new CountConditionModel("What to count", value);
                    conditionsTable.getItems().add(newCondition);
                }
                if (ignoreCheckBox.isSelected()) {
                    String value = modalIgnoreField.getText();
                    CountConditionModel newCondition = new CountConditionModel("What not to count", value);
                    conditionsTable.getItems().add(newCondition);
                }
                if (pdfCheckBox.isSelected()) {

                }

                modalStage.close();
            });

            closeConditionButton.setText("Close");
            closeConditionButton.setOnAction(e -> modalStage.close());

            HBox buttonLayout = new HBox(10, saveConditionButton, closeConditionButton);
            buttonLayout.setAlignment(Pos.CENTER);

            modalLayout.getChildren().add(buttonLayout);

            Scene modalScene = new Scene(modalLayout, 300, 200);
            modalStage.setScene(modalScene);
            modalStage.showAndWait();
        } else {
            Notification.showAlert("Info", "Counting condition not selected", "Please select counting condition");
        }
    }

    private void initializeConditionsTable() {
        conditionsTable = new TableView<>();

        TableColumn<CountConditionModel, Number> indexColumn = new TableColumn<>("№");
        indexColumn.setSortable(false);
        indexColumn.setMinWidth(30);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(conditionsTable.getItems().indexOf(column.getValue()) + 1));
        indexColumn.setCellFactory(column -> new TableCell<CountConditionModel, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        TableColumn<CountConditionModel, String> typeColumn = new TableColumn<>("Condition type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<CountConditionModel, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<CountConditionModel, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setMinWidth(40);

        Callback<TableColumn<CountConditionModel, Void>, TableCell<CountConditionModel, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<CountConditionModel, Void> call(final TableColumn<CountConditionModel, Void> param) {
                final TableCell<CountConditionModel, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("X");

                    {
                        btn.setOnAction(event -> {
                            CountConditionModel data = getTableView().getItems().get(getIndex());
                            conditionsTable.getItems().remove(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        deleteColumn.setCellFactory(cellFactory);
        conditionsTable.getColumns().addAll(indexColumn, typeColumn, valueColumn, deleteColumn);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleAddScreenLabel.setText(bundle.getString("addProgramTitle"));

        labelName.setText(bundle.getString("programNameLabel"));
        labelDate.setText(bundle.getString("labelDate"));
        labelCheckBox.setText(bundle.getString("labelCheckBox"));

        labelAddCondition.setText(bundle.getString("addConditionButton"));
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