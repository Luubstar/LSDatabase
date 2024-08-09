package org.luubstar.lsdatabase.Utils.Database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueriesTest {

    @BeforeEach
    void clear(){
        Database.disconect();
        Database.loadFile(Database.PLANTILLA);
        try{Database.start();}
        catch (Exception e){
            Assertions.fail("Error iniciando la base de datos");}
        Database.clear(Database.actual);
    }

    @Test
    void TestColumnMetadata(){
        String q = Queries.columnMetadataQuery(Database.actual);
        Assertions.assertEquals(q, "PRAGMA table_info(" + Database.actual.nombre() + ")");
    }
}
