module com.example.cloudcalc {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires org.apache.poi.ooxml;
    requires org.jsoup;


    opens com.example.cloudcalc to javafx.fxml;
    exports com.example.cloudcalc;
    exports com.example.cloudcalc.badge.type;
    opens com.example.cloudcalc.badge.type to javafx.fxml;
    exports com.example.cloudcalc.badge;
    opens com.example.cloudcalc.badge to javafx.fxml;
    exports com.example.cloudcalc.button;
    opens com.example.cloudcalc.button to javafx.fxml;
    exports com.example.cloudcalc.builder;
    opens com.example.cloudcalc.builder to javafx.fxml;
    exports com.example.cloudcalc.constant;
    opens com.example.cloudcalc.constant to javafx.fxml;
    exports com.example.cloudcalc.controller;
    opens com.example.cloudcalc.controller to javafx.fxml;
    exports com.example.cloudcalc.view;
    opens com.example.cloudcalc.view to javafx.fxml;
    exports com.example.cloudcalc.model;
    opens com.example.cloudcalc.model to javafx.fxml;
    exports com.example.cloudcalc.entity;
    opens com.example.cloudcalc.entity to javafx.fxml;
    exports com.example.cloudcalc.view.arcade;
    opens com.example.cloudcalc.view.arcade to javafx.fxml;
    exports com.example.cloudcalc.view.prize;
    opens com.example.cloudcalc.view.prize to javafx.fxml;
}