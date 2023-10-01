package com.example.cloudcalc;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainUI {

    private final ProfileDataManager profileDataManager = new ProfileDataManager();

    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager();

    public void showMainScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile("profiles.json");
        for (Profile profile : profiles) {
            layout.getChildren().add(createProfileRow(primaryStage, profile));
        }

        layout.getChildren().addAll(createCreateProfileButton(primaryStage), createIgnoreBadgesButton(primaryStage));

        Scene mainScene = new Scene(layout, 400, 300);
        primaryStage.setScene(mainScene);
    }

    public void showProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                createProfileInfo(profile),
                createPdfLinksSection(profile),
                createBackButton(primaryStage)
        );

        Scene detailsScene = new Scene(layout, 400, 300);
        primaryStage.setScene(detailsScene);
    }

    private void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                new Label("Scan Results for " + profile.getName()),
                createBadgeLabels(profile, siteLinks),
                createBackButton(primaryStage)
        );

        Scene countScene = new Scene(layout, 400, 300);
        primaryStage.setScene(countScene);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Profile profile = new Profile();

        TextField nameField = createTextField("Name");
        TextField dateField = createTextField("Start Date (e.g., 26.09.2023)");
        TextField linkField = createTextField("Profile Link");

        layout.getChildren().addAll(
                nameField,
                dateField,
                linkField,
                createUploadPdfButton(primaryStage, profile),
                createSaveButton(primaryStage, profile, nameField, dateField, linkField),
                createBackButton(primaryStage)
        );

        Scene createProfileScene = new Scene(layout, 300, 200);
        primaryStage.setScene(createProfileScene);
    }

    private Button createSaveButton(Stage primaryStage, Profile profile, TextField nameField, TextField dateField, TextField linkField) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText()));
        return saveButton;
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
            profileDataManager.saveProfileToFile(profile, "profiles.json");
            showMainScreen(primaryStage);
        }
    }


    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private Button createUploadPdfButton(Stage primaryStage, Profile profile) {
        FileChooser fileChooser = new FileChooser();
        Button uploadPdfButton = new Button("Upload PDF");
        uploadPdfButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profile.setPdfFilePath(selectedFile.getAbsolutePath());
            }
        });
        return uploadPdfButton;
    }

    private void showIgnoreScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Label label = new Label("Ignore Screen");

        Button addButton = new Button("Add Ignore Badge");
        addButton.setOnAction(e -> showAddIgnoreBadgeScreen(primaryStage));

        List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile("ignored_badges.json");
        for (String badge : ignoredBadges) {
            HBox badgeRow = new HBox(10);

            Label badgeLabel = new Label(badge);
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                ignoredBadges.remove(badge);
                ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, "ignored_badges.json");
                showIgnoreScreen(primaryStage);
            });

            badgeRow.getChildren().addAll(badgeLabel, deleteButton);
            layout.getChildren().add(badgeRow);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));

        layout.getChildren().addAll(label, addButton, backButton);
        Scene countScene = new Scene(layout, 400, 300);
        primaryStage.setScene(countScene);
    }

    private void showAddIgnoreBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Label label = new Label("Add Ignore Badge Screen");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));

        TextField nameField = new TextField();
        nameField.setPromptText("Lab name");

        Button saveButton = new Button("Save Ignore Badge");
        saveButton.setOnAction(e -> {
            List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile("ignored_badges.json");
            ignoredBadges.add(nameField.getText());
            ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, "ignored_badges.json");
            showIgnoreScreen(primaryStage);
        });

        layout.getChildren().addAll(label, nameField, saveButton, backButton);
        Scene countScene = new Scene(layout, 400, 300);
        primaryStage.setScene(countScene);
    }

    private VBox createBadgeLabels(Profile profile, ArrayList<String> siteLinks) {
        VBox labelsBox = new VBox(5);
        Map<String, Integer> badgeCounts = calculateBadgeCounts(profile, siteLinks);

        labelsBox.getChildren().addAll(
                new Label("Total: " + badgeCounts.get("Total")),
                new Label("PDF badges: " + badgeCounts.get("PDF")),
                new Label("Skill badges: " + badgeCounts.get("Skill")),
                new Label("Ignore badges: " + badgeCounts.get("Ignore"))
        );
        return labelsBox;
    }

    private Map<String, Integer> calculateBadgeCounts(Profile profile, ArrayList<String> siteBadges) {
        Map<String, Integer> badgeCounts = new HashMap<>();

        List<String> pdfBadges = new ArrayList<>(profile.getPdfLinks());
        List<String> ignoreBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile("ignored_badges.json");

        int countAll = siteBadges.size();
        int countPdf;
        int countIgnore;
        int countSkill;

        siteBadges.removeAll(pdfBadges);
        countPdf = countAll - siteBadges.size();

        siteBadges.removeAll(ignoreBadges);
        countSkill = siteBadges.size();
        countIgnore = countAll - (countPdf + countSkill);

        badgeCounts.put("Total", countAll);
        badgeCounts.put("PDF", countPdf);
        badgeCounts.put("Skill", countSkill);
        badgeCounts.put("Ignore", countIgnore);

        return badgeCounts;
    }

    private Button createBackButton(Stage primaryStage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));
        return backButton;
    }

    private VBox createPdfLinksSection(Profile profile) {
        VBox linksVBox = new VBox(5);

        Label linksTitle = new Label("PDF Links:");
        linksVBox.getChildren().add(linksTitle);

        if (profile.getPdfLinks() != null) {
            for (String link : profile.getPdfLinks()) {
                Label linkLabel = new Label(link);
                linksVBox.getChildren().add(linkLabel);
            }
        }

        return linksVBox;
    }

    private VBox createProfileInfo(Profile profile) {
        VBox profileInfoBox = new VBox(10);

        Label nameLabel = new Label("Name: " + profile.getName());
        Label startDateLabel = new Label("Start Date: " + profile.getStartDate());
        Label profileLinkLabel = new Label("Profile Link: " + profile.getProfileLink());

        profileInfoBox.getChildren().addAll(nameLabel, startDateLabel, profileLinkLabel);

        return profileInfoBox;
    }

    private Button createIgnoreBadgesButton(Stage primaryStage) {
        Button ignoreButton = new Button("Ignore Badges");
        ignoreButton.setOnAction(e -> showIgnoreScreen(primaryStage));
        return ignoreButton;
    }

    private Button createCreateProfileButton(Stage primaryStage) {
        Button createProfileButton = new Button("Create Profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(primaryStage));
        return createProfileButton;
    }

    private HBox createProfileRow(Stage primaryStage, Profile profile) {
        HBox profileContainer = new HBox(10);

        Button profileButton = new Button(profile.getName());
        profileButton.setOnAction(e -> showProfileScreen(primaryStage, profile));

        Button deleteButton = createDeleteButton(primaryStage, profile);
        Button scanButton = createScanButton(primaryStage, profile);

        profileContainer.getChildren().addAll(profileButton, deleteButton, scanButton);
        return profileContainer;
    }

    private Button createDeleteButton(Stage primaryStage, Profile profile) {
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            handleDeleteAction(primaryStage, profile);
        });
        return deleteButton;
    }

    private void handleDeleteAction(Stage primaryStage, Profile profile) {
        List<Profile> profiles = profileDataManager.loadProfilesFromFile("profiles.json");
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, "profiles.json");
        showMainScreen(primaryStage);
    }

    private Button createScanButton(Stage primaryStage, Profile profile) {
        Button scanButton = new Button("Scan");
        scanButton.setOnAction(e -> {
            ArrayList<String> extractedData = dataExtractor.performScan(profile);
            showScanScreen(primaryStage, profile, extractedData);
        });
        return scanButton;
    }

}
