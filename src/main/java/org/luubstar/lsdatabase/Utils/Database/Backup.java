package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Generated;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Backup {

    static final String DIR = "backups";
    static final int daysBetweenBackup = 7;
    static final int maxBackups = 4;
    private static final Logger logger = LoggerFactory.getLogger(Backup.class);

    @Generated("Constructor privado")
    private Backup(){}

    static String makeDataString(){
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

     static void makeBackup() throws IOException {
        if(createDirectory()){
            File backup = new File(DIR + "/backup_" + makeDataString() + ".db");
            if(!backup.exists() && lastBackupDate(DIR) >= daysBetweenBackup) {createBackupFile(Backup.DIR);}
        }
    }

    static int lastBackupDate(String dir_) throws IOException {
        File d =  new File(dir_);
        File[] backups = d.listFiles();
        if (backups == null) {
            throw new IOException("Error al leer los ficheros de la carpeta de backups");
        }

        if(backups.length == 0){return daysBetweenBackup;}

        List<File> fileList = new ArrayList<>(List.of(backups));
        Collections.sort(fileList);
        fileList = fileList.reversed();

        String StringDate = fileList.getFirst().getName().replace("backup_", "").replace(".db", "");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate Date = LocalDate.parse(StringDate, formatter);

        return (int) ChronoUnit.DAYS.between(Date, LocalDate.now());
    }

    static void createBackupFile(String dir_) throws IOException {
        File d = new File(dir_);

        Files.copy(Database.actualFile.toPath(), Path.of(dir_ + "/backup_" + makeDataString() + ".db"));

        File[] backups = d.listFiles();
        if (backups == null) {
            throw new IOException("Error al leer los ficheros de la carpeta de backups");
        }

        if (backups.length > maxBackups) {
            List<File> fileList = new ArrayList<>(List.of(backups));
            Collections.sort(fileList);
            if(!fileList.getFirst().delete()){logger.error("¡Error eliminando un backup!");}
        }
    }

    static boolean createDirectory(){
        File d = new File(DIR);

        if(!d.exists()){
            if(!d.mkdir()){logger.debug("La carpeta backups no pudo ser creada"); return false;}
        }

        return true;
    }

    static boolean deleteDirectory(){
        File directory = new File(Backup.DIR);

        File[] files = directory.listFiles();

        if(files != null){
            for(File f : files){
                if(!f.delete()){logger.error("Error limpiando ficheros");}
            }
        }

        if(directory.exists() && directory.isDirectory()) {
            return directory.delete();
        }
        return true;
    }
}
