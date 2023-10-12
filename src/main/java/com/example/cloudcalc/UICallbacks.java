package com.example.cloudcalc;

import com.example.cloudcalc.profile.Profile;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public interface UICallbacks {
    void showMainScreen(Stage primaryStage);
    Label createLabel(String text);
    HBox createTopLayout(Button leftButton, Label title, Button... rightButtons);
    void createScene(Parent layout, Stage primaryStage);
    boolean showConfirmationAlert(String title, String header, String content);
    TextField createTextField(String promptText);
    Node createPdfLinksSection(Profile profile);
    TextFlow createTextFlow(String s, String name);
}
