package com.example.myarena;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Load the CSS immediately at startup
        String cssPath = getClass().getResource("/com/example/myarena/application.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        stage.setTitle("MyArena - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}