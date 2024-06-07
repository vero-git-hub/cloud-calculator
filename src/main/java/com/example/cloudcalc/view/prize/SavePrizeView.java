package com.example.cloudcalc.view.prize;

import com.example.cloudcalc.builder.fields.prize.PrizeFieldManager;
import com.example.cloudcalc.builder.fields.prize.PrizeFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.prize.Prize;
import com.example.cloudcalc.entity.program.Program;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.util.Notification;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class SavePrizeView implements Localizable, PrizeFieldUpdatable {
    private final PrizeController prizeController;
    private ComboBox<String> programsComboBox = new ComboBox<>();
    private TextField nameTextField = new TextField();
    private TextField badgeCountTextField = new TextField();
    Label titleLabel = new Label();
    private VBox layout;
    PrizeFieldManager prizeTextFieldManager;
    Button saveButton = new Button();
    Button cancelButton = new Button();
    String promptTextComboBox = "";

    public SavePrizeView(PrizeController prizeController) {
        this.prizeController = prizeController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        createComboBox(null);

        resetForm();

        layout = new VBox(10);

        prizeTextFieldManager = LanguageManager.getTextFieldPrizeManager();

        createNameField(null);
        createBadgeCountField(null);

        layout.getChildren().addAll(
                createTopLayout(stage),
                nameTextField,
                badgeCountTextField,
                programsComboBox,
                createButtonsSection(stage)
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

    private void createNameField(Prize prize) {
        if(prize == null) {
            nameTextField = prizeTextFieldManager.getNameField();
        } else {
            nameTextField = new TextField(prize.getName());
        }
    }

    private void createBadgeCountField(Prize prize) {
        if(prize == null) {
            badgeCountTextField = prizeTextFieldManager.getBadgeCountField();
        } else {
            badgeCountTextField = new TextField(prize.getPoints() + "");
        }
    }

    private void createComboBox(Prize prize) {
        programsComboBox.getItems().clear();

        List<Program> programs = prizeController.loadProgramsFromFile();
        programs.forEach(program -> programsComboBox.getItems().add(program.getName()));

        if(prize == null) {
            programsComboBox.setPromptText(promptTextComboBox);
        } else {
            programsComboBox.setValue(prize.getProgram());
        }

        programsComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty || item == null) {
                    setText("Select Subject");
                } else {
                    setText(item);
                }
            }
        });
    }

    private HBox createButtonsSection(Stage stage) {
        saveButton.setOnAction(event -> {
            String name = nameTextField.getText();
            String count = badgeCountTextField.getText();
            String program = programsComboBox.getValue();

            if(name == null && name.trim().isEmpty() &&
                    count == null && count.trim().isEmpty() && program == null && program.trim().isEmpty()) {
                Notification.showAlert("Error", "Empty fields", "Please fill all fields.");
            } else {
                Prize prize = new Prize();
                prize.setName(name);
                prize.setPoints(Integer.parseInt(count));
                prize.setProgram(program);
                savePrize(stage, prize);
            }
        });

        return new HBox(10, saveButton, cancelButton);
    }

    private void savePrize(Stage stage, Prize prize) {
        prizeController.savePrize(stage, prize);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("addPrizeTitle"));

        promptTextComboBox = bundle.getString("programsLabelSelect");
        programsComboBox.setPromptText(promptTextComboBox);

        saveButton.setText(bundle.getString("saveButton"));
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

    public void showEditPrizeScreen(Stage stage, Prize prize) {
        createComboBox(prize);

        resetForm();

        layout = new VBox(10);

        prizeTextFieldManager = LanguageManager.getTextFieldPrizeManager();

        createNameField(prize);
        createBadgeCountField(prize);

        layout.getChildren().addAll(
                createTopLayout(stage),
                nameTextField,
                badgeCountTextField,
                programsComboBox,
                createButtonsSection(stage)
        );

        saveButton.setOnAction(event -> {
            String name = nameTextField.getText();
            String count = badgeCountTextField.getText();
            String program = programsComboBox.getValue();

            if (validateInput(name, count, program)) {

                updateExistingPrize(stage, prize, name, count, program);
            }
        });

        prizeController.createScene(layout, stage);
    }

    private void updateExistingPrize(Stage stage, Prize prize, String name, String count, String program) {
        prize.setName(name);
        prize.setPoints(Integer.parseInt(count));
        prize.setProgram(program);

        prizeController.savePrize(stage, prize);
    }

    private boolean validateInput(String name, String count, String program) {
        if (name == null || name.trim().isEmpty()) {
            Notification.showAlert("Empty field", "The name field must not be empty", "Please enter the name");
            return false;
        } else if (count == null) {
            Notification.showAlert("Empty field", "The count field must not be empty", "Please enter the count.");
            return false;
        } else if (program == null) {
            Notification.showAlert("Empty field", "The program field must not be empty", "Please enter the program.");
            return false;
        }
        return true;
    }
}