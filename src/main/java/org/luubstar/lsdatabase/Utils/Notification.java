package org.luubstar.lsdatabase.Utils;
import org.luubstar.lsdatabase.App;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;  
import java.util.Date;

public class Notification{

  String title, message;
  Date fecha;

    public static void main(String[] args){
        try{
          Notification n = new Notification("Titulo", "Mensaje", new Date(System.currentTimeMillis() + 60000));
          n.SendNotification();
          System.out.println("Notificación preparada para dentro de 1 minutos");
        }
        catch(IOException e){System.out.println(e.getMessage());}
    }

    public Notification(String title, String message, Date fecha){
        this.title = title;
        this.message = message;
        this.fecha = fecha;
    }

    public void SendNotification() throws IOException{

        String os = System.getProperty("os.name");
        if (os.contains("Linux") || os.contains("Mac")){
          DateFormat dateFormat = new SimpleDateFormat("HH:mm M/dd/yyyy");
          String strDate = dateFormat.format(fecha);
          ProcessBuilder builder = new ProcessBuilder(
              "bash", "-c",
              "echo notify-send " + title + " " + message + " | at " + strDate);
          builder.inheritIO().start();
        }
        else{
            displayTray();
          }
    }

    private void displayTray() {
        try {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String time = timeFormat.format(fecha);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(fecha);

            Path pathToScript = Paths.get(App.class.getResource("Utils/script.txt").toURI());
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getUniqueName() {
        return "script_" + System.currentTimeMillis();
    }

}