package org.luubstar.lsdatabase.Utils.Database;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackupTest {
    @AfterAll
    @BeforeAll
    static void prepare(){
        try{
            Database.loadFile(Database.DEFAULT);
            Database.start();
        }
        catch (Exception e){Assertions.fail("Error iniciando la base de datos ", e);}

        Backup.deleteDirectory();
    }

    @BeforeEach
    void clear(){
        Backup.deleteDirectory();
        Backup.createDirectory();
    }

    String getDateString(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
    
    void CreateBackup(String n){
        if(new File(n).exists()){Assertions.fail("El fichero ya existe " + n);}
        try{
            Backup.createBackupFile(Backup.DIR);
            File f = new File(Backup.DIR + "/backup_" + getDateString(LocalDate.now()) + ".db");
            if(!f.renameTo(new File(Backup.DIR + "/" + n))){
                Assertions.fail("Error renombrando fichero");
            }
        }
        catch (Exception e){Assertions.fail("Error en la creación de un backup " + e);}
    }

    @Test
    void testDeleteDirectory(){
        boolean delete = Backup.deleteDirectory();
        Assertions.assertTrue(delete, "No se puede eliminar el documento");
    }

    @Test
    void testMakeNewDirectory(){
        Backup.deleteDirectory();
        boolean create = Backup.createDirectory();
        File d = new File(Backup.DIR);
        Assertions.assertTrue(create && d.exists(), "La carpeta se crea correctamente");
    }

    @Test
    void testLastBackupDateWhenEmpty(){
        int v = 0;
        try{v = Backup.lastBackupDate();}
        catch (Exception e){Assertions.fail("Error obteniendo la última fecha");}
        Assertions.assertEquals(Backup.daysBetweenBackup, v, "Si no hay backups, el tiempo mostrado es el máximo");
    }

    @Test
    void testCreateBackupFile(){
        try{Backup.createBackupFile(Backup.DIR);}
        catch (Exception e){Assertions.fail("Error en la creación de un backup ", e);}

        File f = new File(Backup.DIR + "/backup_" + getDateString(LocalDate.now()) + ".db");
        File[] files = new File(Backup.DIR).listFiles();

        Assertions.assertNotNull(files, "No se ha creado el fichero");
        Assertions.assertEquals(1, files.length, "Existe más de un fichero en la carpeta backups");
        Assertions.assertTrue(f.exists(), "No se crean el fichero con el nombre correcto");
    }

    @Test
    void testCreateBackupFileFailed(){
        Assertions.assertThrows(IOException.class, () -> Backup.createBackupFile(""));
    }

    @Test
    void testCreateNoMoreThanMaxBackups(){
        for(int i = 0; i < Backup.maxBackups; i++){CreateBackup(String.valueOf(i));}
        CreateBackup(String.valueOf(Backup.maxBackups));
        File[] files = new File(Backup.DIR).listFiles();

        Assertions.assertNotNull(files, "No se han creado los ficheros");

        List<File> fileList = new ArrayList<>(List.of(files));
        Collections.sort(fileList);

        Assertions.assertEquals(Backup.maxBackups, files.length, "No se han creado la cantidad esperada de ficheros");
        Assertions.assertEquals(fileList.getLast().getName(), String.valueOf(Backup.maxBackups),
                "El nuevo fichero debería sustituir a un fichero");
        Assertions.assertFalse(fileList.contains(new File("0")), "El fichero 0 debería ser eliminado");
    }

    @Test
    void testLastBackupDateFromToday(){
        CreateBackup(getDateString(LocalDate.now()));

        int v = 0;
        try{v = Backup.lastBackupDate();}
        catch (Exception e){Assertions.fail("Error obteniendo la última fecha");}

        Assertions.assertEquals(0, v, "La diferencia de un fichero creado hoy debería ser 0");
    }

    @Test
    void testLastBackupDateFromOtherDay(){
        CreateBackup(getDateString(LocalDate.now().minusDays(2)));

        int v = 0;
        try{v = Backup.lastBackupDate();}
        catch (Exception e){Assertions.fail("Error obteniendo la última fecha");}

        Assertions.assertEquals(2, v, "La diferencia de un fichero creado hace 2 días debería ser 2");
    }

    @Test
    void testMakeBackup(){
        try{Backup.makeBackup();}
        catch (Exception e){Assertions.fail("Error en la función makebackup");}

        File f = new File(Backup.DIR + "/backup_" + getDateString(LocalDate.now()) + ".db");

        Assertions.assertTrue(f.exists(), "La función makeBackup no crea un backup");
    }

    @Test
    void testTimeString(){
        Assertions.assertEquals(getDateString(LocalDate.now()), Backup.makeDataString());
    }

}
