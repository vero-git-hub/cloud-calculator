package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FieldNames;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ResourceBundle;

public class ProfileView implements Localizable {

    private final ProfileController profileController;

    private Label titleLabel;
    TextField nameField;
    TextField dateField;
    TextField linkField;
    private String preText;
    private String startDateText;
    private Label linksTitle = new Label("PDF Links:");


    public ProfileView(ProfileController profileController) {
        this.profileController = profileController;
        this.titleLabel = new Label("CREATE PROFILE");

        nameField = new TextField();
        nameField.setPromptText(FieldNames.NAME.getLabel());

        dateField = new TextField();
        dateField.setPromptText(FieldNames.START_DATE.getLabel());

        linkField = new TextField();
        linkField.setPromptText(FieldNames.PROFILE_LINK.getLabel());

//        preText = "DETAILS for";
//        startDateText = "Start Date:";
//
//        LanguageManager.registerLocalizable(this);
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public void showProfileScreen(Stage primaryStage, Profile profile) {
//        VBox layout = new VBox(10);
//
//        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
//
//        Text preTextLabel = new Text(preText);
//        Hyperlink nameLink = new Hyperlink(profile.getName());
//        nameLink.setOnAction(e -> {
//            try {
//                Desktop.getDesktop().browse(new URI(profile.getProfileLink()));
//            } catch (IOException | URISyntaxException ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        TextFlow textFlow = new TextFlow(preTextLabel, nameLink);
//        HBox topLayout = uiCallbacks.createTopLayoutWithBackAndText(backButton, textFlow);
//
//        layout.getChildren().addAll(
//                topLayout,
//                TableBuilder.createProfileInfoForProfile(profile, uiCallbacks, startDateText),
//                TableBuilder.createPdfLinksSectionForProfile(profile, linksTitle)
//        );
//
//        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
//        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setContent(layout);
//
//        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Profile profile = new Profile();

        Button uploadPdfButton = ButtonFactory.createUploadPdfButton(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profile.setPdfFilePath(selectedFile.getAbsolutePath());
            }
        });

        Button saveButton = ButtonFactory.createSaveButton(e -> profileController.handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText()));

        layout.getChildren().addAll(
                profileController.createTopLayout(primaryStage),
                nameField,
                dateField,
                linkField,
                uploadPdfButton,
                saveButton
        );

        profileController.createScene(layout, primaryStage);
    }

//    public TableColumn<Profile, String> createNameColumn() {
//        return TableBuilder.createNameColumn();
//    }

//    public TableColumn<Profile, Void> createNumberingColumn() {
//        return TableBuilder.createNumberingColumn();
//    }
//
//    public void configureTableColumnsWidthForMain(TableView<Profile> mainTable) {
//        TableBuilder.configureTableColumnsWidthForMain(mainTable);
//    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
//        titleLabel.setText(bundle.getString("createProfileTitle"));
//        nameField.setPromptText(bundle.getString("createProfileNameField"));
//        dateField.setPromptText(bundle.getString("createProfileDateField"));
//        linkField.setPromptText(bundle.getString("createProfileLinkField"));
//
//        preText = bundle.getString("detailsTitle");
//        startDateText = bundle.getString("detailsStartDateText");
//
//        linksTitle.setText(bundle.getString("detailsLinksTitle"));
//
//        alertTitleDeleteProfile = bundle.getString("alertTitleDeleteProfile");
//        alertHeaderDeleteProfile = bundle.getString("alertHeaderDeleteProfile");
//        alertContentDeleteProfile = bundle.getString("alertContentDeleteProfile");
    }

    @Override
    public void updateElements(String newTitle, String newAddScreenTitle, ResourceBundle bundle) {
        Localizable.super.updateElements(newTitle, newAddScreenTitle, bundle);
    }
}
