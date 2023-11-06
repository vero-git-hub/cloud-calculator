package com.example.cloudcalc.builder;

import com.example.cloudcalc.button.ButtonFactory;
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

//    public TextFlow createTextFlow(String boldText, String normalText) {
//        Text bold = new Text(boldText);
//        bold.setStyle("-fx-font-weight: bold;");
//
//        Text normal = new Text(normalText);
//
//        return new TextFlow(bold, normal);
//    }
//
//    public TextField createTextField(String promptText) {
//        TextField textField = new TextField();
//        textField.setPromptText(promptText);
//        return textField;
//    }

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
//
//    private static HBox createTopLayout(Stage primaryStage, String title) {
//        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
//        titleLabel = uiCallbacks.createLabel(title);
//        Button addButton = ButtonFactory.createAddButton(e -> buildAddScreen(primaryStage));
//
//        return uiCallbacks.createTopLayout(backButton, titleLabel, addButton);
//    }
//
//    public static VBox createProfileInfoForProfile(Profile profile, UICallbacks uiCallbacks, String startDateText) {
//        VBox profileInfoBox = new VBox(10);
//
//        TextFlow startDateFlow = uiCallbacks.createTextFlow(startDateText + " ", profile.getStartDate());
//        profileInfoBox.getChildren().addAll(startDateFlow);
//
//        return profileInfoBox;
//    }
//
//    public HBox createTopLayoutForStats(Stage primaryStage, Label titleLabel, MainController mainController) {
//        Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
//        return uiCallbacks.createTopLayout(backButton, titleLabel);
//    }

    public Label createSubtitleLabelForStats(Label subtitleLabel) {
        subtitleLabel.setStyle("-fx-font-style: italic;");
        return subtitleLabel;
    }
}
