package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class DataFile {
    private static final Logger logger = LoggerFactory.getLogger(DataFile.class);
    final File file;

    private final File database;
    private final File data;

    DataFile(String path) throws InvalidParameterException, IOException{
            file = new File(path);
            if (file.exists()) {
                File dest = Zip.extractZip(path);

                database = new File(dest.getAbsolutePath() + "/defaultBase.db");
                data = new File(dest.getAbsolutePath() + "/data");

                if(!database.exists()){
                    throw new InvalidParameterException("La db no existe en " + path);
                }

                if(!data.exists()){
                    throw new InvalidParameterException("La carpeta data no existe en " + path);
                }
            }
            else {
                throw new InvalidParameterException("Fichero no existente");
            }
    }

    public static void copyResource(String resourceFileName, String destinationFilePath) {
        logger.debug("Copiando: {}", resourceFileName);
        Path target = Path.of(destinationFilePath);

        try (InputStream inputStream = DataFile.class.getResourceAsStream(resourceFileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("El archivo " + resourceFileName + " no se encuentra en el jar.");
            }

            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Archivo copiado desde el JAR a {}", destinationFilePath);
        } catch (IOException e) {
            logger.error("Error copiando fichero en JAR", e);
        }
    }

    public void delete(String ID) throws IOException {
        File f = new File(data + "/" + ID);
        if(f.exists()) {
            Files.walkFileTree(Path.of(f.getPath()), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public void save(){save(file);}

    public void save(File f){
        try{
            Zip.save(Arrays.asList(database,data), f);
        }
        catch (Exception e){logger.error("Error guardando el fichero ", e);}
    }

    public File getDatabase() {
        return database;
    }

    public File getData() {
        return data;
    }
}
