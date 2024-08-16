module org.luubstar.lsdatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.slf4j;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.compiler;
    requires java.desktop;

    opens org.luubstar.lsdatabase to javafx.fxml;
    opens org.luubstar.lsdatabase.Utils.Updater to javafx.fxml;
    exports org.luubstar.lsdatabase;
    exports org.luubstar.lsdatabase.Utils.Database;
    exports org.luubstar.lsdatabase.Utils.Updater;
    exports org.luubstar.lsdatabase.Utils;
}