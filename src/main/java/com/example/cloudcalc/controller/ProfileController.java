package com.example.cloudcalc.controller;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.DateUtils;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.entity.*;
import com.example.cloudcalc.entity.prize.Prize;
import com.example.cloudcalc.entity.prize.PrizeInfo;
import com.example.cloudcalc.exception.ProfilePageStructureChangedException;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.model.CountConditionModel;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.util.Notification;
import com.example.cloudcalc.view.ProfileView;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProfileController extends BaseController {
    private final ProfileView profileView;
    private final ProfileModel profileModel;
    private final DataExtractor dataExtractor;
    private final MainController mainController;
    private final ProgramController programController;
    private final PrizeController prizeController;
    private final ElementsBuilder elementsBuilder;
    private final SceneBuilder sceneBuilder;
    public static ResourceBundle bundle;
    private TableBuilder tableBuilder = new TableBuilder();
    String alertTitleDeleteProfile = "Confirmation Dialog";
    String alertHeaderDeleteProfile = "Delete Profile";
    String alertContentDeleteProfile = "Are you sure you want to delete this profile?";

    public ProfileController(ServiceFacade serviceFacade){
        super(serviceFacade);
        bundle = LanguageManager.getBundle();

        this.profileView = new ProfileView(this);
        this.profileModel = new ProfileModel(this);
        this.dataExtractor = serviceFacade.getDataExtractor();
        this.mainController = serviceFacade.getMainController();
        this.elementsBuilder = new ElementsBuilder();
        this.sceneBuilder = new SceneBuilder();
        this.programController = serviceFacade.getProgramController();
        this.prizeController = serviceFacade.getPrizeController();
    }

    public List<Profile> getProfilesFromFile() {
        return profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
    }

    public boolean scanAndUpdateProfile(Profile profile) {
        List<ProgramPrize> userProgramPrizeList = profile.getProgramPrizes();

        List<Program> allPrograms = loadProgramsFromFile();
        List<Prize> allPrizes = prizeController.loadPrizesFromFile();

        if(!allPrograms.isEmpty() && !userProgramPrizeList.isEmpty()) {
                for (Program program : allPrograms) {
                    for (ProgramPrize programPrize : userProgramPrizeList) {

                        String programName = program.getName();
                        String userProgramName = programPrize.getProgram();

                        if(programName.equals(userProgramName)) {
                            CountConditionModel condition = program.getCondition();
                            List<String> countingList = new ArrayList<>();
                            List<String> ignoreList = new ArrayList<>();
                            if (condition != null) {
                                if (condition.getType().equals("What to count")) {
                                    countingList = condition.getValues();
                                } else if (condition.getType().equals("What not to count")) {
                                    ignoreList = condition.getValues();
                                }
                            }

                            int userPoints = 0;
                            try {
                                LocalDate date = program.getDate();
                                Map<String, String> map = dataExtractor.scanProfileLink(profile, date);

                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    if (!countingList.isEmpty()) {
                                        for(String elem : countingList) {
                                            if(elem.equals(entry.getKey())) {
                                                userPoints++;
                                            }
                                        }
                                    } else if (!ignoreList.isEmpty()) {
                                        boolean shouldCount = true;
                                        for (String elem : ignoreList) {
                                            if (elem.equals(entry.getKey())) {
                                                shouldCount = false;
                                                break;
                                            }
                                        }
                                        if (shouldCount) {
                                            userPoints++;
                                        }
                                    }
                                }
                            } catch (ProfilePageStructureChangedException ex) {
                                Notification.showErrorMessage("Error", "Page structure has changed or empty.");
                                return false;
                            }

                            List<PrizeInfo> prizeInfos = programPrize.getPrizeInfoList();
                            if (prizeInfos == null || prizeInfos.isEmpty()) {
                                prizeInfos = new ArrayList<>();
                                prizeInfos.add(new PrizeInfo());
                            }
                            prizeInfos.get(0).setEarnedPoints(userPoints);

                            if(!allPrizes.isEmpty()) {
                                String userPrizeName = getUserPrizeName(allPrizes, userProgramName, userPoints);
                                prizeInfos.get(0).setPrize(userPrizeName);
                            }
                            programPrize.setPrizeInfoList(prizeInfos);
                        }
                    }
                }
        }

        updateProfile(profile);
        return true;
    }

    private static String getUserPrizeName(List<Prize> allPrizes, String userProgramName, int userPoints) {
        String userPrizeName = "-";
        for (Prize prize : allPrizes) {
            if(prize.getProgram().equals(userProgramName)) {
                String prizeName = prize.getName();
                int prizePoints = prize.getPoints();

                if(userPoints >= prizePoints) {
                    userPrizeName = prizeName;
                }
            }
        }
        return userPrizeName;
    }

    private void updateProfile(Profile profile) {
        profile.setLastScannedDate(DateUtils.getCurrentDate());
        profileModel.updateProfile(profile);
    }

    public HBox createTopLayout(Stage primaryStage){
        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
        return elementsBuilder.createTopLayout(backButton, profileView.getTitleLabel());
    }

    public HBox createTopLayoutForAddScreen(Stage primaryStage){
        Button backButton = ButtonFactory.createBackButton(e -> showScreen(primaryStage));
        return elementsBuilder.createTopLayout(backButton, profileView.getTitleLabel());
    }

    public HBox createTopLayoutEditProfileScreen(Stage primaryStage){
        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
        return elementsBuilder.createTopLayout(backButton, profileView.getTitleLabelEditProfileScreen());
    }

    public void createScene(Stage stage, VBox layout) {
        sceneBuilder.createScene(layout, stage);
    }

    public void createScene(Stage stage, ScrollPane scrollPane) {
        sceneBuilder.createScene(scrollPane, stage);
    }

    public void handleProfileSave(Stage primaryStage, Profile profile){
        profileModel.handleProfileSave(primaryStage, profile);
    }

    public void showMainScreen(Stage primaryStage) {
        mainController.showMainScreen(primaryStage);
    }

    public void handleDeleteAction(Stage primaryStage, Profile profile, MainController mainController) {
        profileModel.handleDeleteAction(primaryStage, profile, mainController);
    }

    public List<Program> loadProgramsFromFile() {
        return programController.loadProgramsFromFile();
    }

    public void toggleUserPrograms(Stage primaryStage, Profile profile) {
        profileView.toggleUserPrograms(primaryStage, profile);
    }

    public HBox createTopLayoutDouble(Stage primaryStage, TextFlow textFlow) {
        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
        return elementsBuilder.createTopLayoutWithBackAndText(backButton, textFlow);
    }

    public Hyperlink createLink(Profile profile) {
        return elementsBuilder.createProfileLink(profile);
    }

    @Override
    public void showScreen(Stage stage) {
        profileView.showScreen(stage);
    }

    @Override
    public void showAddScreen(Stage stage) {
        profileView.showCreateProfileScreen(stage);
    }

    @Override
    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }

    public TableView<Profile> createTable(Stage stage, List<Profile> profiles) {
        return tableBuilder.createTableForProfiles(stage, profiles, this);
    }

    public void showEditProfile(Stage stage, Profile profile) {
        profileView.showEditProfileScreen(stage, profile);
    }

    public void deleteProfile(Stage stage, Profile profile) {
        profileModel.handleDeleteAction(stage, profile);
    }

    public boolean showConfirmationAlert() {
        return Notification.showConfirmationAlert(
                alertTitleDeleteProfile, alertHeaderDeleteProfile, alertContentDeleteProfile
        );
    }
}