module org.lab_5v1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires org.slf4j;
    requires jakarta.persistence;

    opens org.lab_5v1 to javafx.fxml, org.hibernate.orm.core;
    opens org.lab_5v1.cpu_lib to org.hibernate.orm.core;
    opens org.lab_5v1.cpu_lib.instructions to org.hibernate.orm.core;
    opens org.lab_5v1.test_db to org.hibernate.orm.core;

    exports org.lab_5v1;
    exports org.lab_5v1.controllers;
    opens org.lab_5v1.controllers to javafx.fxml;
}