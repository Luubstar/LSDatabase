package org.luubstar.lsdatabase.Utils.Database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.util.List;

public class ColumnaTest {

    @Test
    void testConstructor(){
        Columna c = new Columna("Test", true, true, List.of(new String[]{"1"}));

        Assertions.assertNotNull(c);
        Assertions.assertEquals("Test", c.nombre());
        Assertions.assertTrue(c.clavePrimaria() && c.noNulo());
        Assertions.assertEquals(1, c.valores().size());
        Assertions.assertEquals("1", c.valores().getFirst());
    }

    @Test
    void testMockedResultSet(){
        try(ResultSet r = Mockito.mock(ResultSet.class)){

            Mockito.when(r.getString("name")).thenReturn("Test");
            Mockito.when(r.getInt("pk")).thenReturn(1);
            Mockito.when(r.getInt("notnull")).thenReturn(0);
            Columna c = Columna.newFromResultSet(r);

            Assertions.assertNotNull(c);
            Assertions.assertEquals("Test", c.nombre());
            Assertions.assertTrue(c.clavePrimaria() && c.noNulo());
            Assertions.assertEquals(0, c.valores().size());

            Mockito.when(r.getInt("pk")).thenReturn(0); // Caso donde isPrimaryKey es false
            Mockito.when(r.getInt("notnull")).thenReturn(1); // Caso donde isNotNull es true por isNotNull directo

            c = Columna.newFromResultSet(r);
            Assertions.assertEquals("Test", c.nombre());
            Assertions.assertFalse(c.clavePrimaria() && c.noNulo());
            Assertions.assertEquals(0, c.valores().size());
        }
        catch (Exception e){Assertions.fail("Fallo al realizar el mock ", e);}
    }
}
