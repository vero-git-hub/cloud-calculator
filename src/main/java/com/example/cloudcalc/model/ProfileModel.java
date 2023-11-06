package com.example.cloudcalc.model;

import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.entity.Profile;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfileModel {

    public ProfileModel() {}

    public void handleDeleteAction(Stage primaryStage, Profile profile, MainController mainController) {
        List<Profile> profiles = loadProfilesFromFile(FileName.PROFILES_FILE);
        profiles.remove(profile);
        saveProfilesToFile(profiles, FileName.PROFILES_FILE);

        mainController.showMainScreen(primaryStage);
    }

    public void saveProfileToFile(Profile profile, String fileName) {
        JSONArray profilesArray = new JSONArray();
        File file = new File(fileName);

        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                profilesArray = new JSONArray(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updateProfileFile(profile, profilesArray);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(profilesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Profile> loadProfilesFromFile(String fileName) {
        List<Profile> profiles = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = jsonArray.getJSONObject(j);
                Profile profile = new Profile();
                profile.setName(json.getString("name"));
                profile.setStartDate(json.getString("startDate"));
                profile.setProfileLink(json.getString("profileLink"));
                if (json.has("pdfFilePath")) {
                    profile.setPdfFilePath(json.getString("pdfFilePath"));
                }
                if (json.has("pdfLinks")) {
                    JSONArray linksArray = json.getJSONArray("pdfLinks");
                    List<String> links = new ArrayList<>();
                    for (int i = 0; i < linksArray.length(); i++) {
                        links.add(linksArray.getString(i));
                    }
                    profile.setPdfLinks(links);
                }
                if (json.has("prizes")) {
                    JSONArray prizesArray = json.getJSONArray("prizes");
                    List<String> prizes = new ArrayList<>();
                    for (int i = 0; i < prizesArray.length(); i++) {
                        prizes.add(prizesArray.getString(i));
                    }
                    profile.setPrizes(prizes);
                }

                if (json.has("lastScannedDate")) {
                    profile.setLastScannedDate(json.getString("lastScannedDate"));
                }

                profiles.add(profile);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    private void updateProfileFile(Profile profile, JSONArray profilesArray) {
        JSONObject profileJson = new JSONObject();
        profileJson.put("name", profile.getName());
        profileJson.put("startDate", profile.getStartDate());
        profileJson.put("profileLink", profile.getProfileLink());
        profileJson.put("pdfFilePath", profile.getPdfFilePath());

        JSONArray linksArray = new JSONArray(profile.getPdfLinks());
        profileJson.put("pdfLinks", linksArray);

        JSONArray prizesArray = new JSONArray(profile.getPrizes());
        profileJson.put("prizes", prizesArray);

        profileJson.put("lastScannedDate", profile.getLastScannedDate());

        for (int i = 0; i < profilesArray.length(); i++) {
            JSONObject existingProfile = profilesArray.getJSONObject(i);
            if (existingProfile.getString("name").equals(profile.getName())) {
                profilesArray.put(i, profileJson);
                return;
            }
        }

        profilesArray.put(profileJson);
    }

    public void updateProfile(Profile profileToUpdate) {
        JSONArray profilesArray = new JSONArray(loadProfilesFromFile(FileName.PROFILES_FILE));
        updateProfileFile(profileToUpdate, profilesArray);
        saveJSONArrayToFile(profilesArray, FileName.PROFILES_FILE);
    }

    private void saveJSONArrayToFile(JSONArray jsonArray, String fileName) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProfilesToFile(List<Profile> profilesList, String fileName) {
        JSONArray profilesArray = new JSONArray();

        for (Profile profile : profilesList) {
            updateProfileFile(profile, profilesArray);
        }

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(profilesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleProfileSave(
//            Stage primaryStage, Profile profile, String name, String startDate, String profileLink
    ) {
//        if(name != null && !name.isEmpty() &&
//                startDate != null && !startDate.isEmpty() &&
//                profileLink != null && !profileLink.isEmpty()) {
//
//            profile.setName(name);
//            profile.setStartDate(startDate);
//            profile.setProfileLink(profileLink);
//
//            if(profile.getPdfFilePath() != null) {
//                List<String> extractedLinks = dataExtractor.extractHiddenLinksFromPdf(profile.getPdfFilePath());
//                List<String> h1Contents = dataExtractor.extractH1FromLinks(extractedLinks);
//                profile.setPdfLinks(h1Contents);
//            }
//            profileModel.saveProfileToFile(profile, FileName.PROFILES_FILE);
//            uiCallbacks.showMainScreen(primaryStage);
//        }
    }

}
