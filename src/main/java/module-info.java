module org.lab_3v1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens org.lab_3v1 to javafx.fxml;
    exports org.lab_3v1;
    exports org.lab_3v1.controllers;
    opens org.lab_3v1.controllers to javafx.fxml;
}