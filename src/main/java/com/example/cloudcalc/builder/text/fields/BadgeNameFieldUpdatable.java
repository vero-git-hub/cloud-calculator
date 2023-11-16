package com.example.cloudcalc.builder.text.fields;

import javafx.scene.control.TextField;

public interface BadgeNameFieldUpdatable {
    void updateNameFieldPlaceholder(String placeholder);
    TextField getNameField();
}
