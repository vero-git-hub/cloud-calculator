package com.example.cloudcalc;

import com.example.cloudcalc.language.LanguageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainUI mainUI = new MainUI();
        LanguageManager.registerLocalizable(mainUI);

        mainUI.showMainScreen(stage);

        String savedLanguageCode = FileManager.readJsonObjectFromFile(Constants.SETTINGS_FILE).optString("language", "en");
        Locale locale = new Locale(savedLanguageCode);
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        LanguageManager.updateLocalizableComponents(bundle);

        stage.setTitle("Cloud Calculator");
        stage.setOnCloseRequest(event -> LanguageManager.saveLanguagePreference());
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }

}
