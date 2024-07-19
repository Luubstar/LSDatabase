package org.luubstar.lsdatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        preloads();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("demo.css")).toExternalForm());
        stage.setTitle("Database");

        stage.setMinWidth(1050);
        stage.setMinHeight(700);

        stage.setScene(scene);
        stage.show();
    }

    private static void preloads(){
        try{
            Database.loadFile("base.db");
            Database.start();
        }
        catch (Exception e){logger.error("Error fatal en la inicialización de la base de datos ", e); System.exit(1);}
    }
    public static void main(String[] args) {
        launch();
    }
}