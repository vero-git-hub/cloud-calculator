package com.example.cloudcalc;

import com.example.cloudcalc.language.LanguageManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private final MainUI mainUI = new MainUI();

    @Override
    public void start(Stage stage) {
        mainUI.showMainScreen(stage);

        stage.setTitle("Cloud Calculator");
        stage.setOnCloseRequest(event -> {
            LanguageManager.saveLanguagePreference();
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
