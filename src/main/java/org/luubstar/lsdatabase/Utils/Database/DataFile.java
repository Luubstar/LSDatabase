package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
