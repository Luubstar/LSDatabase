package org.luubstar.lsdatabase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.luubstar.lsdatabase.App.writeLast;

public class MainController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private Button button_Inicio;
    @FXML
    private Button button_Buscar;
    @FXML
    private Button button_Anadir;
    @FXML
    private Button button_Facturar;
    @FXML
    private Button button_Recordatorios;

    @FXML
    private MenuItem pInicio;
    @FXML
    private MenuItem pBuscar;
    @FXML
    private MenuItem pAdd;
    @FXML
    private MenuItem pFacturar;
    @FXML
    private MenuItem pRecordatorios;

    public static int index = -1;
    private final BooleanProperty canMove = new SimpleBooleanProperty(false);
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ChangePanel.init(root, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        button_Inicio.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.DASHBOARD));
        button_Buscar.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.BUSQUEDA));
        button_Anadir.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.ANADIR));
        button_Facturar.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.FACTURAR));
        button_Recordatorios.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.RECORDATORIOS));

        pInicio.setOnAction(e -> ChangePanel.changeContent(Panel.DASHBOARD));
        pBuscar.setOnAction(e -> ChangePanel.changeContent(Panel.BUSQUEDA));
        pAdd.setOnAction(e -> ChangePanel.changeContent(Panel.ANADIR));
        pFacturar.setOnAction(e -> ChangePanel.changeContent(Panel.FACTURAR));
        pRecordatorios.setOnAction(e -> ChangePanel.changeContent(Panel.RECORDATORIOS));

        button_Inicio.disableProperty().bind(canMove);
        button_Buscar.disableProperty().bind(canMove);
        button_Anadir.disableProperty().bind(canMove);
        button_Facturar.disableProperty().bind(canMove);
        button_Recordatorios.disableProperty().bind(canMove);

        pInicio.disableProperty().bind(canMove);
        pBuscar.disableProperty().bind(canMove);
        pAdd.disableProperty().bind(canMove);
        pFacturar.disableProperty().bind(canMove);
        pRecordatorios.disableProperty().bind(canMove);
        ChangePanel.changeContent(Panel.DASHBOARD);
    }

    public void blockMovement(boolean b) {
        canMove.set(b);
    }

    public void open() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo");
            fileChooser.setInitialDirectory(new File(new File(MainController.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath()));

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de datos", "*.lsdata")
            );

            File selectedFile = fileChooser.showOpenDialog(button_Inicio.getScene().getWindow());

            if (selectedFile != null) {
                logger.info("Cargando fichero {}", selectedFile);
                Database.disconect();
                Database.loadFile(selectedFile.getPath());
                writeLast(selectedFile.getPath());
                Database.start();

                ((SearchController) ChangePanel.getController(Panel.BUSQUEDA)).search();
            } else {
                logger.info("Dialogo de selección cerrado");
            }
        }
        catch (Exception e){
            logger.error("Error en la lectura ", e);
        }
    }

    public void save(){
        Database.file.save();
        Database.unsaved = false;
    }

    public void saveAs(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo");
            fileChooser.setInitialDirectory(new File(new File(MainController.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath()));

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de datos", "*.lsdata")
            );

            File selectedFile = fileChooser.showSaveDialog(button_Inicio.getScene().getWindow());

            if (selectedFile != null) {
                logger.info("Guardando fichero {}", selectedFile);
                Database.file.save(selectedFile);
                Database.unsaved = false;
            } else {
                logger.info("Dialogo de guardado cerrado");
            }
        }
        catch (Exception e){
            logger.error("Error en la lectura ", e);
        }
    }
}