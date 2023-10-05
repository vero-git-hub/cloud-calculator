package com.example.cloudcalc;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainUI {

    private static final String PRIZES_FILE = "prizes.json";
    private static final String PROFILES_FILE = "profiles.json";
    private static final String IGNORE_FILE = "ignore.json";

    private static final String TOTAL = "Total";
    private static final String IGNORE = "Ignore";
    private static final String PDF_TOTAL = "PDF total";
    private static final String PDF_FOR_PRIZE = "PDF for prize";
    private static final String SKILL = "Skill";
    private static final String SKILL_FOR_PRIZE = "Skill for prize";

    private final ProfileDataManager profileDataManager = new ProfileDataManager();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager();
    private final List<String> receivedPrizes = new ArrayList<>();

    public void showMainScreen(Stage primaryStage) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Button addButton = ButtonFactory.createAddButton(e -> showCreateProfileScreen(primaryStage));
        Button ignoreButton = ButtonFactory.createIgnoreButton(e -> showIgnoreScreen(primaryStage));
        Button prizeButton = ButtonFactory.createPrizeButton(e -> showPrizesScreen(primaryStage));
        Label titleLabel = createLabel("Profiles");

        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().addAll(ignoreButton, leftSpacer, titleLabel, rightSpacer, addButton, prizeButton);
        topLayout.setMinWidth(560);

        VBox layout = new VBox(10);
        layout.getChildren().add(topLayout);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile(PROFILES_FILE);
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

        Button saveButton = ButtonFactory.createSaveProfileButton(e -> {
            handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText());
        });

        HBox topLayout = new HBox();
        topLayout.setAlignment(Pos.CENTER);

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Create Profile");

        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().addAll(backButton, leftSpacer, titleLabel, rightSpacer);

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

        layout.getChildren().addAll(
                createLabel("Ignore Screen"),
                ButtonFactory.createButton("Add Ignore Badge", e -> showAddIgnoreBadgeScreen(primaryStage), null),
                backButton,
                createIgnoredBadgesList(primaryStage)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    private void showPrizesScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Prize> prizes = loadPrizesFromFile(PRIZES_FILE);

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

        HBox topLayout = new HBox();
        topLayout.setAlignment(Pos.CENTER_LEFT);

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Prizes List");
        Button createButton = ButtonFactory.createAddButton(e -> showAddPrizesScreen(primaryStage));

        Pane spacer1 = new Pane();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Pane spacer2 = new Pane();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        topLayout.getChildren().addAll(backButton, spacer1, titleLabel, spacer2, createButton);

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
        List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile(IGNORE_FILE);

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
                ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, IGNORE_FILE);
                showIgnoreScreen(primaryStage);
            }
        };

        Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

        badgeRow.getChildren().addAll(badgeLabel, deleteButton);
        return badgeRow;
    }

    private void showAddIgnoreBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button saveIgnoreBadgeButton = ButtonFactory.createSaveIgnoreBadgeButton(
                () -> {
                    List<String> ignoredBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile(IGNORE_FILE);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");
                    ignoredBadges.add(nameField.getText());
                    ignoredBadgeManager.saveIgnoredBadgesToFile(ignoredBadges, IGNORE_FILE);
                    showIgnoreScreen(primaryStage);
                },
                () -> (TextField) layout.getScene().lookup("#nameField")
        );

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));

        layout.getChildren().addAll(
                createLabel("Add Ignore Badge Screen"),
                createNameTextField(),
                saveIgnoreBadgeButton,
                backButton
        );

        createScene(layout, primaryStage);
    }

    private VBox createBadgeLabels(Profile profile, ArrayList<String> siteLinks) {
        VBox labelsBox = new VBox(5);
        Map<String, Integer> badgeCounts = calculateBadgeCounts(profile, siteLinks);

        String prizesStr = String.join(", ", receivedPrizes);

        labelsBox.getChildren().addAll(
                createTextFlow("Total: ", String.valueOf(badgeCounts.get(TOTAL))),
                createLabel("Ignore: " + badgeCounts.get(IGNORE)),
                createTextFlow("Skill: ", String.valueOf(badgeCounts.get(SKILL))),
                createLabel("PDF total: " + badgeCounts.get(PDF_TOTAL)),
                createTextFlow("PDF for prize: ", String.valueOf(badgeCounts.get(PDF_FOR_PRIZE))),
                createTextFlow("Skill for prize: ", String.valueOf(badgeCounts.get(SKILL_FOR_PRIZE))),
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
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(PROFILES_FILE);
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, PROFILES_FILE);
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

        layout.getChildren().addAll(
                new Label("Scan Results for " + profile.getName()),
                createBadgeLabels(profile, siteLinks),
                backButton
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
            profileDataManager.saveProfileToFile(profile, PROFILES_FILE);
            showMainScreen(primaryStage);
        }
    }

    private TextField createNameTextField() {
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Lab name");
        return nameField;
    }

    private Map<String, Integer> calculateBadgeCounts(Profile profile, ArrayList<String> receivedBadges) {
        Map<String, Integer> badgeCounts = new HashMap<>();

        int total = receivedBadges.size();

        List<String> skillBadges = filterSkillBadges(receivedBadges);
        int skill = skillBadges.size();
        int ignore = total - skill;

        List<String> pdfBadges = getPDFBadges(profile, skillBadges);
        int totalPDF = pdfBadges.size();

        int prizePDF;
        int constantForPrize = 7;

        if(totalPDF > constantForPrize) {
            prizePDF = constantForPrize;
        } else if (totalPDF < constantForPrize) {
            prizePDF = 0;
        } else {
            prizePDF = constantForPrize;
        }

        int prizeSkill;
        if(prizePDF == 0){
            prizeSkill = skill;
        } else {
            prizeSkill = skill - prizePDF;
        }

        receivedPrizes.clear();
        List<Prize> prizes = loadPrizesFromFile(PRIZES_FILE);

        for (Prize prize : prizes) {
            if ("pdf".equals(prize.getType()) && prizePDF >= prize.getCount()) {
                receivedPrizes.add(prize.getName());
            }
            if ("skill".equals(prize.getType()) && prizeSkill >= prize.getCount()) {
                receivedPrizes.add(prize.getName());
            }
        }

        badgeCounts.put(TOTAL, total);
        badgeCounts.put(IGNORE, ignore);
        badgeCounts.put(SKILL, skill);
        badgeCounts.put(PDF_TOTAL, totalPDF);
        badgeCounts.put(PDF_FOR_PRIZE, prizePDF);
        badgeCounts.put(SKILL_FOR_PRIZE, prizeSkill);

        return badgeCounts;
    }

    /**
     * Matching pdf and filtered badges
     * @param profile
     * @param skillBadges
     * @return PDF badges from received badges
     */
    private List<String> getPDFBadges(Profile profile, List<String> skillBadges) {
        List<String> pdfBadgesFromProfile = new ArrayList<>(profile.getPdfLinks());

        Set<String> intersection = new HashSet<>(skillBadges);
        intersection.retainAll(pdfBadgesFromProfile);

        List<String> commonBadges = new ArrayList<>(intersection);

        return commonBadges;
    }

    /**
     * Delete ignore from received badges
     * @param receivedBadges
     * @return received badges without ignore
     */
    private List<String> filterSkillBadges(ArrayList<String> receivedBadges) {
        List<String> ignoreBadges = ignoredBadgeManager.loadIgnoredBadgesFromFile(IGNORE_FILE);
        if (!ignoreBadges.isEmpty()) {
            receivedBadges.removeAll(ignoreBadges);
        }
        return receivedBadges;
    }

    private void deletePrize(Prize prize) {
        List<Prize> prizes = loadPrizesFromFile(PRIZES_FILE);
        prizes.remove(prize);
        savePrizesToFile(prizes, PRIZES_FILE);
    }

    private void savePrizesToFile(List<Prize> prizes, String fileName) {
        JSONArray jsonArray = convertPrizesToJSONArray(prizes);
        writeJsonToFile(jsonArray, fileName);
    }

    private List<Prize> loadPrizesFromFile(String fileName) {
        List<Prize> prizes = new ArrayList<>();
        JSONArray jsonArray = readJsonArrayFromFile(fileName);

        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject prizeObject = jsonArray.getJSONObject(j);
            Prize prize = new Prize();
            prize.setName(prizeObject.getString("name"));
            prize.setType(prizeObject.getString("type"));
            prize.setCount(prizeObject.getInt("count"));
            prizes.add(prize);
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

        Button saveButton = ButtonFactory.createSavePrizeButton(e -> {
            savePrize(namePrizeField.getText(), badgeCountField.getText(), badgeTypeComboBox.getValue());
            showPrizesScreen(primaryStage);
        });

        HBox topLayout = new HBox();
        topLayout.setAlignment(Pos.CENTER);

        Button backButton = ButtonFactory.createBackButton(e -> showMainScreen(primaryStage));
        Label titleLabel = createLabel("Add Prize");

        Pane leftSpacer = new Pane();
        Pane rightSpacer = new Pane();

        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().addAll(backButton, leftSpacer, titleLabel, rightSpacer);

        layout.getChildren().addAll(
                topLayout,
                namePrizeField,
                badgeCountField,
                badgeTypeComboBox,
                saveButton
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

            List<Prize> existingPrizes = loadPrizesFromFile(PRIZES_FILE);
            existingPrizes.add(newPrize);

            JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(PRIZES_FILE), StandardCharsets.UTF_8)) {
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

    private JSONArray readJsonArrayFromFile(String fileName) {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return new JSONArray();
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            return new JSONArray(content);
        } catch (IOException | JSONException e) {
            System.out.println("Error reading JSON from file: " + fileName);
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void writeJsonToFile(JSONArray jsonArray, String fileName) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            System.out.println("Error writing JSON to file: " + fileName);
            e.printStackTrace();
        }
    }

}