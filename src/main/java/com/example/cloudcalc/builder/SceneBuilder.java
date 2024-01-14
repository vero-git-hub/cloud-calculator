package com.example.cloudcalc.builder;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneBuilder {

    public  void createScene(Parent layout, Stage stage) {
        layout.setStyle("-fx-font-size: 18;-fx-padding: 10px;");

        int WIDTH_SCENE = 820;
        int HEIGHT_SCENE = 620;
        Scene mainScene = new Scene(layout, WIDTH_SCENE, HEIGHT_SCENE);
        mainScene.getStylesheets().add(TableBuilder.class.getResource("/styles.css").toExternalForm());

        stage.setScene(mainScene);
    }
}