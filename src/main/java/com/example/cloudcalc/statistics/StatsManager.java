package com.example.cloudcalc.statistics;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.profile.ProfileManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class StatsManager {

    private final UICallbacks uiCallbacks;

    private final ProfileManager profileManager;

    private final ProfileDataManager profileDataManager;

    public StatsManager(UICallbacks uiCallbacks, ProfileManager profileManager, ProfileDataManager profileDataManager) {
        this.uiCallbacks = uiCallbacks;
        this.profileManager = profileManager;
        this.profileDataManager = profileDataManager;
    }

    public void showStatsScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        String title = String.format("STATISTICS");
        Label titleLabel = uiCallbacks.createLabel(title);
        Label subtitleLabel = new Label("To get up-to-date results, perform a scan");
        subtitleLabel.setStyle("-fx-font-style: italic;");

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        TableView<Profile> table = new TableView<>();

        TableColumn<Profile, Integer> numberColumn = new TableColumn<>("No.");
        numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));

        TableColumn<Profile, String> nameColumn = profileManager.createNameColumn();
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        table.getItems().addAll(profiles);

        TableColumn<Profile, String> prizesColumn = new TableColumn<>("Prizes");

        TableColumn<Profile, Void> badgesColumn = profileManager.createBadgesColumn(primaryStage);


        table.getColumns().addAll(numberColumn, nameColumn, prizesColumn, badgesColumn);

        layout.getChildren().addAll(topLayout, subtitleLabel, table);

        uiCallbacks.createScene(layout, primaryStage);
    }
}
