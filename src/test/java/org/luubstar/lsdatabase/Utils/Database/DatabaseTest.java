package org.luubstar.lsdatabase.Utils.Database;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTest {


    @BeforeEach
    void clean(){
        Database.disconect();
        Database.loadFile(Database.PLANTILLA);
        try{Database.start();}
        catch (Exception e){Assertions.fail("Error iniciando la base de datos");}
        Database.clear(Database.actual);
    }

    @Test
    void testLoadToDB(){
        Database.loadFile("123");
        Assertions.assertTrue(new File("123").exists());

        new File("123").delete();

        Database.loadFile(Database.PLANTILLA);
        File f = new File(Database.PLANTILLA);

        Assertions.assertNotNull(f);
        Assertions.assertEquals(Database.file.file,f,"La lectura de la plantilla de la base de datos causa error");
    }

    @Test
    void testConnection(){
        Connection c;
        try{
            c = Database.connectToDb();
            Assertions.assertNotNull(c, "La conexión no debería ser nula");
            Assertions.assertTrue(c.isValid(1000), "La conexión debe ser valida");
            c.close();
        }
        catch (Exception e){Assertions.fail("Error conectandose a la DB ", e);}
    }

    @Test
    void testStart(){
        try{Database.start();}
        catch (Exception e){Assertions.fail("La base de datos falló al iniciarse ", e);}

        Assertions.assertNotNull(Database.actual);
    }

    @Test
    void testAddAndEntries(){
        Database.updateTables();
        Tabla t = Database.actual;
        Assertions.assertEquals(0, Database.entries(t), "La tabla vacía tiene 0 entradas");

        List<String> l = new ArrayList<>();
        for(int i = 0; i < 26; i++){l.add("");}

        Database.add(Database.actual, l);
        Database.updateTables();

        Assertions.assertEquals(Database.actual.columnas().getFirst().valores().size(),Database.entries(Database.actual),
                "Entries debería devolver el tamaño de la tabla");
        Assertions.assertEquals(1, Database.entries(Database.actual), "La tabla tendría que tener una entrada");
    }

    @Test
    void testSearchLike(){
        Database.updateTables();
        Tabla t = Database.actual;
        Assertions.assertEquals(0, Database.entries(t), "La tabla vacía tiene 0 entradas");

        List<String> l = new ArrayList<>();
        for(int i = 0; i < 26; i++){l.add("1");}
        Database.add(t, l);

        Tabla r1 = Database.searchLike(Database.actual, "1");
        Tabla r2 = Database.searchLike(Database.actual, "2");
        Tabla r3 = Database.searchLike(Database.actual, "*");
        Assertions.assertEquals(1, Database.entries(r1), "La tabla tendría que tener una entrada porque encuentra '1'");
        Assertions.assertEquals(0, Database.entries(r2), "Debe de estar vacía si no encuentra nada");
        Assertions.assertEquals(1, Database.entries(r3), "Debe de encontrar todos si se usa *");
    }

    @Test
    void testDelete(){
        Database.updateTables();
        Tabla t = Database.actual;
        Assertions.assertEquals(0, Database.entries(t), "La tabla vacía tiene 0 entradas");

        List<String> l = new ArrayList<>();
        for(int i = 0; i < 26; i++){l.add("1");}
        Database.add(t, l);
        Database.updateTables();

        Database.delete(Database.actual, Database.actual.columnas().getFirst().valores().getFirst());

        Database.updateTables();

        Assertions.assertEquals(0, Database.entries(Database.actual), "La tabla se debe quedar vacía");
    }

    @Test
    void testUpdate(){
        Database.updateTables();
        Tabla t = Database.actual;
        Assertions.assertEquals(0, Database.entries(t), "La tabla vacía tiene 0 entradas");

        List<String> l = new ArrayList<>();
        for(int i = 0; i < 26; i++){l.add("1");}
        Database.add(t, l);

        l = new ArrayList<>();
        for(int i = 0; i < 26; i++){l.add("2");}

        Database.updateTables();

        Database.update(Database.actual, l, Database.actual.columnas().getFirst().valores().getFirst());
        Database.updateTables();

        Assertions.assertEquals(Database.actual.columnas().getLast().valores().getLast(), "2", "El valor debe de haberse actualizado");
    }
}
