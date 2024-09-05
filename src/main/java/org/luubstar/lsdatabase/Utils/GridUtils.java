package org.luubstar.lsdatabase.Utils;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Iterator;
import java.util.function.UnaryOperator;

public class GridUtils {


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
        tf2.setPromptText("... €");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {return change;}
            return null;
        };

        tf2.setTextFormatter(new TextFormatter<>(filter));

        Button b = new Button("Seleccionar");
        b.setPrefSize(107, 25);

        grid.add(new Label("Cliente:"), 0, rowIndex);
        grid.add(tf, 1, rowIndex);
        grid.add(tf2, 2, rowIndex);
        grid.add(b, 3, rowIndex);
        grid.add(new Button("X"), 4, rowIndex);

        GridPane.setMargin(tf, new Insets(0, 5, 0, 5));
        GridPane.setMargin(tf2, new Insets(0, 10, 0, 10));

        RowConstraints row = new RowConstraints();
        row.setMinHeight(40);
        row.setMaxHeight(40);
        row.setPrefHeight(40);

        grid.getRowConstraints().add(row);
    }
}
