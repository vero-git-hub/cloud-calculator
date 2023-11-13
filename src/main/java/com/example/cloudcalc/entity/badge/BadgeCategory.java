package com.example.cloudcalc.entity.badge;

public class BadgeCategory {
    private final String category;
    private final String value;

    public BadgeCategory(String category, String value) {
        this.category = category;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }
}
