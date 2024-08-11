package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.view.DailyStatView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author v_code
 **/
public class DailyStatController extends BaseController {

    private ServiceFacade serviceFacade;
    private DailyStatView dailyStatView = new DailyStatView();

    public DailyStatController(ServiceFacade serviceFacade) {
        super(serviceFacade);
    }

    @Override
    public void showScreen(Stage stage) {
        dailyStatView.showScreen(stage);
    }

    @Override
    public void showAddScreen(Stage stage) {

    }

    @Override
    public void createScene(VBox layout, Stage stage) {

    }
}
