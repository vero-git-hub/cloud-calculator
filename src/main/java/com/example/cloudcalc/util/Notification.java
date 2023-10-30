package com.example.cloudcalc.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Notification {

    public static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
