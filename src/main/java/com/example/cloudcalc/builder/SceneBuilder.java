package com.example.cloudcalc.builder;

import com.example.cloudcalc.controller.IScreenController;
import com.example.cloudcalc.controller.MainController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneBuilder {

//    private static SceneBuilder instance;
//
//    public static synchronized SceneBuilder getInstance() {
//        if (instance == null) {
//            instance = new SceneBuilder();
//        }
//        return instance;
//    }

    public void buildScreen(Stage primaryStage, String title, ElementsBuilder elementsBuilder, TableBuilder tableBuilder, IScreenController screenController, MainController mainController) {
        VBox layout = new VBox(10);
        HBox topLayout = elementsBuilder.createTopLayout(primaryStage, title, mainController, screenController);

        TableView<String> table = tableBuilder.createBadgeTable(primaryStage, screenController);

        layout.getChildren().addAll(topLayout, table);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        VBox.setVgrow(table, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        createScene(scrollPane, primaryStage);
    }



//    public void buildAnArcadeAddScreen(Stage primaryStage, Button backButton) {
//        VBox layout = new VBox(10);
//
////        Button backButton = ButtonFactory.createBackButton(e -> screenDisplayable.showScreen(primaryStage));
//        titleAddScreenLabel = uiCallbacks.createLabel(addScreenLabel);
//
//        Button saveButton = ButtonFactory.createSaveButton(event -> {
//            List<String> ignoredBadges = fileOperationManager.loadBadgesFromFile(fileName);
//            TextField nameField = nameTextFieldUpdatable.getNameTextField();
//
//            String badgeName = nameField.getText().trim();
//            if (badgeName.isEmpty()) {
//                Notification.showAlert(AlertGuardian.nameAlertTitle, AlertGuardian.nameAlertHeader, AlertGuardian.nameAlertContent);
//                return;
//            }
//
//            ignoredBadges.add(badgeName);
//            fileOperationManager.saveBadgesToFile(ignoredBadges, fileName);
//            nameField.setText("");
//
//            screenDisplayable.showScreen(primaryStage);
//        });
//
//        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleAddScreenLabel);
//
//        layout.getChildren().addAll(
//                topLayout,
//                nameTextFieldUpdatable.getNameTextField(),
//                saveButton
//        );
//
//        uiCallbacks.createScene(layout, primaryStage);
//    }

    public  void createScene(Parent layout, Stage primaryStage) {
        layout.setStyle("-fx-font-size: 18;-fx-padding: 10px;");

        int WIDTH_SCENE = 820;
        int HEIGHT_SCENE = 620;
        Scene mainScene = new Scene(layout, WIDTH_SCENE, HEIGHT_SCENE);
        mainScene.getStylesheets().add(TableBuilder.class.getResource("/styles.css").toExternalForm());

        primaryStage.setScene(mainScene);
    }

}
