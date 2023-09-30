package com.example.cloudcalc;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainUI {

    private final ProfileDataManager profileDataManager = new ProfileDataManager();

    private final DataExtractor dataExtractor = new DataExtractor();

    public  void showProfileDetails(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Label nameLabel = new Label("Name: " + profile.getName());
        Label startDateLabel = new Label("Start Date: " + profile.getStartDate());
        Label profileLinkLabel = new Label("Profile Link: " + profile.getProfileLink());

        VBox linksVBox = new VBox(5);
        Label linksTitle = new Label("PDF Links:");
        linksVBox.getChildren().add(linksTitle);
        if(profile.getPdfLinks() != null){
            for (String link : profile.getPdfLinks()) {
                Label linkLabel = new Label(link);
                linksVBox.getChildren().add(linkLabel);
            }
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));

        layout.getChildren().addAll(nameLabel, startDateLabel, profileLinkLabel, linksVBox, backButton);

        Scene detailsScene = new Scene(layout, 400, 300);
        primaryStage.setScene(detailsScene);
    }

    public void showMainScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile("profiles.json");
        for (Profile profile : profiles) {
            HBox profileContainer = new HBox(10);

            Button profileButton = new Button(profile.getName());
            profileButton.setOnAction(e -> showProfileDetails(primaryStage, profile));

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                profiles.remove(profile);
                profileDataManager.saveProfilesToFile(profiles, "profiles.json");
                showMainScreen(primaryStage);
            });
            profileContainer.getChildren().addAll(profileButton, deleteButton);

            Button scanButton = new Button("Scan");
            scanButton.setOnAction(e -> {
                ArrayList<String> extractedData = dataExtractor.performScan(profile);
                showScanScreen(primaryStage, profile, extractedData);
            });
            profileContainer.getChildren().add(scanButton);

            layout.getChildren().add(profileContainer);
        }

        Button createProfileButton = new Button("Create Profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(primaryStage));
        layout.getChildren().add(createProfileButton);

        Button ignoreButton = new Button("Ignore Badges");
        ignoreButton.setOnAction(e -> showIgnoreScreen(primaryStage));
        layout.getChildren().add(ignoreButton);

        Scene mainScene = new Scene(layout, 400, 300);
        primaryStage.setScene(mainScene);
    }

    private void showIgnoreScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Label label = new Label("Ignore Screen");

        Button addButton = new Button("Add Ignore Badge");
        addButton.setOnAction(e -> showAddIgnoreBadgeScreen(primaryStage));

        List<String> ignoredBadges = loadIgnoredBadgesFromFile("ignored_badges.json");
        for (String badge : ignoredBadges) {
            HBox badgeRow = new HBox(10);

            Label badgeLabel = new Label(badge);
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                ignoredBadges.remove(badge);
                saveIgnoredBadgesToFile(ignoredBadges, "ignored_badges.json");
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

    private void saveIgnoredBadgesToFile(List<String> ignoredBadges, String fileName) {
        JSONArray jsonArray = new JSONArray(ignoredBadges);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadIgnoredBadgesFromFile(String fileName) {
        List<String> ignoredBadges = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                ignoredBadges.add(jsonArray.getString(j));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return ignoredBadges;
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
            saveIgnoreBadgeToFile(nameField.getText(), "ignored_badges.json");
            showIgnoreScreen(primaryStage);
        });

        layout.getChildren().addAll(label, nameField, saveButton, backButton);
        Scene countScene = new Scene(layout, 400, 300);
        primaryStage.setScene(countScene);
    }

    private void saveIgnoreBadgeToFile(String badgeName, String fileName) {
        JSONArray badgesArray = new JSONArray();
        File file = new File(fileName);

        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                badgesArray = new JSONArray(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        badgesArray.put(badgeName);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(badgesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        Label titleLabel = new Label("Scan Results for " + profile.getName());
        List<String> pdfLinksList = profile.getPdfLinks();

        int pdfBadges = 0;
        int totalBadges = siteLinks.size();
        for (String data : siteLinks) {
            if (pdfLinksList.contains(data)) {
                pdfBadges++;
                pdfLinksList.remove(data);
            }
        }
        List<String> ignoredBadges = loadIgnoredBadgesFromFile("ignored_badges.json");
        int ignoreBadges = ignoredBadges.size();

        int skillBadges = totalBadges - pdfBadges - ignoreBadges;

        Label totalLabel = new Label("Total: " + totalBadges);
        Label pdfLabel = new Label("PDF badges: " + pdfBadges);
        Label skillLabel = new Label("Skill badges: " + skillBadges);
        Label ignoreLabel = new Label("Ignore badges: " + ignoreBadges);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));

        layout.getChildren().addAll(titleLabel, totalLabel, pdfLabel, skillLabel, ignoreLabel, backButton);

        Scene countScene = new Scene(layout, 400, 300);
        primaryStage.setScene(countScene);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Profile profile = new Profile();

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField dateField = new TextField();
        dateField.setPromptText("Start Date (e.g., 26.09.2023)");

        TextField linkField = new TextField();
        linkField.setPromptText("Profile Link");

        FileChooser fileChooser = new FileChooser();
        Button uploadPdfButton = new Button("Upload PDF");
        uploadPdfButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profile.setPdfFilePath(selectedFile.getAbsolutePath());
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if(nameField.getText() != null && dateField.getText() != null
                    && linkField.getText() != null) {
                profile.setName(nameField.getText());
                profile.setStartDate(dateField.getText());
                profile.setProfileLink(linkField.getText());

                if(profile.getPdfFilePath() != null) {
                    List<String> extractedLinks = dataExtractor.extractHiddenLinksFromPdf(profile.getPdfFilePath());
                    List<String> h1Contents = dataExtractor.extractH1FromLinks(extractedLinks);
                    profile.setPdfLinks(h1Contents);
                }

                profileDataManager.saveProfileToFile(profile, "profiles.json");
                showMainScreen(primaryStage);
            }

        });

        layout.getChildren().addAll(nameField, dateField, linkField, uploadPdfButton, saveButton);

        Scene createProfileScene = new Scene(layout, 300, 200);
        primaryStage.setScene(createProfileScene);
    }

}
