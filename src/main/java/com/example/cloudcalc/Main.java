package com.example.cloudcalc;

import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.language.LanguageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ServiceFacade serviceFacade = ServiceFacade.getInstance();
        MainController mainController = new MainController(serviceFacade);
        LanguageManager.registerLocalizable(mainController.getMainView());

        mainController.showMainScreen(stage);

        String savedLanguageCode = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE).optString("language", "en");
        Locale locale = new Locale(savedLanguageCode);
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        LanguageManager.updateLocalizableComponents(bundle);

        TextFieldManager textFieldManager = new TextFieldManager(bundle);
        LanguageManager.setTextFieldManager(textFieldManager);


        stage.setTitle("Cloud Calculator");
        stage.setOnCloseRequest(event -> LanguageManager.saveLanguagePreference());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
