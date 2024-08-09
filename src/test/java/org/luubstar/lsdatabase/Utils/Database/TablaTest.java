package org.luubstar.lsdatabase.Utils.Database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TablaTest {
    @Test
    void testSQLErrorWhenNullConnection(){
        Tabla t = Tabla.createEmpty("");

        Assertions.assertNull(t.generateTable(null));
    }
    @Test
    void testSQLErrorWhenBadTableName(){
        Database.loadFile(Database.PLANTILLA);
        try{Database.start();}catch (Exception e){Assertions.fail("Error iniciando base de datos");}
        Database.clear(Database.actual);
        Database.updateTables();

        Tabla t = Tabla.createEmpty("");
        Assertions.assertEquals(t.columnas().size(), 0, "La tabla estará vacía");
    }
}
