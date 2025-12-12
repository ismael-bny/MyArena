package com.example.myarena;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Le chemin correct vers login-page.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/login-page.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);

        stage.setTitle("MyArena - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}