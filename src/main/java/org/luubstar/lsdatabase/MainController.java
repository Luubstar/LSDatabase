package org.luubstar.lsdatabase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Panel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    public static int index = -1;
    private final BooleanProperty canMove = new SimpleBooleanProperty(false);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {ChangePanel.init(root, this);} catch (IOException e) {throw new RuntimeException(e);}
        button_Inicio.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.DASHBOARD));
        button_Buscar.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.BUSQUEDA));
        button_Anadir.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.ANADIR));
        button_Facturar.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.FACTURAR));
        button_Recordatorios.setOnMouseReleased(e -> ChangePanel.changeContent(Panel.RECORDATORIOS));

        button_Inicio.disableProperty().bind(canMove);
        button_Buscar.disableProperty().bind(canMove);
        button_Anadir.disableProperty().bind(canMove);
        button_Facturar.disableProperty().bind(canMove);
        button_Recordatorios.disableProperty().bind(canMove);
        ChangePanel.changeContent(Panel.DASHBOARD);
    }

    public void blockMovement(boolean b){
        canMove.set(b);
    }
}