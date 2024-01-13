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
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SaveProgramView implements Localizable, ProgramFieldUpdatable {
    private final ProgramController programController;
    private VBox layout;
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
    private Label subtitleLabel = new Label("If you can get more than 1 prize, create another program. Does not apply to Arcade.");
    Button createButton = new Button();
    private ComboBox<String> conditionTypeComboBox;
    private TextField badgeNameField;
    private Button addBadgeButton;
    private VBox badgeListContainer;
    private final String typeTitle = "Type:";
    private final String badgeTitle = "Badge:";

    public SaveProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);

        initializeBadgeEntryFields();
        initializeConditionTypeComboBox();
        initializeConditionsTable();
    }

    private void addBadgeToList() {
        String badgeName = badgeNameField.getText();
        String conditionType = conditionTypeComboBox.getValue();

        if (!badgeName.isEmpty() && conditionType != null) {
            CountConditionModel condition = new CountConditionModel(conditionType, Arrays.asList(badgeName));
            conditionsTable.getItems().add(condition);
            badgeNameField.clear();
        }
    }

    private void initializeBadgeEntryFields() {
        badgeNameField = new TextField();
        badgeNameField.setPromptText("Enter Badge Name");
        addBadgeButton = new Button("Add Badge");
        addBadgeButton.setOnAction(e -> addBadgeToList());
    }

    private void initializeConditionTypeComboBox() {
        List<String> conditionTypes = Arrays.asList("What to count", "What not to count");
        conditionTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(conditionTypes));

        conditionTypeComboBox.setPromptText("Select Condition Type");

        conditionTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                badgeNameField.setDisable(false);
            }
        });

        conditionTypeComboBox.setOnAction(e -> {
            if (conditionTypeComboBox.getValue() != null) {
                conditionTypeComboBox.setDisable(true);
            }
        });

        badgeNameField.setDisable(true);
    }

    private void resetConditionTypeComboBox() {
        conditionTypeComboBox.setValue(null);
        conditionTypeComboBox.setPromptText("Select Condition Type");
        conditionTypeComboBox.setDisable(false);

        conditionTypeComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty || item == null) {
                    setText("Select Subject");
                } else {
                    setText(item);
                }
            }
        });

        badgeNameField.setDisable(true);
    }

    public Label getSubtitleLabel() {
        return subtitleLabel;
    }

    public void showScreen(Stage stage) {
        resetConditionTypeComboBox();
        resetForm();
        layout = new VBox(10);

        HBox badgeEntryLayout = new HBox(10);
        badgeEntryLayout.getChildren().addAll(
                new Label(typeTitle), conditionTypeComboBox,
                new Label(badgeTitle), badgeNameField,
                addBadgeButton
        );

        layout.getChildren().addAll(
                createTopLayout(stage),
                programController.createSubtitleLabel(),
                createFormLayout(),
                badgeEntryLayout,
                conditionsTable,
                createButtonsSection(stage)
        );

        programController.createScene(layout, stage);
    }

    private void resetForm() {
        programNameField.clear();
        startDatePicker.setValue(null);
        conditionsTable.getItems().clear();
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

                List<String> badges = new ArrayList<>();
                for (CountConditionModel conditionModel : conditionsTable.getItems()) {
                    badges.addAll(conditionModel.getValues());
                }

                CountConditionModel condition = new CountConditionModel();
                condition.setType(conditionTypeComboBox.getValue());
                condition.setValues(badges);
                program.setCondition(condition);

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

    private void showAddConditionModal() {
        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Add condition");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(10));

        modalLayout.getChildren().addAll(new HBox(10, new Label("Condition Type:"), conditionTypeComboBox), badgeNameField, addBadgeButton, badgeListContainer);

        Button saveConditionButton = new Button("Save Condition");
        saveConditionButton.setOnAction(e -> saveCondition());
        modalLayout.getChildren().add(saveConditionButton);

        Scene modalScene = new Scene(modalLayout, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }

    private void saveCondition() {
        List<String> badges = badgeListContainer.getChildren().stream()
                .map(node -> ((Label) node).getText())
                .collect(Collectors.toList());

        if (!badges.isEmpty()) {
            String conditionType = conditionTypeComboBox.getValue();
            CountConditionModel condition = new CountConditionModel(conditionType, badges);
            conditionsTable.getItems().add(condition);
            badgeListContainer.getChildren().clear();
        }
        modalStage.close();
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

        TableColumn<CountConditionModel, String> valueColumn = new TableColumn<>("Values");
        valueColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                String.join(", ", cellData.getValue().getValues())
        ));

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

        layout.getChildren().addAll(
                createTopLayout(stage),
                createFormLayout(),
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

    private void updateExistingProgram(Stage stage, Program program, String programName, LocalDate selectedDate) {
        program.setName(programName);
        program.setDate(selectedDate);

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