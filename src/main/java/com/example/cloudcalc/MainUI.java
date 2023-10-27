package com.example.cloudcalc;

import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.IgnoredBadgeManager;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.profile.ProfileManager;
import com.example.cloudcalc.scan.ScanManager;
import com.example.cloudcalc.statistics.StatsManager;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainUI implements UICallbacks{

    private final ProfileDataManager profileDataManager = new ProfileDataManager();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final IgnoredBadgeManager ignoredBadgeManager = new IgnoredBadgeManager(this);
    private final PrizeManager prizeManager = new PrizeManager(this);
    private final ScanManager scanManager = createScanManager();
    private final ProfileManager profileManager = createProfileManager();
    private final BadgeManager badgeManager = new BadgeManager(dataExtractor, prizeManager, this, profileDataManager);
    private final StatsManager statsManager = new StatsManager(this, profileManager, profileDataManager, dataExtractor, badgeManager, prizeManager);
    private final int  WIDTH_SCENE = 600;
    private final int  HEIGHT_SCENE = 500;

    private ScanManager createScanManager() {
        BadgeManager badgeManager = new BadgeManager(dataExtractor, prizeManager, this, profileDataManager);
        return new ScanManager(badgeManager, this, profileDataManager);
    }

    private ProfileManager createProfileManager() {
        return new ProfileManager(dataExtractor, profileDataManager, this, scanManager);
    }

    @Override
    public void showMainScreen(Stage primaryStage) {
        HBox topLayout = initializeButtons(primaryStage);

        VBox layout = new VBox(10);
        layout.getChildren().add(topLayout);

        TableView<Profile> table = initializeTable(primaryStage);
        layout.getChildren().add(table);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        createScene(scrollPane, primaryStage);
    }

    private HBox initializeButtons(Stage primaryStage) {
        Button addButton = ButtonFactory.createAddButton(e -> profileManager.showCreateProfileScreen(primaryStage));
        Button statsButton = ButtonFactory.createStatsButton(e -> statsManager.showStatsScreen(primaryStage));
        Button ignoreButton = ButtonFactory.createIgnoreButton(e -> ignoredBadgeManager.showIgnoreScreen(primaryStage));
        Button prizeButton = ButtonFactory.createPrizeButton(e -> prizeManager.showPrizesScreen(primaryStage));

        Label titleLabel = createLabel("PROFILES");
        return createExtendedTopLayout(Arrays.asList(statsButton, addButton), titleLabel, ignoreButton, prizeButton);
    }

    private TableView<Profile> initializeTable(Stage primaryStage) {
        TableView<Profile> table = new TableView<>();

        TableColumn<Profile, Void> numberColumn = profileManager.createNumberingColumn(table);
        TableColumn<Profile, String> nameColumn = profileManager.createNameColumn();
        TableColumn<Profile, Void> badgesColumn = profileManager.createBadgesColumn(primaryStage);
        TableColumn<Profile, Profile> viewingColumn = profileManager.createViewingColumn(primaryStage);
        TableColumn<Profile, Profile> actionColumn = profileManager.createActionColumn(primaryStage);

        configureTableColumnsWidth(table, numberColumn, nameColumn, badgesColumn, viewingColumn, actionColumn);
        table.getColumns().addAll(numberColumn, nameColumn, badgesColumn, viewingColumn, actionColumn);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        table.getItems().addAll(profiles);

        return table;
    }

    private void configureTableColumnsWidth(TableView<Profile> table, TableColumn<Profile, Void> numberColumn, TableColumn<Profile, String> nameColumn, TableColumn<Profile, Void> badgesColumn, TableColumn<Profile, Profile> viewingColumn, TableColumn<Profile, Profile> actionColumn) {
        double width = 0.18;
        numberColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.40));
        badgesColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        viewingColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));
        actionColumn.prefWidthProperty().bind(table.widthProperty().multiply(width));

        numberColumn.setResizable(false);
        nameColumn.setResizable(false);
        badgesColumn.setResizable(false);
        viewingColumn.setResizable(false);
        actionColumn.setResizable(false);
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
    public HBox createExtendedTopLayout(List<Button> leftButtons, Label title, Button... rightButtons) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().addAll(leftButtons);
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

        Scene mainScene = new Scene(layout, WIDTH_SCENE, HEIGHT_SCENE);
        mainScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(mainScene);
    }

}