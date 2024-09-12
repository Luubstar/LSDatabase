package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.luubstar.lsdatabase.Utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class FacturarController implements SidePanel {
    private static final Logger log = LoggerFactory.getLogger(FacturarController.class);
    @FXML
    VBox pane;
    @FXML
    Button button_save;
    @FXML
    Button button_clear;
    @FXML
    GridPane grid;
    @FXML
    HBox controles;

    Form f;

    int selectionID = 0;
    int removeID = 0;

    StringProperty Nombre = new SimpleStringProperty("");
    StringProperty Fecha = new SimpleStringProperty("");

    IntegerProperty IVA = new SimpleIntegerProperty(21);
    IntegerProperty IRPF = new SimpleIntegerProperty(-15);

    List<List<String>> clientes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnimateButton.animateButton(button_clear);
        AnimateButton.animateButton(button_save);
        f = createFrom();
        render(createFrom());
        reconfigureGrid();
    }

    @Override
    public void start() {}

    private Form createFrom(){
        return Form.of(
                Group.of(
                        Field.ofStringType(Nombre).label("Nombre:").placeholder("Nombre"),
                        Field.ofStringType(Fecha).label("Fecha límite:").placeholder(LocalDate.now().toString()),
                        Field.ofIntegerType(IVA).label("% IVA:").placeholder("21"),
                        Field.ofIntegerType(IRPF).label("% IRPF:").placeholder("-15")
                )
        ).title("Factura").binding(BindingMode.CONTINUOUS);
    }

    private void render(Form f){
        pane.getChildren().clear();
        FormRenderer renderer = new FormRenderer(f);
        renderer.getChildren().getFirst().getStyleClass().add("formpanel");
        pane.getChildren().remove(grid);
        pane.getChildren().addAll(renderer, controles, grid);
    }

    public void select(int p){
        List<String> selected = openWindow(Panel.BUSQUEDA, new ClientSelectorController());
        if(selected == null){return;}
        addListAtIndex(p-1, selected);
        Node v = GridUtils.getNodeByRowColumnIndex(grid, p, 1);

        if(v != null){
            ((TextField) v).setText(selected.get(1) + " " + selected.get(2) + " " + selected.get(3));
        }
    }

    public void deselect(int p){
        addListAtIndex(p-1, null);
        Node v = GridUtils.getNodeByRowColumnIndex(grid, p, 1);
        Node v2 = GridUtils.getNodeByRowColumnIndex(grid, p, 2);

        if(v != null && v2 != null){
            ((TextField) v).setText("");
            ((TextField) v2).setText("");
        }
        if(v != null && p <= 2){
            ((TextField) v).setText("");
        }
    }

    public void addClient(){
        GridUtils.addNewRow(grid, selectionID);
        reconfigureGrid();
    }

    public void removeClient(){
        if(selectionID > 3) {
            GridUtils.deleteRow(grid, selectionID-1);
            selectionID--;
            reconfigureGrid();
        }
    }

    public void clear(){f.reset();}

    public void add() throws URISyntaxException {
        GridUtils.reconfigureGrid(grid, clientes);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo");
        fileChooser.setInitialDirectory(new File(new File(MainController.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath()));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de excel", "*.xlsx")
        );

        File selectedFile = fileChooser.showSaveDialog(button_save.getScene().getWindow());

        if (selectedFile != null) {
            if(ExportExcel.exportExcel(Nombre.getValue(), Fecha.getValue(), String.valueOf(IVA.getValue()), String.valueOf(IRPF.getValue()), clientes, selectedFile)){
                Popup.notify("Fichero creado correctamente");
            }
        }

    }

    private void reconfigureGrid(){
        selectionID = 1;
        removeID = 1;

        List<Node> sortedNodes = grid.getChildren().stream()
                .sorted(Comparator.comparingInt((Node n) -> GridPane.getRowIndex(n) == null ? 0 : GridPane.getRowIndex(n))
                        .thenComparingInt(n -> GridPane.getColumnIndex(n) == null ? 0 : GridPane.getColumnIndex(n)))
                .toList();

        sortedNodes.forEach(node -> {
            if (node instanceof Button button) {
                if ("Seleccionar".equals(button.getText())) {
                    final int sID = selectionID;
                    button.setOnAction(e -> select(sID));
                    selectionID++;
                } else if ("X".equals(button.getText())) {
                    final int rID = removeID;
                    button.setOnAction(e -> deselect(rID));
                    removeID++;
                }
            }
        });
    }

    void addListAtIndex(int index, List<String> list) {
        while (clientes.size() <= index) {
            clientes.add(null);
        }
        clientes.set(index, list);
    }

    public List<String> openWindow(Panel panel, Object controller){
        try {
            FXMLLoader loader = new FXMLLoader(FacturarController.class.getResource(panel.ruta + ".fxml"));
            loader.setController(controller);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(FacturarController.class.getResource("light.css")).toExternalForm());

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Selecciona un cliente");
            ((ClientSelectorController) controller).setDialog(stage);
            stage.showAndWait();

            return ((ClientSelectorController) controller).getResults();
        } catch (Exception e) {
            log.error("Error abriendo la ventana", e);
            return null;
        }
    }


}
