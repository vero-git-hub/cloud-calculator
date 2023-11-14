package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.constant.IBadgeCategory;
import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.entity.badge.TypeBadge;
import com.example.cloudcalc.entity.badge.BadgeCategory;
import com.example.cloudcalc.model.PrizeModel;
import com.example.cloudcalc.util.Notification;
import com.example.cloudcalc.view.prize.AddPrizeView;
import com.example.cloudcalc.view.prize.PrizeView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PrizeController {

    private PrizeView prizeView = new PrizeView(this);
    private PrizeModel prizeModel = new PrizeModel(this);

    private TableBuilder tableBuilder = new TableBuilder();
    private final SceneBuilder sceneBuilder = new SceneBuilder();
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final MainController mainController;
    private final AddPrizeView addPrizeView = new AddPrizeView(this);

//    private final MainManager mainManager;

    private final ServiceFacade serviceFacade;
//
    private final Map<String, Prize> receivedPrizes = new HashMap<>();
//    private Label titleLabel;
//    private Label addPrizeTitle;
    private TextField namePrizeField;
    private TextField badgeCountField;

    String alertTitleDeletePrize = "Confirmation Dialog";
    String alertHeaderDeletePrize = "Delete Prize";
    String alertContentDeletePrize = "Are you sure you want to delete this prize?";

    public PrizeController(ServiceFacade serviceFacade) {
        this.mainController = serviceFacade.getMainController();
        this.serviceFacade = serviceFacade;

        namePrizeField = new TextField();
        namePrizeField.setPromptText("Enter name prize");

        badgeCountField = new TextField();
        badgeCountField.setPromptText("Enter badge count");
    }

    public TextField getNamePrizeField() {
        return namePrizeField;
    }

    public TextField getBadgeCountField() {
        return badgeCountField;
    }

    //    public PrizeManager() {
//        this.mainManager = mainManager;
//        this.typeBadgeDataManager = new TypeBadgeDataManager();
//        this.typeBadgeManager = new TypeBadgeManager(mainManager, typeBadgeDataManager, this);
//
//        titleLabel = TableBuilder.createLabel("PRIZES");
//        addPrizeTitle = TableBuilder.createLabel("Add Prize");
//
//        LanguageManager.registerLocalizable(this);
//    }
//
    public Map<String, Prize> getReceivedPrizes() {
        return receivedPrizes;
    }

    public void showScreen(Stage stage) {
        prizeView.showScreen(stage);
    }

    public List<Prize> loadPrizesFromFile(){
        return prizeModel.loadPrizesFromFile();
    }

    public TableView<Prize> createTableForPrize(Stage stage, List<Prize> prizes) {
        return tableBuilder.createTableForPrize(stage, prizes, this);
    }

    public void showAddPrizesScreen(Stage stage) {
        addPrizeView.showScreen(stage);
    }

    public void savePrize(Stage stage, String badgeType) {
        String namePrize = namePrizeField.getText();
        String badgeCount = badgeCountField.getText();
        if (namePrize != null && badgeCount != null && !badgeCount.isEmpty() && badgeType != null) {
            prizeModel.savePrize(namePrize, badgeCount, badgeType);
            showScreen(stage);
        }
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

    public HBox createTopLayoutForAddScreen(Button backButton, Label titleLabel) {
        return elementsBuilder.createTopLayout(backButton, titleLabel);
    }

    public void showMainScreen(Stage stage) {
        mainController.showMainScreen(stage);
    }

    public List<TypeBadge> loadTypesBadgeFromFile() {
        return serviceFacade.getTypeBadgeController().getTypeBadgeModel().loadTypesBadgeFromFile();
    }

    public void showAddTypeBadgeScreen(Stage stage) {
        serviceFacade.showTypeBadgeScreen(stage);
    }


    public List<String> determinePrizesForBadgeCount(int prizePDF, int prizeSkill, int prizeActivity, int prizeTypeBadge) {
        List<Prize> prizes = loadPrizesFromFile();

        receivedPrizes.clear();

        prizes.stream()
                .filter(prize -> "pdf".equals(prize.getType()))
                .filter(prize -> prizePDF >= prize.getCount())
                .findFirst()
                .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.PDF_FOR_PRIZE, prize));

        prizes.stream()
                .filter(prize -> "skill".equals(prize.getType()))
                .sorted(Comparator.comparing(Prize::getCount).reversed())
                .filter(prize -> prizeSkill >= prize.getCount())
                .findFirst()
                .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.SKILL_FOR_PRIZE, prize));

        if (prizeActivity >= 30) {
            prizes.stream()
                    .filter(prize -> "activity".equals(prize.getType()))
                    .sorted(Comparator.comparing(Prize::getCount).reversed())
                    .filter(prize -> prizeActivity >= prize.getCount())
                    .findFirst()
                    .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.SKILL_FOR_ACTIVITY, prize));
        }

        prizes.stream()
                .filter(prize -> "pl-02.10.2023".equals(prize.getType()))
                .sorted(Comparator.comparing(Prize::getCount).reversed())
                .filter(prize ->  prizeTypeBadge >= prize.getCount())
                .findFirst()
                .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.SKILL_FOR_PL, prize));

        return receivedPrizes.values().stream().map(Prize::getName).collect(Collectors.toList());
    }

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
