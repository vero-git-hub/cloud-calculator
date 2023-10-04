package com.example.cloudcalc;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PdfLinkItem {
    private final SimpleIntegerProperty index;
    private final SimpleStringProperty link;

    public PdfLinkItem(int index, String link) {
        this.index = new SimpleIntegerProperty(index);
        this.link = new SimpleStringProperty(link);
    }

    public int getIndex() {
        return index.get();
    }

    public String getLink() {
        return link.get();
    }
}
