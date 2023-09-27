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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

            List<String> extractedLinks = extractHiddenLinksFromPdf(profile.getPdfFilePath());
            System.out.println(extractedLinks);
            List<String> h1Contents = extractH1FromLinks(extractedLinks);
            profile.setExtractedLinks(h1Contents);

            saveProfileToFile(profile, "profiles.json");

            primaryStage.setScene(new Scene(new VBox(20, createProfileButton), 300, 200));
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

}
