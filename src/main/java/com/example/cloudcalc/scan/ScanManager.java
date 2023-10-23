package com.example.cloudcalc.scan;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.prize.Prize;
import com.example.cloudcalc.profile.Profile;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScanManager {

    private final BadgeManager badgeManager;
    private final UICallbacks uiCallbacks;

    public ScanManager(BadgeManager badgeManager, UICallbacks uiCallbacks) {
        this.badgeManager = badgeManager;
        this.uiCallbacks = uiCallbacks;
    }

    public void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        String title = String.format("SCAN for %s", profile.getName());
        Label titleLabel = uiCallbacks.createLabel(title);

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        Map<String, String> badgeCounts = badgeManager.calculateBadgeCounts(profile, siteLinks);
        TableView<Map.Entry<String, String>> mainCategoriesTable = createMainCategoriesTable(badgeCounts);

        TableView<Map.Entry<String, String>> prizeCategoriesTable = createPrizeCategoriesTable(badgeCounts);
        layout.getChildren().addAll(topLayout, mainCategoriesTable, prizeCategoriesTable);

        uiCallbacks.createScene(layout, primaryStage);
    }


    private TableView<Map.Entry<String, String>> createMainCategoriesTable(Map<String, String> badgeCounts) {
        TableView<Map.Entry<String, String>> table = new TableView<>();

        List<String> mainCategories = Arrays.asList(Constants.TOTAL, Constants.IGNORE, Constants.SKILL, Constants.PDF_TOTAL);

        List<Map.Entry<String, String>> mainCategoryEntries = badgeCounts.entrySet().stream()
                .filter(entry -> mainCategories.contains(entry.getKey()))
                .collect(Collectors.toList());

        table.getItems().addAll(mainCategoryEntries);

        TableColumn<Map.Entry<String, String>, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));

        TableColumn<Map.Entry<String, String>, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));

        TableColumn<Map.Entry<String, String>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        indexColumn.setResizable(false);
        categoryColumn.setResizable(false);
        valueColumn.setResizable(false);

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        categoryColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.45));
        valueColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.45));
        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        table.getColumns().addAll(indexColumn, categoryColumn, valueColumn);

        return table;
    }

    private TableView<Map.Entry<String, String>> createPrizeCategoriesTable(Map<String, String> badgeCounts) {
        Map<String, Prize> receivedPrizes = badgeManager.getPrizeManager().getReceivedPrizes();

        TableView<Map.Entry<String, String>> table = new TableView<>();

        List<String> prizeCategories = Arrays.asList(
                Constants.PDF_FOR_PRIZE,
                Constants.SKILL_FOR_PRIZE,
                Constants.SKILL_FOR_ACTIVITY,
                Constants.SKILL_FOR_PL
        );

        List<Map.Entry<String, String>> prizeCategoryEntries = badgeCounts.entrySet().stream()
                .filter(entry -> prizeCategories.contains(entry.getKey()))
                .collect(Collectors.toList());

        table.getItems().addAll(prizeCategoryEntries);

        TableColumn<Map.Entry<String, String>, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));

        TableColumn<Map.Entry<String, String>, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));

        TableColumn<Map.Entry<String, String>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        TableColumn<Map.Entry<String, String>, String> prizesColumn = new TableColumn<>("Prizes");
        prizesColumn.setCellValueFactory(cellData -> {
            String categoryKey = cellData.getValue().getKey();
            if (receivedPrizes.containsKey(categoryKey)) {
                return new SimpleStringProperty(receivedPrizes.get(categoryKey).getName());
            } else {
                return new SimpleStringProperty("");
            }
        });

        indexColumn.setResizable(false);
        categoryColumn.setResizable(false);
        valueColumn.setResizable(false);
        prizesColumn.setResizable(false);

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        categoryColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.30));
        valueColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.30));
        prizesColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.30));

        table.getColumns().addAll(indexColumn, categoryColumn, valueColumn, prizesColumn);

        return table;
    }

}
