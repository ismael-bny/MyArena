module com.example.myarena {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;  // Pour JDBC/PostgreSQL

    opens com.example.myarena to javafx.fxml;
    exports com.example.myarena;

    opens com.example.myarena.ui to javafx.fxml;  // Pour LoginFrame
    exports com.example.myarena.ui;

    exports com.example.myarena.domain;  // Pour User, UserRole, UserStatus
}