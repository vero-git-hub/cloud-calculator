package com.example.cloudcalc;

import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.IgnoredBadgeManager;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainUI implements UICallbacks{

    private final ProfileDataManager profileDataManager = new ProfileDataManager();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager();
    private final BadgeManager badgeManager = new BadgeManager(dataExtractor);
    private final PrizeManager prizeManager = new PrizeManager(badgeManager, this);
    private final List<String> receivedPrizes = new ArrayList<>();

    public void showProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));

        layout.getChildren().addAll(
                backButton,
                createProfileInfo(profile),
                createPdfLinksSection(profile)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Profile profile = new Profile();

        TextField nameField = createTextField("Name");
        TextField dateField = createTextField("Start Date (e.g., 26.09.2023)");
        TextField linkField = createTextField("Profile Link");

        Button uploadPdfButton = ButtonFactory.createUploadPdfButton(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profile.setPdfFilePath(selectedFile.getAbsolutePath());
            }
        });

        Button saveButton = ButtonFactory.createSaveButton(e -> handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText()));

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Create Profile");

        HBox topLayout = createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                linkField,
                uploadPdfButton,
                saveButton
        );

        createScene(layout, primaryStage);
    }

    private void showIgnoreScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Ignore Screen");
        Button addButton = ButtonFactory.createAddButton(e -> showAddIgnoreBadgeScreen(primaryStage));

        HBox topLayout = createTopLayout(backButton, titleLabel, addButton);

        layout.getChildren().addAll(
                topLayout,
                createIgnoredBadgesList(primaryStage)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    private VBox createIgnoredBadgesList(Stage primaryStage) {
        VBox badgesList = new VBox(10);
        List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);

        if (ignoredBadges.isEmpty()) {
            badgesList.getChildren().add(createLabel("No ignored badges"));
        } else {
            for (String badge : ignoredBadges) {
                badgesList.getChildren().add(createBadgeRow(primaryStage, badge, ignoredBadges));
            }
        }

        return badgesList;
    }

    private HBox createBadgeRow(Stage primaryStage, String badge, List<String> ignoredBadges) {
        HBox badgeRow = new HBox(10);
        Label badgeLabel = new Label(badge);

        EventHandler<ActionEvent> deleteAction = e -> {
            if (showConfirmationAlert("Confirmation Dialog", "Delete Badge", "Are you sure you want to delete this badge?")) {
                ignoredBadges.remove(badge);
                ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                showIgnoreScreen(primaryStage);
            }
        };

        Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

        badgeRow.getChildren().addAll(badgeLabel, deleteButton);
        return badgeRow;
    }

    private void showAddIgnoreBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> showIgnoreScreen(primaryStage));
        Label titleLabel = createLabel("Add Ignore Badge Screen");

        Button saveIgnoreBadgeButton = ButtonFactory.createSaveIgnoreBadgeButton(
                () -> {
                    List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");
                    ignoredBadges.add(nameField.getText());
                    ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                    showIgnoreScreen(primaryStage);
                },
                () -> (TextField) layout.getScene().lookup("#nameField")
        );

        HBox topLayout = createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                createNameTextField(),
                saveIgnoreBadgeButton
        );

        createScene(layout, primaryStage);
    }

    private VBox createBadgeLabels(Profile profile, ArrayList<String> siteLinks) {
        VBox labelsBox = new VBox(5);
        Map<String, String> badgeCounts = badgeManager.calculateBadgeCounts(profile, siteLinks);
        receivedPrizes.clear();
        receivedPrizes.addAll(badgeManager.getReceivedPrizes());

        String prizesStr = String.join(", ", receivedPrizes);

        labelsBox.getChildren().addAll(
                createTextFlow("Total: ", String.valueOf(badgeCounts.get(Constants.TOTAL))),
                createLabel("Ignore: " + badgeCounts.get(Constants.IGNORE)),
                createTextFlow("Skill: ", String.valueOf(badgeCounts.get(Constants.SKILL))),
                createLabel("PDF total: " + badgeCounts.get(Constants.PDF_TOTAL)),
                createTextFlow("PDF for prize: ", String.valueOf(badgeCounts.get(Constants.PDF_FOR_PRIZE))),
                createTextFlow("Skill for prize: ", String.valueOf(badgeCounts.get(Constants.SKILL_FOR_PRIZE))),
                createTextFlow("Skill for activity: ", String.valueOf(badgeCounts.get(Constants.SKILL_FOR_ACTIVITY))),
                createTextFlow("Skill for pl-02.10.2023: ", String.valueOf(badgeCounts.get(Constants.SKILL_FOR_PL))),
                createTextFlow("Prize received: ", prizesStr.isEmpty() ? "None" : prizesStr)
        );
        return labelsBox;
    }

    private VBox createPdfLinksSection(Profile profile) {
        VBox linksVBox = new VBox(5);

        Label linksTitle = new Label("PDF Links:");
        linksTitle.setStyle("-fx-font-weight: bold;");
        linksVBox.getChildren().add(linksTitle);

        TableView<PdfLinkItem> table = new TableView<>();

        TableColumn<PdfLinkItem, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));

        TableColumn<PdfLinkItem, String> linkColumn = new TableColumn<>("Link");
        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));

        indexColumn.setResizable(false);
        linkColumn.setResizable(false);

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        linkColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.95));

        table.getColumns().addAll(indexColumn, linkColumn);

        if (profile.getPdfLinks() != null) {
            int index = 1;
            for (String link : profile.getPdfLinks()) {
                table.getItems().add(new PdfLinkItem(index++, link));
            }
        }

        linksVBox.getChildren().add(table);

        return linksVBox;
    }

    private VBox createProfileInfo(Profile profile) {
        VBox profileInfoBox = new VBox(10);

        TextFlow nameFlow = createTextFlow("Name: ", profile.getName());
        TextFlow startDateFlow = createTextFlow("Start Date: ", profile.getStartDate());
        TextFlow profileLinkFlow = createTextFlow("Profile Link: ", profile.getProfileLink());

        profileInfoBox.getChildren().addAll(nameFlow, startDateFlow, profileLinkFlow);

        return profileInfoBox;
    }

    private TextFlow createTextFlow(String boldText, String normalText) {
        Text bold = new Text(boldText);
        bold.setStyle("-fx-font-weight: bold;");

        Text normal = new Text(normalText);

        return new TextFlow(bold, normal);
    }

    private HBox createProfileRow(Stage primaryStage, Profile profile) {
        HBox profileContainer = new HBox(10);

        Button scanButton = ButtonFactory.createScanButton(e -> {
            ArrayList<String> extractedData = dataExtractor.performScan(profile);
            showScanScreen(primaryStage, profile, extractedData);
        });

        EventHandler<ActionEvent> deleteAction = e -> {
            if (showConfirmationAlert("Confirmation Dialog", "Delete Profile", "Are you sure you want to delete this profile?")) {
                handleDeleteAction(primaryStage, profile);
            }
        };

        Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

        profileContainer.getChildren().addAll(
                ButtonFactory.createButton(profile.getName(), e -> showProfileScreen(primaryStage, profile), null),
                scanButton,
                deleteButton
        );
        return profileContainer;
    }

    private void handleDeleteAction(Stage primaryStage, Profile profile) {
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, Constants.PROFILES_FILE);
        showMainScreen(primaryStage);
    }

    private void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Scan Results for " + profile.getName());

        HBox topLayout = createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                createBadgeLabels(profile, siteLinks)
        );

        createScene(layout, primaryStage);
    }

    private void handleProfileSave(Stage primaryStage, Profile profile, String name, String startDate, String profileLink) {
        if(name != null && !name.isEmpty() &&
                startDate != null && !startDate.isEmpty() &&
                profileLink != null && !profileLink.isEmpty()) {

            profile.setName(name);
            profile.setStartDate(startDate);
            profile.setProfileLink(profileLink);

            if(profile.getPdfFilePath() != null) {
                List<String> extractedLinks = dataExtractor.extractHiddenLinksFromPdf(profile.getPdfFilePath());
                List<String> h1Contents = dataExtractor.extractH1FromLinks(extractedLinks);
                profile.setPdfLinks(h1Contents);
            }
            profileDataManager.saveProfileToFile(profile, Constants.PROFILES_FILE);
            showMainScreen(primaryStage);
        }
    }

    private TextField createNameTextField() {
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Lab name");
        return nameField;
    }

    @Override
    public void showMainScreen(Stage primaryStage) {
        Button addButton = ButtonFactory.createAddButton(e -> showCreateProfileScreen(primaryStage));
        Button ignoreButton = ButtonFactory.createIgnoreButton(e -> showIgnoreScreen(primaryStage));
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
                layout.getChildren().add(createProfileRow(primaryStage, profile));
            }
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
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
    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }
}