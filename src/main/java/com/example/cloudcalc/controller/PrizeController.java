package com.example.cloudcalc.controller;

//import com.example.cloudcalc.badge.type.TypeBadgeManager;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
        import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.model.PrizeModel;
import com.example.cloudcalc.util.Notification;
import com.example.cloudcalc.view.prize.PrizeView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

        import java.util.List;
//import com.example.cloudcalc.badge.type.TypeBadgeManager;


public class PrizeController {

    private PrizeView prizeView = new PrizeView(this);
    private PrizeModel prizeModel = new PrizeModel(this);

    private TableBuilder tableBuilder = new TableBuilder();
    private final SceneBuilder sceneBuilder = new SceneBuilder();
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final MainController mainController;

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

    public PrizeController(ServiceFacade serviceFacade) {
        this.mainController = serviceFacade.getMainController();
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

    public void showScreen(Stage stage) {
        prizeView.showScreen(stage);
    }

    public List<Prize> loadPrizesFromFile(){
        return prizeModel.loadPrizesFromFile();
    }

    public TableView<Prize> createTableForPrize(Stage stage, List<Prize> prizes) {
        return tableBuilder.createTableForPrize(stage, prizes, this);
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
//        if (namePrize != null && badgeCount != null && !badgeCount.isEmpty() && badgeType != null) {
//            Prize newPrize = new Prize();
//            newPrize.setName(namePrize);
//            newPrize.setType(badgeType);
//
//            try {
//                newPrize.setCount(Integer.parseInt(badgeCount));
//            } catch (NumberFormatException e) {
//                return;
//            }
//
//            List<Prize> existingPrizes = loadPrizesFromFile(FileName.PRIZES_FILE);
//            existingPrizes.add(newPrize);
//
//            JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);
//
//            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FileName.PRIZES_FILE), StandardCharsets.UTF_8)) {
//                writer.write(jsonArray.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void deleteAction(Stage stage, Prize prize) {
        prizeModel.deleteAction(stage, prize);
    }

    public boolean showConfirmationAlert() {
        return Notification.showConfirmationAlert(
                alertTitleDeletePrize, alertHeaderDeletePrize, alertContentDeletePrize
        );
    }

    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }

    public HBox createTopLayout(Button backButton, Label titleLabel, Button createButton) {
        return elementsBuilder.createTopLayout(backButton, titleLabel, createButton);
    }

    public void showMainScreen(Stage stage) {
        mainController.showMainScreen(stage);
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