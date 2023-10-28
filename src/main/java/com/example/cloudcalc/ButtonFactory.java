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

    private static final String DELETE_ICON = "/images/delete-48.png";
    private static final String PRIZE_ICON = "/images/gift-64.png";
    private static final String ADD_ICON = "/images/plus-64.png";
    private static final String BACK_ICON = "/images/go-back-64.png";
    private static final String IGNORE_ICON = "/images/no-image-40.png";
    private static final String STATS_ICON = "/images/icons8-euro-money-64.png";
    private static final String UPDATE_ICON = "/images/update-64.png";

    public static Button createSavePrizeButton(Consumer<ActionEvent> action) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> action.accept(e));
        return saveButton;
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

    public static Button createSaveButton(Consumer<ActionEvent> action) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> action.accept(e));
        return saveButton;
    }

    public static Button createDeleteButton(EventHandler<ActionEvent> action) {
        Image deleteImage = new Image(ButtonFactory.class.getResourceAsStream(DELETE_ICON));
        return createButton("", action, deleteImage);
    }

    public static Button createBackButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(BACK_ICON));
        return createButton("", action, image);
    }

    public static Button createPrizeButton(EventHandler<ActionEvent> action) {
        Image prizeImage = new Image(ButtonFactory.class.getResourceAsStream(PRIZE_ICON));
        return createButton("", action, prizeImage);
    }

    public static Button createAddButton(EventHandler<ActionEvent> action) {
        Image addImage = new Image(ButtonFactory.class.getResourceAsStream(ADD_ICON));
        return createButton("", action, addImage);
    }

    public static Button createStatsButton(EventHandler<ActionEvent> action) {
        Image addImage = new Image(ButtonFactory.class.getResourceAsStream(STATS_ICON));
        return createButton("", action, addImage);
    }

    public static Button createIgnoreButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(IGNORE_ICON));
        return createButton("", action, image);
    }

    public static Button createUpdateButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(UPDATE_ICON));
        return createButton("", action, image);
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
