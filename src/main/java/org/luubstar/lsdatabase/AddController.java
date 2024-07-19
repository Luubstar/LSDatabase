package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Database.Columna;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Database.Tabla;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class AddController implements SidePanel {
    @FXML
    VBox pane;
    @FXML
    Button button_back;
    @FXML
    Button button_delete;
    final BooleanProperty isEditing = new SimpleBooleanProperty(false);

    FormRenderer renderer;
    Tabla actual;
    List<StringField> lista;
    String ID = "";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //TODO: Aquí también leemos la 1º tabla...
        actual = Database.tablas.getFirst();
        lista = formByTable(actual);
        renderer = new FormRenderer(Form.of(Group.of(lista.toArray(new Element[0]))).title(actual.nombre()));
        pane.getChildren().add(renderer);

        isEditing.set(false);
        button_back.visibleProperty().bind(isEditing);
        button_delete.visibleProperty().bind(isEditing);
    }

    @Override
    public void start() {}

    public void visualizarDatos(Tabla t, ObservableList<String> datos){
        lista = formByTable(t,datos);
        renderer = new FormRenderer(Form.of(Group.of(lista.toArray(new Element[0]))).title(t.nombre()));
        pane.getChildren().setAll(renderer);
        ChangePanel.getNavigator().blockMovement(true);
        isEditing.set(true);
    }

    public List<StringField> formByTable(Tabla t){
        List<StringField> lista = new LinkedList<>();
        for(int i = 1; i < t.columnas().size();i++){
            Columna c = t.columnas().get(i);
            if(!c.clavePrimaria()) {lista.add(Field.ofStringType("").label(c.nombre()).required(c.noNulo()));}
        }
        return lista;
    }

    public List<StringField> formByTable(Tabla t, ObservableList<String> datos){
        List<StringField> lista = new LinkedList<>();
        for(int i = 0; i < t.columnas().size();i++){
            Columna c = t.columnas().get(i);
            if(!c.clavePrimaria()) {lista.add(Field.ofStringType(datos.get(i)).label(c.nombre()).required(c.noNulo()));}
            else{ID = datos.get(i);}
        }
        return lista;
    }
    
    public void clear(){
        lista = formByTable(actual);
        renderer = new FormRenderer(Form.of(Group.of(lista.toArray(new Element[0]))).title(actual.nombre()));
        pane.getChildren().setAll(renderer);
    }

    //TODO: Añadir popup de ok
    //TODO: Actualizar tabla
    public void add(){
        List<String> valores = new LinkedList<>();
        for (StringField e : lista) {
            valores.add(e.getValue());
        }

        if(!isEditing.get()) {
            Database.add(actual, valores);
        }
        else{
            Database.update(actual, valores, ID);
            ChangePanel.getNavigator().blockMovement(true);
            isEditing.set(true);
            ChangePanel.changeContent(1);
            ((SearchController) ChangePanel.getController(1)).search();
        }
        clear();
    }

        //TODO: añadir popup de aviso y ok
    public void delete(){
        Database.delete(actual, ID);
        clear();
        ChangePanel.getNavigator().blockMovement(true);
        isEditing.set(true);
        ChangePanel.changeContent(1);
        ((SearchController) ChangePanel.getController(1)).search();
    }

    public void back(){
        clear();
        ChangePanel.getNavigator().blockMovement(false);
        isEditing.set(false);
        ChangePanel.changeContent(1);
        ((SearchController) ChangePanel.getController(1)).search();
    }
}