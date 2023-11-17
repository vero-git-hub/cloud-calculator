package com.example.cloudcalc;

import com.example.cloudcalc.builder.fields.badge.BadgeFieldManager;
import com.example.cloudcalc.builder.fields.prize.PrizeFieldManager;
import com.example.cloudcalc.builder.fields.profile.ProfileFieldManager;
import com.example.cloudcalc.builder.fields.type.TypeBadgeFieldManager;
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

        initializeLocalization();

        stage.setTitle("Cloud Calculator");
        stage.setOnCloseRequest(event -> LanguageManager.saveLanguagePreference());
        stage.show();
    }

    private void initializeLocalization() {
        String savedLanguageCode = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE).optString("language", "en");
        Locale locale = new Locale(savedLanguageCode);
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        LanguageManager.updateLocalizableComponents(bundle);

        BadgeFieldManager textFieldManager = new BadgeFieldManager(bundle);
        LanguageManager.setTextFieldManager(textFieldManager);

        PrizeFieldManager prizeTextFieldManager = new PrizeFieldManager(bundle);
        LanguageManager.setTextFieldPrizeManager(prizeTextFieldManager);

        TypeBadgeFieldManager typeBadgeFieldManager = new TypeBadgeFieldManager(bundle);
        LanguageManager.setTypeBadgeTextFieldsManager(typeBadgeFieldManager);

        ProfileFieldManager profileFieldManager = new ProfileFieldManager(bundle);
        LanguageManager.setProfileFieldManager(profileFieldManager);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
