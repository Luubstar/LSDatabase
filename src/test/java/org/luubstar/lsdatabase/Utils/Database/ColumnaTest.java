package org.luubstar.lsdatabase.Utils.Database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    
}
