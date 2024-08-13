package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.ProgramPrize;
import com.example.cloudcalc.entity.dailystat.ScanResult;
import com.example.cloudcalc.entity.prize.PrizeInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_code
 **/
public class DailyStatModel {

    private JSONObject getOrCreateDailyStat(JSONObject settings) {
        JSONObject dailyStat;
        if (settings.has("dailyStat")) {
            dailyStat = settings.getJSONObject("dailyStat");
        } else {
            dailyStat = new JSONObject();
            settings.put("dailyStat", dailyStat);
        }
        return dailyStat;
    }

    public void saveSelectedProfiles(List<String> selectedProfiles) {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

        JSONObject dailyStat = getOrCreateDailyStat(settings);

        JSONArray profilesArray = new JSONArray(selectedProfiles);
        dailyStat.put("selectedProfiles", profilesArray);

        FileManager.writeJsonToFile(settings, FileName.SETTINGS_FILE);
    }

    public void saveSelectedPrograms(List<String> selectedPrograms) {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

        JSONObject dailyStat = getOrCreateDailyStat(settings);

        JSONArray programsArray = new JSONArray(selectedPrograms);
        dailyStat.put("selectedPrograms", programsArray);

        FileManager.writeJsonToFile(settings, FileName.SETTINGS_FILE);
    }

    public void saveTemplate(String text) {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

        JSONObject dailyStat = getOrCreateDailyStat(settings);

        dailyStat.put("templateText", text);
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

    public List<String> loadSelectedPrograms() {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);
        List<String> selectedPrograms = new ArrayList<>();

        if (settings.has("dailyStat")) {
            JSONObject dailyStat = settings.getJSONObject("dailyStat");
            if (dailyStat.has("selectedPrograms")) {
                JSONArray programsArray = dailyStat.getJSONArray("selectedPrograms");
                for (int i = 0; i < programsArray.length(); i++) {
                    selectedPrograms.add(programsArray.getString(i));
                }
            }
        }
        return selectedPrograms;
    }

    public String loadTemplate() {
        JSONObject settings = FileManager.readJsonObjectFromFile(FileName.SETTINGS_FILE);

        if (settings.has("dailyStat")) {
            JSONObject dailyStat = settings.getJSONObject("dailyStat");
            if (dailyStat.has("templateText")) {
                return dailyStat.getString("templateText");
            }
        }
        return "";
    }

    public  Map<String, Map<String, Integer>> scanProfiles(List<Profile> profiles, List<String> selectedPrograms) {
        ScanResult scanResult = new ScanResult();

        for (Profile profile : profiles) {
            for (String selectedProgram : selectedPrograms) {
                List<ProgramPrize> profilePrograms = profile.getProgramPrizes();
                for (ProgramPrize programPrize : profilePrograms) {
                    String programName = programPrize.getProgram();

                    if (programName.equals(selectedProgram)) {
                        List<PrizeInfo> prizeInfoList = programPrize.getPrizeInfoList();
                        int points = prizeInfoList.get(0).getEarnedPoints();
                        String profileName = profile.getName();
                        scanResult.addScanResult(profileName, programName, points);
                    }
                }
            }
        }

        Map<String, Map<String, Integer>> allResults = scanResult.getAllResults();
        return allResults;

    }
}