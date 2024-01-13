package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.ProgramController;

import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.util.FunctionUtils;
import com.example.cloudcalc.util.Notification;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

public class ProgramModel {
    private final ProgramController programController;

    public ProgramModel(ProgramController programController) {
        this.programController = programController;
    }

    public List<Program> loadProgramsFromFile() {
        List<Program> programs = new ArrayList<>();
        JSONArray jsonArray = FileManager.readJsonArrayFromFile(FileName.PROGRAMS_FILE);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject programObject = jsonArray.getJSONObject(i);
            Program program = new Program();

            if (programObject.has("id")) {
                program.setId(programObject.getInt("id"));
            }

            if (programObject.has("name") && programObject.has("date") && programObject.has("condition")) {
                program.setName(programObject.getString("name"));

                String dateString = programObject.getString("date");
                try {
                    LocalDate date = LocalDate.parse(dateString);
                    program.setDate(date);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                    Notification.showErrorMessage("Date error", "Check that the date is correct.");
                }

                JSONObject conditionObject = programObject.getJSONObject("condition");
                CountConditionModel condition = new CountConditionModel();

                condition.setType(conditionObject.getString("type"));

                if (conditionObject.has("values")) {
                    JSONArray valuesArray = conditionObject.getJSONArray("values");
                    List<String> values = new ArrayList<>();
                    for (int k = 0; k < valuesArray.length(); k++) {
                        values.add(valuesArray.getString(k));
                    }
                    condition.setValues(values);
                }

                program.setCondition(condition);

                programs.add(program);
            } else {
                Notification.showErrorMessage("Error keys", "Wrong keys has detected while loading programs.");
            }
        }

        return programs;
    }

    public void saveProgram(Program program) {
        List<Program> existingPrograms = loadProgramsFromFile();

        if (program.getId() == 0) {
            program.setId(FunctionUtils.getNextId(convertProgramsToJSONArray(existingPrograms)));
            existingPrograms.add(program);
        } else {
            for (int i = 0; i < existingPrograms.size(); i++) {
                if (existingPrograms.get(i).getId() == program.getId()) {
                    existingPrograms.set(i, program);
                    break;
                }
            }
        }
        JSONArray jsonArray = convertProgramsToJSONArray(existingPrograms);
        FileManager.writeJsonToFile(jsonArray, FileName.PROGRAMS_FILE);
    }

    private JSONArray convertProgramsToJSONArray(List<Program> programs) {
        JSONArray jsonArray = new JSONArray();
        for (Program program : programs) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", program.getId());
            jsonObject.put("name", program.getName());
            jsonObject.put("date", program.getDate().toString());

            CountConditionModel condition = program.getCondition();
            if (condition != null) {
                JSONObject conditionObject = new JSONObject();
                conditionObject.put("type", condition.getType());

                JSONArray valuesArray = new JSONArray();
                if (condition.getValues() != null) {
                    for (String value : condition.getValues()) {
                        valuesArray.put(value);
                    }
                }
                conditionObject.put("values", valuesArray);

                jsonObject.put("condition", conditionObject);
            }

            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void deleteProgram(Stage stage, Program programToDelete) {
        boolean isConfirmationAlert = programController.showConfirmationAlert();

        if (isConfirmationAlert) {
            List<Profile> profiles = programController.loadProfilesFromFile();

            boolean isProgramAssigned = profiles.stream()
                    .flatMap(profile -> profile.getProgramPrizes().stream())
                    .anyMatch(programPrize -> programPrize.getProgram().equals(programToDelete.getName()));

            if (isProgramAssigned) {
                Notification.showAlert("Cannot be deleted", "The program is assigned to the profile", "Please cancel the assignment before deleting.");
            } else {
                List<Program> programs = loadProgramsFromFile();
                programs.removeIf(p -> p.getId() == programToDelete.getId());

                saveProgramsToFile(programs);
                programController.showScreen(stage);
            }
        }
    }

    private void saveProgramsToFile(List<Program> programs) {
        JSONArray jsonArray = convertProgramsToJSONArray(programs);
        FileManager.writeJsonToFile(jsonArray, FileName.PROGRAMS_FILE);
    }
}