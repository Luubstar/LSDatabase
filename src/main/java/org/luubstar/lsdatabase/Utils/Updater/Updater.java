package org.luubstar.lsdatabase.Utils.Updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Updater {
    private static final Logger logger = LoggerFactory.getLogger(Updater.class);
    private static final String OWNER = "Luubstar";
    private static final String REPO = "LSBase";

    public static void update(){
        try{
            if(isBiggerVersion()){
                downloadVersion();
                logger.info("La aplicación está actualizada");
            }
            else{logger.info("La aplicación está al día");}
        }
        catch (Exception e){
            logger.error("La actualización no pudo realizarse correctamente ", e);
        }
    }

    private static boolean isBiggerVersion(){
      return Version.isBiggerVersion(Updater.OWNER, Updater.REPO);
    }

    private static void downloadVersion()  {
        logger.debug("INICIANDO DESCARGA");
        String downloadUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", Updater.OWNER, Updater.REPO);

        try {
            String assetUrl = Requester.getLatestReleaseAssetUrl(downloadUrl);
            if (assetUrl != null) {
                String saveFilePath = "descargado.jar";
                Requester.downloadFile(assetUrl, saveFilePath);
                logger.info("Archivo descargado satisfactoriamente");
            }
            else {logger.error("No se encontró archivo a descargar ");}

        } catch (Exception e) {logger.error("Error en la descarga del fichero ", e);}


        File oldFile = new File("App.jar");
        if(!oldFile.delete()){logger.error("Fichero base no puede ser eliminado");}
        else{
            boolean ignored = new File("descargado.jar").renameTo(oldFile);
        }
    }

}
