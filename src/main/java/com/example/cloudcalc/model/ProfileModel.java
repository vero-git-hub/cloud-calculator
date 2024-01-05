package com.example.cloudcalc.model;

import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.exception.PDFIsEmpty;
import com.example.cloudcalc.exception.PageStructureFromPDFChangedException;
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
    private final ProfileController profileController;
    public ProfileModel(ProfileController profileController) {
        this.profileController = profileController;
    }

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

        if (profile.getId() == 0) {
            profile.setId(getNextProfileId(profilesArray));
        }

        updateProfileFile(profile, profilesArray);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(profilesArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getNextProfileId(JSONArray profilesArray) {
        int maxId = 0;
        for (int i = 0; i < profilesArray.length(); i++) {
            int currentId = profilesArray.getJSONObject(i).getInt("id");
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        return maxId + 1;
    }

    public List<Profile> loadProfilesFromFile(String fileName) {
        List<Profile> profiles = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = jsonArray.getJSONObject(j);
                Profile profile = new Profile();
                profile.setId(json.getInt("id"));
                profile.setName(json.getString("name"));
                profile.setLink(json.getString("link"));
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

                if (json.has("programs")) {
                    JSONArray programsArray = json.getJSONArray("programs");
                    List<String> programs = new ArrayList<>();
                    for (int i = 0; i < programsArray.length(); i++) {
                        programs.add(programsArray.getString(i));
                    }
                    profile.setPrograms(programs);
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

        profileJson.put("id", profile.getId());
        profileJson.put("name", profile.getName());
        profileJson.put("link", profile.getLink());

        JSONArray prizesArray = new JSONArray(profile.getPrizes());
        profileJson.put("prizes", prizesArray);
        profileJson.put("lastScannedDate", profile.getLastScannedDate());

        JSONArray programs = new JSONArray(profile.getPrograms());
        profileJson.put("programs", programs);

        boolean profileUpdated = false;

        for (int i = 0; i < profilesArray.length(); i++) {
            JSONObject existingProfile = profilesArray.getJSONObject(i);
            if (existingProfile.getInt("id") == profile.getId()) {
                profilesArray.put(i, profileJson);
                profileUpdated = true;
                break;
            }
        }

        if (!profileUpdated) {
            profilesArray.put(profileJson);
        }
    }

    public void updateProfile(Profile profileToUpdate) {
        JSONArray profilesArray = new JSONArray(loadProfilesFromFile(FileName.PROFILES_FILE));
        updateProfileFile(profileToUpdate, profilesArray);
        saveJSONArrayToFile(profilesArray, FileName.PROFILES_FILE);
    }

    private void saveJSONArrayToFile(JSONArray jsonArray, String fileName) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toString(4));
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
            fileWriter.write(profilesArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleProfileSave(Stage primaryStage, Profile profile) {
        saveProfileToFile(profile, FileName.PROFILES_FILE);
        profileController.showMainScreen(primaryStage);
    }

    private void saveFromPdfFile (Profile profile) throws PageStructureFromPDFChangedException, PDFIsEmpty {
//        if(profile.getPdfFilePath() != null) {
//
//            List<String> extractedLinks = profileController.extractHiddenLinksFromPdf(profile.getPdfFilePath());
//            if(!extractedLinks.isEmpty()) {
//                List<String> h1Contents = profileController.extractH1FromLinks(extractedLinks);
//
//                if(!h1Contents.isEmpty()) {
//                    profile.setPdfLinks(h1Contents);
//                } else {
//                    throw new PageStructureFromPDFChangedException();
//                }
//            } else {
//                throw new PageStructureFromPDFChangedException();
//            }
//
//        } else {
//            throw new PDFIsEmpty();
//        }
    }
}