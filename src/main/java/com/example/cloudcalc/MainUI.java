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
                showCountScreen(primaryStage, profile, extractedData);
            });
            profileContainer.getChildren().add(scanButton);

            layout.getChildren().add(profileContainer);
        }

        Button createProfileButton = new Button("Create Profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(primaryStage));

        layout.getChildren().add(createProfileButton);

        Scene mainScene = new Scene(layout, 400, 300);
        primaryStage.setScene(mainScene);
    }

    private void showCountScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
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
        int otherBadges = totalBadges - pdfBadges;

        Label totalLabel = new Label("Total: " + totalBadges);
        Label pdfLabel = new Label("PDF badges: " + pdfBadges);
        Label otherLabel = new Label("Other badges: " + otherBadges);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));

        layout.getChildren().addAll(titleLabel, totalLabel, pdfLabel, otherLabel, backButton);

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
