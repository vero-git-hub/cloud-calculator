package com.example.cloudcalc;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ButtonFactory {

    private static final String DELETE_ICON = "/delete_icon48.png";

    public static Button createSavePrizeButton(Consumer<ActionEvent> action) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> action.accept(e));
        return saveButton;
    }

    public static Button createScanButton(Consumer<ActionEvent> action) {
        Button scanButton = new Button("Scan");
        scanButton.setOnAction(e -> action.accept(e));
        return scanButton;
    }

    public static Button createSaveIgnoreBadgeButton(Runnable action, Supplier<TextField> nameFieldSupplier) {
        Button saveButton = new Button("Save Ignore Badge");
        saveButton.setOnAction(e -> {
            TextField nameField = nameFieldSupplier.get();
            action.run();
        });
        return saveButton;
    }

    public static Button createUploadPdfButton(Consumer<ActionEvent> action) {
        Button uploadPdfButton = new Button("Upload PDF");
        uploadPdfButton.setOnAction(e -> action.accept(e));
        return uploadPdfButton;
    }

    public static Button createSaveProfileButton(Consumer<ActionEvent> action) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> action.accept(e));
        return saveButton;
    }

    public static Button createDeleteButton(EventHandler<ActionEvent> action) {
        Image deleteImage = new Image(ButtonFactory.class.getResourceAsStream(DELETE_ICON));
        return createButton("", action, deleteImage);
    }

    public static Button createBackButton(EventHandler<ActionEvent> backAction) {
        return createButton("Back", backAction, null);
    }

    public static Button createButton(String text, EventHandler<ActionEvent> action, Image iconImage) {
        Button button = new Button(text);
        button.setOnAction(action);

        if (iconImage != null) {
            ImageView imageView = new ImageView(iconImage);
            int iconSize = 27;
            imageView.setFitWidth(iconSize);
            imageView.setFitHeight(iconSize);
            button.setGraphic(imageView);
        }

        return button;
    }

}
