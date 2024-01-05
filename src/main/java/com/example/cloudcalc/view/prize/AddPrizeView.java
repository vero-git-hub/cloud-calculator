package com.example.cloudcalc.view.prize;

import com.example.cloudcalc.builder.fields.prize.PrizeFieldManager;
import com.example.cloudcalc.builder.fields.prize.PrizeFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class AddPrizeView implements Localizable, PrizeFieldUpdatable {
    private final PrizeController prizeController;
    private String title = "ADD PRIZE";
    private ComboBox<String> programsComboBox = new ComboBox<>();
    private TextField nameTextField = new TextField();
    private TextField badgeCountTextField = new TextField();

    public AddPrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;

        programsComboBox.setPromptText("Select program");

        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        resetForm();
        VBox layout = new VBox(10);

        List<Program> programs = prizeController.loadProgramsFromFile();
        programsComboBox.getItems().clear();
        programs.forEach(program -> programsComboBox.getItems().add(program.getName()));

        PrizeFieldManager prizeTextFieldManager = LanguageManager.getTextFieldPrizeManager();
        nameTextField = prizeTextFieldManager.getNameField();
        badgeCountTextField = prizeTextFieldManager.getBadgeCountField();

        Button saveButton = ButtonFactory.createSaveButton(e -> {
            Prize prize = new Prize();
            prize.setName(nameTextField.getText());
            prize.setCount(Integer.parseInt(badgeCountTextField.getText()));
            prize.setProgram(programsComboBox.getValue());
            prizeController.savePrize(stage, prize);
        });

        Button backButton = ButtonFactory.createBackButton(e -> prizeController.showScreen(stage));

        HBox topLayout = prizeController.createTopLayoutForAddScreen(backButton, new Label(title));

        layout.getChildren().addAll(
                topLayout,
                nameTextField,
                badgeCountTextField,
                programsComboBox,
                saveButton
        );

        prizeController.createScene(layout, stage);
    }

    private void resetForm() {
        nameTextField.clear();
        badgeCountTextField.clear();
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addPrizeTitle");
        programsComboBox.setPromptText(bundle.getString("programsLabelSelect"));

        updateNameFieldPlaceholder(bundle.getString("addPrizeNameField"));
        updateBadgeCountFieldPlaceholder(bundle.getString("addPrizeCountField"));
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        if (nameTextField != null) {
            nameTextField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateBadgeCountFieldPlaceholder(String placeholder) {
        if (badgeCountTextField != null) {
            badgeCountTextField.setPromptText(placeholder);
        }
    }
}