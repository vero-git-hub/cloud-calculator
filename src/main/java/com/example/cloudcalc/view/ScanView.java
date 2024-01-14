package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ScanController;
import com.example.cloudcalc.entity.*;
import com.example.cloudcalc.entity.prize.PrizeData;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ScanView implements Localizable {

    private final ScanController scanController;
    private Label subtitleLabel = new Label("Labs with the same names are counted as one");
    private String titlePreText = "Earned for ";

    public ScanView(ScanController scanController) {
        this.scanController = scanController;

        subtitleLabel.setStyle("-fx-font-style: italic;");
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage primaryStage, Profile profile) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> scanController.showStatsScreen(primaryStage));

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

        HBox topLayout = scanController.createTopLayoutWithBackAndText(backButton, textFlow);

        List<PrizeData> prizeDataList = parseProfilePrizes(profile);

        TableView<PrizeData> scanTable = createScanTable(prizeDataList);

        layout.getChildren().addAll(topLayout, subtitleLabel, scanTable);
        scanController.createScene(layout, primaryStage);
    }

    private List<PrizeData> parseProfilePrizes(Profile profile) {
        List<PrizeData> prizeDataList = new ArrayList<>();
        JSONArray programPrizesArray = new JSONArray(profile.getProgramPrizes());

        for (int j = 0; j < programPrizesArray.length(); j++) {
            JSONObject programPrizesObj = programPrizesArray.getJSONObject(j);
            String program = programPrizesObj.getString("program");

            JSONArray prizeInfoListArray = programPrizesObj.getJSONArray("prizeInfoList");
            for (int k = 0; k < prizeInfoListArray.length(); k++) {
                JSONObject prizeInfoObj = prizeInfoListArray.getJSONObject(k);
                int earnedPoints = prizeInfoObj.getInt("earnedPoints");
                String prize = prizeInfoObj.optString("prize", "No prize");

                prizeDataList.add(new PrizeData(program, earnedPoints, prize));
            }
        }

        return prizeDataList;
    }

    private TableView<PrizeData> createScanTable(List<PrizeData> prizeDataList) {
        TableView<PrizeData> table = new TableView<>();
        ObservableList<PrizeData> data = FXCollections.observableArrayList(prizeDataList);

        TableColumn<PrizeData, Number> indexColumn = new TableColumn<>("№");
        indexColumn.setSortable(false);
        indexColumn.setMinWidth(30);
        indexColumn.setPrefWidth(50);
        indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(column.getValue()) + 1));

        TableColumn<PrizeData, String> programColumn = new TableColumn<>("Program");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));
        programColumn.setPrefWidth(300);

        TableColumn<PrizeData, Integer> pointsColumn = new TableColumn<>("Earned Points");
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("earnedPoints"));
        pointsColumn.setPrefWidth(148);

        TableColumn<PrizeData, String> prizeColumn = new TableColumn<>("Prize");
        prizeColumn.setCellValueFactory(new PropertyValueFactory<>("prize"));
        prizeColumn.setPrefWidth(300);

        table.getColumns().addAll(indexColumn, programColumn, prizeColumn, pointsColumn);
        table.setItems(data);

        return table;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titlePreText = bundle.getString("scanTitle");
        subtitleLabel.setText(bundle.getString("scanSubtitle"));
    }
}