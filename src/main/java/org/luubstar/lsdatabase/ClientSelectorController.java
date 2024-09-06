package org.luubstar.lsdatabase;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.luubstar.lsdatabase.Utils.AnimateButton;
import org.luubstar.lsdatabase.Utils.Database.Columna;
import org.luubstar.lsdatabase.Utils.Database.Database;
import org.luubstar.lsdatabase.Utils.Database.Tabla;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class ClientSelectorController implements SidePanel {
    private static final Logger log = LoggerFactory.getLogger(ClientSelectorController.class);
    @FXML
    TextField input_busqueda;
    @FXML
    TableView<ObservableList<String>> tabla;
    @FXML
    ScrollPane scrollpane;
    @FXML
    Button button_busqueda;


    private Stage dialog;
    private Tabla original;
    private Tabla actual;
    private List<String> IDs;

    private List<String> results;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        original = Database.actual;
        createTableview(original);

        AnimateButton.animateButton(button_busqueda);
    }

    @Override
    public void start() {}

    public void createTableview(Tabla datos){
        IDs = new LinkedList<>();
        actual = original;
        tabla.getItems().clear();
        tabla.getColumns().clear();

        createColumns(datos);
        createRows(datos);
    }

    public void search(){
        actual = Database.searchLike(original, input_busqueda.getText());
        createTableview(actual);
    }

    public void visualizarCliente(ObservableList<String> datos, String ID){
        log.info("Clicado!");
    }

    public void createColumns(Tabla datos){
        int colIndex = -1;
        for (int i = 0; i < datos.columnas().size(); i++) {
            Columna columna = datos.columnas().get(i);
            if(!columna.clavePrimaria()) {
                TableColumn<ObservableList<String>, String> tableColumn = new TableColumn<>(columna.nombre());

                final int finalColIndex = ++colIndex;
                tableColumn.setCellValueFactory(cellData -> {
                    ObservableList<String> rowValues = cellData.getValue();
                    return new SimpleObjectProperty<>(rowValues.get(finalColIndex));
                });
                tableColumn.setMinWidth(100);

                tabla.getColumns().add(tableColumn);
            }
        }
    }

    public void createRows(Tabla datos){
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
        setRowFactory();
        tabla.setItems(data);
    }

    public void setRowFactory(){
        tabla.setRowFactory( tv -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    ObservableList<String> rowData = row.getItem();
                    rowData.addFirst(IDs.get(row.getIndex()));
                    setResults(rowData);
                    dialog.close();
                }
            });
            return row ;
        });
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public void setDialog(Stage dialog) {
        this.dialog = dialog;
    }
}