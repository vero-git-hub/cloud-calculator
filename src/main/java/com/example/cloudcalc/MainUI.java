package com.example.cloudcalc;

import com.example.cloudcalc.badge.IgnoredBadgeManager;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.profile.ProfileManager;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainUI implements UICallbacks{

    private final ProfileDataManager profileDataManager = new ProfileDataManager();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager(this);
    private final PrizeManager prizeManager = new PrizeManager(this);
    private final ProfileManager profileManager = new ProfileManager(dataExtractor, profileDataManager, this, prizeManager);

    @Override
    public void showMainScreen(Stage primaryStage) {
        Button addButton = ButtonFactory.createAddButton(e -> profileManager.showCreateProfileScreen(primaryStage));
        Button ignoreButton = ButtonFactory.createIgnoreButton(e -> ignoredBadgeManager.showIgnoreScreen(primaryStage));
        Button prizeButton = ButtonFactory.createPrizeButton(e -> prizeManager.showPrizesScreen(primaryStage));
        Label titleLabel = createLabel("Profiles");

        HBox topLayout = createTopLayout(addButton, titleLabel, ignoreButton, prizeButton);

        VBox layout = new VBox(10);
        layout.getChildren().add(topLayout);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        if (profiles.isEmpty()) {
            layout.getChildren().add(createLabel("No profiles"));
        } else {
            for (Profile profile : profiles) {
                layout.getChildren().add(profileManager.createProfileRow(primaryStage, profile));
            }
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    @Override
    public boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @Override
    public TextFlow createTextFlow(String boldText, String normalText) {
        Text bold = new Text(boldText);
        bold.setStyle("-fx-font-weight: bold;");

        Text normal = new Text(normalText);

        return new TextFlow(bold, normal);
    }

    @Override
    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    @Override
    public TextField createNameTextField() {
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Lab name");
        return nameField;
    }

    @Override
    public Label createLabel(String text) {
        return new Label(text);
    }

    @Override
    public HBox createTopLayout(Button leftButton, Label title, Button... rightButtons) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().add(leftButton);
        topLayout.getChildren().add(leftSpacer);
        topLayout.getChildren().add(title);
        topLayout.getChildren().add(rightSpacer);
        topLayout.getChildren().addAll(rightButtons);
        topLayout.setMinWidth(560);
        return topLayout;
    }

    @Override
    public void createScene(Parent layout, Stage primaryStage) {
        layout.setStyle("-fx-font-size: 18;-fx-padding: 10px;");

        Scene mainScene = new Scene(layout, 600, 400);
        primaryStage.setScene(mainScene);
    }

}