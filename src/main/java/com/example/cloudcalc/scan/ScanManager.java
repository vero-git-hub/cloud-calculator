package com.example.cloudcalc.scan;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.BadgeCategory;
import com.example.cloudcalc.badge.BadgeCounts;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.prize.Prize;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ScanManager implements Localizable {

    private final BadgeManager badgeManager;
    private final UICallbacks uiCallbacks;
    private final ProfileDataManager profileDataManager;
    private Label subtitleLabel;
    private String preText;

    public ScanManager(BadgeManager badgeManager, UICallbacks uiCallbacks, ProfileDataManager profileDataManager) {
        this.badgeManager = badgeManager;
        this.uiCallbacks = uiCallbacks;
        this.profileDataManager = profileDataManager;

        preText = "SCAN for ";
        subtitleLabel = new Label("Labs with the same names are counted as one");
        subtitleLabel.setStyle("-fx-font-style: italic;");
        LanguageManager.registerLocalizable(this);
    }

    public void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));

        Text preTextLabel = new Text(preText);
        Hyperlink nameLink = new Hyperlink(profile.getName());
        nameLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(profile.getProfileLink()));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        TextFlow textFlow = new TextFlow(preTextLabel, nameLink);

        HBox topLayout = uiCallbacks.createTopLayoutWithBackAndText(backButton, textFlow);

        BadgeCounts badgeCounts = badgeManager.calculateBadgeCounts(profile, siteLinks);
        TableView<BadgeCategory> mainCategoriesTable = createMainCategoriesTable(badgeCounts);

        TableView<BadgeCategory> prizeCategoriesTable = createPrizeCategoriesTable(profile, badgeCounts);
        layout.getChildren().addAll(topLayout, mainCategoriesTable, subtitleLabel, prizeCategoriesTable);

        uiCallbacks.createScene(layout, primaryStage);
    }

    private TableView<BadgeCategory> createMainCategoriesTable(BadgeCounts badgeCounts) {
        TableView<BadgeCategory> table = new TableView<>();
        table.getItems().addAll(createBadgeCategoryList(badgeCounts));
        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        table.getColumns().addAll(createIndexColumn(table), createCategoryColumn(table, 0.45), createValueColumn(table, 0.45));

        return table;
    }

    private List<BadgeCategory> createBadgeCategoryList(BadgeCounts badgeCounts) {
        List<BadgeCategory> categories = new ArrayList<>();
        categories.add(new BadgeCategory(Constants.TOTAL, String.valueOf(badgeCounts.getTotal())));
        categories.add(new BadgeCategory(Constants.IGNORE, String.valueOf(badgeCounts.getIgnore())));
        categories.add(new BadgeCategory(Constants.ARCADE, String.valueOf(badgeCounts.getArcade())));
        categories.add(new BadgeCategory(Constants.SKILL, String.valueOf(badgeCounts.getSkill())));
        categories.add(new BadgeCategory(Constants.PDF_TOTAL, String.valueOf(badgeCounts.getPdf())));
        return categories;
    }

    private TableView<BadgeCategory> createPrizeCategoriesTable(Profile profile, BadgeCounts badgeCounts) {
        Map<String, Prize> receivedPrizes = getReceivedPrizes();

        List<String> prizeNames = receivedPrizes.values().stream().map(Prize::getName).collect(Collectors.toList());
        profile.setPrizes(prizeNames);
        profileDataManager.updateProfile(profile);

        TableView<BadgeCategory> table = new TableView<>();
        table.getItems().addAll(createBadgeCategoriesList(badgeCounts));

        setupTableColumns(table, receivedPrizes);

        return table;
    }

    private Map<String, Prize> getReceivedPrizes() {
        return badgeManager.getPrizeManager().getReceivedPrizes();
    }

    private void printReceivedPrizes(Map<String, Prize> receivedPrizes) {
        for (Map.Entry<String, Prize> entry : receivedPrizes.entrySet()) {
            String key = entry.getKey();
            Prize value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    private List<BadgeCategory> createBadgeCategoriesList(BadgeCounts badgeCounts) {
        List<BadgeCategory> categories = new ArrayList<>();
        categories.add(new BadgeCategory(Constants.PDF_FOR_PRIZE, String.valueOf(badgeCounts.getPrizePDF())));
        categories.add(new BadgeCategory(Constants.SKILL_FOR_PRIZE, String.valueOf(badgeCounts.getPrizeSkill())));
        categories.add(new BadgeCategory(Constants.SKILL_FOR_ACTIVITY, String.valueOf(badgeCounts.getPrizeActivity())));
        categories.add(new BadgeCategory(Constants.SKILL_FOR_PL, String.valueOf(badgeCounts.getPrizePL())));
        return categories;
    }

    private void setupTableColumns(TableView<BadgeCategory> table, Map<String, Prize> receivedPrizes) {
        table.getColumns().addAll(
                createIndexColumn(table),
                createCategoryColumn(table, 0.30),
                createValueColumn(table, 0.30),
                createPrizesColumn(receivedPrizes, table)
        );
    }

    private TableColumn<BadgeCategory, Integer> createIndexColumn(TableView<BadgeCategory> table) {
        TableColumn<BadgeCategory, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(column -> {
            return new ReadOnlyObjectWrapper<>(table.getItems().indexOf(column.getValue()) + 1);
        });

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        return indexColumn;
    }

    private TableColumn<BadgeCategory, String> createCategoryColumn(TableView<BadgeCategory> table, double width) {
        TableColumn<BadgeCategory, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        categoryColumn.setResizable(false);
        categoryColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        return categoryColumn;
    }

    private TableColumn<BadgeCategory, String> createValueColumn(TableView<BadgeCategory> table, double width) {
        TableColumn<BadgeCategory, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));
        valueColumn.setResizable(false);
        valueColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        return valueColumn;
    }

    private TableColumn<BadgeCategory, String> createPrizesColumn(Map<String, Prize> receivedPrizes, TableView<BadgeCategory> table) {
        TableColumn<BadgeCategory, String> prizesColumn = new TableColumn<>("Prizes");
        prizesColumn.setCellValueFactory(cellData -> {
            String categoryKey = cellData.getValue().getCategory();
            if (receivedPrizes.containsKey(categoryKey)) {
                return new SimpleStringProperty(receivedPrizes.get(categoryKey).getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        prizesColumn.setResizable(false);
        prizesColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.30));
        return prizesColumn;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        preText = bundle.getString("scanTitle");
        subtitleLabel.setText(bundle.getString("scanSubtitle"));
    }
}
