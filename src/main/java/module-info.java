module org.luubstar.lsdatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens org.luubstar.lsdatabase to javafx.fxml;
    exports org.luubstar.lsdatabase;
    exports org.luubstar.lsdatabase.Utils.Database;
}