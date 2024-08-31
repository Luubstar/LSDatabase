package org.luubstar.lsdatabase.Utils;
import org.luubstar.lsdatabase.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notification{
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    String title, message;
    Date fecha;
    String ID;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Notification(String title, String message, Date fecha){
        this.title = title;
        this.message = message;
        this.fecha = fecha;
    }

    public void SendNotification(){
        try{
            ChangePanel.getNavigator().addNotificacion(this);
            String os = System.getProperty("os.name");
            if (os.contains("Linux") || os.contains("Mac")){
              DateFormat dateFormat = new SimpleDateFormat("HH:mm M/dd/yyyy");
              String strDate = dateFormat.format(fecha);
              ProcessBuilder builder = new ProcessBuilder(
                  "bash", "-c",
                  "echo notify-send " + title + " " + message + " | at " + strDate);
              builder.inheritIO().start();
            }
        }
        catch(Exception e){logger.error("Error en la creación de la notificación ", e);}
    }


    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<String> getVals(){
        List<String> ar = new ArrayList<>();
        ar.add(title);
        ar.add(message);
        ar.add(sdf.format(fecha));
        return ar;
    }
}