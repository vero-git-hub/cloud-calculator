package com.example.cloudcalc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfileDataManager {

    private final DateUtils dateUtils = new DateUtils();

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
                if (json.has("extractedLinks")) {
                    JSONArray linksArray = json.getJSONArray("extractedLinks");
                    List<String> links = new ArrayList<>();
                    for (int i = 0; i < linksArray.length(); i++) {
                        links.add(linksArray.getString(i));
                    }
                    profile.setExtractedLinks(links);
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
        JSONArray linksArray = new JSONArray(profile.getExtractedLinks());
        profileJson.put("extractedLinks", linksArray);

        profilesArray.put(profileJson);
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

    public Map<String, String> scanProfileLink(Profile profile) {
        Map<String, String> resultMap = new LinkedHashMap<>();

        try {
            Document doc = Jsoup.connect(profile.getProfileLink()).timeout(10 * 1000).get();

            Elements subheadElements = doc.select(".ql-subhead-1.l-mts");
            Elements bodyElements = doc.select(".ql-body-2.l-mbs");

            LocalDate profileDate = dateUtils.convertProfileStartDate(profile.getStartDate());

            if(subheadElements.size() == bodyElements.size()) {
                for(int i = 0; i < subheadElements.size(); i++) {
                    String key = subheadElements.get(i).text();
                    String valueStr = bodyElements.get(i).text();

                    LocalDate valueDate = dateUtils.extractDateFromValue(valueStr);

                    if(!valueDate.isBefore(profileDate)) {
                        resultMap.put(key, valueStr);
                    }
                }
            } else {
                System.out.println("Number of subhead and body elements do not match!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

}
