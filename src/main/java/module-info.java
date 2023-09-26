module com.example.cloudcalc {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.apache.pdfbox;


    opens com.example.cloudcalc to javafx.fxml;
    exports com.example.cloudcalc;
}