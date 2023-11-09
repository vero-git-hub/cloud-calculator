package com.example.cloudcalc.controller;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.DateUtils;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.scan.ScanManager;
import com.example.cloudcalc.view.ProfileView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    private ServiceFacade serviceFacade;
    private ProfileView profileView;
    private final ProfileModel profileModel;
    private final DataExtractor dataExtractor;
    private final ScanManager scanManager;
    private final MainController mainController;
    private final ElementsBuilder elementsBuilder;
    private final SceneBuilder sceneBuilder;

    public ProfileController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
        this.profileView = new ProfileView(this);
        this.profileModel = new ProfileModel(this);
        this.dataExtractor = serviceFacade.getDataExtractor();
        this.scanManager = serviceFacade.getScanManager();
        this.mainController = serviceFacade.getMainController();
        this.elementsBuilder = new ElementsBuilder();
        this.sceneBuilder = new SceneBuilder();
    }

    public void handleDeleteAction(Stage primaryStage, Profile profile, MainController mainController) {
        profileModel.handleDeleteAction(primaryStage, profile, mainController);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        profileView.showCreateProfileScreen(primaryStage);
    }

    public List<Profile> getProfilesFromFile() {
        return profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
    }


    public void scanAndUpdateProfile(Stage primaryStage, Profile profile) {
        ArrayList<String> siteLinks = dataExtractor.performScan(profile);
        profile.setLastScannedDate(DateUtils.getCurrentDate());
        profileModel.updateProfile(profile);

        scanManager.showScanScreen(primaryStage, profile, siteLinks);
    }

    public void showProfileDetailsScreen(Stage primaryStage, Profile profile) {

    }

    public HBox createTopLayout(Stage primaryStage){
        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
        return elementsBuilder.createTopLayout(backButton, profileView.getTitleLabel());
    }

    public void createScene(VBox layout, Stage primaryStage) {
        sceneBuilder.createScene(layout, primaryStage);
    }

    public void handleProfileSave(Stage primaryStage, Profile profile, String name, String startDate, String profileLink){
        profileModel.handleProfileSave(primaryStage, profile, name, startDate, profileLink);
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

}
