package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ScanController;
import com.example.cloudcalc.entity.*;
import com.example.cloudcalc.entity.badge.BadgeCounts;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ScanView implements Localizable {

    private final ScanController scanController;
    private Label subtitleLabel = new Label("Labs with the same names are counted as one");
    private String titlePreText = "Earned for ";

    public ScanView(ScanController scanController) {
        this.scanController = scanController;

        subtitleLabel.setStyle("-fx-font-style: italic;");
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> scanController.showStatsScreen(primaryStage));

        Text preTextLabel = new Text(titlePreText);
        Hyperlink nameLink = new Hyperlink(profile.getName());
        nameLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(profile.getLink()));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        TextFlow textFlow = new TextFlow(preTextLabel, nameLink);

        HBox topLayout = scanController.createTopLayoutWithBackAndText(backButton, textFlow);

        List<PrizeData> prizeDataList = parseProfilePrizes(profile);

        TableView<PrizeData> scanTable = createScanTable(prizeDataList);

        layout.getChildren().addAll(topLayout, subtitleLabel, scanTable);
        scanController.createScene(layout, primaryStage);
    }

    private List<PrizeData> parseProfilePrizes(Profile profile) {
        List<PrizeData> prizeDataList = new ArrayList<>();
        JSONArray programPrizesArray = new JSONArray(profile.getProgramPrizes());

        for (int j = 0; j < programPrizesArray.length(); j++) {
            JSONObject programPrizesObj = programPrizesArray.getJSONObject(j);
            String program = programPrizesObj.getString("program");

            JSONArray prizeInfoListArray = programPrizesObj.getJSONArray("prizeInfoList");
            for (int k = 0; k < prizeInfoListArray.length(); k++) {
                JSONObject prizeInfoObj = prizeInfoListArray.getJSONObject(k);
                int earnedPoints = prizeInfoObj.getInt("earnedPoints");
                String prize = prizeInfoObj.getString("prize");

                prizeDataList.add(new PrizeData(program, earnedPoints, prize));
            }
        }

        return prizeDataList;
    }

    private TableView<PrizeData> createScanTable(List<PrizeData> prizeDataList) {
        TableView<PrizeData> table = new TableView<>();
        ObservableList<PrizeData> data = FXCollections.observableArrayList(prizeDataList);

        TableColumn<PrizeData, Number> indexColumn = new TableColumn<>("№");
        indexColumn.setSortable(false);
        indexColumn.setMinWidth(30);
        indexColumn.setPrefWidth(50);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(column.getValue()) + 1));

        TableColumn<PrizeData, String> programColumn = new TableColumn<>("Program");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));
        programColumn.setPrefWidth(300);

        TableColumn<PrizeData, Integer> pointsColumn = new TableColumn<>("Earned Points");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("earnedPoints"));
        pointsColumn.setPrefWidth(148);

        TableColumn<PrizeData, String> prizeColumn = new TableColumn<>("Prize");
        prizeColumn.setCellValueFactory(new PropertyValueFactory<>("prize"));
        prizeColumn.setPrefWidth(300);

        table.getColumns().addAll(indexColumn, programColumn, prizeColumn, pointsColumn);
        table.setItems(data);

        System.out.println("createScanTable");
        return table;
    }

    private void updateProfileWithReceivedPrizes(Profile profile, Map<String, Prize> receivedPrizes) {
        List<ProgramPrize> programPrizes = profile.getProgramPrizes();

        if (programPrizes != null && !programPrizes.isEmpty()) {
            for (ProgramPrize programPrize : programPrizes) {
                String programName = programPrize.getProgram();
                if (receivedPrizes.containsKey(programName)) {
                    Prize prize = receivedPrizes.get(programName);
                    List<PrizeInfo> prizeInfoList = new ArrayList<>();
                    for (PrizeInfo prizeInfo : programPrize.getPrizeInfoList()) {
                        if (prize.getName().equals(prizeInfo.getPrize())) {
                            prizeInfo.setEarnedPoints(prize.getPoints());
                        }
                        prizeInfoList.add(prizeInfo);
                    }
                    programPrize.setPrizeInfoList(prizeInfoList);
                }
            }
        }
    }

    private TableView<Profile> createMainCategoriesTable(BadgeCounts badgeCounts) {
        TableView<Profile> table = new TableView<>();
        table.getItems().addAll(createBadgeCategoryList(badgeCounts));
        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        //table.getColumns().addAll(createIndexColumn(table), createCategoryColumn(table, 0.45), createValueColumn(table, 0.45));

        return table;
    }

    private List<Profile> createBadgeCategoryList(BadgeCounts badgeCounts) {
        List<Profile> categories = new ArrayList<>();
//        categories.add(new Profile(IBadgeCategory.TOTAL, String.valueOf(badgeCounts.getTotal())));
//        categories.add(new Profile(IBadgeCategory.IGNORE, String.valueOf(badgeCounts.getIgnore())));
//        categories.add(new Profile(IBadgeCategory.TOTAL_ARCADE, String.valueOf(badgeCounts.getTotalArcade())));
//        categories.add(new Profile(IBadgeCategory.ARCADE, String.valueOf(badgeCounts.getArcade())));
//        categories.add(new Profile(IBadgeCategory.SKILL_ARCADE, String.valueOf(badgeCounts.getSkillForArcade())));
//        categories.add(new Profile(IBadgeCategory.SKILL, String.valueOf(badgeCounts.getSkill())));
//        categories.add(new Profile(IBadgeCategory.PDF_TOTAL, String.valueOf(badgeCounts.getPdf())));
        return categories;
    }

    private Map<String, Prize> getReceivedPrizes() {
        return scanController.getReceivedPrizes();
    }

    private void printReceivedPrizes(Map<String, Prize> receivedPrizes) {
        for (Map.Entry<String, Prize> entry : receivedPrizes.entrySet()) {
            String key = entry.getKey();
            Prize value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    private void setupTableColumns(TableView<ProgramData> table) {
        table.getColumns().addAll(
                createIndexColumn(table),
                createProgramNameColumn(),
                createEarnedPointsColumn(),
                createPrizesColumn()
        );
    }

    private TableColumn<ProgramData, Integer> createIndexColumn(TableView<ProgramData> table) {
        TableColumn<ProgramData, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));
        indexColumn.setPrefWidth(40);
        return indexColumn;
    }

    private TableColumn<ProgramData, String> createProgramNameColumn() {
        TableColumn<ProgramData, String> programNameColumn = new TableColumn<>("Program Name");
        programNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProgramName()));
        return programNameColumn;
    }

    private TableColumn<ProgramData, Integer> createEarnedPointsColumn() {
        TableColumn<ProgramData, Integer> earnedPointsColumn = new TableColumn<>("Earned Points");
        earnedPointsColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEarnedPoints()));
        return earnedPointsColumn;
    }

    private TableColumn<ProgramData, List<PrizeInfo>> createPrizesColumn() {
        TableColumn<ProgramData, List<PrizeInfo>> prizesColumn = new TableColumn<>("Prizes");
        prizesColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrizeInfoList()));
        return prizesColumn;
    }

    private TableColumn<Profile, Integer> createIndexColumn1(TableView<Profile> table) {
        System.out.println("createIndexColumn");
        TableColumn<Profile, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(column -> {
            System.out.println("indexColumn.setCellValueFactory");
            return new ReadOnlyObjectWrapper<>(table.getItems().indexOf(column.getValue()) + 1);
        });

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        return indexColumn;
    }

    private TableColumn<Profile, String> createProgramColumn(TableView<Profile> table, double width) {
        TableColumn<Profile, String> programColumn = new TableColumn<>("Programs");

//        programColumn.setCellValueFactory(data -> {
//            Profile profile = data.getValue();
//
//            List<String> programNames = profile.getProgramPrizes().stream()
//                    .map(ProgramPrize::getProgram)
//                    .collect(Collectors.toList());
//
//            return new SimpleStringProperty(String.join(", ", programNames));
//        });
//
//
//        programColumn.setResizable(false);
//        programColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        return programColumn;
    }

    private TableColumn<Profile, String> createValueColumn(TableView<Profile> table, double width) {
        TableColumn<Profile, String> valueColumn = new TableColumn<>("Points");

        // TODO: Count badges

        valueColumn.setCellValueFactory(data -> {
            System.out.println("setCellValueFactory");
            Profile profile = data.getValue();

            return new SimpleStringProperty("TODO: Replace this with actual data");
        });

        //valueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));
        valueColumn.setResizable(false);
        valueColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        return valueColumn;
    }

    private TableColumn<Profile, String> createPrizesColumn(Map<String, Prize> receivedPrizes, TableView<Profile> table) {
        TableColumn<Profile, String> prizesColumn = new TableColumn<>("Prize");

        // TODO: Calculate prizes

//        prizesColumn.setCellValueFactory(cellData -> {
//            String categoryKey = cellData.getValue().getCategory();
//            if (receivedPrizes.containsKey(categoryKey)) {
//                return new SimpleStringProperty(receivedPrizes.get(categoryKey).getName());
//            } else {
//                return new SimpleStringProperty("");
//            }
//        });
        prizesColumn.setResizable(false);
        prizesColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.30));
        return prizesColumn;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titlePreText = bundle.getString("scanTitle");
        subtitleLabel.setText(bundle.getString("scanSubtitle"));
    }
}