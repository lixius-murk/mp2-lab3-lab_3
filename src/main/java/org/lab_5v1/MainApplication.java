package org.lab_5v1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainFrame.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1010, 700);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
