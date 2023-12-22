package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.ProgramController;

import com.example.cloudcalc.entity.Program;
import com.example.cloudcalc.util.Notification;
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
                List<CountConditionModel> conditions = new ArrayList<>();
                for (int j = 0; j < conditionsArray.length(); j++) {
                    JSONObject conditionObject = conditionsArray.getJSONObject(j);
                    CountConditionModel condition = new CountConditionModel();

                    condition.setType(conditionObject.getString("type"));
                    condition.setValue(conditionObject.getString("value"));

                    conditions.add(condition);
                }
                program.setConditions(conditions);
                programs.add(program);
            } else {
                Notification.showErrorMessage("Error keys", "Wrong keys has detected while loading programs.");
            }
        }

        return programs;
    }

    public void saveProgram(Program program) {
        List<Program> existingPrograms = loadProgramsFromFile();
        existingPrograms.add(program);
        JSONArray jsonArray = convertProgramsToJSONArray(existingPrograms);
        FileManager.writeJsonToFile(jsonArray, FileName.PROGRAMS_FILE);
    }

    private JSONArray convertProgramsToJSONArray(List<Program> programs) {
        JSONArray jsonArray = new JSONArray();
        for (Program program : programs) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("name", program.getName());
            jsonObject.put("date", program.getDate());
            jsonObject.put("conditions", program.getConditions());

            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void deleteProgram(Program program) {
        List<Program> programs = loadProgramsFromFile();
        programs.remove(program);
        saveProgramsToFile(programs);
    }

    private void saveProgramsToFile(List<Program> programs) {
        JSONArray jsonArray = convertProgramsToJSONArray(programs);
        FileManager.writeJsonToFile(jsonArray, FileName.PROGRAMS_FILE);
    }
}