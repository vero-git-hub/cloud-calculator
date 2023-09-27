package com.example.cloudcalc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    Button createProfileButton;

    @Override
    public void start(Stage stage) {
        createProfileButton = new Button("Create profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(stage));

        showMainScreen(stage);

        stage.setTitle("Cloud Calculator");
        stage.show();
    }

    private void showProfileDetails(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Label nameLabel = new Label("Name: " + profile.getName());
        Label startDateLabel = new Label("Start Date: " + profile.getStartDate());
        Label profileLinkLabel = new Label("Profile Link: " + profile.getProfileLink());

        VBox linksVBox = new VBox(5);
        Label linksTitle = new Label("Extracted Links:");
        linksVBox.getChildren().add(linksTitle);
        if(profile.getExtractedLinks() != null){
            for (String link : profile.getExtractedLinks()) {
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

    public static void main(String[] args) {
        launch(args);
    }

    private void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Profile profile = new Profile();

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField dateField = new TextField();
        dateField.setPromptText("Start Date (e.g., Sep 26, 2023)");

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
                    List<String> extractedLinks = extractHiddenLinksFromPdf(profile.getPdfFilePath());
                    List<String> h1Contents = extractH1FromLinks(extractedLinks);
                    profile.setExtractedLinks(h1Contents);
                }

                saveProfileToFile(profile, "profiles.json");
                showMainScreen(primaryStage);
            }

        });

        layout.getChildren().addAll(nameField, dateField, linkField, uploadPdfButton, saveButton);

        Scene createProfileScene = new Scene(layout, 300, 200);
        primaryStage.setScene(createProfileScene);
    }

    public List<String> extractH1FromLinks(List<String> extractedLinks) {
        List<String> h1Contents = new ArrayList<>();

        for (String link : extractedLinks) {
            try {
                Document doc = Jsoup.connect(link).timeout(10 * 1000).get();
                Elements h1Elements = doc.select("h1[class=\"ql-headline-1\"]");
                for (int i = 0; i < h1Elements.size(); i++) {
                    String str = h1Elements.get(i).text();
                    h1Contents.add(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return h1Contents;
    }

    private void saveProfileToFile(Profile profile, String fileName) {
        JSONArray profilesArray = new JSONArray();
        File file = new File(fileName);

        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                profilesArray = new JSONArray(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updateProfileFile(profile, profilesArray);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(profilesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> extractHiddenLinksFromPdf(String pdfFilePath) {
        List<String> links = new ArrayList<>();

        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            for (PDPage page : document.getPages()) {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annotation;
                        if (link.getAction() instanceof PDActionURI) {
                            PDActionURI uri = (PDActionURI) link.getAction();
                            String linkUrl = uri.getURI();
                            if (linkUrl.startsWith("https://www.cloudskillsboost.google/")) {
                                links.add(linkUrl);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return links;
    }

    private List<Profile> loadProfilesFromFile(String fileName) {
        List<Profile> profiles = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = jsonArray.getJSONObject(j);
                Profile profile = new Profile();
                profile.setName(json.getString("name"));
                profile.setStartDate(json.getString("startDate"));
                profile.setProfileLink(json.getString("profileLink"));
                if (json.has("pdfFilePath")) {
                    profile.setPdfFilePath(json.getString("pdfFilePath"));
                }
                if (json.has("extractedLinks")) {
                    JSONArray linksArray = json.getJSONArray("extractedLinks");
                    List<String> links = new ArrayList<>();
                    for (int i = 0; i < linksArray.length(); i++) {
                        links.add(linksArray.getString(i));
                    }
                    profile.setExtractedLinks(links);
                }

                profiles.add(profile);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return profiles;
    }


    private void showMainScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Profile> profiles = loadProfilesFromFile("profiles.json");
        for (Profile profile : profiles) {
            HBox profileContainer = new HBox(10);

            Button profileButton = new Button(profile.getName());
            profileButton.setOnAction(e -> showProfileDetails(primaryStage, profile));

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                profiles.remove(profile);
                saveProfilesToFile(profiles, "profiles.json");
                showMainScreen(primaryStage);
            });
            profileContainer.getChildren().addAll(profileButton, deleteButton);

            layout.getChildren().add(profileContainer);
        }

        Button createProfileButton = new Button("Create Profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(primaryStage));

        layout.getChildren().add(createProfileButton);

        Scene mainScene = new Scene(layout, 400, 300);
        primaryStage.setScene(mainScene);
    }

    private void saveProfilesToFile(List<Profile> profilesList, String fileName) {
        JSONArray profilesArray = new JSONArray();

        for (Profile profile : profilesList) {
            updateProfileFile(profile, profilesArray);
        }

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(profilesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProfileFile(Profile profile, JSONArray profilesArray) {
        JSONObject profileJson = new JSONObject();
        profileJson.put("name", profile.getName());
        profileJson.put("startDate", profile.getStartDate());
        profileJson.put("profileLink", profile.getProfileLink());
        profileJson.put("pdfFilePath", profile.getPdfFilePath());
        JSONArray linksArray = new JSONArray(profile.getExtractedLinks());
        profileJson.put("extractedLinks", linksArray);

        profilesArray.put(profileJson);
    }

}
