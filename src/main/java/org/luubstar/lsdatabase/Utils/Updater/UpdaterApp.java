package org.luubstar.lsdatabase.Utils.Updater;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.luubstar.lsdatabase.App;

import java.io.IOException;

public class UpdaterApp extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Update.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Updating...");

        stage.setScene(scene);
        stage.show();
    }
}
