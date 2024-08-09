package org.luubstar.lsdatabase.Utils.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record Columna(String nombre, boolean clavePrimaria, boolean noNulo, List<String> valores){

    static Columna newFromResultSet(ResultSet rs) throws SQLException {
        String columnName = rs.getString("name");
        boolean isPrimaryKey = rs.getInt("pk") > 0;
        boolean isNotNull = rs.getInt("notnull") > 0 || isPrimaryKey;

        return new Columna(columnName, isPrimaryKey, isNotNull, new ArrayList<>());
    }
}
