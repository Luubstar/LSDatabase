package org.luubstar.lsdatabase;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.luubstar.lsdatabase.Utils.ChangePanel;
import org.luubstar.lsdatabase.Utils.Database.Columna;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Database.Tabla;

import java.net.URL;
import java.util.*;

public class SearchController implements SidePanel {
    @FXML
    TextField input_busqueda;
    @FXML
    TableView<ObservableList<String>> tabla;
    @FXML
    ScrollPane scrollpane;
    Tabla original;
    Tabla actual;
    List<String> IDs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        original = Database.tablas.getFirst();
        createTableview(original);
    }

    @Override
    public void start() {}

    public void createTableview(Tabla datos){
        IDs = new LinkedList<>();
        actual = original;
        tabla.getItems().clear();
        tabla.getColumns().clear();
        //TODO: Esto asume que la primera columna es el ID, el Primary Key y tal...

        for (int i = 1; i < datos.columnas().size(); i++) {
            Columna columna = datos.columnas().get(i);
            TableColumn<ObservableList<String>, String> tableColumn = new TableColumn<>(columna.nombre());
            final int colIndex = i-1;
            tableColumn.setCellValueFactory(cellData -> {
                ObservableList<String> rowValues = cellData.getValue();
                return new SimpleObjectProperty<>(rowValues.get(colIndex));
            });
            tableColumn.setMinWidth(100);
            tabla.getColumns().add(tableColumn);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (int i = 0; i < datos.columnas().getFirst().valores().size(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int j = 0; j < datos.columnas().size(); j++) {
                Columna columna = datos.columnas().get(j);
                if(columna.clavePrimaria()){IDs.add(columna.valores().get(i));}
                else{row.add(columna.valores().get(i));}
            }
            data.add(row);
        }

        tabla.setRowFactory( tv -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ObservableList<String> rowData = row.getItem();
                    visualizarCliente(rowData, IDs.get(row.getIndex()));
                }
            });
            return row ;
        });

        tabla.setItems(data);
    }

    public void search(){
        String s = input_busqueda.getText();
        Tabla t = Database.searchLike(original, s);
        actual = t;
        createTableview(t);
    }

    public void visualizarCliente(ObservableList<String> datos, String ID){
        datos.addFirst(ID);
        ChangePanel.changeContent(2);
        ((AddController) ChangePanel.getController(2)).visualizarDatos(actual, datos);
        search();
    }
}