package org.luubstar.lsdatabase.Utils;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    public static void fileDropped(DragEvent event){
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            logger.info("El fichero ha sido soltado");
        }
        event.setDropCompleted(true);
        event.consume();
    }

}
