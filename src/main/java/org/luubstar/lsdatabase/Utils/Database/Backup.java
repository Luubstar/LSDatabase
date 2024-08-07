package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class Backup {

    private static final String dir = "backups";
    private static final int daysBetweenBackup = 7;
    private static final Logger logger = LoggerFactory.getLogger(Backup.class);
    protected static void makeBackup() throws IOException {
        if(createDirectory()){
            File backup = new File(dir + "/backup_" + LocalDate.now() + ".db");
            if(!backup.exists() && lastBackupDate() >= daysBetweenBackup) {createBackupFile();}
        }
    }

    private static int lastBackupDate() throws IOException {
        File d =  new File(dir);
        File[] backups = d.listFiles();
        if (backups == null) {
            throw new IOException("Error al leer los ficheros de la carpeta de backups");
        }

        List<File> fileList = new ArrayList<>(List.of(backups));
        Collections.sort(fileList);
        fileList = fileList.reversed();

        String StringDate = fileList.getFirst().getName().replace("backup_", "").replace(".db", "");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate Date = LocalDate.parse(StringDate, formatter);

        return (int) ChronoUnit.DAYS.between(Date, LocalDate.now());
    }

    private static void createBackupFile() throws IOException {
        File d =  new File(dir);

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = localDate.format(formatter);

        Files.copy(Database.actualFile.toPath(), Path.of(dir + "/backup_" + dateString + ".db"));

        File[] backups = d.listFiles();
        if (backups == null) {
            throw new IOException("Error al leer los ficheros de la carpeta de backups");
        }

        List<File> fileList = new ArrayList<>(List.of(backups));

        if (fileList.size() > 7) {
            Collections.sort(fileList);
            if(!fileList.getFirst().delete()){logger.error("¡Error eliminando un backup!");}
        }
    }

    private static boolean createDirectory(){
        File d = new File(dir);

        if(!d.exists()){
            if(d.mkdir()){logger.debug("La carpeta backups ha sido creada");}
            else{logger.debug("La carpeta backups no pudo ser creada"); return false;}
        }

        return true;
    }
}
