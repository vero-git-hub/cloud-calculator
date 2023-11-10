package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.view.TypeBadgeView;
import javafx.stage.Stage;

public class TypeBadgeController {

    private TypeBadgeView typeBadgeView = new TypeBadgeView(this);
    public TypeBadgeController(ServiceFacade serviceFacade) {

    }

    public void showScreen(Stage stage) {
        typeBadgeView.showScreen(stage);
    }
}
