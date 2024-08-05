package org.luubstar.lsdatabase.Utils.Updater;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(Updater.class);

    @FXML
    ProgressBar bar;
    @FXML
    Label value;

    public static DoubleProperty barra = new SimpleDoubleProperty(0.0);
    public static StringProperty text = new SimpleStringProperty(" - / - ");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("INICIANDO UPDATER");
        bar.progressProperty().bind(barra);
        value.textProperty().bind(text);
        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() {
                Updater.update();
                return null;
            }
        };

        Thread t = new Thread(downloadTask);

        t.start();
        downloadTask.setOnSucceeded(event -> {
            logger.debug("ACTUALIZACIÓN COMPLETADA");
            System.exit(0);
        });
    }
}
