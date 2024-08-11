package com.example.cloudcalc.button;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ButtonFactory {

    private static final String DELETE_ICON = "/images/crud/delete-48.png";
    private static final String PRIZE_ICON = "/images/main/gift-64.png";
    private static final String ADD_ICON = "/images/plus-64.png";
    private static final String BACK_ICON = "/images/go-back-64.png";
    private static final String STATS_ICON = "/images/main/combo-chart-48.png";
    private static final String SCAN_ICON = "/images/calculator-64.png";
    private static final String SAVE_ICON = "/images/crud/save-64.png";
    private static final String BOOK_ICON = "/images/main/book-48.png";
    private static final String EDIT_ICON = "/images/crud/edit-48.png";
    private static final String PROFILE_ICON = "/images/main/test-account-60.png";
    private static final String DAILY_STAT_ICON = "/images/main/calendar-64.png";

    public static Button createSaveButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(SAVE_ICON));
        return createButton("", action, image, "");
    }

    public static Button createDeleteButton(EventHandler<ActionEvent> action) {
        Image deleteImage = new Image(ButtonFactory.class.getResourceAsStream(DELETE_ICON));
        return createButton("", action, deleteImage, "");
    }

    public static Button createEditButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(EDIT_ICON));
        return createButton("", action, image, "");
    }

    public static Button createBackButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(BACK_ICON));
        return createButton("", action, image, "");
    }

    public static Button createPrizeButton(EventHandler<ActionEvent> action) {
        Image prizeImage = new Image(ButtonFactory.class.getResourceAsStream(PRIZE_ICON));
        return createButton("", action, prizeImage, "");
    }

    public static Button createAddButton(EventHandler<ActionEvent> action) {
        Image addImage = new Image(ButtonFactory.class.getResourceAsStream(ADD_ICON));
        return createButton("", action, addImage, "");
    }

    public static Button createStatsButton(EventHandler<ActionEvent> action) {
        Image addImage = new Image(ButtonFactory.class.getResourceAsStream(STATS_ICON));
        return createButton("", action, addImage, "");
    }

    public static Button createScanButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(SCAN_ICON));
        return createButton("", action, image, "");
    }

    public static Button createProgramButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(BOOK_ICON));
        return createButton("", action, image, "");
    } 
    
    public static Button createDailyStatButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(DAILY_STAT_ICON));
        return createButton("", action, image, "");
    }

    public static Button createProfilesButton(EventHandler<ActionEvent> action) {
        Image image = new Image(ButtonFactory.class.getResourceAsStream(PROFILE_ICON));
        return createButton("PROFILIES", action, image, "right");
    }

    public static Button createButton(String text, EventHandler<ActionEvent> action, Image iconImage, String iconPosition) {
        Button button = new Button();
        button.setOnAction(action);

        HBox hbox = new HBox();
        if (!text.isEmpty()) {
            hbox.setSpacing(10); // Distance between text and icon
        }
        hbox.setAlignment(Pos.CENTER); // Centering content vertically

        ImageView imageView = null;
        if (iconImage != null) {
            imageView = new ImageView(iconImage);
            int iconSize = 27;
            imageView.setFitWidth(iconSize);
            imageView.setFitHeight(iconSize);
        }

        if (text.isEmpty()) {
            // If the text is empty, add only the icon
            if (imageView != null) {
                hbox.getChildren().add(imageView);
            }
        } else {
            Text buttonText = new Text(text);
            switch (iconPosition.toLowerCase()) {
                case "left":
                    if (imageView != null) {
                        hbox.getChildren().addAll(imageView, buttonText);
                    } else {
                        hbox.getChildren().add(buttonText);
                    }
                    break;
                case "right":
                    hbox.getChildren().add(buttonText);
                    if (imageView != null) {
                        hbox.getChildren().add(imageView);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid icon position: " + iconPosition);
            }
        }

        button.setGraphic(hbox);
        return button;
    }
}