package com.example.cloudcalc.controller;

import com.example.cloudcalc.*;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.view.ProfileView;
import com.example.cloudcalc.scan.ScanManager;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    private ServiceFacade serviceFacade;
    private ProfileView profileView;
    private final ProfileModel profileModel;
    private final DataExtractor dataExtractor;
    private final ScanManager scanManager;

    public ProfileController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
        this.profileView = new ProfileView(this);
        this.profileModel = new ProfileModel();
        this.dataExtractor = serviceFacade.getDataExtractor();
        this.scanManager = serviceFacade.getScanManager();
    }

    public void handleDeleteAction(Stage primaryStage, Profile profile, MainController mainController) {
        profileModel.handleDeleteAction(primaryStage, profile, mainController);
    }


    public void handleProfileSave() {
        profileModel.handleProfileSave();
    }


    public void showCreateProfileScreen(Stage primaryStage) {
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

    public void showProfileScreen(Stage primaryStage, Profile profile) {

    }
}
