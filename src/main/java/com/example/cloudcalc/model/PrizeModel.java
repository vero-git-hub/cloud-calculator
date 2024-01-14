package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.prize.Prize;
import com.example.cloudcalc.util.FunctionUtils;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrizeModel {

    private final PrizeController prizeController;

    public PrizeModel(PrizeController prizeController) {
        this.prizeController = prizeController;
    }

    public List<Prize> loadPrizesFromFile() {
        List<Prize> prizes = new ArrayList<>();
        JSONArray jsonArray = FileManager.readJsonArrayFromFile(FileName.PRIZES_FILE);

        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject prizeObject = jsonArray.getJSONObject(j);
            Prize prize = new Prize();
            prize.setId(prizeObject.getInt("id"));
            prize.setName(prizeObject.getString("name"));
            prize.setPoints(prizeObject.getInt("points"));
            if (prizeObject.has("program")) {
                prize.setProgram(prizeObject.getString("program"));
            }
            prizes.add(prize);
        }

        return prizes;
    }

    public void deleteAction(Stage stage, Prize prize) {
        boolean isConfirmationAlert = prizeController.showConfirmationAlert();

        if (isConfirmationAlert) {
            deletePrize(prize);
            prizeController.showScreen(stage);
        }
    }

    private void deletePrize(Prize prize) {
        List<Prize> prizes = loadPrizesFromFile();
        prizes.remove(prize);
        savePrizesToFile(prizes);
    }

    private void savePrizesToFile(List<Prize> prizes) {
        JSONArray jsonArray = convertPrizesToJSONArray(prizes);
        FileManager.writeJsonToFile(jsonArray, FileName.PRIZES_FILE);
    }

    private JSONArray convertPrizesToJSONArray(List<Prize> prizes) {
        JSONArray jsonArray = new JSONArray();
        for (Prize prize : prizes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", prize.getId());
            jsonObject.put("name", prize.getName());
            jsonObject.put("points", prize.getPoints());
            jsonObject.put("program", prize.getProgram());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void savePrize(Prize prize) {
        List<Prize> existingPrizes = loadPrizesFromFile();

        if (prize.getId() == 0) {
            prize.setId(FunctionUtils.getNextId(convertPrizesToJSONArray(existingPrizes)));
            existingPrizes.add(prize);
        } else {
            for (int i = 0; i < existingPrizes.size(); i++) {
                if (existingPrizes.get(i).getId() == prize.getId()) {
                    existingPrizes.set(i, prize);
                    break;
                }
            }
        }

        JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);

        FileManager.writeJsonToFile(jsonArray, FileName.PRIZES_FILE);
    }
}