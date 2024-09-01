package org.luubstar.lsdatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final String CACHEFILE = "last.txt";
    public static Stage st;
    static String[] appargs;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            logger.info("");
            logger.info("INICIANDO APLICACIÓN\n");
            st = stage;
            preloads();

            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light.css")).toExternalForm());
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
            FileInputStream f = new FileInputStream(CACHEFILE);
            String r = new String(f.readAllBytes());
            f.close();

            File last = new File(r);
            if(appargs.length > 0 && !appargs[0].trim().isEmpty()){
                logger.debug(Arrays.toString(appargs), appargs.length, appargs[0]);
                StringBuilder s = new StringBuilder();
                for(String arg : appargs){
                    s.append(arg).append(" ");
                }
                logger.debug("Abriendo {}", s);
                ChangePanel.getNavigator().open(s.toString());
            }
            else if(!last.exists()) {
                logger.debug("Copiando default");
                Database.loadFile(Database.DEFAULT);
                writeLast(Database.DEFAULT);
            }
            else{
                logger.debug("Leyendo el último fichero");
                Database.loadFile(readLast());
            }
            Database.start();
        }
        catch (Exception e){logger.error("Error fatal en la inicialización de la base de datos ", e); System.exit(1);}
    }

    public static void main(String[] args) {
        logger.debug("Archivos pasados en main {}", args.length);
        appargs = args;
        launch();
    }

    private void handleWindowClose(WindowEvent event) {
        if (Database.unsaved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Datos no guardados");
            alert.setHeaderText("Tienes datos sin guardar");
            alert.setContentText("¿Quieres guardar y salir?");

            // Configurar botones personalizados
            ButtonType saveAndExitButton = new ButtonType("Guardar y salir",ButtonBar.ButtonData.LEFT);
            ButtonType exitWithoutSavingButton = new ButtonType("Salir sin guardar");
            ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(saveAndExitButton, exitWithoutSavingButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == saveAndExitButton) {
                    Database.unsaved = false;
                    Database.file.save();
                    logger.info("Salido guardando");
                    System.exit(0);
                } else if (result.get() == exitWithoutSavingButton) {
                    logger.info("Salido sin guardar");
                    System.exit(0);
                } else {
                    event.consume();
                }
            }

        }
    }

    public static void writeLast(String route) throws IOException {
        FileOutputStream f = new FileOutputStream(CACHEFILE);
        f.write(route.getBytes());
        f.close();
    }

    private static String readLast() throws IOException {
        FileInputStream f = new FileInputStream(CACHEFILE);
        String res = new String(f.readAllBytes());
        f.close();
        return res;
    }

}