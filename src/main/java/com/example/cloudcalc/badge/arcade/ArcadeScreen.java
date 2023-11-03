package com.example.cloudcalc.badge.arcade;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.table.NameTextFieldUpdatable;
import com.example.cloudcalc.table.TableBuilder;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ArcadeScreen implements ScreenDisplayable, Localizable, NameTextFieldUpdatable {

    private final ArcadeManager arcadeManager;
    private String title = "ARCADE";
    private String addArcadeTitle = "ADD ARCADE BADGE";

    private TextField nameTextField = new TextField();

    public ArcadeScreen(ArcadeManager arcadeManager) {
        this.arcadeManager = arcadeManager;

        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage primaryStage) {
        TableBuilder.initVariables(Constants.ARCADE_FILE, arcadeManager.getUiCallbacks(), arcadeManager.getFileOperationManager(), this, addArcadeTitle, this);
        TableBuilder.buildScreen(primaryStage, title);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("arcadeTitle");
        addArcadeTitle = bundle.getString("addArcadeTitle");

        this.updateElements(title, addArcadeTitle, bundle);

        updateNameTextFieldPlaceholder(bundle.getString("addScreenNameField"));
    }

    @Override
    public void updateNameTextFieldPlaceholder(String placeholder) {
        if (nameTextField != null) {
            nameTextField.setPromptText(placeholder);
        }
    }

    @Override
    public TextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new TextField();
            nameTextField.setPromptText("Lab name");
            nameTextField.setId("nameField");
        }
        return nameTextField;
    }
}
