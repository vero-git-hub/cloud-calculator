package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.program.Program;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.model.ProgramModel;
import com.example.cloudcalc.util.Notification;
import com.example.cloudcalc.view.program.ProgramView;
import com.example.cloudcalc.view.program.SaveProgramView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class ProgramController extends BaseController {
    private ProgramView programView = new ProgramView(this);
    private ProgramModel programModel = new ProgramModel(this);
    private final SaveProgramView saveProgramView = new SaveProgramView(this);
    public static ResourceBundle bundle;
    String alertTitleDeletePrize = "Confirmation Dialog";
    String alertHeaderDeletePrize = "Delete Program";
    String alertContentDeletePrize = "Are you sure you want to delete this program?";
    private ProfileController profileController;

    public ProgramController(ServiceFacade serviceFacade) {
        super(serviceFacade);
        bundle = LanguageManager.getBundle();
        this.profileController = serviceFacade.getProfileController();
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public boolean showConfirmationAlert() {
        return Notification.showConfirmationAlert(
                alertTitleDeletePrize, alertHeaderDeletePrize, alertContentDeletePrize
        );
    }

    @Override
    public void showScreen(Stage stage) {
        programView.showScreen(stage);
    }

    @Override
    public void showAddScreen(Stage stage) {
        saveProgramView.showScreen(stage);
    }

    @Override
    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }

    public HBox createTopLayoutForAddScreen(Button backButton, Label titleAddScreenLabel) {
        return elementsBuilder.createTopLayout(backButton, titleAddScreenLabel);
    }

    public void saveProgram(Stage stage, Program program) {
        programModel.saveProgram(program);
        showScreen(stage);
    }

    public List<Program> loadProgramsFromFile() {
        return programModel.loadProgramsFromFile();
    }

    public List<Profile> loadProfilesFromFile() {
        profileController = serviceFacade.getProfileController();
        return profileController.getProfilesFromFile();
    }

    public void deleteAction(Stage stage, Program program) {
        programModel.deleteProgram(stage, program);
    }

    public void showEditProgramScreen(Stage primaryStage, Program program) {
        saveProgramView.showEditProgramScreen(primaryStage, program);
    }

//    public Label createSubtitleLabel() {
//        return elementsBuilder.createSubtitleLabel(saveProgramView.getSubtitleLabel());
//    }
}