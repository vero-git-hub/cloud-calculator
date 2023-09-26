package com.example.cloudcalc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    Button createProfileButton;

    @Override
    public void start(Stage stage) {
        createProfileButton = new Button("Create profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(stage));

        VBox root = new VBox(20);
        root.getChildren().add(createProfileButton);

        Scene scene = new Scene(root, 300, 200);
        stage.setTitle("Cloud Calculator");
        stage.setScene(scene);
        stage.show();
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
            profile.setName(nameField.getText());
            profile.setStartDate(dateField.getText());
            profile.setProfileLink(linkField.getText());

            List<String> extractedLinks = extractLinkTitlesFromPdf(profile.getPdfFilePath());
            profile.setExtractedLinks(extractedLinks);

            saveProfileToFile(profile, "profiles.json");

            primaryStage.setScene(new Scene(new VBox(20, createProfileButton), 300, 200));
        });

        layout.getChildren().addAll(nameField, dateField, linkField, uploadPdfButton, saveButton);

        Scene createProfileScene = new Scene(layout, 300, 200);
        primaryStage.setScene(createProfileScene);
    }

    private void saveProfileToFile(Profile profile, String fileName) {
        JSONObject profileJson = new JSONObject();
        profileJson.put("name", profile.getName());
        profileJson.put("startDate", profile.getStartDate());
        profileJson.put("profileLink", profile.getProfileLink());
        profileJson.put("pdfFilePath", profile.getPdfFilePath());

        JSONArray linksArray = new JSONArray(profile.getExtractedLinks());
        profileJson.put("extractedLinks", linksArray);

        try (FileWriter file = new FileWriter(fileName, true)) {
            file.write(profileJson.toString());
            file.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> extractLinkTitlesFromPdf(String pdfFilePath) {
        List<String> linkTitles = new ArrayList<>();

        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            for (PDPage page : document.getPages()) {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annotation;
                        if (link.getAction() instanceof PDActionURI) {
                            PDActionURI uri = (PDActionURI) link.getAction();
                            String linkUri = uri.getURI();
                            if (linkUri.startsWith("https://www.cloudskillsboost.google/")) {
                                String linkTitle = link.getContents();
                                if (linkTitle != null && !linkTitle.isEmpty()) {
                                    linkTitles.add(linkTitle);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkTitles;
    }

}
