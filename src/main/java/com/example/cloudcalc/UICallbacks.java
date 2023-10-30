package com.example.cloudcalc;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

public interface UICallbacks {
    void showMainScreen(Stage primaryStage);
    Label createLabel(String text);
    HBox createTopLayout(Button leftButton, Label title, Button... rightButtons);
    HBox createExtendedTopLayout(List<Button> leftButtons, Label title, Button... rightButtons);
    void createScene(Parent layout, Stage primaryStage);
    boolean showConfirmationAlert(String title, String header, String content);
    TextField createTextField(String promptText);
    TextFlow createTextFlow(String s, String name);
    TextField createNameTextField();
    HBox createTopLayoutForScan(Button backButton, TextFlow textFlow);
}
