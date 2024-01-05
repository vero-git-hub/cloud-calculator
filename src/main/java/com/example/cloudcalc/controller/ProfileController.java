package com.example.cloudcalc.controller;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.DateUtils;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.view.ProfileView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    private ServiceFacade serviceFacade;
    private final ProfileView profileView;
    private final ProfileModel profileModel;
    private final DataExtractor dataExtractor;
    //private final ScanView scanView;
    private final MainController mainController;
    private final ProgramController programController;
    private final ElementsBuilder elementsBuilder;
    private final SceneBuilder sceneBuilder;
    private final TableBuilder tableBuilder = new TableBuilder();

    public ProfileController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
        this.profileView = new ProfileView(this);
        this.profileModel = new ProfileModel(this);
        this.dataExtractor = serviceFacade.getDataExtractor();
        //this.scanView = serviceFacade.getScanManager();
        this.mainController = serviceFacade.getMainController();
        this.elementsBuilder = new ElementsBuilder();
        this.sceneBuilder = new SceneBuilder();
        this.programController = serviceFacade.getProgramController();
    }

    public MainController getMainController() {
        return mainController;
    }

    public ProfileModel getProfileModel() {
        return profileModel;
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        profileView.showCreateProfileScreen(primaryStage);
    }

    public void showEditProfileScreen(Stage primaryStage, Profile profile) {
        profileView.showEditProfileScreen(primaryStage, profile);
    }

    public List<Profile> getProfilesFromFile() {
        return profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
    }

    public void scanAndUpdateProfile(Stage primaryStage, Profile profile) {
        ArrayList<String> siteLinks = dataExtractor.performScan(profile);
        profile.setLastScannedDate(DateUtils.getCurrentDate());
        profileModel.updateProfile(profile);

        //scanView.showScreen(primaryStage, profile, siteLinks);
    }

    public void showProfileDetailsScreen(Stage stage, Profile profile) {
        profileView.showProfileScreen(stage, profile);
    }

    public VBox createProfileInfoForProfile(Profile profile, String startDateText) {
        return elementsBuilder.createProfileInfoForProfile(profile, startDateText);
    }

    public VBox createPdfLinksSectionForProfile(Profile profile, Label linksTitle) {
        return tableBuilder.createPdfLinksSectionForProfile(profile, linksTitle);
    }

    public HBox createTopLayoutWithBackAndText(Button backButton, TextFlow textFlow) {
        return elementsBuilder.createTopLayoutWithBackAndText(backButton, textFlow);
    }

    public HBox createTopLayout(Stage primaryStage){
        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
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

    public List<String> extractHiddenLinksFromPdf(String pdfFilePath) {
        return dataExtractor.extractHiddenLinksFromPdf(pdfFilePath);
    }

    public List<String> extractH1FromLinks(List<String> extractedLinks) {
        return dataExtractor.extractH1FromLinks(extractedLinks);
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
}