package com.example.cloudcalc.view.prize;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.prize.Prize;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class PrizeView implements Localizable {

    private final PrizeController prizeController;
    private String title = "PRIZES";

    public PrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

        List<Prize> prizes = prizeController.loadPrizesFromFile();

        TableView<Prize> table = prizeController.createTableForPrize(stage, prizes);

        Button backButton = ButtonFactory.createBackButton(e -> prizeController.showMainScreen(stage));

        Button createButton = ButtonFactory.createAddButton(e -> prizeController.showAddScreen(stage));

        HBox topLayout = prizeController.createTopLayout(backButton, new Label(title), createButton);

        layout.getChildren().addAll(
                topLayout,
                table
        );

        prizeController.createScene(layout, stage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("prizeTitle");
    }
}
