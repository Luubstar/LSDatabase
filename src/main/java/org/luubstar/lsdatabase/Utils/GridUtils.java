package org.luubstar.lsdatabase.Utils;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

public class GridUtils {


    private static final Logger log = LoggerFactory.getLogger(GridUtils.class);

    static public Node getNodeByRowColumnIndex(GridPane grid, final int row, final int column) {
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

    static public void deleteRow(GridPane grid, int rowIndex) {
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

        if (rowIndex < grid.getRowConstraints().size()) {
            grid.getRowConstraints().remove(rowIndex);
        }
    }

    static public void addNewRow(GridPane grid, int rowIndex) {
        TextField tf = new TextField();
        tf.setEditable(false);
        tf.setPromptText("...");

        TextField tf2 = new TextField();
        tf2.setPromptText("nº");
        tf2.setText("1");

        TextField tf3 = new TextField();
        tf3.setPromptText("... €");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {return change;}
            return null;
        };

        tf2.setTextFormatter(new TextFormatter<>(filter));
        tf3.setTextFormatter(new TextFormatter<>(filter));

        Button b = new Button("Seleccionar");
        b.setPrefSize(107, 25);
        b.getStyleClass().add("actionbuttons");

        Button x = new Button("X");
        x.getStyleClass().add("closebuttons");

        grid.add(new Label("Cliente:"), 0, rowIndex);
        grid.add(tf, 1, rowIndex);
        grid.add(tf2, 2, rowIndex);
        grid.add(tf3, 3, rowIndex);
        grid.add(b, 4, rowIndex);
        grid.add(x, 5, rowIndex);

        GridPane.setMargin(tf, new Insets(0, 5, 0, 5));
        GridPane.setMargin(tf2, new Insets(0, 10, 0, 10));
        GridPane.setMargin(tf3, new Insets(0, 10, 0, 10));

        RowConstraints row = new RowConstraints();
        row.setMinHeight(40);
        row.setMaxHeight(40);
        row.setPrefHeight(40);

        grid.getRowConstraints().add(row);
    }

    static public void reconfigureGrid(GridPane grid, List<List<String>> c){
        List<Node> sortedNodes = grid.getChildren().stream()
                .sorted(Comparator.comparingInt((Node n) -> GridPane.getRowIndex(n) == null ? 0 : GridPane.getRowIndex(n))
                        .thenComparingInt(n -> GridPane.getColumnIndex(n) == null ? 0 : GridPane.getColumnIndex(n)))
                .toList();

        for(int i = 2; i < (grid.getRowCount()-1); i++){
            c.get(i).add(((TextField) sortedNodes.get(8 + ((i-2)*5) + 2)).getText());
            c.get(i).add(((TextField) sortedNodes.get(8 + ((i-2)*5) + 3)).getText());
        }
    }
}
