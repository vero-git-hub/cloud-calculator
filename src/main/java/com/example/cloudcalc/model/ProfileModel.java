package com.example.cloudcalc.model;

import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.entity.PrizeInfo;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.ProgramPrize;
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

                if (json.has("lastScannedDate")) {
                    profile.setLastScannedDate(json.getString("lastScannedDate"));
                }

                if (json.has("programPrizes")) {
                    JSONArray programPrizesArray = json.getJSONArray("programPrizes");
                    List<ProgramPrize> programPrizes = new ArrayList<>();
                    for (int i = 0; i < programPrizesArray.length(); i++) {
                        JSONObject programPrizeJson = programPrizesArray.getJSONObject(i);
                        ProgramPrize programPrize = new ProgramPrize();
                        programPrize.setProgram(programPrizeJson.getString("program"));

                        if (programPrizeJson.has("prizeInfoList")) {
                            JSONArray prizeInfoListArray = programPrizeJson.getJSONArray("prizeInfoList");
                            List<PrizeInfo> prizeInfoList = new ArrayList<>();
                            for (int k = 0; k < prizeInfoListArray.length(); k++) {
                                JSONObject prizeInfoJson = prizeInfoListArray.getJSONObject(k);
                                PrizeInfo prizeInfo = new PrizeInfo();
                                prizeInfo.setPrize(prizeInfoJson.getString("prize"));
                                prizeInfo.setEarnedPoints(prizeInfoJson.getInt("earnedPoints"));
                                prizeInfoList.add(prizeInfo);
                            }
                            programPrize.setPrizeInfoList(prizeInfoList);
                        }

                        programPrizes.add(programPrize);
                    }
                    profile.setProgramPrizes(programPrizes);
                }

                profiles.add(profile);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    private void updateProfileFile(Profile profile, JSONArray profilesArray) {
        JSONObject profileJson = createProfileJsonObject(profile);

        if (profile.getProgramPrizes() != null) {
            JSONArray programPrizesArray = createProgramPrizesJsonArray(profile.getProgramPrizes());
            profileJson.put("programPrizes", programPrizesArray);
        }

        updateProfileArray(profilesArray, profileJson);
    }


    private JSONObject createProfileJsonObject(Profile profile) {
        JSONObject profileJson = new JSONObject();
        profileJson.put("id", profile.getId());
        profileJson.put("name", profile.getName());
        profileJson.put("link", profile.getLink());
        profileJson.put("lastScannedDate", profile.getLastScannedDate());
        return profileJson;
    }

    private JSONArray createProgramPrizesJsonArray(List<ProgramPrize> programPrizes) {
        JSONArray programPrizesArray = new JSONArray();

        for (ProgramPrize programPrize : programPrizes) {
            JSONObject programPrizeJson = new JSONObject();
            programPrizeJson.put("program", programPrize.getProgram());

            JSONArray prizeInfoListArray = createPrizeInfoListJsonArray(programPrize.getPrizeInfoList());
            programPrizeJson.put("prizeInfoList", prizeInfoListArray);

            programPrizesArray.put(programPrizeJson);
        }

        return programPrizesArray;
    }

    private JSONArray createPrizeInfoListJsonArray(List<PrizeInfo> prizeInfoList) {
        JSONArray prizeInfoListArray = new JSONArray();

        for (PrizeInfo prizeInfo : prizeInfoList) {
            JSONObject prizeInfoJson = new JSONObject();
            prizeInfoJson.put("prize", prizeInfo.getPrize());
            prizeInfoJson.put("earnedPoints", prizeInfo.getEarnedPoints());
            prizeInfoListArray.put(prizeInfoJson);
        }

        return prizeInfoListArray;
    }

    private void updateProfileArray(JSONArray profilesArray, JSONObject profileJson) {
        boolean profileUpdated = false;

        for (int i = 0; i < profilesArray.length(); i++) {
            JSONObject existingProfile = profilesArray.getJSONObject(i);
            if (existingProfile.getInt("id") == profileJson.getInt("id")) {
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