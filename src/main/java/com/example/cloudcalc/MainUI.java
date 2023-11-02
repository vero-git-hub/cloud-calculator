package com.example.cloudcalc;

import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.badge.arcade.ArcadeManager;
import com.example.cloudcalc.badge.ignored.IgnoreManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.language.Language;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.profile.ProfileManager;
import com.example.cloudcalc.scan.ScanManager;
import com.example.cloudcalc.statistics.StatsManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.ResourceBundle;

public class MainUI implements UICallbacks, Localizable {

    private final ProfileDataManager profileDataManager = new ProfileDataManager();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final PrizeManager prizeManager = new PrizeManager(this);
    private final FileOperationManager fileOperationManager = new FileOperationManager();
    private final ScanManager scanManager = createScanManager();
    private final ProfileManager profileManager = createProfileManager();
    private final IgnoreManager ignoreManager = createIgnoredBadgeScreen();

    private final BadgeManager badgeManager = new BadgeManager(dataExtractor, prizeManager, profileDataManager, fileOperationManager);
    private final StatsManager statsManager = new StatsManager(this, profileManager, profileDataManager, dataExtractor, badgeManager, prizeManager);

    private final ArcadeManager arcadeManager = createArcadeManager();

    private TableView<Profile> mainTable;

    Label titleLabel;

    public MainUI() {
        titleLabel = createLabel("PROFILES");
        LanguageManager.registerLocalizable(this);
    }

    private ArcadeManager createArcadeManager() {
        return new ArcadeManager(this, fileOperationManager);
    }

    private IgnoreManager createIgnoredBadgeScreen() {
        return new IgnoreManager(this, fileOperationManager);
    }

    private ScanManager createScanManager() {
        BadgeManager badgeManager = new BadgeManager(dataExtractor, prizeManager, profileDataManager, fileOperationManager);
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

        VBox.setVgrow(table, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        createScene(scrollPane, primaryStage);
    }

    private HBox initializeButtons(Stage primaryStage) {
        Button addButton = ButtonFactory.createAddButton(e -> profileManager.showCreateProfileScreen(primaryStage));
        Button statsButton = ButtonFactory.createStatsButton(e -> statsManager.showStatsScreen(primaryStage));
        Button ignoreButton = ButtonFactory.createIgnoreButton(e -> ignoreManager.getIgnoreScreen().showScreen(primaryStage));
        Button prizeButton = ButtonFactory.createPrizeButton(e -> prizeManager.showPrizesScreen(primaryStage));
        Button arcadeButton = ButtonFactory.createArcadeButton(e -> arcadeManager.getArcadeScreen().showScreen(primaryStage));

        ComboBox<Language> languageComboBox = LanguageManager.createLanguageComboBox();

        return createExtendedTopLayout(Arrays.asList(statsButton, addButton), titleLabel, arcadeButton, ignoreButton, prizeButton, languageComboBox);
    }

    private TableView<Profile> initializeTable(Stage primaryStage) {
        mainTable = new TableView<>();

        TableColumn<Profile, Void> numberColumn = profileManager.createNumberingColumn();
        TableColumn<Profile, String> nameColumn = profileManager.createNameColumn();
        TableColumn<Profile, Void> badgesColumn = profileManager.createBadgesColumn(primaryStage);
        TableColumn<Profile, Profile> viewingColumn = profileManager.createViewingColumn(primaryStage);
        TableColumn<Profile, Profile> actionColumn = profileManager.createActionColumn(primaryStage);

        mainTable.getColumns().addAll(numberColumn, nameColumn, badgesColumn, viewingColumn, actionColumn);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        mainTable.getItems().addAll(profiles);
        configureTableColumnsWidth();
        return mainTable;
    }

    private void configureTableColumnsWidth() {
        double numberColumnPercentage = 0.05;
        double badgesColumnPercentage = 0.1;
        double viewingColumnPercentage = 0.1;
        double actionColumnPercentage = 0.1;
        double nameColumnPercentage = 1.0 - (numberColumnPercentage + badgesColumnPercentage + viewingColumnPercentage + actionColumnPercentage);

        mainTable.getColumns().get(0).prefWidthProperty().bind(mainTable.widthProperty().multiply(numberColumnPercentage));
        mainTable.getColumns().get(1).prefWidthProperty().bind(mainTable.widthProperty().multiply(nameColumnPercentage));
        mainTable.getColumns().get(2).prefWidthProperty().bind(mainTable.widthProperty().multiply(badgesColumnPercentage));
        mainTable.getColumns().get(3).prefWidthProperty().bind(mainTable.widthProperty().multiply(viewingColumnPercentage));
        mainTable.getColumns().get(4).prefWidthProperty().bind(mainTable.widthProperty().multiply(actionColumnPercentage));
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
    public HBox createExtendedTopLayout(List<Button> leftButtons, Label title, Node... rightNodes) {
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
        topLayout.getChildren().addAll(rightNodes);
        topLayout.setMinWidth(560);
        return topLayout;
    }

    @Override
    public HBox createTopLayoutWithBackAndText(Button backButton, TextFlow textFlow) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().add(backButton);
        topLayout.getChildren().add(leftSpacer);
        topLayout.getChildren().add(textFlow);
        topLayout.getChildren().add(rightSpacer);

        return topLayout;
    }

    @Override
    public void createScene(Parent layout, Stage primaryStage) {
        layout.setStyle("-fx-font-size: 18;-fx-padding: 10px;");

        int WIDTH_SCENE = 820;
        int HEIGHT_SCENE = 620;
        Scene mainScene = new Scene(layout, WIDTH_SCENE, HEIGHT_SCENE);
        mainScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(mainScene);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("profilesLabel"));
    }
}