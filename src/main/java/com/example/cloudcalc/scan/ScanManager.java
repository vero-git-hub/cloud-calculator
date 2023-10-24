package com.example.cloudcalc.scan;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.BadgeCategory;
import com.example.cloudcalc.badge.BadgeCounts;
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
import java.util.List;
import java.util.Map;

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

        Label subtitleLabel = new Label("Labs with the same names are counted as one");
        subtitleLabel.setStyle("-fx-font-style: italic;");

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        BadgeCounts badgeCounts = badgeManager.calculateBadgeCounts(profile, siteLinks);
        TableView<BadgeCategory> mainCategoriesTable = createMainCategoriesTable(badgeCounts);

        TableView<BadgeCategory> prizeCategoriesTable = createPrizeCategoriesTable(badgeCounts);
        layout.getChildren().addAll(topLayout, mainCategoriesTable, subtitleLabel, prizeCategoriesTable);

        uiCallbacks.createScene(layout, primaryStage);
    }

    private TableView<BadgeCategory> createMainCategoriesTable(BadgeCounts badgeCounts) {
        TableView<BadgeCategory> table = new TableView<>();

        List<BadgeCategory> categories = new ArrayList<>();
        categories.add(new BadgeCategory(Constants.TOTAL, String.valueOf(badgeCounts.getTotal())));
        categories.add(new BadgeCategory(Constants.IGNORE, String.valueOf(badgeCounts.getIgnore())));
        categories.add(new BadgeCategory(Constants.SKILL, String.valueOf(badgeCounts.getSkill())));
        categories.add(new BadgeCategory(Constants.PDF_TOTAL, String.valueOf(badgeCounts.getTotalPDF())));

        table.getItems().addAll(categories);

        TableColumn<BadgeCategory, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));

        TableColumn<BadgeCategory, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));

        TableColumn<BadgeCategory, String> valueColumn = new TableColumn<>("Value");
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

    private TableView<BadgeCategory> createPrizeCategoriesTable(BadgeCounts badgeCounts) {
        Map<String, Prize> receivedPrizes = badgeManager.getPrizeManager().getReceivedPrizes();

        TableView<BadgeCategory> table = new TableView<>();

        List<BadgeCategory> categories = new ArrayList<>();
        categories.add(new BadgeCategory(Constants.PDF_FOR_PRIZE, String.valueOf(badgeCounts.getPrizePDF())));
        categories.add(new BadgeCategory(Constants.SKILL_FOR_PRIZE, String.valueOf(badgeCounts.getPrizeSkill())));
        categories.add(new BadgeCategory(Constants.SKILL_FOR_ACTIVITY, String.valueOf(badgeCounts.getPrizeActivity())));
        categories.add(new BadgeCategory(Constants.SKILL_FOR_PL, String.valueOf(badgeCounts.getPrizePL())));

        table.getItems().addAll(categories);

        TableColumn<BadgeCategory, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));

        TableColumn<BadgeCategory, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));

        TableColumn<BadgeCategory, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        TableColumn<BadgeCategory, String> prizesColumn = new TableColumn<>("Prizes");
        prizesColumn.setCellValueFactory(cellData -> {
            String categoryKey = cellData.getValue().getCategory();
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
