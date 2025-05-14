module ChatApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.security.jgss;
    requires java.sql;
    requires jBCrypt;
    requires java.desktop;
    requires static lombok;
    requires mysql.connector.j;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    opens view to javafx.graphics;
    exports view;
    opens app to javafx.graphics;
    exports app;
    exports controller;
    opens controller to javafx.fxml;
}
