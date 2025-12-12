module com.example.myarena {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.myarena to javafx.fxml;
    exports com.example.myarena;
}