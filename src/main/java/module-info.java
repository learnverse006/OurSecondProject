module ChatApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.security.jgss;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;
    opens view to javafx.graphics;
    exports view;
}
