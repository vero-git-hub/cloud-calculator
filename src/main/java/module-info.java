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
    exports com.example.cloudcalc.profile;
    opens com.example.cloudcalc.profile to javafx.fxml;
    exports com.example.cloudcalc.badge;
    opens com.example.cloudcalc.badge to javafx.fxml;
    exports com.example.cloudcalc.prize;
    opens com.example.cloudcalc.prize to javafx.fxml;
}