package com.example.cloudcalc;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainUI {

    private final ProfileDataManager profileDataManager = new ProfileDataManager();

    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager();

    public void showMainScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.getChildren().add(createLabel("Profiles:"));
        List<Profile> profiles = profileDataManager.loadProfilesFromFile("profiles.json");

        if (profiles.isEmpty()) {
            layout.getChildren().add(createLabel("No profiles"));
        } else {
            for (Profile profile : profiles) {
                layout.getChildren().add(createProfileRow(primaryStage, profile));
            }
        }

        layout.getChildren().add(createLabel("Actions:"));
        layout.getChildren().addAll(createCreateProfileButton(primaryStage), createIgnoreBadgesButton(primaryStage) , createSettingPrizesButton(primaryStage));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    private void createScene(Parent layout, Stage primaryStage) {
        layout.setStyle("-fx-font-size: 18;-fx-padding: 10px;");

        Scene mainScene = new Scene(layout, 600, 400);
        primaryStage.setScene(mainScene);
    }

    public void showProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                createBackButton(primaryStage),
                createProfileInfo(profile),
                createPdfLinksSection(profile)

        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    private void showScanScreen(Stage primaryStage, Profile profile, ArrayList<String> siteLinks) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                new Label("Scan Results for " + profile.getName()),
                createBadgeLabels(profile, siteLinks),
                createBackButton(primaryStage)
        );

        createScene(layout, primaryStage);
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        Profile profile = new Profile();

        TextField nameField = createTextField("Name");
        TextField dateField = createTextField("Start Date (e.g., 26.09.2023)");
        TextField linkField = createTextField("Profile Link");

        layout.getChildren().addAll(
                createLabel("Create Profile Screen"),
                nameField,
                dateField,
                linkField,
                createUploadPdfButton(primaryStage, profile),
                createSaveButton(primaryStage, profile, nameField, dateField, linkField),
                createBackButton(primaryStage)
        );

        createScene(layout, primaryStage);
    }

    private Button createSaveButton(Stage primaryStage, Profile profile, TextField nameField, TextField dateField, TextField linkField) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText()));
        return saveButton;
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
            profileDataManager.saveProfileToFile(profile, "profiles.json");
            showMainScreen(primaryStage);
        }
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private Button createUploadPdfButton(Stage primaryStage, Profile profile) {
        FileChooser fileChooser = new FileChooser();
        Button uploadPdfButton = new Button("Upload PDF");
        uploadPdfButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                profile.setPdfFilePath(selectedFile.getAbsolutePath());
            }
        });
        return uploadPdfButton;
    }

    private void showIgnoreScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                createLabel("Ignore Screen"),
                createAddIgnoreBadgeButton(primaryStage),
                createBackButton(primaryStage),
                createIgnoredBadgesList(primaryStage)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    private Label createLabel(String text) {
        return new Label(text);
    }

    private Button createAddIgnoreBadgeButton(Stage primaryStage) {
        Button addButton = new Button("Add Ignore Badge");
        addButton.setOnAction(e -> showAddIgnoreBadgeScreen(primaryStage));
        return addButton;
    }

    private VBox createIgnoredBadgesList(Stage primaryStage) {
        VBox badgesList = new VBox(10);
        List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile("ignored_badges.json");

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
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            ignoredBadges.remove(badge);
            ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, "ignored_badges.json");
            showIgnoreScreen(primaryStage);
        });

        badgeRow.getChildren().addAll(badgeLabel, deleteButton);
        return badgeRow;
    }

    private void showAddIgnoreBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                createLabel("Add Ignore Badge Screen"),
                createNameTextField(),
                createSaveIgnoreBadgeButton(primaryStage),
                createBackButton(primaryStage)
        );

        createScene(layout, primaryStage);
    }

    private Button createSaveIgnoreBadgeButton(Stage primaryStage) {
        Button saveButton = new Button("Save Ignore Badge");
        saveButton.setOnAction(e -> {
            List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile("ignored_badges.json");
            TextField nameField = (TextField) ((Node) e.getSource()).getScene().lookup("#nameField");
            ignoredBadges.add(nameField.getText());
            ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, "ignored_badges.json");
            showIgnoreScreen(primaryStage);
        });
        return saveButton;
    }

    private TextField createNameTextField() {
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Lab name");
        return nameField;
    }

    private VBox createBadgeLabels(Profile profile, ArrayList<String> siteLinks) {
        VBox labelsBox = new VBox(5);
        Map<String, Integer> badgeCounts = calculateBadgeCounts(profile, siteLinks);

        labelsBox.getChildren().addAll(
                createTextFlow("Total: ", String.valueOf(badgeCounts.get("Total"))),
                createTextFlow("PDF badges: ", String.valueOf(badgeCounts.get("PDF"))),
                createTextFlow("Skill badges: ", String.valueOf(badgeCounts.get("Skill"))),
                createTextFlow("Ignore badges: ", String.valueOf(badgeCounts.get("Ignore")))
        );
        return labelsBox;
    }

    private Map<String, Integer> calculateBadgeCounts(Profile profile, ArrayList<String> siteBadges) {
        Map<String, Integer> badgeCounts = new HashMap<>();

        List<String> pdfBadges = new ArrayList<>(profile.getPdfLinks());
        List<String> ignoreBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile("ignored_badges.json");

        int countAll = siteBadges.size();
        int countPdf;
        int countIgnore;
        int countSkill;

        siteBadges.removeAll(pdfBadges);
        countPdf = countAll - siteBadges.size();

        siteBadges.removeAll(ignoreBadges);
        countSkill = siteBadges.size();
        countIgnore = countAll - (countPdf + countSkill);

        badgeCounts.put("Total", countAll);
        badgeCounts.put("PDF", countPdf);
        badgeCounts.put("Skill", countSkill);
        badgeCounts.put("Ignore", countIgnore);

        return badgeCounts;
    }

    private Button createBackButton(Stage primaryStage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showMainScreen(primaryStage));
        return backButton;
    }

    private VBox createPdfLinksSection(Profile profile) {
        VBox linksVBox = new VBox(5);

        Label linksTitle = new Label("PDF Links:");
        linksTitle.setStyle("-fx-font-weight: bold;");
        linksVBox.getChildren().add(linksTitle);

        if (profile.getPdfLinks() != null) {
            for (String link : profile.getPdfLinks()) {
                Label linkLabel = new Label(link);
                linksVBox.getChildren().add(linkLabel);
            }
        }

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

        TextFlow textFlow = new TextFlow(bold, normal);
        return textFlow;
    }

    private Button createSettingPrizesButton(Stage primaryStage) {
        Button settingsPrizesButton = new Button("Settings Prizes");
        settingsPrizesButton.setOnAction(e -> showSettingsPrizesScreen(primaryStage));
        return settingsPrizesButton;
    }

    private void showSettingsPrizesScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Prize> prizes = loadPrizesFromFile("settings_prizes.json");

        TableView<Prize> table = new TableView<>();

        TableColumn<Prize, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Prize, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Prize, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        TableColumn<Prize, Void> deleteColumn = new TableColumn<>("Actions");
        Callback<TableColumn<Prize, Void>, TableCell<Prize, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Prize, Void> call(final TableColumn<Prize, Void> param) {
                final TableCell<Prize, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Prize prize = getTableView().getItems().get(getIndex());
                            deletePrize(prize);
                            showSettingsPrizesScreen(primaryStage);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        deleteColumn.setCellFactory(cellFactory);

        nameColumn.setResizable(false);
        typeColumn.setResizable(false);
        countColumn.setResizable(false);
        deleteColumn.setResizable(false);

        double width = 0.25;
        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        typeColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        countColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        deleteColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));

        table.getColumns().add(nameColumn);
        table.getColumns().add(typeColumn);
        table.getColumns().add(countColumn);
        table.getColumns().add(deleteColumn);
        table.getItems().addAll(prizes);

        Button addPrizeButton = new Button("Add Prize");
        addPrizeButton.setOnAction(e -> showAddPrizesScreen(primaryStage));

        layout.getChildren().addAll(
                createLabel("Settings Prizes Screen"),
                table,
                addPrizeButton,
                createBackButton(primaryStage)
        );

        createScene(layout, primaryStage);
    }

    private void deletePrize(Prize prize) {
        List<Prize> prizes = loadPrizesFromFile("settings_prizes.json");
        prizes.remove(prize);
        savePrizesToFile(prizes, "settings_prizes.json");
    }

    private void savePrizesToFile(List<Prize> prizes, String fileName) {
        JSONArray jsonArray = convertPrizesToJSONArray(prizes);
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Prize> loadPrizesFromFile(String fileName) {
        List<Prize> prizes = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject prizeObject = jsonArray.getJSONObject(j);
                Prize prize = new Prize();
                prize.setName(prizeObject.getString("name"));
                prize.setType(prizeObject.getString("type"));
                prize.setCount(prizeObject.getInt("count"));
                prizes.add(prize);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return prizes;
    }

    private void showAddPrizesScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        TextField namePrizeField = new TextField();
        namePrizeField.setPromptText("Enter name prize");

        TextField badgeCountField = new TextField();
        badgeCountField.setPromptText("Enter badge count");

        ComboBox<String> badgeTypeComboBox = new ComboBox<>();
        badgeTypeComboBox.getItems().addAll("pdf", "skill");
        badgeTypeComboBox.setPromptText("Select badge type");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            savePrize(namePrizeField.getText(), badgeCountField.getText(), badgeTypeComboBox.getValue());
            showSettingsPrizesScreen(primaryStage);
        });

        layout.getChildren().addAll(
                createLabel("Add Prizes Screen"),
                namePrizeField,
                badgeCountField,
                badgeTypeComboBox,
                saveButton,
                createBackButton(primaryStage)
        );

        createScene(layout, primaryStage);
    }

    private void savePrize(String namePrize, String badgeCount, String badgeType) {
        if (namePrize != null && badgeCount != null && !badgeCount.isEmpty() && badgeType != null) {
            Prize newPrize = new Prize();
            newPrize.setName(namePrize);
            newPrize.setType(badgeType);

            try {
                newPrize.setCount(Integer.parseInt(badgeCount));
            } catch (NumberFormatException e) {
                System.out.println("Error: Badge count must be a valid number.");
                return;
            }

            List<Prize> existingPrizes = loadPrizesFromFile("settings_prizes.json");
            existingPrizes.add(newPrize);

            JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);

            try (FileWriter file = new FileWriter("settings_prizes.json")) {
                file.write(jsonArray.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray convertPrizesToJSONArray(List<Prize> prizes) {
        JSONArray jsonArray = new JSONArray();
        for (Prize prize : prizes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", prize.getName());
            jsonObject.put("type", prize.getType());
            jsonObject.put("count", prize.getCount());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private Button createIgnoreBadgesButton(Stage primaryStage) {
        Button ignoreButton = new Button("Ignore Badges");
        ignoreButton.setOnAction(e -> showIgnoreScreen(primaryStage));
        return ignoreButton;
    }

    private Button createCreateProfileButton(Stage primaryStage) {
        Button createProfileButton = new Button("Create Profile");
        createProfileButton.setOnAction(e -> showCreateProfileScreen(primaryStage));
        return createProfileButton;
    }

    private HBox createProfileRow(Stage primaryStage, Profile profile) {
        HBox profileContainer = new HBox(10);

        Button profileButton = new Button(profile.getName());
        profileButton.setOnAction(e -> showProfileScreen(primaryStage, profile));

        Button deleteButton = createDeleteButton(primaryStage, profile);
        Button scanButton = createScanButton(primaryStage, profile);

        profileContainer.getChildren().addAll(profileButton, deleteButton, scanButton);
        return profileContainer;
    }

    private Button createDeleteButton(Stage primaryStage, Profile profile) {
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            handleDeleteAction(primaryStage, profile);
        });
        return deleteButton;
    }

    private void handleDeleteAction(Stage primaryStage, Profile profile) {
        List<Profile> profiles = profileDataManager.loadProfilesFromFile("profiles.json");
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, "profiles.json");
        showMainScreen(primaryStage);
    }

    private Button createScanButton(Stage primaryStage, Profile profile) {
        Button scanButton = new Button("Scan");
        scanButton.setOnAction(e -> {
            ArrayList<String> extractedData = dataExtractor.performScan(profile);
            showScanScreen(primaryStage, profile, extractedData);
        });
        return scanButton;
    }

}
