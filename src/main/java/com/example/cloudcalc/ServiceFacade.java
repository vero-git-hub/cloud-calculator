package com.example.cloudcalc;

import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.controller.*;

import com.example.cloudcalc.model.ProfileModel;
import javafx.stage.Stage;

public class ServiceFacade {

    private static ServiceFacade instance;
    private ProfileModel profileModel;
    private DataExtractor dataExtractor;
    private PrizeController prizeController;
    private FileOperationManager fileOperationManager;
    private ProfileController profileController;
    private StatsController statsController;
    private MainController mainController = new MainController(this);
    private ScanController scanController = new ScanController(this);
    private ProgramController programController = new ProgramController(this);
    private DailyStatController dailyStatController = new DailyStatController(this);

    private ServiceFacade() {
        dataExtractor = new DataExtractor();
        prizeController = new PrizeController(this);
        fileOperationManager = new FileOperationManager();
        profileController = createProfileController();
        this.profileModel = new ProfileModel(profileController);
        statsController = new StatsController(this);
    }

    public static synchronized ServiceFacade getInstance() {
        if (instance == null) {
            instance = new ServiceFacade();
        }
        return instance;
    }

    private ProfileController createProfileController() {
        return new ProfileController(this);
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