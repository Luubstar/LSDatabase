package org.luubstar.lsdatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    @Override
    public void start(Stage stage) throws IOException {
        try {
            logger.info("");
            logger.info("INICIANDO APLICACIÓN\n");
            preloads();

            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("demo.css")).toExternalForm());
            stage.setTitle("Database");

            stage.setMinWidth(1050);
            stage.setMinHeight(700);

            stage.setScene(scene);
            stage.setOnCloseRequest(this::handleWindowClose);
            stage.show();
        }
        catch (Exception e){logger.error("Se ha detectado un error en el inicio ", e);}
    }

    private static void preloads(){
        try{
            Database.loadFile(Database.DEFAULT);
            Database.start();
        }
        catch (Exception e){logger.error("Error fatal en la inicialización de la base de datos ", e); System.exit(1);}
    }
    public static void main(String[] args) {
        launch();
    }

    private void handleWindowClose(WindowEvent event) {
        if (Database.unsaved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Datos no guardados");
            alert.setHeaderText("Tienes datos sin guardar");
            alert.setContentText("¿Quieres guardar y salir?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                Database.unsaved = false;
                Database.file.save();
            }
            else{
                event.consume();
            }
        }
    }


}