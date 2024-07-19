package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.CustomValidator;
import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

public class RecordatoriosController implements SidePanel {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    @FXML
    VBox pane;
    @FXML
    Button button_create;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        render(createFrom());
    }

    public void start(){}

    private Form createFrom(){
        return Form.of(
                Group.of(
                        Field.ofStringType("").label("Titulo").placeholder("Titulo").required(true),
                        Field.ofDate(LocalDate.now()).required(true).label("Fecha").validate(CustomValidator.forPredicate(
                                d -> !d.isBefore(LocalDate.now()), "La fecha es anterior a hoy")),
                        Field.ofStringType("").label("Mensaje").placeholder("Mensaje").required(true),
                        Field.ofSingleSelectionType(Arrays.asList("8:00", "17:00", "21:00"), 0)
                                .label("Hora de envío:")
                                .render(new SimpleRadioButtonControl<>())
                )
        ).title("Recordatorio");
    }

    private void render(Form f){
        pane.getChildren().clear();
        FormRenderer renderer = new FormRenderer(f);
        pane.getChildren().add(renderer);
    }

    private void clear(){render(null);}
    private void create(){



        clear();
    }
}
