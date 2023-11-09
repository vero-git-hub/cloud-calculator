package com.example.cloudcalc.constant;

public enum FieldNames {
    NAME("Name"),
    START_DATE("Start Date (e.g., 26.09.2023)"),
    PROFILE_LINK("Profile Link");

    private final String label;

    FieldNames(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
