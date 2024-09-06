package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import org.luubstar.lsdatabase.Utils.*;
import org.luubstar.lsdatabase.Utils.Database.Columna;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Database.Tabla;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AddController implements SidePanel {
    @FXML
    VBox pane;
    @FXML
    Button button_back;
    @FXML
    Button button_delete;
    @FXML
    Button button_save;
    @FXML
    Button button_clear;

    final BooleanProperty isEditing = new SimpleBooleanProperty(false);

    FormRenderer renderer;
    Form f;
    List<StringField> lista;
    String ID = "";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        visualizarDatos(Database.actual, FXCollections.observableArrayList());

        ChangePanel.getNavigator().blockMovement(false);
        isEditing.set(false);

        button_back.visibleProperty().bind(isEditing);
        button_delete.visibleProperty().bind(isEditing);

        AnimateButton.animateButton(button_back);
        AnimateButton.animateButton(button_delete);
        AnimateButton.animateButton(button_save);
        AnimateButton.animateButton(button_clear);
    }

    @Override
    public void start() {}

    public void visualizarDatos(Tabla t, ObservableList<String> datos){
        lista = formByTable(t,datos);
        f = Form.of(Group.of(lista.toArray(new Element[0]))).title(t.nombre());
        f.getFields().getFirst().required(true);

        renderer = new FormRenderer(f);
        renderer.getChildren().getFirst().getStyleClass().add("formpanel");

        pane.getChildren().setAll(renderer);

        if(!datos.isEmpty()){addDropZone(datos.getFirst());}
        button_save.disableProperty().bind(f.validProperty().not());
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
            else if (i < datos.size()){ID = datos.get(i);}
        }
        return lista;
    }
    
    public void clear(){
        f.reset();
    }

    public void add(){
        List<String> valores = formValues();
        if(!isEditing.get()) {Database.add(Database.actual, valores);}
        else{
            Database.update(Database.actual, valores, ID);
            editing(false);
        }

        Popup.notify("Entrada añadida exitosamente");
        ((SearchController) ChangePanel.getController(Panel.BUSQUEDA)).search();
        clear();
    }

    public void delete() throws IOException {
        if(Popup.askForConfirmation("¿Desea eliminar la entrada?")) {
            Database.delete(Database.actual, ID);
            Database.file.delete(ID);
            clear();
            Popup.notify("Entrada eliminada exitosamente");
            editing(false);
        }
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

    private void addDropZone(String index){
        Label titulo = new Label("Ficheros");
        VBox Drop = new VBox();

        Drop.getStyleClass().add("dropzone");
        Drop.setPrefSize(380, 0);

        Dropzone.generateText(Drop, index);
        Drop.setOnDragOver(event -> Dropzone.configureZone(Drop, event));
        Drop.setOnDragEntered(event -> Dropzone.setOnEntered(Drop,event));
        Drop.setOnDragExited(event -> {Dropzone.setOnExit(Drop,event); Dropzone.generateText(Drop, index);});
        Drop.setOnDragDropped(event -> Dropzone.fileDropped(Drop, event, index));

        pane.getChildren().addAll(titulo, Drop);
    }

}