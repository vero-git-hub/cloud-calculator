package com.example.cloudcalc.prize;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.badge.type.TypeBadgeDataManager;
//import com.example.cloudcalc.badge.type.TypeBadgeManager;

import com.example.cloudcalc.badge.type.TypeBadge;
import com.example.cloudcalc.badge.type.TypeBadgeDataManager;
import com.example.cloudcalc.constant.FileName;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
//import com.example.cloudcalc.badge.type.TypeBadgeManager;


public class PrizeController {

//    private final MainManager mainManager;
//    private final TypeBadgeDataManager typeBadgeDataManager;
//    private final TypeBadgeManager typeBadgeManager;
//
//    private final Map<String, Prize> receivedPrizes = new HashMap<>();
//    private Label titleLabel;
//    private Label addPrizeTitle;
//    private TextField namePrizeField;
//    private TextField badgeCountField;
//    private ComboBox<String> badgeTypeComboBox;
    String alertTitleDeletePrize = "Confirmation Dialog";
    String alertHeaderDeletePrize = "Delete Prize";
    String alertContentDeletePrize = "Are you sure you want to delete this prize?";

    public PrizeController() {
    }

//    public PrizeManager() {
//        this.mainManager = mainManager;
//        this.typeBadgeDataManager = new TypeBadgeDataManager();
//        this.typeBadgeManager = new TypeBadgeManager(mainManager, typeBadgeDataManager, this);
//
//        titleLabel = TableBuilder.createLabel("PRIZES");
//        addPrizeTitle = TableBuilder.createLabel("Add Prize");
//
//        namePrizeField = new TextField();
//        namePrizeField.setPromptText("Enter name prize");
//
//        badgeCountField = new TextField();
//        badgeCountField.setPromptText("Enter badge count");
//
//        badgeTypeComboBox = new ComboBox<>();
//        badgeTypeComboBox.setPromptText("Select badge type");
//
//        LanguageManager.registerLocalizable(this);
//    }
//
//    public Map<String, Prize> getReceivedPrizes() {
//        return receivedPrizes;
//    }

    public void showPrizesScreen(Stage primaryStage) {
//        VBox layout = new VBox(10);
//
//        List<Prize> prizes = loadPrizesFromFile(FileName.PRIZES_FILE);
//
//        TableView<Prize> table = new TableView<>();
//
//        TableColumn<Prize, String> nameColumn = new TableColumn<>("Name");
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//
//        TableColumn<Prize, String> typeColumn = new TableColumn<>("Type");
//        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
//
//        TableColumn<Prize, Integer> countColumn = new TableColumn<>("Count");
//        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
//
//        TableColumn<Prize, Void> deleteColumn = new TableColumn<>("Actions");
//        Callback<TableColumn<Prize, Void>, TableCell<Prize, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Prize, Void> call(final TableColumn<Prize, Void> param) {
//                return new TableCell<>() {
//                    final EventHandler<ActionEvent> deleteAction = (ActionEvent event) -> {
//                        Prize prize = getTableView().getItems().get(getIndex());
//
//                        boolean isConfirmationAlert = Notification.showConfirmationAlert(alertTitleDeletePrize, alertHeaderDeletePrize, alertContentDeletePrize);
//
//                        if (isConfirmationAlert) {
//                            deletePrize(prize);
//                            showPrizesScreen(primaryStage);
//                        }
//                    };
//
//                    final Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);
//
//                    @Override
//                    public void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            setGraphic(deleteButton);
//                        }
//                    }
//                };
//            }
//        };
//        deleteColumn.setCellFactory(cellFactory);
//
//        nameColumn.setResizable(false);
//        typeColumn.setResizable(false);
//        countColumn.setResizable(false);
//        deleteColumn.setResizable(false);
//
//        double width = 0.25;
//        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
//        typeColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
//        countColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
//        deleteColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
//
//        table.getColumns().add(nameColumn);
//        table.getColumns().add(typeColumn);
//        table.getColumns().add(countColumn);
//        table.getColumns().add(deleteColumn);
//        table.getItems().addAll(prizes);
//
//        Button backButton = ButtonFactory.createBackButton(e -> mainManager.showMainScreen(primaryStage));
//
//        Button createButton = ButtonFactory.createAddButton(e -> showAddPrizesScreen(primaryStage));
//
//        HBox topLayout = TableBuilder.createTopLayout(backButton, titleLabel, createButton);
//
//        layout.getChildren().addAll(
//                topLayout,
//                table
//        );
//
//        TableBuilder.createScene(layout, primaryStage);
    }

    private void deletePrize(Prize prize) {
        List<Prize> prizes = loadPrizesFromFile(FileName.PRIZES_FILE);
        prizes.remove(prize);
        savePrizesToFile(prizes);
    }

    private void savePrizesToFile(List<Prize> prizes) {
        JSONArray jsonArray = convertPrizesToJSONArray(prizes);
        FileManager.writeJsonToFile(jsonArray, FileName.PRIZES_FILE);
    }

    public void showAddPrizesScreen(Stage primaryStage) {
//        VBox layout = new VBox(10);
//
//        HBox typeLayout = new HBox();
//        typeLayout.setAlignment(Pos.CENTER);
//
//        List<TypeBadge> typeBadgeList = typeBadgeDataManager.loadTypesBadgeFromFile(FileName.TYPES_BADGE_FILE);
//        typeBadgeList.forEach(typeBadge -> badgeTypeComboBox.getItems().add(typeBadge.getName()));
//
//        Button addButton = ButtonFactory.createAddButton(e -> typeBadgeManager.showAddTypeBadgeScreen(primaryStage));
//
//        Pane leftSpacer = new Pane();
//        Pane rightSpacer = new Pane();
//
//        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
//        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
//        typeLayout.getChildren().addAll(badgeTypeComboBox, addButton, rightSpacer);
//
//        Button saveButton = ButtonFactory.createSaveButton(e -> {
//            savePrize(namePrizeField.getText(), badgeCountField.getText(), badgeTypeComboBox.getValue());
//            showPrizesScreen(primaryStage);
//        });
//
//        Button backButton = ButtonFactory.createBackButton(e -> showPrizesScreen(primaryStage));
//
//        HBox topLayout = TableBuilder.createTopLayout(backButton, addPrizeTitle);
//
//        layout.getChildren().addAll(
//                topLayout,
//                namePrizeField,
//                badgeCountField,
//                typeLayout,
//                saveButton
//        );
//
//        TableBuilder.createScene(layout, primaryStage);
    }

    private void savePrize(String namePrize, String badgeCount, String badgeType) {
        if (namePrize != null && badgeCount != null && !badgeCount.isEmpty() && badgeType != null) {
            Prize newPrize = new Prize();
            newPrize.setName(namePrize);
            newPrize.setType(badgeType);

            try {
                newPrize.setCount(Integer.parseInt(badgeCount));
            } catch (NumberFormatException e) {
                return;
            }

            List<Prize> existingPrizes = loadPrizesFromFile(FileName.PRIZES_FILE);
            existingPrizes.add(newPrize);

            JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FileName.PRIZES_FILE), StandardCharsets.UTF_8)) {
                writer.write(jsonArray.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Prize> loadPrizesFromFile(String fileName) {
        List<Prize> prizes = new ArrayList<>();
        JSONArray jsonArray = FileManager.readJsonArrayFromFile(fileName);

        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject prizeObject = jsonArray.getJSONObject(j);
            Prize prize = new Prize();
            prize.setName(prizeObject.getString("name"));
            prize.setType(prizeObject.getString("type"));
            prize.setCount(prizeObject.getInt("count"));
            if (prizeObject.has("program")) {
                prize.setProgram(prizeObject.getString("program"));
            }
            prizes.add(prize);
        }

        return prizes;
    }

    private JSONArray convertPrizesToJSONArray(List<Prize> prizes) {
        JSONArray jsonArray = new JSONArray();
        for (Prize prize : prizes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", prize.getName());
            jsonObject.put("type", prize.getType());
            jsonObject.put("count", prize.getCount());
            jsonObject.put("program", prize.getProgram());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

//    public List<String> determinePrizesForBadgeCount(int prizePDF, int prizeSkill, int prizeActivity, int prizeTypeBadge) {
//        List<Prize> prizes = loadPrizesFromFile(FileName.PRIZES_FILE);
//
//        receivedPrizes.clear();
//
//        prizes.stream()
//                .filter(prize -> "pdf".equals(prize.getType()))
//                .filter(prize -> prizePDF >= prize.getCount())
//                .findFirst()
//                .ifPresent(prize -> receivedPrizes.put(BadgeCategory.PDF_FOR_PRIZE, prize));
//
//        prizes.stream()
//                .filter(prize -> "skill".equals(prize.getType()))
//                .sorted(Comparator.comparing(Prize::getCount).reversed())
//                .filter(prize -> prizeSkill >= prize.getCount())
//                .findFirst()
//                .ifPresent(prize -> receivedPrizes.put(BadgeCategory.SKILL_FOR_PRIZE, prize));
//
//        if (prizeActivity >= 30) {
//            prizes.stream()
//                    .filter(prize -> "activity".equals(prize.getType()))
//                    .sorted(Comparator.comparing(Prize::getCount).reversed())
//                    .filter(prize -> prizeActivity >= prize.getCount())
//                    .findFirst()
//                    .ifPresent(prize -> receivedPrizes.put(BadgeCategory.SKILL_FOR_ACTIVITY, prize));
//        }
//
//        prizes.stream()
//                .filter(prize -> "pl-02.10.2023".equals(prize.getType()))
//                .sorted(Comparator.comparing(Prize::getCount).reversed())
//                .filter(prize ->  prizeTypeBadge >= prize.getCount())
//                .findFirst()
//                .ifPresent(prize -> receivedPrizes.put(BadgeCategory.SKILL_FOR_PL, prize));
//
//        return receivedPrizes.values().stream().map(Prize::getName).collect(Collectors.toList());
//    }

//    @Override
//    public void updateLocalization(ResourceBundle bundle) {
//        titleLabel.setText(bundle.getString("prizeTitle"));
//        addPrizeTitle.setText(bundle.getString("addPrizeTitle"));
//        namePrizeField.setPromptText(bundle.getString("addPrizeNameField"));
//        badgeCountField.setPromptText(bundle.getString("addPrizeCountField"));
//        badgeTypeComboBox.setPromptText(bundle.getString("addPrizeTypeComboBox"));
//
//        alertTitleDeletePrize = bundle.getString("alertTitleDeletePrize");
//        alertHeaderDeletePrize = bundle.getString("alertHeaderDeletePrize");
//        alertContentDeletePrize = bundle.getString("alertContentDeletePrize");
//    }
}
