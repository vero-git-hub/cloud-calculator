module com.example.cloudcalc {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cloudcalc to javafx.fxml;
    exports com.example.cloudcalc;
}