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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements SidePanel {

    @FXML
    GridPane pane;

    private final IntegerProperty clientsInDB = new SimpleIntegerProperty();

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

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
        try{
            //TODO: mejorar rendimiento
            Database.start();
            clientsInDB.set(Database.entries(Database.actual));
        }
        catch (Exception e){logger.error("Error recargando tamaño", e);}
    }

}