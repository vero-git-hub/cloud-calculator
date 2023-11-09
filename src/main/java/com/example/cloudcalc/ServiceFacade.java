package com.example.cloudcalc;

import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.badge.arcade.ArcadeController;

import com.example.cloudcalc.badge.ignored.IgnoreController;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.prize.PrizeController;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.scan.ScanManager;
import com.example.cloudcalc.controller.StatsController;
import javafx.stage.Stage;

public class ServiceFacade {

    private static ServiceFacade instance;
    private ProfileModel profileModel;
    private DataExtractor dataExtractor;
    private PrizeController prizeController;
    private FileOperationManager fileOperationManager;
    private ScanManager scanManager;
    private ProfileController profileController;
    private IgnoreController ignoreController;
    private BadgeManager badgeManager;
    private StatsController statsController;
    private ArcadeController arcadeController;
    private MainController mainController = new MainController(this);

    private ServiceFacade() {

        dataExtractor = new DataExtractor();
        prizeController = new PrizeController();
        fileOperationManager = new FileOperationManager();
        scanManager = createScanManager();
        profileController = createProfileController();
        this.profileModel = new ProfileModel(profileController);
        ignoreController = new IgnoreController();
        badgeManager = createBadgeManager();
        statsController = new StatsController(this);
        arcadeController = new ArcadeController();
    }

    public static synchronized ServiceFacade getInstance() {
        if (instance == null) {
            instance = new ServiceFacade();
        }
        return instance;
    }

    private BadgeManager createBadgeManager() {
        return new BadgeManager(dataExtractor, prizeController, profileModel, fileOperationManager);
    }

    private ProfileController createProfileController() {
        return new ProfileController(this);
    }

    private ScanManager createScanManager() {
        BadgeManager badgeManager = new BadgeManager(dataExtractor, prizeController, profileModel, fileOperationManager);
        return new ScanManager(badgeManager, profileModel);
    }

    public ProfileModel getProfileDataManager() {
        return profileModel;
    }

    public DataExtractor getDataExtractor() {
        return dataExtractor;
    }

    public PrizeController getPrizeController() {
        return prizeController;
    }

    public FileOperationManager getFileOperationManager() {
        return fileOperationManager;
    }

    public ScanManager getScanManager() {
        return scanManager;
    }

    public ProfileController getProfileManager() {
        return profileController;
    }

    public BadgeManager getBadgeManager() {
        return badgeManager;
    }

    public StatsController getStatsController() {
        return statsController;
    }

    public ArcadeController getArcadeController() {
        return arcadeController;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        profileController.showCreateProfileScreen(primaryStage);
    }

    public void showStatsScreen(Stage primaryStage) {
        statsController.showStatsScreen(primaryStage);
    }

    public void showIgnoreScreen(Stage primaryStage) {
        ignoreController.showIgnoreScreen(primaryStage);
    }

    public void showPrizesScreen(Stage primaryStage) {
        prizeController.showPrizesScreen(primaryStage);
    }

    public void showArcadeScreen(Stage primaryStage) {
        arcadeController.showArcadeScreen(primaryStage);
    }

}
