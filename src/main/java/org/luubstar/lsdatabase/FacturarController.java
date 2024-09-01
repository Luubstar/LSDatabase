package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.luubstar.lsdatabase.Utils.AnimateButton;
import org.luubstar.lsdatabase.Utils.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    Form f;

    int selectionID = 0;
    int removeID = 0;

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
                        Field.ofStringType("").label("Nombre:").placeholder("Nombre"),
                        Field.ofStringType("").label("Fecha límite:").placeholder(LocalDate.now().toString()),
                        Field.ofIntegerType(21).label("% IVA:").placeholder("21"),
                        Field.ofIntegerType(-15).label("% IRPF:").placeholder("-15")
                )
        ).title("Factura");
    }

    private void render(Form f){
        pane.getChildren().clear();
        FormRenderer renderer = new FormRenderer(f);
        renderer.getChildren().getFirst().getStyleClass().add("formpanel");
        pane.getChildren().remove(grid);
        pane.getChildren().addAll(renderer, grid);
    }

    public void select(int p){
        List<String> selected = openWindow(Panel.BUSQUEDA, new ClientSelectorController());
        if(selected == null){return;}
        addListAtIndex(p, selected);

        Node v = getNodeByRowColumnIndex(p, 1);
        if(v != null){
            ((TextField) v).setText(selected.get(1) + " " + selected.get(2) + " " + selected.get(3));
        }

        if(grid.getRowCount() -1 == p){
            addNewRow(++p);
            reconfigureGrid();
        }

        log.info(Arrays.toString(clientes.toArray()));
    }

    public void deselect(int p){
        addListAtIndex(p, null);
        Node v = getNodeByRowColumnIndex(p, 1);

        if(p > 2){deleteRow(p);}

        if(v != null){
            ((TextField) v).setText("");
        }
    }

    private void reconfigureGrid(){
        selectionID = 0;
        removeID = 0;

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

    public Node getNodeByRowColumnIndex(final int row, final int column) {
        for (Node node : grid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeColumn = GridPane.getColumnIndex(node);

            if (nodeRow == null) {
                nodeRow = 0;
            }
            if (nodeColumn == null) {
                nodeColumn = 0;
            }

            if (nodeRow == row && nodeColumn == column) {
                return node;
            }
        }
        return null;
    }

    void deleteRow(int rowIndex) {
        Iterator<Node> iterator = grid.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            Integer row = GridPane.getRowIndex(node);
            if (row != null && row == rowIndex) {
                iterator.remove();
            }
        }

        for (Node node : grid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            if (row != null && row > rowIndex) {
                GridPane.setRowIndex(node, row - 1);
            }
        }
    }

    public void addNewRow( int rowIndex) {
        TextField tf = new TextField();
        tf.setEditable(false);
        tf.setPromptText("...");

        Button b = new Button("Seleccionar");
        b.setPrefSize(107, 25);

        grid.add(new Label("Cliente:"), 0, rowIndex);
        grid.add(tf, 1, rowIndex);
        grid.add(b, 2, rowIndex);
        grid.add(new Button("X"), 3, rowIndex);

        GridPane.setMargin(tf, new Insets(5, 5, 0, 0));
    }

    void addListAtIndex(int index, List<String> list) {
        while (clientes.size() <= index) {
            clientes.add(null);
        }
        clientes.set(index, list);
    }

    public void clear(){f.reset();}

    public void add(){}

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
