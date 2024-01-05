package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.model.CountConditionModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ProgramView implements Localizable {
    private final ProgramController programController;
    private String title = "PROGRAMS";

    public ProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        List<Program> programs = programController.loadProgramsFromFile();

        TableView<Program> tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(programs));

        TableColumn<Program, Number> indexColumn = new TableColumn<>("№");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(column.getValue()) + 1));
        indexColumn.setCellFactory(column -> new TableCell<Program, Number>() {
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

        TableColumn<Program, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Program, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Program, Void> deleteColumn = createDeleteColumn(stage);

        tableView.getColumns().addAll(indexColumn, nameColumn, dateColumn, deleteColumn);

        TableView<CountConditionModel> subTableView = new TableView<>();

        TableColumn<CountConditionModel, Number> subIndexColumn = new TableColumn<>("#");
        subIndexColumn.setSortable(false);
        subIndexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(subTableView.getItems().indexOf(column.getValue()) + 1));
        subIndexColumn.setCellFactory(column -> new TableCell<CountConditionModel, Number>() {
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

        TableColumn<CountConditionModel, String> typeColumn = new TableColumn<>("Condition Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<CountConditionModel, String> valueColumn = new TableColumn<>("Condition Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        subTableView.getColumns().addAll(subIndexColumn, typeColumn, valueColumn);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                subTableView.setItems(FXCollections.observableArrayList(newSelection.getConditions()));
            } else {
                subTableView.setItems(FXCollections.observableArrayList());
            }
        });

        VBox layout = new VBox(10);
        Button backButton = ButtonFactory.createBackButton(e -> programController.showMainScreen(stage));
        Button createButton = ButtonFactory.createAddButton(e -> programController.showAddScreen(stage));
        HBox topLayout = programController.createTopLayout(backButton, new Label(title), createButton);

        layout.getChildren().addAll(topLayout, tableView, subTableView);

        programController.createScene(layout, stage);
    }

    private TableColumn<Program, Void> createDeleteColumn(Stage stage) {
        TableColumn<Program, Void> deleteColumn = new TableColumn<>("Delete");

        Callback<TableColumn<Program, Void>, TableCell<Program, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Program, Void> call(final TableColumn<Program, Void> param) {
                return new TableCell<>() {
                    final EventHandler<ActionEvent> deleteAction = (ActionEvent event) -> {
                        Program program = getTableView().getItems().get(getIndex());
                        programController.deleteAction(stage, program);
                    };

                    final Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        };

        deleteColumn.setCellFactory(cellFactory);

        return deleteColumn;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("programTitle");
    }
}