package com.example.cloudcalc.view.program;

import com.example.cloudcalc.builder.GridPaneBuilder;
import com.example.cloudcalc.builder.fields.program.ProgramFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.entity.program.CountCondition;
import com.example.cloudcalc.entity.program.Program;
import com.example.cloudcalc.entity.program.SpecialConditions;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.util.Notification;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SaveProgramView implements Localizable, ProgramFieldUpdatable {
    private final ProgramController programController;
    private VBox layout;
    GridPane gridPane;
    Label labelName = new Label();
    TextField programNameField = new TextField();
    Label labelDate = new Label();
    DatePicker startDatePicker = new DatePicker();
    Label titleAddScreenLabel = new Label();
    Button saveButton = new Button();
    Button cancelButton = new Button();
    private TableView<CountCondition> conditionsTable;
    //private Label subtitleLabel = new Label("Doesn't count copies.");
    private ComboBox<String> conditionTypeComboBox;
    private TextField badgeNameField;
    private TextField badgePointsField;
    private Button addBadgeButton;
    private Button specialConditionButton;
    private Program currentProgram;
    //private final String typeTitle = "Type:";
    //private final String badgeTitle = "Badge:";

    public SaveProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);

        initializeBadgeEntryFields();
        initializeConditionTypeComboBox();
        initializeConditionsTable();
    }

    private void initializeBadgeEntryFields() {
        badgeNameField = new TextField();
        badgeNameField.setPromptText("Enter Badge Name");
        addBadgeButton = new Button("Add Badge");
        addBadgeButton.setOnAction(e -> addBadgeToList());

        badgePointsField = new TextField();
        badgePointsField.setPromptText("Enter Points");

        specialConditionButton = new Button("*");
        specialConditionButton.setOnAction(e -> openSpecialConditions());
    }

    /**
     * Show Configure Special Conditions screen
     */
    private void openSpecialConditions() {
        Stage specialConditionsStage = new Stage();
        specialConditionsStage.setTitle("Special Conditions Configuration");

        // Create an interface for the window
        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.setSpacing(8);

        Label instructions = new Label("Configure your special badge counting conditions here:");
        // Adding interface elements for configuring conditions

        // Fields for entering values
        Label nLabel = new Label("Each [n] badges:");
        TextField nField = new TextField();
        nField.setPromptText("Enter the number of badges");

        Label xLabel = new Label("Equals [x] points:");
        TextField xField = new TextField();
        xField.setPromptText("Enter the number of points");

        // Field for loading a file of icons that do not need to be taken into account
        Label ignoreBadgesLabel = new Label("Upload badges to ignore (txt file):");
        Button uploadButton = new Button("Upload File");
        Label fileLabel = new Label();

        List<String> badgesToIgnore = new ArrayList<>();

        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(specialConditionsStage);
            if (file != null) {
                fileLabel.setText("File selected: " + file.getName());
                badgesToIgnore.clear();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        badgesToIgnore.add(line.trim());
                    }
                } catch (IOException e) {
                    Notification.showErrorMessage("File Error", "Error reading file. Please try again.");
                }
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                int n = Integer.parseInt(nField.getText());
                int x = Integer.parseInt(xField.getText());

                // Saving special conditions to a Program object
                SpecialConditions specialConditions = new SpecialConditions();
                specialConditions.setBadgesToIgnore(badgesToIgnore);
                specialConditions.setBadgesPerPoint(n);
                specialConditions.setPointsPerBadge(x);
                currentProgram.setSpecialConditions(specialConditions);

                specialConditionsStage.close();
            } catch (NumberFormatException e) {
                Notification.showErrorMessage("Invalid input", "Please enter valid numbers for badges and points.");
            }
        });

        layout.getChildren().addAll(instructions, nLabel, nField, xLabel, xField, ignoreBadgesLabel, uploadButton, fileLabel, saveButton);

        Scene scene = new Scene(layout, 400, 300);
        specialConditionsStage.setScene(scene);
        specialConditionsStage.show();
    }

    /**
     * Add badge&points in table on the Add Program screen
     */
    private void addBadgeToList() {
        String badgeName = badgeNameField.getText();
        String conditionType = conditionTypeComboBox.getValue();
        String pointsString = badgePointsField.getText();

        int points;
        try {
            points = Integer.parseInt(pointsString);
        } catch (NumberFormatException e) {
            showError("Invalid points value. Please enter a valid number.");
            return;
        }

        if (!badgeName.isEmpty() && conditionType != null) {
            // Create an instance of ValueWithPoints
            CountCondition.Badges badges = new CountCondition.Badges(badgeName, points);

            // Create a CountConditionModel instance with a type and a list of values with points
            CountCondition condition = new CountCondition(
                    conditionType,
                    List.of(badges)
            );

            // Add to table
            conditionsTable.getItems().add(condition);

            // Clear the input field
            badgeNameField.clear();
            badgePointsField.clear();
        } else {
            showError("Please fill in all fields.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

//    public Label getSubtitleLabel() {
//        return subtitleLabel;
//    }

    public void showScreen(Stage stage) {
        resetConditionTypeComboBox();
        resetForm();
        layout = new VBox(10);
        currentProgram = new Program();

        HBox badgeEntryLayout = new HBox(10);
        badgeEntryLayout.getChildren().addAll(
                //new Label(typeTitle),
                conditionTypeComboBox,
                //new Label(badgeTitle),
                badgeNameField,
                badgePointsField,
                addBadgeButton,
                specialConditionButton
        );

        layout.getChildren().addAll(
                createTopLayout(stage),
                //programController.createSubtitleLabel(),
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
                currentProgram.setName(programName);
                currentProgram.setDate(selectedDate);

                List<CountCondition> countConditions = new ArrayList<>();

                for (CountCondition conditionModel : conditionsTable.getItems()) {
                    List<CountCondition.Badges> valuesWithPoints = conditionModel.getValues().stream()
                            .map(vp -> new CountCondition.Badges(vp.getTitle(), vp.getPoints()))
                            .collect(Collectors.toList());

                    CountCondition condition = new CountCondition();
                    condition.setType(conditionModel.getType());
                    condition.setValues(valuesWithPoints);
                    countConditions.add(condition);
                }

                currentProgram.setConditions(countConditions);

                saveProgram(stage, currentProgram);
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

    /**
     * Display in table on the Add Program screen
     */
    private void initializeConditionsTable() {
        conditionsTable = new TableView<>();

        TableColumn<CountCondition, Number> indexColumn = new TableColumn<>("№");
        indexColumn.setSortable(false);
        indexColumn.setMinWidth(30);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(conditionsTable.getItems().indexOf(column.getValue()) + 1));
        indexColumn.setCellFactory(column -> new TableCell<CountCondition, Number>() {
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

        TableColumn<CountCondition, String> typeColumn = new TableColumn<>("Condition type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<CountCondition, String> valueColumn = new TableColumn<>("Values");
        valueColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getValues().stream()
                        .map(CountCondition.Badges::getTitle)
                        .collect(Collectors.joining(", "))
        ));

        TableColumn<CountCondition, Integer> pointsColumn = new TableColumn<>("Points");
        pointsColumn.setCellValueFactory(cellData -> {
            int totalPoints = cellData.getValue().getValues().stream()
                    .mapToInt(CountCondition.Badges::getPoints)
                    .sum();
            return new ReadOnlyObjectWrapper<>(totalPoints);
        });


        TableColumn<CountCondition, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setMinWidth(40);

        Callback<TableColumn<CountCondition, Void>, TableCell<CountCondition, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<CountCondition, Void> call(final TableColumn<CountCondition, Void> param) {
                final TableCell<CountCondition, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("X");

                    {
                        btn.setOnAction(event -> {
                            CountCondition data = getTableView().getItems().get(getIndex());
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
        conditionsTable.getColumns().addAll(indexColumn, typeColumn, valueColumn, pointsColumn, deleteColumn);
    }

    public void showEditProgramScreen(Stage stage, Program program) {
        resetForm();
        layout = new VBox(10);

        programNameField.setText(program.getName());
        startDatePicker.setValue(program.getDate());

        // Processing conditions and filling the table
        List<CountCondition> conditionModels = new ArrayList<>();
        for (CountCondition condition : program.getConditions()) {
            for (CountCondition.Badges badges : condition.getValues()) {
                CountCondition conditionModel = new CountCondition(
                        condition.getType(),
                        List.of(new CountCondition.Badges(
                                badges.getTitle(),
                                badges.getPoints()
                        ))
                );
                conditionModels.add(conditionModel);
            }
        }
        conditionsTable.setItems(FXCollections.observableArrayList(conditionModels));

        // Setting ComboBox and input field values
        conditionTypeComboBox.setValue(program.getConditions().isEmpty() ? null : program.getConditions().get(0).getType());
        conditionTypeComboBox.setDisable(true);

        // Layout for adding icons
        HBox badgeEntryLayout = new HBox(10);
        badgeEntryLayout.getChildren().addAll(
                //new Label(typeTitle),
                conditionTypeComboBox,
                //new Label(badgeTitle),
                badgeNameField,
                badgePointsField,
                addBadgeButton
        );

        layout.getChildren().addAll(
                createTopLayout(stage),
                createFormLayout(),
                badgeEntryLayout,
                conditionsTable,
                createButtonsSection(stage)
        );

        // Action for the save button
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

        List<CountCondition> updatedConditions = new ArrayList<>();
        for (CountCondition conditionModel : conditionsTable.getItems()) {
            List<CountCondition.Badges> valuesWithPoints = conditionModel.getValues().stream()
                    .map(vp -> new CountCondition.Badges(vp.getTitle(), vp.getPoints()))
                    .collect(Collectors.toList());

            CountCondition condition = new CountCondition();
            condition.setType(conditionModel.getType());
            condition.setValues(valuesWithPoints);
            updatedConditions.add(condition);
        }

        program.setConditions(updatedConditions);

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

        saveButton.setText(bundle.getString("saveButton"));
        cancelButton.setText(bundle.getString("cancelButton"));

        updateProgramNameFieldPlaceholder(bundle.getString("programNameField"));
        updateDateFieldPlaceholder(bundle.getString("startDatePicker"));
    }

    @Override
    public void updateCountFieldPlaceholder(String placeholder) {

    }

    @Override
    public void updateIgnoreFieldPlaceholder(String placeholder) {

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