package com.example.cloudcalc.model;

import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.IgnoreController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.util.AlertGuardian;
import com.example.cloudcalc.util.Notification;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class IgnoreModel {

    private final IgnoreController ignoreController;

    public IgnoreModel(IgnoreController ignoreController) {
        this.ignoreController = ignoreController;
    }

    public void handleSave(Stage primaryStage, FileOperationManager fileOperationManager) {
        TextFieldManager textFieldManager = LanguageManager.getTextFieldManager();
        String fileName = FileName.IGNORE_FILE;

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

        ignoreController.showScreen(primaryStage);
    }
}
