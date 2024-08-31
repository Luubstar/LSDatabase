package org.luubstar.lsdatabase;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.luubstar.lsdatabase.Utils.Notification;
import org.luubstar.lsdatabase.Utils.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class RecordatoriosController implements SidePanel {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    List<String> horas = Arrays.asList("8:00", "17:00", "21:00");
    List<Integer> horasEntero = Arrays.asList(8, 17, 21);
    @FXML
    VBox pane;
    @FXML
    Button button_create;
    Form form;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        form = createFrom();
        render(form);
    }

    public void start(){}

    private Form createFrom(){
        return Form.of(
                Group.of(
                        Field.ofStringType("").label("Titulo").placeholder("Titulo"),
                        Field.ofStringType("").label("Mensaje").placeholder("Mensaje"),
                        Field.ofDate(LocalDate.now()).required(true).label("Fecha"),
                        Field.ofSingleSelectionType(horas, 0)
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

    public void clear(){render(createFrom());}

    public void create(){
        String titulo = ((StringField) form.getFields().get(0)).getValue();
        String mensaje = ((StringField) form.getFields().get(1)).getValue();
        LocalDate fecha = ((DateField) form.getFields().get(2)).getValue();
        String s = (String) ((SingleSelectionField<?>) form.getFields().get(3)).getSelection();
        int h = horasEntero.get(horas.indexOf(s)) * 60*60*1000;

        java.sql.Date sqlDate = java.sql.Date.valueOf(fecha);
        Date d = new Date(sqlDate.getTime());
        d.setTime(d.getTime() + h);

        Notification n = new Notification(titulo, mensaje, d);
        n.SendNotification();
        logger.info("Notificación creada correctamente");
        Popup.notify("Notificación creada exitosamente");
        clear();
    }
}
