package org.luubstar.lsdatabase.Utils.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record Columna(String nombre, boolean clavePrimaria, boolean noNulo, List<String> valores) implements Comparable<Columna>{

    public static Columna newFromResultSet(ResultSet rs) throws SQLException {
        String columnName = rs.getString("name");
        boolean isPrimaryKey = rs.getInt("pk") > 0;
        boolean isNotNull = rs.getInt("notnull") > 0 || isPrimaryKey;

        return new Columna(columnName, isPrimaryKey, isNotNull, new ArrayList<>());
    }

    @Override
    public String toString() {
        return "[ " + nombre + "| pk? " + clavePrimaria + "| nn? " + noNulo + " ]";
    }

    @Override
    public int compareTo(Columna o) {
        int v = this.clavePrimaria ? 1 : 0;
        int ov = o.clavePrimaria ? 1 : 0;
        return  ov - v;
    }
}
