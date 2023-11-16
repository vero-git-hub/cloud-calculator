package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.language.Language;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.entity.Profile;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class MainView implements Localizable {

    private final MainController controller;
    private Label titleLabel;
    Label profilesLabel = new Label("PROFILES");

    public MainView(MainController controller) {
        this.controller = controller;

        titleLabel = controller.getLabel("COUNT YOUR BADGES!");

        LanguageManager.registerLocalizable(this);
    }

    public void showMainScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        layout.getChildren().add(initializeTopLayout(primaryStage));
        layout.getChildren().add(createHeaderWithAddButton(primaryStage));

        layout.getChildren().add(setupTable(primaryStage));
        setupAndShowMainScreen(layout, primaryStage);
    }


    private void setupAndShowMainScreen(VBox layout, Stage primaryStage) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        VBox.setVgrow(layout.getChildren().get(2), Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        controller.getScene(scrollPane, primaryStage);
    }

    private TableView<Profile> setupTable(Stage primaryStage) {
        return controller.getTable(primaryStage);
    }

    private ComboBox<Language> createLanguageComboBox() {
        return LanguageManager.createLanguageComboBox();
    }

    private HBox createHeaderWithAddButton(Stage primaryStage) {
        HBox labelAndButton = new HBox(5);
        labelAndButton.setAlignment(Pos.CENTER_LEFT);

        Button addButton = ButtonFactory.createAddButton(e -> controller.getCreateProfileScreen(primaryStage));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ComboBox<Language> languageComboBox = createLanguageComboBox();

        labelAndButton.getChildren().addAll(profilesLabel, addButton, spacer, languageComboBox);
        return labelAndButton;
    }

    private HBox initializeTopLayout(Stage primaryStage) {
        return controller.initializeButtons(primaryStage);
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("mainTitle"));
        profilesLabel.setText(bundle.getString("profilesLabel"));

    }
}