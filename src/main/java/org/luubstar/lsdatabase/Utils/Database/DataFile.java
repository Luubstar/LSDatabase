package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataFile {
    private static final Logger logger = LoggerFactory.getLogger(DataFile.class);
    final File file;
    File dest;

    private final File database;
    private final File data;

    DataFile(String path) throws InvalidParameterException, IOException{
            file = new File(path);
            if (file.exists()) {
                extractZip(path);

                database = new File(dest.getAbsolutePath() + "/defaultBase.db");
                data = new File(dest.getAbsolutePath() + "/data");

                if(!database.exists()){
                    throw new InvalidParameterException("La db no existe en " + path);
                }

                if(!data.exists()){
                    throw new InvalidParameterException("La carpeta data no existe en " + path);
                }

                logger.debug("Datafile leida correctamente en {}", dest);

            }
            else {
                throw new InvalidParameterException("Fichero no existente");
            }
    }

    public void save(){save(file);}

    public void save(File f){

    }

    void extractZip(String path) throws IOException {
        dest = Files.createTempDirectory("LSDatabase").toFile();
        dest.deleteOnExit();

        ZipInputStream zip = new ZipInputStream(new FileInputStream(path));
        ZipEntry entry = zip.getNextEntry();

        while(entry != null){
            createFile(dest, entry, zip);
            entry = zip.getNextEntry();
        }

        zip.closeEntry();
        zip.close();
    }

    void createFile(File dest, ZipEntry entry, ZipInputStream zip) throws IOException {
        File newFile = newFile(dest, entry);
        byte[] buffer = new byte[1024];
        if (entry.isDirectory()) {
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                throw new IOException("Error creando el directorio " + newFile);
            }
        } else {

            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
                throw new IOException("Error creando el directorio " + parent);
            }

            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zip.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("La entrada está fuera de la localización indicada:  " + zipEntry.getName());
        }

        return destFile;
    }

    public File getDatabase() {
        return database;
    }

    public File getData() {
        return data;
    }
}
