package com.example.cloudcalc.view;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ProgramView implements Localizable {
    private final ProgramController programController;
    private String title = "PROGRAMS";

    public ProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        VBox layout = new VBox(10);

//        List<Prize> prizes = prizeController.loadPrizesFromFile();
//
//        TableView<Prize> table = prizeController.createTableForPrize(stage, prizes);
//
        Button backButton = ButtonFactory.createBackButton(e -> programController.showMainScreen(stage));
//
        Button createButton = ButtonFactory.createAddButton(e -> programController.showAddScreen(stage));

        HBox topLayout = programController.createTopLayout(backButton, new Label(title), createButton);

        layout.getChildren().addAll(
                topLayout
        );

        programController.createScene(layout, stage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("programTitle");
    }
}