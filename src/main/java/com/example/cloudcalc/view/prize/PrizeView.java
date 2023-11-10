package com.example.cloudcalc.view.prize;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.Prize;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class PrizeView {

    private final PrizeController prizeController;
    private String title = "PRIZES";

    public PrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

        List<Prize> prizes = prizeController.loadPrizesFromFile();

        TableView<Prize> table = prizeController.createTableForPrize(stage, prizes);

        Button backButton = ButtonFactory.createBackButton(e -> prizeController.showMainScreen(stage));

        Button createButton = ButtonFactory.createAddButton(e -> prizeController.showAddPrizesScreen(stage));

        HBox topLayout = prizeController.createTopLayout(backButton, new Label(title), createButton);

        layout.getChildren().addAll(
                topLayout,
                table
        );

        prizeController.createScene(layout, stage);
    }
}
