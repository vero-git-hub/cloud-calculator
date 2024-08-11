package com.example.cloudcalc;

import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.controller.*;

import com.example.cloudcalc.model.ProfileModel;
import javafx.stage.Stage;

public class ServiceFacade {
    private final ProfileModel profileModel;
    private final DataExtractor dataExtractor;
    private final PrizeController prizeController;
    private final ProfileController profileController;
    private final StatsController statsController;
    private final MainController mainController;
    private final ScanController scanController;
    private final ProgramController programController;
    private final DailyStatController dailyStatController;
    private static ServiceFacade instance;
    private FileOperationManager fileOperationManager;

    private ServiceFacade() {
        this.dataExtractor = new DataExtractor();
        this.prizeController = new PrizeController(this);
        this.fileOperationManager = new FileOperationManager();
        this.profileController = new ProfileController(this);
        this.profileModel = new ProfileModel(profileController);
        this.statsController = new StatsController(this);

        this.mainController = new MainController(this);
        this.scanController = new ScanController(this);
        this.programController = new ProgramController(this);
        this.dailyStatController = new DailyStatController(this);
    }

    public static synchronized ServiceFacade getInstance() {
        if (instance == null) {
            instance = new ServiceFacade();
        }
        return instance;
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

    public ProfileController getProfileController() {
        return profileController;
    }

    public StatsController getStatsController() {
        return statsController;
    }

    public MainController getMainController() {
        return mainController;
    }

    public ScanController getScanController() {
        return scanController;
    }

    public ProgramController getProgramController() {
        return programController;
    }

    public void showStatsScreen(Stage primaryStage) {
        statsController.showStatsScreen(primaryStage);
    }

    public void showPrizesScreen(Stage primaryStage) {
        prizeController.showScreen(primaryStage);
    }

    public void showProgramScreen(Stage stage) {
        programController.showScreen(stage);
    }

    public void showProfilesScreen(Stage stage){
        profileController.showScreen(stage);
    }

    public void showDailyStatScreen(Stage primaryStage) {
        dailyStatController.showScreen(primaryStage);
    }
}