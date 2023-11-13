package com.example.cloudcalc.model;

import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.TypeBadgeController;
import com.example.cloudcalc.entity.TypeBadge;
import javafx.stage.Stage;
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

public class TypeBadgeModel {

    private TypeBadgeController typeBadgeController;

    public TypeBadgeModel(TypeBadgeController typeBadgeController) {
        this.typeBadgeController = typeBadgeController;
    }


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

    public List<TypeBadge> loadTypesBadgeFromFile() {
        List<TypeBadge> types = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(FileName.TYPES_BADGE_FILE)), StandardCharsets.UTF_8);
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

    public void handleTypeBadgeSave(Stage stage, String name, String startDate) {
        if(name != null && !name.isEmpty() &&
                startDate != null && !startDate.isEmpty()) {

            TypeBadge typeBadge = new TypeBadge();
            typeBadge.setName(name);
            typeBadge.setStartDate(startDate);

            saveTypeBadgeToFile(typeBadge, FileName.TYPES_BADGE_FILE);
            typeBadgeController.showAddPrizesScreen(stage);
        }
    }
}
