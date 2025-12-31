module com.example.myarena {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;  // Pour JDBC/PostgreSQL

    opens com.example.myarena to javafx.fxml;
    exports com.example.myarena;

    opens com.example.myarena.ui to javafx.fxml;  // Pour LoginFrame
    exports com.example.myarena.ui;

    exports com.example.myarena.domain;  // Pour User, UserRole, UserStatus

    exports com.example.myarena.facade;
    exports com.example.myarena.services;
    exports com.example.myarena.persistance.dao;
    exports com.example.myarena.persistance.factory;
}