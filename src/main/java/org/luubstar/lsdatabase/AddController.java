package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Database.Columna;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Database.Tabla;
import org.luubstar.lsdatabase.Utils.Panel;

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
    List<StringField> lista;
    String ID = "";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO: Aquí también leemos la 1º tabla...
        lista = formByTable(Database.actual);
        renderer = new FormRenderer(Form.of(Group.of(lista.toArray(new Element[0]))).title(Database.actual.nombre()));
        pane.getChildren().add(renderer);
        button_back.visibleProperty().bind(isEditing);
        button_delete.visibleProperty().bind(isEditing);
    }

    @Override
    public void start() {}

    public void visualizarDatos(Tabla t, ObservableList<String> datos){
        lista = formByTable(t,datos);
        renderer = new FormRenderer(Form.of(Group.of(lista.toArray(new Element[0]))).title(t.nombre()));
        pane.getChildren().setAll(renderer);
        editing(true);
    }

    public List<StringField> formByTable(Tabla t){return formByTable(t, FXCollections.observableArrayList());}

    public List<StringField> formByTable(Tabla t, ObservableList<String> datos){
        List<StringField> lista = new LinkedList<>();
        for(int i = 0; i < t.columnas().size();i++){
            Columna c = t.columnas().get(i);
            String def = "";
            if(i < datos.size()){def = datos.get(i);}
            if(!c.clavePrimaria()) {lista.add(Field.ofStringType(def).label(c.nombre()).required(c.noNulo()));}
            else{ID = datos.get(i);}
        }
        return lista;
    }
    
    public void clear(){
        lista = formByTable(Database.actual);
        renderer = new FormRenderer(Form.of(Group.of(lista.toArray(new Element[0]))).title(Database.actual.nombre()));
        pane.getChildren().setAll(renderer);
    }

    //TODO: Añadir popup de ok
    //TODO: Actualizar tabla
    public void add(){
        List<String> valores = formValues();
        if(!isEditing.get()) {Database.add(Database.actual, valores);}
        else{
            Database.update(Database.actual, valores, ID);
            editing(false);
        }
        clear();
    }

    //TODO: añadir popup de aviso y ok
    public void delete(){
        Database.delete(Database.actual, ID);
        clear();
        editing(false);
    }

    public void back(){
        clear();
        editing(false);
    }

    private void editing(boolean b){
        ChangePanel.getNavigator().blockMovement(b);
        isEditing.set(b);
        if(!b){
            ChangePanel.changeContent(Panel.BUSQUEDA);
            ((SearchController) ChangePanel.getController(Panel.BUSQUEDA)).search();
        }
    }

    private List<String> formValues(){
        List<String> res = new LinkedList<>();
        for (StringField e : lista) {res.add(e.getValue());}
        return res;
    }
}