package com.example.cloudcalc.builder;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.controller.IScreenController;
import com.example.cloudcalc.controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

public class ElementsBuilder {

    public HBox createTopLayoutWithBackAndText(Button backButton, TextFlow textFlow) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().add(backButton);
        topLayout.getChildren().add(leftSpacer);
        topLayout.getChildren().add(textFlow);
        topLayout.getChildren().add(rightSpacer);

        return topLayout;
    }

    public Label createLabel(String text) {
        return new Label(text);
    }

    public HBox createExtendedTopLayout(List<Button> leftButtons, Label title, Node... rightNodes) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().addAll(leftButtons);
        topLayout.getChildren().add(leftSpacer);
        topLayout.getChildren().add(title);
        topLayout.getChildren().add(rightSpacer);
        topLayout.getChildren().addAll(rightNodes);
        topLayout.setMinWidth(560);
        return topLayout;
    }

    public HBox createTopLayout(Button leftButton, Label title, Button... rightButtons) {
        HBox topLayout = new HBox(10);
        topLayout.setAlignment(Pos.CENTER);

        Pane leftSpacer = new Pane();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Pane rightSpacer = new Pane();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        topLayout.getChildren().add(leftButton);
        topLayout.getChildren().add(leftSpacer);
        if (title == null) {
            System.out.println("Title null");
        }
        topLayout.getChildren().add(title);
        topLayout.getChildren().add(rightSpacer);
        topLayout.getChildren().addAll(rightButtons);
        topLayout.setMinWidth(560);
        return topLayout;
    }

    public HBox createTopLayout(Stage primaryStage, String title, MainController mainController, IScreenController screenController) {
        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
        Label titleLabel = new Label(title);
        Button addButton = ButtonFactory.createAddButton(e -> screenController.showAddScreen(primaryStage));

        return createTopLayout(backButton, titleLabel, addButton);
    }

    public Label createSubtitleLabel(Label subtitleLabel) {
        subtitleLabel.setStyle("-fx-font-style: italic;");
        return subtitleLabel;
    }
}
