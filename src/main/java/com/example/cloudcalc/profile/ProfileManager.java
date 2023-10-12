package com.example.cloudcalc.profile;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.UICallbacks;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ProfileManager {

    private final UICallbacks uiCallbacks;
    private final DataExtractor dataExtractor;
    private final ProfileDataManager profileDataManager;

    public ProfileManager(DataExtractor dataExtractor, ProfileDataManager profileDataManager, UICallbacks uiCallbacks) {
        this.uiCallbacks = uiCallbacks;
        this.dataExtractor = dataExtractor;
        this.profileDataManager = profileDataManager;
    }

    public void showProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));

        layout.getChildren().addAll(
                backButton,
                createProfileInfo(profile),
                uiCallbacks.createPdfLinksSection(profile)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Profile profile = new Profile();

        TextField nameField = uiCallbacks.createTextField("Name");
        TextField dateField = uiCallbacks.createTextField("Start Date (e.g., 26.09.2023)");
        TextField linkField = uiCallbacks.createTextField("Profile Link");

        Button uploadPdfButton = ButtonFactory.createUploadPdfButton(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profile.setPdfFilePath(selectedFile.getAbsolutePath());
            }
        });

        Button saveButton = ButtonFactory.createSaveButton(e -> handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText()));

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("Create Profile");

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                linkField,
                uploadPdfButton,
                saveButton
        );

        uiCallbacks.createScene(layout, primaryStage);
    }

    private void handleProfileSave(Stage primaryStage, Profile profile, String name, String startDate, String profileLink) {
        if(name != null && !name.isEmpty() &&
                startDate != null && !startDate.isEmpty() &&
                profileLink != null && !profileLink.isEmpty()) {

            profile.setName(name);
            profile.setStartDate(startDate);
            profile.setProfileLink(profileLink);

            if(profile.getPdfFilePath() != null) {
                List<String> extractedLinks = dataExtractor.extractHiddenLinksFromPdf(profile.getPdfFilePath());
                List<String> h1Contents = dataExtractor.extractH1FromLinks(extractedLinks);
                profile.setPdfLinks(h1Contents);
            }
            profileDataManager.saveProfileToFile(profile, Constants.PROFILES_FILE);
            uiCallbacks.showMainScreen(primaryStage);
        }
    }

    private VBox createProfileInfo(Profile profile) {
        VBox profileInfoBox = new VBox(10);

        TextFlow nameFlow = uiCallbacks.createTextFlow("Name: ", profile.getName());
        TextFlow startDateFlow = uiCallbacks.createTextFlow("Start Date: ", profile.getStartDate());
        TextFlow profileLinkFlow = uiCallbacks.createTextFlow("Profile Link: ", profile.getProfileLink());

        profileInfoBox.getChildren().addAll(nameFlow, startDateFlow, profileLinkFlow);

        return profileInfoBox;
    }
}
