package com.example.cloudcalc.model;

import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.controller.ArcadeController;
import com.example.cloudcalc.util.AlertGuardian;
import com.example.cloudcalc.util.Notification;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class ArcadeModel {

    private final ArcadeController arcadeController;


    public ArcadeModel(ArcadeController arcadeController) {
        this.arcadeController = arcadeController;
    }

    public void handleSave(Stage primaryStage, FileOperationManager fileOperationManager, String fileName, TextFieldManager textFieldManager) {
        List<String> badges = fileOperationManager.loadBadgesFromFile(fileName);
        TextField nameField = textFieldManager.getNameTextField();

        String badgeName = nameField.getText().trim();
        if (badgeName.isEmpty()) {
            Notification.showAlert(AlertGuardian.nameAlertTitle, AlertGuardian.nameAlertHeader, AlertGuardian.nameAlertContent);
            return;
        }

        badges.add(badgeName);
        fileOperationManager.saveBadgesToFile(badges, fileName);
        nameField.setText("");

        arcadeController.showScreen(primaryStage);
    }
}