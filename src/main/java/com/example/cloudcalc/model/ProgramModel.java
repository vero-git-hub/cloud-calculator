package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.ProgramController;

import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.program.CountCondition;
import com.example.cloudcalc.entity.program.Program;
import com.example.cloudcalc.entity.program.SpecialConditions;
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

            if (programObject.has("name") && programObject.has("date") && programObject.has("conditions")) {
                program.setName(programObject.getString("name"));

                String dateString = programObject.getString("date");
                try {
                    LocalDate date = LocalDate.parse(dateString);
                    program.setDate(date);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                    Notification.showErrorMessage("Date error", "Check that the date is correct.");
                }

                JSONArray conditionsArray = programObject.getJSONArray("conditions");
                List<CountCondition> conditions = new ArrayList<>();

                for (int j = 0; j < conditionsArray.length(); j++) {
                    JSONObject conditionObject = conditionsArray.getJSONObject(j);
                    CountCondition condition = new CountCondition();
                    condition.setType(conditionObject.getString("type"));

                    if (conditionObject.has("badges")) {
                        JSONArray valuesArray = conditionObject.getJSONArray("badges");
                        List<CountCondition.Badges> values = new ArrayList<>();

                        for (int k = 0; k < valuesArray.length(); k++) {
                            JSONObject valueObject = valuesArray.getJSONObject(k);
                            String title = valueObject.getString("title");
                            int points = valueObject.getInt("points");
                            CountCondition.Badges badges = new CountCondition.Badges(title, points);
                            values.add(badges);
                        }

                        condition.setValues(values);
                    }

                    conditions.add(condition);
                }

                program.setConditions(conditions);

                if (programObject.has("specialConditions")) {
                    JSONObject specialConditionsObject = programObject.getJSONObject("specialConditions");
                    SpecialConditions specialConditions = new SpecialConditions();

                    if (specialConditionsObject.has("badgesToIgnore")) {
                        JSONArray badgesToIgnoreArray = specialConditionsObject.getJSONArray("badgesToIgnore");
                        List<String> badgesToIgnore = new ArrayList<>();
                        for (int k = 0; k < badgesToIgnoreArray.length(); k++) {
                            badgesToIgnore.add(badgesToIgnoreArray.getString(k));
                        }
                        specialConditions.setBadgesToIgnore(badgesToIgnore);
                    }

                    if (specialConditionsObject.has("badgesPerPoint")) {
                        specialConditions.setBadgesPerPoint(specialConditionsObject.getInt("badgesPerPoint"));
                    }

                    if (specialConditionsObject.has("pointsPerBadge")) {
                        specialConditions.setPointsPerBadge(specialConditionsObject.getInt("pointsPerBadge"));
                    }

                    program.setSpecialConditions(specialConditions);
                }

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

            JSONArray conditionsArray = new JSONArray();
            for (CountCondition condition : program.getConditions()) {
                JSONObject conditionObject = new JSONObject();
                conditionObject.put("type", condition.getType());

                JSONArray valuesArray = new JSONArray();
                for (CountCondition.Badges value : condition.getValues()) {
                    JSONObject valueObject = new JSONObject();
                    valueObject.put("title", value.getTitle());
                    valueObject.put("points", value.getPoints());
                    valuesArray.put(valueObject);
                }
                conditionObject.put("badges", valuesArray);
                conditionsArray.put(conditionObject);
            }
            jsonObject.put("conditions", conditionsArray);

            SpecialConditions specialConditions = program.getSpecialConditions();
            JSONObject specialConditionsObject = new JSONObject();
            if (specialConditions != null) {
                specialConditionsObject.put("badgesToIgnore", new JSONArray(specialConditions.getBadgesToIgnore()));
                specialConditionsObject.put("badgesPerPoint", specialConditions.getBadgesPerPoint());
                specialConditionsObject.put("pointsPerBadge", specialConditions.getPointsPerBadge());
            } else {
                specialConditionsObject.put("badgesToIgnore", new JSONArray());
                specialConditionsObject.put("badgesPerPoint", 0);
                specialConditionsObject.put("pointsPerBadge", 0);
            }
            jsonObject.put("specialConditions", specialConditionsObject);

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

    public void addSpecialConditions(String programName, List<String> badgesToIgnore, int badgesPerPoint, int pointsPerBadge) {
        List<Program> programs = loadProgramsFromFile();

        for (Program program : programs) {
            if (program.getName().equals(programName)) {
                SpecialConditions specialConditions = program.getSpecialConditions();
                if (specialConditions == null) {
                    specialConditions = new SpecialConditions();
                }
                specialConditions.setBadgesToIgnore(badgesToIgnore);
                specialConditions.setBadgesPerPoint(badgesPerPoint);
                specialConditions.setPointsPerBadge(pointsPerBadge);
                program.setSpecialConditions(specialConditions);
                break;
            }
        }

        saveProgramsToFile(programs);
    }
}