package org.luubstar.lsdatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.luubstar.lsdatabase.Utils.Notification;
import org.luubstar.lsdatabase.Utils.Validation.ValidationWrap;
import org.luubstar.lsdatabase.Utils.Validation.Validator;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class RecordatoriosController implements SidePanel {
    @FXML
    protected TextField input_Titulo;
    @FXML
    protected TextField input_Mensaje;
    @FXML
    protected DatePicker dpicker_FechaEnvio;
    @FXML
    protected Button button_Crear;
    @FXML
    protected GridPane grid;
    @FXML
    protected TextField input_Hora;
    @FXML
    protected TextField input_Minutos;

    Validator validator;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO: CAMBIAR ESTO A MI CLASE DE FORMULARIOS
        dpicker_FechaEnvio.setValue(LocalDate.now());
        input_Minutos.setOnKeyTyped(e -> checkIfNumber(input_Minutos));
        input_Hora.setOnKeyTyped(e -> checkIfNumber(input_Hora));

        validator = new Validator();
        try{
            startValidators();
            startWrappers();
        }
        catch (Exception e){System.out.println(e.getMessage());}
    }

    public void start(){
        int h = LocalDateTime.now().getHour();
        int m = LocalDateTime.now().getMinute() + 2;
        if(LocalDateTime.now().getMinute() + 2 >= 60){
            h++;
            m = 0;
        }
        input_Hora.setText(String.valueOf(h));
        input_Minutos.setText(String.valueOf(m));
    }

    public void createTask(){
        int h = Integer.parseInt(input_Hora.getText());
        int m = Integer.parseInt(input_Minutos.getText());

        Date d = Date.from(dpicker_FechaEnvio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        d.setTime(d.getTime() + (h* 60L + m) * 60000);

        Notification n = new Notification(input_Titulo.getText(), input_Mensaje.getText(), d);
        System.out.println("Notificación creada");
        try{n.SendNotification();}
        catch (IOException e){System.out.println("Error creando la notificacion");}
    }

    public void startValidators(){
        validator.add(input_Titulo, v -> {
            if(input_Titulo.getText().isEmpty()){
                v.logError(input_Titulo, "El título no puede estar vacío");
            }
        });
        validator.add(input_Mensaje, v -> {
            if(input_Mensaje.getText().isEmpty()){
                v.logError(input_Mensaje, "El mensaje no puede estar vacío");
            }
        });

        validator.add(dpicker_FechaEnvio, v -> {
            LocalDate d = LocalDate.now();
            if(dpicker_FechaEnvio.getValue().isBefore(d)){
                v.logError(dpicker_FechaEnvio, "La fecha no puede ser anterior a hoy");
            }
        });

        validator.add(input_Hora, v -> {
            try {
                int h = Integer.parseInt(input_Hora.getText());
                int m = Integer.parseInt(input_Minutos.getText());
                Date ad = new Date();
                Date d = Date.from(dpicker_FechaEnvio.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                d.setTime(d.getTime() + (h*60L + m-1)*60000);
                if (h < 0 || h >= 24) {
                    v.logError(input_Hora, "El valor de la hora está fuera del rango [0, 23]");
                }
                if(d.before(ad)){
                    v.logAviso(input_Hora, "La fecha tiene menos de un minuto de margen con la hora actual");
                }
            }
            catch (Exception ignored){v.logError(input_Hora, "La fecha tiene que ser un número");}
        }).add(input_Minutos, v -> {
            try {
                int h = Integer.parseInt(input_Hora.getText());
                int m = Integer.parseInt(input_Minutos.getText());
                Date ad = new Date();
                Date d = Date.from(dpicker_FechaEnvio.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                d.setTime(d.getTime() + (h*60L + m-1)*60000);
                if (m < 0 || m >= 60) {
                    v.logError(input_Minutos, "El valor de los mensajes está fuera del rango [0, 59]");
                }
                if(d.before(ad)){
                    v.logAviso(input_Minutos, "La fecha tiene menos de un minuto de margen con la hora actual");
                }
            }
            catch (Exception ignored){v.logError(input_Minutos, "La fecha tiene que ser un número");}
        });

    }
    public void startWrappers(){
        ValidationWrap wrap = validator.wrap(button_Crear, "No se puede crear el recordatorio: \n");
        wrap.setAction(w -> {createTask();});
    }

    public  void checkIfNumber(@SuppressWarnings("exports") TextField t){
        if (!t.getText().chars().allMatch(Character::isDigit)) {
            String cleanedText = t.getText().replaceAll("[^0-9]", "");
            t.setText(cleanedText);
        }
    }
}
