package com.example.cloudcalc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TypeBadgeDataManager {

    public void saveTypeBadgeToFile(TypeBadge typeBadge, String fileName) {
        JSONArray typesArray = new JSONArray();
        File file = new File(fileName);

        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                typesArray = new JSONArray(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updateTypeBadgeFile(typeBadge, typesArray);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(typesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTypeBadgeFile(TypeBadge typeBadge, JSONArray array) {
        JSONObject json = new JSONObject();
        json.put("name", typeBadge.getName());
        json.put("startDate", typeBadge.getStartDate());

        array.put(json);
    }

    public List<TypeBadge> loadTypesBadgeFromFile(String fileName) {
        List<TypeBadge> types = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = jsonArray.getJSONObject(j);
                TypeBadge typeBadge = new TypeBadge();
                typeBadge.setName(json.getString("name"));
                typeBadge.setStartDate(json.getString("startDate"));

                types.add(typeBadge);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return types;
    }
}
