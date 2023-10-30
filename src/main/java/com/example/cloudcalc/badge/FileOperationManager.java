package com.example.cloudcalc.badge;

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

public class FileOperationManager {

    public List<String> loadBadgesFromFile(String fileName) {
        List<String> badges = new ArrayList<>();

        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return badges;
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                badges.add(jsonArray.getString(j));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return badges;
    }

    public void saveBadgesToFile(List<String> badges, String fileName) {
        JSONArray jsonArray = new JSONArray(badges);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
