package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.entity.badge.TypeBadge;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.model.PrizeModel;
import com.example.cloudcalc.util.Notification;
import com.example.cloudcalc.view.prize.SavePrizeView;
import com.example.cloudcalc.view.prize.PrizeView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PrizeController extends BaseController {
    private PrizeView prizeView = new PrizeView(this);
    private PrizeModel prizeModel = new PrizeModel(this);
    private TableBuilder tableBuilder = new TableBuilder();
    private SavePrizeView savePrizeView;

    public static ResourceBundle bundle;
    private final Map<String, Prize> receivedPrizes = new HashMap<>();
    String alertTitleDeletePrize = "Confirmation Dialog";
    String alertHeaderDeletePrize = "Delete Prize";
    String alertContentDeletePrize = "Are you sure you want to delete this prize?";
    private ProgramController programController;
    private ScanController scanController;

    public PrizeController(ServiceFacade serviceFacade) {
        super(serviceFacade);
        bundle = LanguageManager.getBundle();
        this.programController = serviceFacade.getProgramController();
        this.scanController = serviceFacade.getScanController();

        savePrizeView = new SavePrizeView(this);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public Map<String, Prize> getReceivedPrizes() {
        return receivedPrizes;
    }

    public ScanController getScanController() {
        return scanController;
    }

    @Override
    public void showScreen(Stage stage) {
        prizeView.showScreen(stage);
    }

    @Override
    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }

    @Override
    public void showAddScreen(Stage stage) {
        savePrizeView.showScreen(stage);
    }

    public List<Prize> loadPrizesFromFile(){
        return prizeModel.loadPrizesFromFile();
    }

    public TableView<Prize> createTableForPrize(Stage stage, List<Prize> prizes) {
        return tableBuilder.createTableForPrize(stage, prizes, this);
    }

    public void savePrize(Stage stage, Prize prize) {
        prizeModel.savePrize(prize);
        showScreen(stage);
    }

    public void deleteAction(Stage stage, Prize prize) {
        prizeModel.deleteAction(stage, prize);
    }

    public boolean showConfirmationAlert() {
        return Notification.showConfirmationAlert(
                alertTitleDeletePrize, alertHeaderDeletePrize, alertContentDeletePrize
        );
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

//        prizes.stream()
//                .filter(prize -> "pdf".equals(prize.getType()))
//                .filter(prize -> prizePDF >= prize.getCount())
//                .findFirst()
//                .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.PDF_FOR_PRIZE, prize));
//
//        prizes.stream()
//                .filter(prize -> "skill".equals(prize.getType()))
//                .sorted(Comparator.comparing(Prize::getCount).reversed())
//                .filter(prize -> prizeSkill >= prize.getCount())
//                .findFirst()
//                .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.SKILL_FOR_PRIZE, prize));
//
//        if (prizeActivity >= 30) {
//            prizes.stream()
//                    .filter(prize -> "activity".equals(prize.getType()))
//                    .sorted(Comparator.comparing(Prize::getCount).reversed())
//                    .filter(prize -> prizeActivity >= prize.getCount())
//                    .findFirst()
//                    .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.SKILL_FOR_ACTIVITY, prize));
//        }
//
//        prizes.stream()
//                .filter(prize -> "pl-02.10.2023".equals(prize.getType()))
//                .sorted(Comparator.comparing(Prize::getCount).reversed())
//                .filter(prize ->  prizeTypeBadge >= prize.getCount())
//                .findFirst()
//                .ifPresent(prize -> receivedPrizes.put(IBadgeCategory.SKILL_FOR_PL, prize));

        return receivedPrizes.values().stream().map(Prize::getName).collect(Collectors.toList());
    }

    public List<Program> loadProgramsFromFile() {
        return programController.loadProgramsFromFile();
    }

    public void showEditPrizeScreen(Stage stage, Prize prize) {
        savePrizeView.showEditPrizeScreen(stage, prize);
    }
}