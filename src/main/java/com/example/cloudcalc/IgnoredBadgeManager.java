package com.example.cloudcalc;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IgnoredBadgeManager {

    public List<String> loadIgnoredBadgesFromFile(String fileName) {
        List<String> ignoredBadges = new ArrayList<>();

        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return ignoredBadges;
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                ignoredBadges.add(jsonArray.getString(j));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return ignoredBadges;
    }

    public void saveIgnoredBadgesToFile(List<String> ignoredBadges, String fileName) {
        JSONArray jsonArray = new JSONArray(ignoredBadges);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
