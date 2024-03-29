package com.example.cloudcalc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public static JSONArray readJsonArrayFromFile(String fileName) {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return new JSONArray();
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            return new JSONArray(content);
        } catch (IOException | JSONException e) {
            System.out.println("Error reading JSON from file: " + fileName);
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void writeJsonToFile(JSONArray jsonArray, String fileName) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toString(4));
        } catch (IOException e) {
            System.out.println("Error writing JSON to file: " + fileName);
            e.printStackTrace();
        }
    }

    public static JSONObject readJsonObjectFromFile(String fileName) {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return new JSONObject();
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            return new JSONObject(content);
        } catch (IOException | JSONException e) {
            System.out.println("Error reading JSON from file: " + fileName);
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static void writeJsonToFile(JSONObject jsonObject, String fileName) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write(jsonObject.toString(4));
        } catch (IOException e) {
            System.out.println("Error writing JSON to file: " + fileName);
            e.printStackTrace();
        }
    }

}
