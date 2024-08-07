package org.luubstar.lsdatabase;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.luubstar.lsdatabase.Utils.Database.Database;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements SidePanel {

    @FXML
    GridPane pane;

    private final IntegerProperty clientsInDB = new SimpleIntegerProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tile numberTile = TileBuilder.create()
                .skinType(Tile.SkinType.NUMBER)
                .description("Clientes registrados")
                .backgroundColor(Color.GRAY)
                .decimals(0)
                .textAlignment(TextAlignment.CENTER)
                .descriptionAlignment(Pos.CENTER)
                .build();

        numberTile.valueProperty().bind(clientsInDB);

        pane.add(numberTile, 2,0);
    }

    @Override
    public void start() {
        clientsInDB.set(Database.entries(Database.actual));
    }

}