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
}