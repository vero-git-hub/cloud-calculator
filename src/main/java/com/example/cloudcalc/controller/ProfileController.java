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
import com.example.cloudcalc.entity.program.Program;
import com.example.cloudcalc.entity.program.SpecialConditions;
import com.example.cloudcalc.exception.ProfilePageStructureChangedException;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.entity.program.CountCondition;
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
        // Read user programs (name, points, prizes)- profiles.json
        List<ProgramPrize> userProgramPrizeList = profile.getProgramPrizes();

        // Read programs - programs.json
        List<Program> allPrograms = loadProgramsFromFile();
        // Read prizes - prizes.json
        List<Prize> allPrizes = prizeController.loadPrizesFromFile();

        // if programs and user programs exist
        if(!allPrograms.isEmpty() && !userProgramPrizeList.isEmpty()) {
                // Iterate every program (programs from programs.json)
                for (Program program : allPrograms) {
                    String programName = program.getName();

                    // Get special conditions for the program
                    SpecialConditions specialConditions = program.getSpecialConditions();
                    List<String> badgesToIgnore = specialConditions != null ? specialConditions.getBadgesToIgnore() : new ArrayList<>();
                    int countMergeBadges = specialConditions != null ? specialConditions.getBadgesPerPoint() : 0;
                    int pointsPerBadge = specialConditions != null ? specialConditions.getPointsPerBadge() : 0;

                    // Iterate user programs
                    for (ProgramPrize programPrize : userProgramPrizeList) {
                        String userProgramName = programPrize.getProgram();

                        // If program matches with user program
                        if(programName.equals(userProgramName)) {
                            // Define program conditions
                            List<CountCondition> conditions = program.getConditions();

                            // Separate conditions on the "What to count" and "What not to count" lists
                            List<CountCondition.Badges> countingList = new ArrayList<>();
                            List<CountCondition.Badges> ignoreList = new ArrayList<>();

                            if (conditions != null) {
                                for (CountCondition condition : conditions) {
                                    if (condition.getType().equals("What to count")) {
                                        countingList.addAll(condition.getValues());
                                    } else if (condition.getType().equals("What not to count")) {
                                        ignoreList.addAll(condition.getValues());
                                    }
                                }
                            }

                            int userPoints = 0;
                            int countSkillBadges = 0;
                            try {
                                // Start date to count
                                LocalDate date = program.getDate();
                                // Profile scanning starting from this date (above)
                                Map<String, String> map = dataExtractor.scanProfileLink(profile, date);

                                // Iterate over each map element: Loop through all entries in the profile data map
                                for (Map.Entry<String, String> profileBadge : map.entrySet()) {
                                    // Extract the key (badge name) from the current entry
                                    String badgeTitleProfile = profileBadge.getKey();
                                    boolean isCounted = false;

                                    // Check the list of "What to count"
                                    if (!countingList.isEmpty()) {
                                        // Iterate badge in conditions
                                        for (CountCondition.Badges badge: countingList) {
                                            String badgeTitle = badge.getTitle();
                                            if (badgeTitle.equals(badgeTitleProfile)) {
                                                // When calculating, add the number of points indicated for this badge
                                                int badgePoints = badge.getPoints();
                                                userPoints += badgePoints;
                                                isCounted = true;
                                                //System.out.println("count -> " + badgeTitle);
                                                break;
                                            }
                                        }
                                    // Check the list of "What not to count"
                                    }
                                    else if (!ignoreList.isEmpty()) {
                                        // If countingList is empty
                                        isCounted = true;
                                        for (CountCondition.Badges badge : ignoreList) {
                                            String badgeTitle = badge.getTitle();
                                            if (badgeTitle.equals(badgeTitleProfile)) {
                                                isCounted = false;
                                                //System.out.println("ignore -> " + badgeTitle);
                                                break;
                                            }
                                        }
                                        // Add [one] point if the badge counted, and it is not on the ignore list.
                                        // i.e. not count "ignore", and count all the rest
                                        if (isCounted) {
                                            userPoints++;
                                        }
                                    }

                                    // Handle special conditions if the list is not empty
                                    if(!badgesToIgnore.isEmpty() && countMergeBadges > 0) {
                                        boolean isBadgeIgnored = badgesToIgnore.contains(badgeTitleProfile);

                                        if (!isBadgeIgnored) {
                                            // Increment the count of skill badges if the badge is not in the ignore list
                                            countSkillBadges++;
                                        }
                                    }
                                }

                                // Apply special conditions after all other calculations
                                if (countMergeBadges > 0) {
                                    int additionalPoints = (countSkillBadges / countMergeBadges) * pointsPerBadge;
                                    userPoints += additionalPoints;
                                }

                            } catch (ProfilePageStructureChangedException ex) {
                                Notification.showErrorMessage("Error scanning profile", "Reason: The profile link is invalid or the page structure has changed.");
                                return false;
                            }

                            // Add prize in profile
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

        // update last scanning date
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

    public List<Profile> findProfilesByName(List<String> names) {
        return profileModel.findProfilesByName(names);
    }
}