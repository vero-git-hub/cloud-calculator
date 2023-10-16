package com.example.cloudcalc.profile;

import com.example.cloudcalc.*;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.prize.PrizeManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileManager {

    private final UICallbacks uiCallbacks;
    private final DataExtractor dataExtractor;
    private final ProfileDataManager profileDataManager;
    private final BadgeManager badgeManager;
    private final List<String> receivedPrizes = new ArrayList<>();

    public ProfileManager(DataExtractor dataExtractor, ProfileDataManager profileDataManager, UICallbacks uiCallbacks, PrizeManager prizeManager) {
        this.uiCallbacks = uiCallbacks;
        this.dataExtractor = dataExtractor;
        this.profileDataManager = profileDataManager;
        this.badgeManager = new BadgeManager(dataExtractor, prizeManager, uiCallbacks);
    }

    public void showProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));

        layout.getChildren().addAll(
                backButton,
                createProfileInfo(profile),
                createPdfLinksSection(profile)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    private VBox createPdfLinksSection(Profile profile) {
        VBox linksVBox = new VBox(5);

        Label linksTitle = new Label("PDF Links:");
        linksTitle.setStyle("-fx-font-weight: bold;");
        linksVBox.getChildren().add(linksTitle);

        TableView<PdfLinkItem> table = new TableView<>();

        TableColumn<PdfLinkItem, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));

        TableColumn<PdfLinkItem, String> linkColumn = new TableColumn<>("Link");
        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));

        indexColumn.setResizable(false);
        linkColumn.setResizable(false);

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        linkColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.95));

        table.getColumns().addAll(indexColumn, linkColumn);

        if (profile.getPdfLinks() != null) {
            int index = 1;
            for (String link : profile.getPdfLinks()) {
                table.getItems().add(new PdfLinkItem(index++, link));
            }
        }

        linksVBox.getChildren().add(table);

        return linksVBox;
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

    public HBox createProfileRow(Stage primaryStage, Profile profile) {
        HBox profileContainer = new HBox(10);

        Button detailButton = ButtonFactory.createButton("Details", e -> {
            showProfileScreen(primaryStage, profile);
        }, null);

        EventHandler<ActionEvent> deleteAction = e -> {
            if (uiCallbacks.showConfirmationAlert("Confirmation Dialog", "Delete Profile", "Are you sure you want to delete this profile?")) {
                handleDeleteAction(primaryStage, profile);
            }
        };

        Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

        profileContainer.getChildren().addAll(
                ButtonFactory.createButton(profile.getName(), e -> {
                    ArrayList<String> extractedData = dataExtractor.performScan(profile);
                    showScanScreen(primaryStage, profile, extractedData);
                }, null),
                detailButton,
                deleteButton
        );
        return profileContainer;
    }

    public void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("Scan Results for " + profile.getName());

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                createBadgeLabels(profile, siteLinks)
        );

        uiCallbacks.createScene(layout, primaryStage);
    }

    private VBox createBadgeLabels(Profile profile, ArrayList<String> siteLinks) {
        VBox labelsBox = new VBox(5);
        Map<String, String> badgeCounts = badgeManager.calculateBadgeCounts(profile, siteLinks);
        receivedPrizes.clear();
        receivedPrizes.addAll(badgeManager.getReceivedPrizes());

        String prizesStr = String.join(", ", receivedPrizes);

        labelsBox.getChildren().addAll(
                uiCallbacks.createTextFlow("Total: ", String.valueOf(badgeCounts.get(Constants.TOTAL))),
                uiCallbacks.createLabel("Ignore: " + badgeCounts.get(Constants.IGNORE)),
                uiCallbacks.createTextFlow("Skill: ", String.valueOf(badgeCounts.get(Constants.SKILL))),
                uiCallbacks.createLabel("PDF total: " + badgeCounts.get(Constants.PDF_TOTAL)),
                uiCallbacks.createTextFlow("PDF for prize: ", String.valueOf(badgeCounts.get(Constants.PDF_FOR_PRIZE))),
                uiCallbacks.createTextFlow("Skill for prize: ", String.valueOf(badgeCounts.get(Constants.SKILL_FOR_PRIZE))),
                uiCallbacks.createTextFlow("Skill for activity: ", String.valueOf(badgeCounts.get(Constants.SKILL_FOR_ACTIVITY))),
                uiCallbacks.createTextFlow("Skill for pl-02.10.2023: ", String.valueOf(badgeCounts.get(Constants.SKILL_FOR_PL))),
                uiCallbacks.createTextFlow("Prize received: ", prizesStr.isEmpty() ? "None" : prizesStr)
        );
        return labelsBox;
    }

    private void handleDeleteAction(Stage primaryStage, Profile profile) {
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, Constants.PROFILES_FILE);

        uiCallbacks.showMainScreen(primaryStage);
    }
}
