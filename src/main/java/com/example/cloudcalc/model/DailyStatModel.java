package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_code
 **/
public class DailyStatModel {

    public void saveSelectedProfiles(Stage stage, List<String> selectedProfiles) {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

        JSONObject dailyStat;
        if (settings.has("dailyStat")) {
            dailyStat = settings.getJSONObject("dailyStat");
        } else {
            dailyStat = new JSONObject();
            settings.put("dailyStat", dailyStat);
        }

        JSONArray profilesArray = new JSONArray(selectedProfiles);
        dailyStat.put("selectedProfiles", profilesArray);

        FileManager.writeJsonToFile(settings, FileName.SETTINGS_FILE);
    }

    public List<String> loadSelectedProfiles() {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);
        List<String> selectedProfiles = new ArrayList<>();

        if (settings.has("dailyStat")) {
            JSONObject dailyStat = settings.getJSONObject("dailyStat");
            if (dailyStat.has("selectedProfiles")) {
                JSONArray profilesArray = dailyStat.getJSONArray("selectedProfiles");
                for (int i = 0; i < profilesArray.length(); i++) {
                    selectedProfiles.add(profilesArray.getString(i));
                }
            }
        }

        return selectedProfiles;
    }
}
