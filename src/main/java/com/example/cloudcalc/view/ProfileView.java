package com.example.cloudcalc.view;

import com.example.cloudcalc.builder.fields.profile.ProfileFieldManager;
import com.example.cloudcalc.builder.fields.profile.ProfileFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

public class ProfileView implements Localizable, ProfileFieldUpdatable {

    private final ProfileController profileController;
    private final Label titleLabelEditProfileScreen;
    private Label titleLabel;
    TextField nameField;
    TextField dateField;
    TextField linkField;
    private String titlePreText = "DETAILS for";
    private String startDateText = "Start Date: ";
    private Label linksTitle = new Label("PDF Links:");
    private Button uploadPdfButton = new Button("html");

    public ProfileView(ProfileController profileController) {
        this.profileController = profileController;
        this.titleLabel = new Label("CREATE PROFILE");
        this.titleLabelEditProfileScreen = new Label("EDIT PROFILE");

        LanguageManager.registerLocalizable(this);
    }

    public Label getTitleLabelEditProfileScreen() {
        return titleLabelEditProfileScreen;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public void showProfileScreen(Stage stage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> profileController.getMainController().showMainScreen(stage));

        Text preTextLabel = new Text(titlePreText);
        Hyperlink nameLink = new Hyperlink(profile.getName());
        nameLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(profile.getLink()));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });

        TextFlow textFlow = new TextFlow(preTextLabel, nameLink);
        HBox topLayout = profileController.createTopLayoutWithBackAndText(backButton, textFlow);

        layout.getChildren().addAll(
                topLayout,
                profileController.createProfileInfoForProfile(profile, startDateText),
                profileController.createPdfLinksSectionForProfile(profile, linksTitle)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(layout);

        profileController.createScene(stage, scrollPane);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Profile profile = new Profile();

        ProfileFieldManager profileFieldManager = LanguageManager.getProfileFieldManager();
        nameField = profileFieldManager.getNameField();
        linkField = profileFieldManager.getLinkField();

        nameField.clear();
        linkField.clear();

        Button saveButton = ButtonFactory.createSaveButton(e -> profileController.handleProfileSave(primaryStage, profile, nameField.getText(), linkField.getText()));

        layout.getChildren().addAll(
                profileController.createTopLayout(primaryStage),
                nameField,
                linkField,
                saveButton
        );

        profileController.createScene(primaryStage, layout);
    }

    public void showEditProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        ProfileFieldManager profileFieldManager = LanguageManager.getProfileFieldManager();
        nameField = profileFieldManager.getNameField();
        linkField = profileFieldManager.getLinkField();

        nameField.setText(profile.getName());
        linkField.setText(profile.getLink());

        Button saveButton = ButtonFactory.createSaveButton(e -> profileController.handleProfileSave(primaryStage, profile, nameField.getText(), linkField.getText()));

        layout.getChildren().addAll(
                profileController.createTopLayoutEditProfileScreen(primaryStage),
                nameField,
                linkField,
                saveButton
        );

        profileController.createScene(primaryStage, layout);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("createProfileTitle"));
        titlePreText = bundle.getString("detailsTitle");
        startDateText = bundle.getString("detailsStartDateText");
        linksTitle.setText(bundle.getString("detailsLinksTitle"));

        updateNameFieldPlaceholder(bundle.getString("createProfileNameField"));
        updateDateFieldPlaceholder(bundle.getString("createProfileDateField"));
        updateLinkFieldPlaceholder(bundle.getString("createProfileLinkField"));

        if (uploadPdfButton != null) {
            uploadPdfButton.setText(bundle.getString("uploadPdfButtonText"));
        }

//        alertTitleDeleteProfile = bundle.getString("alertTitleDeleteProfile");
//        alertHeaderDeleteProfile = bundle.getString("alertHeaderDeleteProfile");
//        alertContentDeleteProfile = bundle.getString("alertContentDeleteProfile");
    }

    @Override
    public void updateElements(String newTitle, String newAddScreenTitle, ResourceBundle bundle) {
        Localizable.super.updateElements(newTitle, newAddScreenTitle, bundle);
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        if (nameField != null) {
            nameField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        if (dateField != null) {
            dateField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateLinkFieldPlaceholder(String placeholder) {
        if (linkField != null) {
            linkField.setPromptText(placeholder);
        }
    }
}