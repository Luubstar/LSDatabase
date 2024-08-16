package org.luubstar.lsdatabase.Utils;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;


public class Dropzone {

    private static final Logger logger = LoggerFactory.getLogger(Dropzone.class);

    public static void configureZone(Node n, DragEvent event){
        if (event.getGestureSource() != n && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
        }
        event.consume();
    }

    public static void setOnEntered(Node n, DragEvent event){
        if (event.getGestureSource() != n && event.getDragboard().hasFiles()) {
            n.getStyleClass().add("dropping");
            n.setCursor(Cursor.HAND);
        }
        event.consume();
    }

    public static void setOnExit(Node n, DragEvent event){
        if (event.getGestureSource() != n && event.getDragboard().hasFiles()) {
            n.getStyleClass().remove("dropping");
            n.setCursor(Cursor.DEFAULT);
        }
        event.consume();
    }

    public static void fileDropped(VBox l, DragEvent event, String index){
        try {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                List<File> files = dragboard.getFiles();

                File data = Database.file.getData();
                File carpeta = new File(data.getAbsolutePath() + "/" + index);

                if (!carpeta.exists()){carpeta.mkdir();}

                for (File f : files){
                    Files.copy(f.toPath(), new File(carpeta, f.getName()).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                generateText(l, index);
                Popup.notify("Ficheros añadidos exitosamente");
            }
            event.setDropCompleted(true);
            event.consume();
        }
        catch (IOException e){
            logger.error("Error en el copiado de los ficheros ", e);
        }
    }

    public static void generateText(VBox l, String index){
        l.getChildren().clear();

        File data = Database.file.getData();
        File carpeta = new File(data.getAbsolutePath() + "/" + index);

        File[] files = carpeta.listFiles();

        if(files != null){
            for(File f: files){
                addFileEntry(l, f);
            }
        }
    }

    private static void addFileEntry(VBox root, File file) {
        HBox fileEntry = new HBox(10);
        Hyperlink fileLink = new Hyperlink(file.getName());
        Button removeButton = new Button("X");

        fileLink.setOnAction(event -> openFile(file));
        removeButton.setOnMouseReleased((event -> {
            if(Popup.askForConfirmation("¿Desea eliminar el fichero?")) {
                file.delete();
                root.getChildren().remove(fileEntry);
            }}));

        fileEntry.getChildren().addAll(fileLink, removeButton);
        root.getChildren().add(fileEntry);
    }

    private static void openFile(File f){
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(f);
            } catch (IOException e) {
                logger.error("Error abriendo fichero ", e);
                Popup.notify("Error abriendo el fichero");
            }
        } else {
            logger.error("No se puede abrir ficheros en este sistema");
            Popup.notify("No se puede abrir ficheros en este sistema");
        }
    }
}
