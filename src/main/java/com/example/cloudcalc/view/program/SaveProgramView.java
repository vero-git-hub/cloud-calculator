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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SaveProgramView implements Localizable, ProgramFieldUpdatable {
    private final ProgramController programController;
    private VBox layout;
    private HBox countBox;
    private HBox ignoreBox;
    TextField countField = new TextField();
    TextField ignoreField = new TextField();
    String countTooltip;
    String ignoreTooltip;
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
    Button saveConditionButton = new Button();
    Button closeConditionButton = new Button();
    private Label countLabel = new Label("What to count (" +
            "separated by commas):");
    private Label ignoreLabel = new Label("What not to count (" +
            "separated by commas):");
    private Label subtitleLabel = new Label("If you can get more than 1 prize, create another program. Does not apply to Arcade.");
    Button createButton = new Button();
    private RadioButton countRadioButton = new RadioButton("Count");
    private RadioButton ignoreRadioButton = new RadioButton("Ignore");
    private ToggleGroup radioGroup = new ToggleGroup();

    public SaveProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);
        initializeConditionsTable();
    }

    public Label getSubtitleLabel() {
        return subtitleLabel;
    }

    public void showScreen(Stage stage) {
        resetForm();
        layout = new VBox(10);
        layout.getChildren().addAll(
                createTopLayout(stage),
                programController.createSubtitleLabel(),
                createFormLayout(),
                createCheckBoxSection(),
                createAddConditionButton(),
                conditionsTable,
                createButtonsSection(stage)
        );

        programController.createScene(layout, stage);
    }

    private void resetForm() {
        programNameField.clear();
        startDatePicker.setValue(null);
        conditionsTable.getItems().clear();
        radioGroup.selectToggle(null);
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
        HBox radioButtons = new HBox(10, countBox, ignoreBox);
        return new VBox(10, labelCheckBox, radioButtons);
    }

    private void createBox() {
        countRadioButton.setToggleGroup(radioGroup);
        ignoreRadioButton.setToggleGroup(radioGroup);

        countBox = new HBox(5, countRadioButton, createInfoIcon(countTooltip));
        ignoreBox = new HBox(5, ignoreRadioButton, createInfoIcon(ignoreTooltip));
    }

    private HBox createAddConditionButton() {
        createButton = ButtonFactory.createAddButton(e -> showAddConditionModal());
        labelAddCondition.setAlignment(Pos.CENTER);
        HBox hbox = new HBox(10, labelAddCondition, createButton);
        hbox.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(labelAddCondition, Priority.ALWAYS);
        return hbox;
    }

    private HBox createButtonsSection(Stage stage) {
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
                saveProgram(stage, program);
            }
        });
        return new HBox(10, saveButton, cancelButton);
    }

    private boolean isConditionsTableEmpty() {
        return conditionsTable.getItems().isEmpty();
    }

    private void saveProgram(Stage stage, Program program) {
        programController.saveProgram(stage, program);
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
        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();

        if (selectedRadioButton != null) {
            modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Add condition");

            VBox modalLayout = new VBox(10);
            modalLayout.setPadding(new Insets(10));

            TextField modalTextField = new TextField();
            Label modalLabel = new Label();

            if (selectedRadioButton == countRadioButton) {
                modalLabel.setText("What to count:");
                modalLayout.getChildren().add(new HBox(modalLabel, modalTextField));
            } else if (selectedRadioButton == ignoreRadioButton) {
                modalLabel.setText("What not to count:");
                modalLayout.getChildren().add(new HBox(modalLabel, modalTextField));
            }

            saveConditionButton.setText("Save");
            saveConditionButton.setOnAction(e -> {
                String value = modalTextField.getText();
                String conditionType = modalLabel.getText();

                if (!value.isEmpty()) {
                    CountConditionModel newCondition = new CountConditionModel(conditionType, value);
                    conditionsTable.getItems().add(newCondition);
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

    public void showEditProgramScreen(Stage stage, Program program) {
        resetForm();
        layout = new VBox(10);

        programNameField.setText(program.getName());
        startDatePicker.setValue(program.getDate());

        setRadioButtonSelection(program);

        conditionsTable.getItems().addAll(program.getConditions());

        layout.getChildren().addAll(
                createTopLayout(stage),
                createFormLayout(),
                createCheckBoxSection(),
                createAddConditionButton(),
                conditionsTable,
                createButtonsSection(stage)
        );

        saveButton.setOnAction(event -> {
            String programName = programNameField.getText();
            LocalDate selectedDate = startDatePicker.getValue();

            if (validateInput(programName, selectedDate)) {
                updateExistingProgram(stage, program, programName, selectedDate);
            }
        });

        programController.createScene(layout, stage);
    }

    private void setRadioButtonSelection(Program program) {
        if (program.getConditions().stream().anyMatch(cond -> "What to count".equals(cond.getType()))) {
            countRadioButton.setSelected(true);
        } else if (program.getConditions().stream().anyMatch(cond -> "What not to count".equals(cond.getType()))) {
            ignoreRadioButton.setSelected(true);
        }
    }

    private void updateExistingProgram(Stage stage, Program program, String programName, LocalDate selectedDate) {
        program.setName(programName);
        program.setDate(selectedDate);

        List<CountConditionModel> list = new ArrayList<>(conditionsTable.getItems());
        program.setConditions(list);

        programController.saveProgram(stage, program);
    }

    private boolean validateInput(String programName, LocalDate selectedDate) {
        if (programName == null || programName.trim().isEmpty()) {
            Notification.showAlert("Empty field", "The program name field must not be empty", "Please enter the program name");
            return false;
        } else if (selectedDate == null) {
            Notification.showAlert("Empty field", "No date selected.", "Please select a date.");
            return false;
        } else if (isConditionsTableEmpty()) {
            Notification.showAlert("Empty table", "No conditions.", "Please add a condition.");
            return false;
        }
        return true;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleAddScreenLabel.setText(bundle.getString("addProgramTitle"));

        labelName.setText(bundle.getString("programNameLabel"));
        labelDate.setText(bundle.getString("labelDate"));
        labelCheckBox.setText(bundle.getString("labelCheckBox"));

        labelAddCondition.setText(bundle.getString("addConditionButton"));
        countRadioButton.setText(bundle.getString("countCheckBox"));
        ignoreRadioButton.setText(bundle.getString("ignoreCheckBox"));

        countTooltip = bundle.getString("countTooltip");
        ignoreTooltip = bundle.getString("ignoreTooltip");

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