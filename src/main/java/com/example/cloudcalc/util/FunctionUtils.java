package com.example.cloudcalc.util;

import org.json.JSONArray;

public class FunctionUtils {
    public static int getNextId(JSONArray jsonArray) {
        int maxId = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            int currentId = jsonArray.getJSONObject(i).getInt("id");
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        return maxId + 1;
    }
}