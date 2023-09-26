package com.example.cloudcalc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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

            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            primaryStage.setScene(new Scene(new VBox(20, createProfileButton), 300, 200));
        });

        layout.getChildren().addAll(nameField, dateField, linkField, uploadPdfButton, saveButton);

        Scene createProfileScene = new Scene(layout, 300, 200);
        primaryStage.setScene(createProfileScene);
    }

}
