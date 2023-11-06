package com.example.cloudcalc;

import com.example.cloudcalc.controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ServiceFacade serviceFacade = ServiceFacade.getInstance();
        MainController mainController = new MainController(serviceFacade);
//        LanguageManager.registerLocalizable(mainController.getMainView());

        mainController.showMainScreen(stage);

//        String savedLanguageCode = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE).optString("language", "en");
//        Locale locale = new Locale(savedLanguageCode);
//        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
//        LanguageManager.updateLocalizableComponents(bundle);

        stage.setTitle("Cloud Calculator");
        //stage.setOnCloseRequest(event -> LanguageManager.saveLanguagePreference());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
