module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.core;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}