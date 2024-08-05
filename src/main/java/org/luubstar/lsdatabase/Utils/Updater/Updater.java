package org.luubstar.lsdatabase.Utils.Updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Updater {
    private static final Logger logger = LoggerFactory.getLogger(Updater.class);
    private static final String OWNER = "Luubstar";
    private static final String REPO = "LSBase";

    public static void update(){
        try{
            if(isBiggerVersion(OWNER, REPO)){
                downloadVersion(OWNER, REPO);
                logger.info("La aplicación está actualizada");
            }
            else{logger.info("La aplicación está al día");}
        }
        catch (Exception e){
            logger.error("La actualización no pudo realizarse correctamente ", e);
        }
    }

    private static boolean isBiggerVersion(String owner, String repo){
      return Version.isBiggerVersion(owner, repo);
    }

    private static void downloadVersion(String owner, String repo) throws IOException {
        String downloadUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("nuevoArchivo.jar")) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        // Opcional: Reemplazar el archivo viejo por el nuevo
        File oldFile = new File("antiguoArchivo.jar");
        oldFile.delete();
        new File("nuevoArchivo.jar").renameTo(oldFile);
    }
}
