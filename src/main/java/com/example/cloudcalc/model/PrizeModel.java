package com.example.cloudcalc.model;

import com.example.cloudcalc.FileManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.PrizeController;
import com.example.cloudcalc.entity.Prize;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
            prize.setName(prizeObject.getString("name"));
            prize.setType(prizeObject.getString("type"));
            prize.setCount(prizeObject.getInt("count"));
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
            jsonObject.put("name", prize.getName());
            jsonObject.put("type", prize.getType());
            jsonObject.put("count", prize.getCount());
            jsonObject.put("program", prize.getProgram());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void savePrize(String namePrize, String badgeCount, String badgeType) {
            Prize newPrize = new Prize();
            newPrize.setName(namePrize);
            newPrize.setType(badgeType);

            try {
                newPrize.setCount(Integer.parseInt(badgeCount));
            } catch (NumberFormatException e) {
                return;
            }

            List<Prize> existingPrizes = loadPrizesFromFile();
            existingPrizes.add(newPrize);

            JSONArray jsonArray = convertPrizesToJSONArray(existingPrizes);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FileName.PRIZES_FILE), StandardCharsets.UTF_8)) {
                writer.write(jsonArray.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
