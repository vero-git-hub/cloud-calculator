package com.example.cloudcalc.util;

import com.example.cloudcalc.Main;

import java.util.ResourceBundle;

public class TranslateUtils {
    public static String getTranslate(String key) {
        ResourceBundle bundle = Main.bundle;
        return bundle.getString(key);
    }
}