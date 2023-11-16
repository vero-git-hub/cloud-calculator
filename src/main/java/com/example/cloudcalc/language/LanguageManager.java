package com.example.cloudcalc.language;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.builder.text.fields.BadgeFieldManager;
import com.example.cloudcalc.builder.text.fields.PrizeFieldManager;
import com.example.cloudcalc.builder.text.fields.TypeBadgeFieldsManager;
import com.example.cloudcalc.constant.FileName;
import javafx.scene.control.ComboBox;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    public static ComboBox<Language> languageComboBox;
    private static List<Localizable> localizables = new ArrayList<>();

    private static BadgeFieldManager textFieldManager;
    private static PrizeFieldManager prizeTextFieldManager;
    private static TypeBadgeFieldsManager typeBadgeFieldsManager;

    public static void setTextFieldManager(BadgeFieldManager manager) {
        textFieldManager = manager;
    }

    public static BadgeFieldManager getTextFieldManager() {
        return textFieldManager;
    }

    public static PrizeFieldManager getTextFieldPrizeManager() {
        return prizeTextFieldManager;
    }

    public static void setTextFieldPrizeManager(PrizeFieldManager prizeTextFieldManager) {
        LanguageManager.prizeTextFieldManager = prizeTextFieldManager;
    }

    public static TypeBadgeFieldsManager getTypeBadgeTextFieldsManager() {
        return typeBadgeFieldsManager;
    }

    public static void setTypeBadgeTextFieldsManager(TypeBadgeFieldsManager typeBadgeFieldsManager) {
        LanguageManager.typeBadgeFieldsManager = typeBadgeFieldsManager;
    }

    public static void registerLocalizable(Localizable localizable) {
        localizables.add(localizable);
    }

    public static void updateLocalizableComponents(ResourceBundle bundle) {
        for (Localizable localizable : localizables) {
            localizable.updateLocalization(bundle);
        }
    }

    public static ComboBox<Language> createLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new ComboBox<>();
            languageComboBox.getItems().addAll(
                    new Language("en", "English"),
                    new Language("ru", "Русский"),
                    new Language("uk", "Українська")
            );
        }

        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

        String savedLanguageCode = settings.optString("language", "en");

        Language savedLanguage = languageComboBox.getItems().stream()
                .filter(language -> language.getCode().equals(savedLanguageCode))
                .findFirst()
                .orElse(new Language("en", "English"));

        languageComboBox.setValue(savedLanguage);
        languageComboBox.valueProperty().addListener((obs, oldLanguage, newLanguage) -> {
            if (newLanguage != null) {
                Locale locale = new Locale(newLanguage.getCode());
                ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
                saveLanguagePreference();
                updateLocalizableComponents(bundle);
            }
        });

        return languageComboBox;
    }

    public static void saveLanguagePreference() {
        Language selectedLanguage = languageComboBox.getValue();
        if (selectedLanguage != null) {
            String languageCode = selectedLanguage.getCode();
            JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

            settings.put("language", languageCode);
            FileManager.writeJsonToFile(settings, FileName.SETTINGS_FILE);
        }

    }

}
