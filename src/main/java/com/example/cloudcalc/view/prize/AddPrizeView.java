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
    private ComboBox<String> programsComboBox = new ComboBox<>();
    private TextField nameTextField = new TextField();
    private TextField badgeCountTextField = new TextField();
    Label titleLabel = new Label();
    private VBox layout;
    PrizeFieldManager prizeTextFieldManager;
    Button saveButton = new Button();
    Button cancelButton = new Button();

    public AddPrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        resetForm();
        layout = new VBox(10);

        prizeTextFieldManager = LanguageManager.getTextFieldPrizeManager();

        layout.getChildren().addAll(
                createTopLayout(stage),
                createNameField(),
                createBadgeCountField(),
                createComboBox(),
                createSaveButton(stage)
        );

        prizeController.createScene(layout, stage);
    }

    private void resetForm() {
        nameTextField.clear();
        badgeCountTextField.clear();
    }

    private HBox createTopLayout(Stage stage) {
        Button backButton = ButtonFactory.createBackButton(e -> prizeController.showScreen(stage));
        return prizeController.createTopLayoutForAddScreen(backButton, titleLabel);
    }

    private TextField createNameField() {
        return nameTextField = prizeTextFieldManager.getNameField();
    }

    private TextField createBadgeCountField() {
        return badgeCountTextField = prizeTextFieldManager.getBadgeCountField();
    }

    private ComboBox<String> createComboBox() {
        List<Program> programs = prizeController.loadProgramsFromFile();
        programsComboBox.getItems().clear();
        programs.forEach(program -> {
            programsComboBox.getItems().add(program.getName());
        });
        return programsComboBox;
    }

    private HBox createSaveButton(Stage stage) {
        saveButton = ButtonFactory.createSaveButton(e -> {
            Prize prize = new Prize();
            prize.setName(nameTextField.getText());
            prize.setCount(Integer.parseInt(badgeCountTextField.getText()));
            prize.setProgram(programsComboBox.getValue());
            prizeController.savePrize(stage, prize);
        });

        return new HBox(10, saveButton, cancelButton);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        String title = bundle.getString("addPrizeTitle");
        titleLabel.setText(title);
        String promptText = bundle.getString("programsLabelSelect");
        programsComboBox.setPromptText(promptText);
        cancelButton.setText(bundle.getString("cancelButton"));

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