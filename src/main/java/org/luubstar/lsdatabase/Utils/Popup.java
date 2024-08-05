package org.luubstar.lsdatabase.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class Popup {

    public static boolean askForConfirmation(String mensaje){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación requerida");
        alert.setHeaderText(mensaje);
        alert.setContentText("");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void notify(String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificación del sistema");
        alert.setHeaderText(mensaje);
        alert.showAndWait();
    }

}
