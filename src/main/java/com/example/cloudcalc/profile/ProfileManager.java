package com.example.cloudcalc.profile;

import com.example.cloudcalc.*;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.scan.ScanManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileManager implements Localizable {

    private final UICallbacks uiCallbacks;
    private final DataExtractor dataExtractor;
    private final ProfileDataManager profileDataManager;
    private final ScanManager scanManager;
    private Label titleLabel;
    TextField nameField;
    TextField dateField;
    TextField linkField;
    private String preText;
    private String startDateText;
    private Label linksTitle = new Label("PDF Links:");

    public ProfileManager(DataExtractor dataExtractor, ProfileDataManager profileDataManager, UICallbacks uiCallbacks, ScanManager scanManager) {
        this.uiCallbacks = uiCallbacks;
        this.dataExtractor = dataExtractor;
        this.profileDataManager = profileDataManager;
        this.scanManager = scanManager;

        titleLabel = uiCallbacks.createLabel("Create Profile");

        nameField = uiCallbacks.createTextField("Name");
        dateField = uiCallbacks.createTextField("Start Date (e.g., 26.09.2023)");
        linkField = uiCallbacks.createTextField("Profile Link");

        preText = "DETAILS for";
        startDateText = "Start Date:";

        LanguageManager.registerLocalizable(this);
    }

    public void showProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));

        Text preTextLabel = new Text(preText);
        Hyperlink nameLink = new Hyperlink(profile.getName());
        nameLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(profile.getProfileLink()));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });

        TextFlow textFlow = new TextFlow(preTextLabel, nameLink);
        HBox topLayout = uiCallbacks.createTopLayoutWithBackAndText(backButton, textFlow);

        layout.getChildren().addAll(
                topLayout,
                createProfileInfo(profile),
                createPdfLinksSection(profile)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(layout);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    private VBox createPdfLinksSection(Profile profile) {
        VBox linksVBox = new VBox(5);

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
        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        linksVBox.getChildren().add(table);

        return linksVBox;
    }

    private VBox createProfileInfo(Profile profile) {
        VBox profileInfoBox = new VBox(10);

        TextFlow startDateFlow = uiCallbacks.createTextFlow(startDateText + " ", profile.getStartDate());
        profileInfoBox.getChildren().addAll(startDateFlow);

        return profileInfoBox;
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

        Button saveButton = ButtonFactory.createSaveButton(e -> handleProfileSave(primaryStage, profile, nameField.getText(), dateField.getText(), linkField.getText()));

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));


        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                nameField,
                dateField,
                linkField,
                uploadPdfButton,
                saveButton
        );

        uiCallbacks.createScene(layout, primaryStage);
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
            uiCallbacks.showMainScreen(primaryStage);
        }
    }

    public TableColumn<Profile, Void> createNumberingColumn() {
        TableColumn<Profile, Void> numberColumn = new TableColumn<>("№");
        numberColumn.setMinWidth(40);
        numberColumn.setCellValueFactory(param -> null);
        numberColumn.setCellFactory(col -> {
            return new TableCell<Profile, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                    } else {
                        setText(Integer.toString(getIndex() + 1));
                    }
                }
            };
        });
        return numberColumn;
    }

    public TableColumn<Profile, String> createNameColumn() {
        TableColumn<Profile, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        return nameColumn;
    }

    public TableColumn<Profile, Void> createBadgesColumn(Stage primaryStage) {
        TableColumn<Profile, Void> badgesColumn = new TableColumn<>("Scan");
        badgesColumn.setCellValueFactory(param -> null);
        badgesColumn.setCellFactory(col -> {
            return new TableCell<Profile, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Profile profile = getTableView().getItems().get(getIndex());
                        EventHandler<ActionEvent> scanAction = e -> {
                            ArrayList<String> siteLinks = dataExtractor.performScan(profile);
                            profile.setLastScannedDate(getCurrentDate());
                            profileDataManager.updateProfile(profile);

                            scanManager.showScanScreen(primaryStage, profile, siteLinks);
                        };
                        Button scanButton = ButtonFactory.createScanButton(scanAction);
                        setGraphic(scanButton);
                    }
                }
            };
        });
        return badgesColumn;
    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }


    public TableColumn<Profile, Profile> createViewingColumn(Stage primaryStage) {
        TableColumn<Profile, Profile> viewingColumn = new TableColumn<>("View");
        viewingColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        viewingColumn.setCellFactory(param -> new TableCell<Profile, Profile>() {
            @Override
            protected void updateItem(Profile profile, boolean empty) {
                super.updateItem(profile, empty);
                if (profile == null || empty) {
                    setGraphic(null);
                    return;
                }

                EventHandler<ActionEvent> viewAction = e -> {
                    showProfileScreen(primaryStage, profile);
                };
                Button viewButton = ButtonFactory.createViewButton(viewAction);
                setGraphic(viewButton);
            }
        });
        return viewingColumn;
    }

    public TableColumn<Profile, Profile> createActionColumn(Stage primaryStage) {
        TableColumn<Profile, Profile> actionColumn = new TableColumn<>("Delete");
        actionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        actionColumn.setCellFactory(param -> new TableCell<Profile, Profile>() {
            @Override
            protected void updateItem(Profile profile, boolean empty) {
                super.updateItem(profile, empty);
                if (profile == null || empty) {
                    setGraphic(null);
                    return;
                }

                EventHandler<ActionEvent> deleteAction = e -> {
                    if (uiCallbacks.showConfirmationAlert("Confirmation Dialog", "Delete Profile", "Are you sure you want to delete this profile?")) {
                        handleDeleteAction(primaryStage, profile);
                    }
                };
                Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);
                setGraphic(deleteButton);
            }
        });
        return actionColumn;
    }

    private void handleDeleteAction(Stage primaryStage, Profile profile) {
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        profiles.remove(profile);
        profileDataManager.saveProfilesToFile(profiles, Constants.PROFILES_FILE);

        uiCallbacks.showMainScreen(primaryStage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("createProfileTitle"));
        nameField.setPromptText(bundle.getString("createProfileNameField"));
        dateField.setPromptText(bundle.getString("createProfileDateField"));
        linkField.setPromptText(bundle.getString("createProfileLinkField"));

        preText = bundle.getString("detailsTitle");
        startDateText = bundle.getString("detailsStartDateText");

        linksTitle.setText(bundle.getString("detailsLinksTitle"));
    }
}
