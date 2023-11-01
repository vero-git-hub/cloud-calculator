package com.example.cloudcalc.language;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.FileManager;
import javafx.scene.control.ComboBox;
import org.json.JSONObject;

public class LanguageManager {

    public static ComboBox<Language> languageComboBox;

    public static ComboBox<Language> createLanguageComboBox() {
        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll(
                new Language("en", "English"),
                new Language("ru", "Русский"),
                new Language("uk", "Українська")
        );

        JSONObject settings = FileManager.readJsonObjectFromFile(Constants.SETTINGS_FILE);

        String savedLanguageCode = settings.optString("language", "en");

        Language savedLanguage = languageComboBox.getItems().stream()
                .filter(language -> language.getCode().equals(savedLanguageCode))
                .findFirst()
                .orElse(new Language("en", "English"));

        languageComboBox.setValue(savedLanguage);
        languageComboBox.valueProperty().addListener((obs, oldLanguage, newLanguage) -> {

        });

        return languageComboBox;
    }

    public static void saveLanguagePreference() {
        Language selectedLanguage = languageComboBox.getValue();
        if (selectedLanguage != null) {
            String languageCode = selectedLanguage.getCode();

            JSONObject settings = FileManager.readJsonObjectFromFile(Constants.SETTINGS_FILE);

            settings.put("language", languageCode);

            FileManager.writeJsonToFile(settings, Constants.SETTINGS_FILE);
        }

    }

}
