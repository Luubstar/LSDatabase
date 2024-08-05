package org.luubstar.lsdatabase.Utils;
import org.luubstar.lsdatabase.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;  
import java.util.Date;
import java.util.Objects;

public class Notification{
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    String title, message;
    Date fecha;

    public Notification(String title, String message, Date fecha){
        this.title = title;
        this.message = message;
        this.fecha = fecha;
    }

    public void SendNotification(){
        try{
            String os = System.getProperty("os.name");
            if (os.contains("Linux") || os.contains("Mac")){
              DateFormat dateFormat = new SimpleDateFormat("HH:mm M/dd/yyyy");
              String strDate = dateFormat.format(fecha);
              ProcessBuilder builder = new ProcessBuilder(
                  "bash", "-c",
                  "echo notify-send " + title + " " + message + " | at " + strDate);
              builder.inheritIO().start();
            }
            else{displayTray();}
        }
        catch(Exception e){logger.error("Error en la creación de la notificación ", e);}
    }

    private void displayTray() throws IOException, InterruptedException, URISyntaxException {
        try {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String time = timeFormat.format(fecha);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(fecha);

            Path pathToScript = Paths.get(Objects.requireNonNull(App.class.getResource("Utils/script.txt")).toURI());
            String powershellCommand = Files.readString(pathToScript, StandardCharsets.UTF_8);

            String filename = getUniqueName() + ".ps1";

            Path scriptPath = Paths.get("Notificaciones", filename);
            Files.createDirectories(scriptPath.getParent()); // Crear el directorio si no existe
            Files.write(scriptPath, powershellCommand.getBytes());

            String currentDirectory = System.getProperty("user.dir");
            String scriptFullPath = currentDirectory + "\\Notificaciones\\" + filename;


            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "schtasks", "/create", "/tn", "PowerShellTask",
                    "/tr", "powershell -WindowStyle Hidden -File \"" + scriptFullPath +  "\" -title \"" + title + "\" -message \"" + message + "\"","/sc", "once", "/st", time, "/sd", date, "/f");

            Process process = builder.start();
            process.waitFor();

        } catch (IOException | URISyntaxException e) {logger.error("Error en la lectura-escritura del script de la notificación"); throw e;}
        catch (InterruptedException e){logger.error("Proceso interrumpido mientras se preparaba la notificación"); throw e;}
    }

    private static String getUniqueName() {
    return "script_" + System.currentTimeMillis();
}

}