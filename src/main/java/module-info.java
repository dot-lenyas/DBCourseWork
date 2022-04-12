module com.example.database {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.database to javafx.fxml;
    exports com.example.database;
    exports com.example.database.Controllers;
    opens com.example.database.Controllers to javafx.fxml;
}