package com.example.cloudcalc.view;

import com.example.cloudcalc.builder.fields.profile.ProfileFieldUpdatable;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.entity.ProgramPrize;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProfileView implements Localizable, ProfileFieldUpdatable {

    private final ProfileController profileController;
    private final Label titleLabelEditProfileScreen;
    private Label titleLabel;
    TextField nameField;
    TextField dateField;
    TextField linkField;
    private Label programsLabel;

    public ProfileView(ProfileController profileController) {
        this.profileController = profileController;
        this.titleLabel = new Label("CREATE PROFILE");
        this.titleLabelEditProfileScreen = new Label("EDIT PROFILE");
        this.programsLabel = new Label("Select programs");

        LanguageManager.registerLocalizable(this);
    }

    public Label getTitleLabelEditProfileScreen() {
        return titleLabelEditProfileScreen;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public void showCreateProfileScreen(Stage primaryStage) {
        VBox layout = createLayout();
        nameField.clear();
        linkField.clear();

        Profile profile = createEmptyProfile();

        layout.getChildren().addAll(
                profileController.createTopLayout(primaryStage),
                nameField,
                linkField,
                programsLabel
        );

        List<Program> programs = profileController.loadProgramsFromFile();
        List<CheckBox> checkBoxes = createCheckBoxes(programs, layout);

        Button saveButton = createSaveButton(primaryStage, checkBoxes, profile);
        layout.getChildren().add(saveButton);

        profileController.createScene(primaryStage, layout);
    }

    private VBox createLayout() {
        VBox layout = new VBox(10);
        nameField = LanguageManager.getProfileFieldManager().getNameField();
        linkField = LanguageManager.getProfileFieldManager().getLinkField();
        return layout;
    }

    private Profile createEmptyProfile() {
        return new Profile();
    }

    private List<CheckBox> createCheckBoxes(List<Program> programs, VBox layout) {
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Program program : programs) {
            CheckBox checkBox = new CheckBox(program.getName());
            checkBoxes.add(checkBox);
            layout.getChildren().add(checkBox);
        }
        return checkBoxes;
    }

    private Button createSaveButton(Stage primaryStage, List<CheckBox> checkBoxes, Profile profile) {
        return ButtonFactory.createSaveButton(e -> {
            List<String> selectedProgramNames = checkBoxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toList());

            String name = nameField.getText();
            String link = linkField.getText();
            if (name != null && !name.isEmpty() && link != null && !link.isEmpty()) {
                profile.setName(name);
                profile.setLink(link);
            }

            List<ProgramPrize> programPrizes = createProgramPrizes(selectedProgramNames);
            profile.setProgramPrizes(programPrizes);

            profileController.handleProfileSave(primaryStage, profile);
        });
    }

    private List<ProgramPrize> createProgramPrizes(List<String> programNames) {
        return programNames.stream()
                .map(programName -> {
                    ProgramPrize programPrize = new ProgramPrize();
                    programPrize.setProgram(programName);
                    programPrize.setPrizeInfoList(new ArrayList<>());
                    return programPrize;
                })
                .collect(Collectors.toList());
    }

    public void showEditProfileScreen(Stage primaryStage, Profile profile) {
        VBox layout = createLayout();
        setupNameAndLinkFields(profile);

        layout.getChildren().addAll(
                profileController.createTopLayoutEditProfileScreen(primaryStage),
                nameField,
                linkField,
                programsLabel
        );

        List<Program> programs = profileController.loadProgramsFromFile();
        List<CheckBox> checkBoxes = createCheckBoxes(layout, programs, profile);

        Button saveButton = createSaveButton(checkBoxes, profile, primaryStage);
        layout.getChildren().add(saveButton);

        profileController.createScene(primaryStage, layout);
    }

    private void setupNameAndLinkFields(Profile profile) {
        nameField.setText(profile.getName());
        linkField.setText(profile.getLink());
    }

    private boolean programIsSelected(Profile profile, String programName) {
        return profile.getProgramPrizes().stream()
                .anyMatch(programPrize -> programPrize.getProgram().equals(programName));
    }

    private List<CheckBox> createCheckBoxes(VBox layout, List<Program> programs, Profile profile) {
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Program program : programs) {
            CheckBox checkBox = new CheckBox(program.getName());

            if (programIsSelected(profile, program.getName())) {
                checkBox.setSelected(true);
            }

            checkBoxes.add(checkBox);
            layout.getChildren().add(checkBox);
        }
        return checkBoxes;
    }

    private Button createSaveButton(List<CheckBox> checkBoxes, Profile profile, Stage primaryStage) {
        return ButtonFactory.createSaveButton(e -> {
            List<String> selectedProgramNames = checkBoxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toList());

            profile.setName(nameField.getText());
            profile.setLink(linkField.getText());

            List<ProgramPrize> existingProgramPrizes = profile.getProgramPrizes();
            List<ProgramPrize> programPrizes = createProgramPrizes(selectedProgramNames, existingProgramPrizes);
            profile.setProgramPrizes(programPrizes);

            profileController.handleProfileSave(primaryStage, profile);
        });
    }

    private List<ProgramPrize> createProgramPrizes(List<String> selectedProgramNames, List<ProgramPrize> existingProgramPrizes) {
        List<ProgramPrize> newProgramPrizes = new ArrayList<>();
        for (String programName : selectedProgramNames) {
            ProgramPrize existingPrize = existingProgramPrizes.stream()
                    .filter(prize -> prize.getProgram().equals(programName))
                    .findFirst()
                    .orElse(null);

            if (existingPrize != null) {
                newProgramPrizes.add(existingPrize);
            } else {
                newProgramPrizes.add(new ProgramPrize(programName, new ArrayList<>()));
            }
        }
        return newProgramPrizes;
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("createProfileTitle"));
        programsLabel.setText(bundle.getString("programsLabelSelect"));

        updateNameFieldPlaceholder(bundle.getString("createProfileNameField"));
        updateDateFieldPlaceholder(bundle.getString("createProfileDateField"));
        updateLinkFieldPlaceholder(bundle.getString("createProfileLinkField"));
    }

    @Override
    public void updateElements(String newTitle, String newAddScreenTitle, ResourceBundle bundle) {
        Localizable.super.updateElements(newTitle, newAddScreenTitle, bundle);
    }

    @Override
    public void updateNameFieldPlaceholder(String placeholder) {
        if (nameField != null) {
            nameField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateDateFieldPlaceholder(String placeholder) {
        if (dateField != null) {
            dateField.setPromptText(placeholder);
        }
    }

    @Override
    public void updateLinkFieldPlaceholder(String placeholder) {
        if (linkField != null) {
            linkField.setPromptText(placeholder);
        }
    }
}