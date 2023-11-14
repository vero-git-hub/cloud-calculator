package com.example.cloudcalc;

import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.controller.*;

import com.example.cloudcalc.controller.IgnoreController;
import com.example.cloudcalc.model.ProfileModel;
import javafx.stage.Stage;

public class ServiceFacade {

    private static ServiceFacade instance;
    private ProfileModel profileModel;
    private DataExtractor dataExtractor;
    private PrizeController prizeController;
    private FileOperationManager fileOperationManager;
    //private ScanView scanView;
    private ProfileController profileController;
    private IgnoreController ignoreController;
    private BadgeManager badgeManager;
    private StatsController statsController;
    private ArcadeController arcadeController;
    private TypeBadgeController typeBadgeController = new TypeBadgeController(this);
    private MainController mainController = new MainController(this);
    private ScanController scanController = new ScanController(this);

    private ServiceFacade() {
        dataExtractor = new DataExtractor();
        prizeController = new PrizeController(this);
        fileOperationManager = new FileOperationManager();
        //scanView = createScanManager();
        profileController = createProfileController();
        this.profileModel = new ProfileModel(profileController);
        ignoreController = new IgnoreController(this);
        badgeManager = createBadgeManager();
        statsController = new StatsController(this);
        arcadeController = new ArcadeController(this);
    }

    public static synchronized ServiceFacade getInstance() {
        if (instance == null) {
            instance = new ServiceFacade();
        }
        return instance;
    }

    private BadgeManager createBadgeManager() {
        return new BadgeManager(this);
    }

    private ProfileController createProfileController() {
        return new ProfileController(this);
    }

    //private ScanView createScanManager() {
        //BadgeManager badgeManager = new BadgeManager(dataExtractor, prizeController, profileModel, fileOperationManager);
        //return new ScanView(getScanController());
    //}

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

    //public ScanView getScanManager() {
        //return scanView;
    //}

    public ProfileController getProfileController() {
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

    public TypeBadgeController getTypeBadgeController() {
        return typeBadgeController;
    }

    public MainController getMainController() {
        return mainController;
    }

    public ScanController getScanController() {
        return scanController;
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        profileController.showCreateProfileScreen(primaryStage);
    }

    public void showStatsScreen(Stage primaryStage) {
        statsController.showStatsScreen(primaryStage);
    }

    public void showIgnoreScreen(Stage primaryStage) {
        ignoreController.showScreen(primaryStage);
    }

    public void showPrizesScreen(Stage primaryStage) {
        prizeController.showScreen(primaryStage);
    }

    public void showArcadeScreen(Stage primaryStage) {
        arcadeController.showScreen(primaryStage);
    }

    public void showTypeBadgeScreen(Stage stage) {
        typeBadgeController.showScreen(stage);
    }


}
