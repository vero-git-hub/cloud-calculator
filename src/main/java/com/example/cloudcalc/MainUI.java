package com.example.cloudcalc;

import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.IgnoredBadgeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.type.TypeBadge;
import com.example.cloudcalc.type.TypeBadgeDataManager;
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
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainUI {

    private final ProfileDataManager profileDataManager = new ProfileDataManager();
    private final TypeBadgeDataManager typeBadgeDataManager = new TypeBadgeDataManager();
    private final FileManager fileManager = new FileManager();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager();
    private final BadgeManager badgeManager = new BadgeManager(dataExtractor);
    private final List<String> receivedPrizes = new ArrayList<>();

    public void showMainScreen(Stage primaryStage) {
        Button addButton = ButtonFactory.createAddButton(e -> showCreateProfileScreen(primaryStage));
        Button ignoreButton = ButtonFactory.createIgnoreButton(e -> showIgnoreScreen(primaryStage));
        Button prizeButton = ButtonFactory.createPrizeButton(e -> showPrizesScreen(primaryStage));
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

    private HBox createTopLayout(Button leftButton, Label title, Button... rightButtons) {
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

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText());
        });

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

    private void showPrizesScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Prize> prizes = badgeManager.loadPrizesFromFile(Constants.PRIZES_FILE);

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
                    EventHandler<ActionEvent> deleteAction = (ActionEvent event) -> {
                        Prize prize = getTableView().getItems().get(getIndex());
                        if (showConfirmationAlert("Confirmation Dialog", "Delete Prize", "Are you sure you want to delete this prize?")) {
                            deletePrize(prize);
                            showPrizesScreen(primaryStage);
                        }
                    };

                    Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
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

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Prizes List");
        Button createButton = ButtonFactory.createAddButton(e -> showAddPrizesScreen(primaryStage));

        HBox topLayout = createTopLayout(backButton, titleLabel, createButton);

        layout.getChildren().addAll(
                topLayout,
                table
        );

        createScene(layout, primaryStage);
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private Label createLabel(String text) {
        return new Label(text);
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

        TextFlow textFlow = new TextFlow(bold, normal);
        return textFlow;
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

    private boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void handleDeleteAction(Stage primaryStage, Profile profile) {
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, Constants.PROFILES_FILE);
        showMainScreen(primaryStage);
    }

    private void createScene(Parent layout, Stage primaryStage) {
        layout.setStyle("-fx-font-size: 18;-fx-padding: 10px;");

        Scene mainScene = new Scene(layout, 600, 400);
        primaryStage.setScene(mainScene);
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

    private void deletePrize(Prize prize) {
        List<Prize> prizes = badgeManager.loadPrizesFromFile(Constants.PRIZES_FILE);
        prizes.remove(prize);
        savePrizesToFile(prizes, Constants.PRIZES_FILE);
    }

    private void savePrizesToFile(List<Prize> prizes, String fileName) {
        JSONArray jsonArray = convertPrizesToJSONArray(prizes);
        fileManager.writeJsonToFile(jsonArray, fileName);
    }

    private void showAddPrizesScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        TextField namePrizeField = new TextField();
        namePrizeField.setPromptText("Enter name prize");

        TextField badgeCountField = new TextField();
        badgeCountField.setPromptText("Enter badge count");

        HBox typeLayout = new HBox();
        typeLayout.setAlignment(Pos.CENTER);

        List<TypeBadge> typeBadgeList = typeBadgeDataManager.loadTypesBadgeFromFile(Constants.TYPES_BADGE_FILE);
        ComboBox<String> badgeTypeComboBox = new ComboBox<>();
        typeBadgeList.forEach(typeBadge -> badgeTypeComboBox.getItems().add(typeBadge.getName()));
        badgeTypeComboBox.setPromptText("Select badge type");
        Button addButton = ButtonFactory.createAddButton(e -> showAddTypeBadgeScreen(primaryStage));

        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        typeLayout.getChildren().addAll(badgeTypeComboBox, addButton, rightSpacer);

        Button saveButton = ButtonFactory.createSavePrizeButton(e -> {
            savePrize(namePrizeField.getText(), badgeCountField.getText(), badgeTypeComboBox.getValue());
            showPrizesScreen(primaryStage);
        });


        Button backButton = ButtonFactory.createBackButton(e -> showPrizesScreen(primaryStage));
        Label titleLabel = createLabel("Add Prize");

        HBox topLayout = createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                namePrizeField,
                badgeCountField,
                typeLayout,
                saveButton
        );

        createScene(layout, primaryStage);
    }

    private void showAddTypeBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        TypeBadge typeBadge = new TypeBadge();
        
        TextField nameField = createTextField("Name");
        TextField dateField = createTextField("Start Date (e.g., 26.09.2023)");

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            handleTypeBadgeSave(primaryStage, typeBadge, nameField.getText(), dateField.getText());
        });

        Button backButton = ButtonFactory.createBackButton(e -> showAddPrizesScreen(primaryStage));
        Label titleLabel = createLabel("Create Badge Type");

        HBox topLayout = createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                saveButton
        );

        createScene(layout, primaryStage);
    }

    private void handleTypeBadgeSave(Stage primaryStage, TypeBadge typeBadge, String name, String startDate) {
        if(name != null && !name.isEmpty() &&
                startDate != null && !startDate.isEmpty()) {

            typeBadge.setName(name);
            typeBadge.setStartDate(startDate);

            typeBadgeDataManager.saveTypeBadgeToFile(typeBadge, Constants.TYPES_BADGE_FILE);
            showAddPrizesScreen(primaryStage);
        }
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

            List<Prize> existingPrizes = badgeManager.loadPrizesFromFile(Constants.PRIZES_FILE);
            existingPrizes.add(newPrize);

            JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(Constants.PRIZES_FILE), StandardCharsets.UTF_8)) {
                writer.write(jsonArray.toString());
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

}