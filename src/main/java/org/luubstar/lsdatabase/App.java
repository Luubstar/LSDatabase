package org.luubstar.lsdatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.luubstar.lsdatabase.Utils.Database.Database;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
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
        catch (Exception e){System.out.println(e.getMessage()); System.exit(1);}
    }
    public static void main(String[] args) {
        launch();
    }
}