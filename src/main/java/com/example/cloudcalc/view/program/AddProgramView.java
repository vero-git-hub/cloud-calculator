package com.example.cloudcalc.view.program;

import com.example.cloudcalc.controller.ProgramController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddProgramView implements Localizable {
    private final ProgramController programController;

    private String title = "ADD PROGRAM";

    public AddProgramView(ProgramController programController) {
        this.programController = programController;
        LanguageManager.registerLocalizable(this);
    }

    public void showScreen(Stage stage) {
        System.out.println("add program");
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        title = bundle.getString("addProgramTitle");
    }
}