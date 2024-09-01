package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.luubstar.lsdatabase.Utils.AnimateButton;
import org.luubstar.lsdatabase.Utils.Panel;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class FacturarController implements SidePanel {
    @FXML
    VBox pane;
    @FXML
    Button button_save;
    @FXML
    Button button_clear;
    @FXML
    GridPane grid;

    Form f;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AnimateButton.animateButton(button_clear);
        AnimateButton.animateButton(button_save);
        f = createFrom();
        render(createFrom());
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

    public void clear(){f.reset();}

    public void add(){}

    public void select(){
        openWindow(Panel.BUSQUEDA, this);
    }

    public void openWindow(Panel panel, SidePanel controllerInstance){
        try {
            FXMLLoader loader = new FXMLLoader(FacturarController.class.getResource(panel.ruta + ".fxml"));
            Parent root = loader.load();
            loader.setController(controllerInstance);

            // Crear la escena
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(FacturarController.class.getResource("light.css")).toExternalForm());
            // Crear la ventana
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UTILITY);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Selecciona un cliente");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de errores básico
        }
    }

    public void deselect(){}
}
