package com.example.cloudcalc.badge.ignored;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.badge.ScreenDisplayable;
import com.example.cloudcalc.table.NameTextFieldUpdatable;
import com.example.cloudcalc.table.TableBuilder;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class IgnoreScreen implements ScreenDisplayable, Localizable, NameTextFieldUpdatable {

    private final IgnoreManager ignoreManager;

    private String title = "IGNORE";

    private String addIgnoreTitle = "ADD IGNORE BADGE";

    private TextField nameTextField = new TextField();

    public IgnoreScreen(IgnoreManager ignoreManager) {
        this.ignoreManager = ignoreManager;

        LanguageManager.registerLocalizable(this);
    }

    @Override
    public void showScreen(Stage primaryStage) {
        TableBuilder.initVariables(Constants.IGNORE_FILE, ignoreManager.getUiCallbacks(), ignoreManager.getFileOperationManager(), this, addIgnoreTitle, this);
        TableBuilder.buildScreen(primaryStage, title);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("ignoreTitle");
        addIgnoreTitle = bundle.getString("addIgnoreTitle");

        this.updateElements(title, addIgnoreTitle, bundle);

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
