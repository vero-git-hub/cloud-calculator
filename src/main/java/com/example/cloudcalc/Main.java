package com.example.cloudcalc;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {

    Button createProfileButton;

    private final MainUI mainUI = new MainUI();

    @Override
    public void start(Stage stage) {
        createProfileButton = new Button("Create profile");
        createProfileButton.setOnAction(e -> mainUI.showCreateProfileScreen(stage));

        mainUI.showMainScreen(stage);

        stage.setTitle("Cloud Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
