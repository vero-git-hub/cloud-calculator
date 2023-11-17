package com.example.cloudcalc.builder.fields.badge;

import javafx.scene.control.TextField;

public interface BadgeNameFieldUpdatable {
    void updateNameFieldPlaceholder(String placeholder);
    TextField getNameField();
}
