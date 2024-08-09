package org.luubstar.lsdatabase.Utils.Database;

import java.util.List;

public class Queries {
    protected static String searchQuery(Tabla t, String s){
        StringBuilder query = new StringBuilder("SELECT * FROM " + t.nombre() + " WHERE ");
        for (int i = 0; i < t.columnas().size(); i++) {
            if (!t.columnas().get(i).clavePrimaria()) {
                query.append('"').append(t.columnas().get(i).nombre()).append('"').append(" LIKE '%").append(s).append("%' OR ");
            }
        }
        query.setLength(query.length() - 4);
        return query.toString();
    }

    protected static String addQuery(Tabla t, List<String> valores){
        StringBuilder query = new StringBuilder("INSERT INTO ").append(t.nombre()).append(" (");

        for(Columna c : t.columnas()){
            if(!c.clavePrimaria()){query.append('"').append(c.nombre()).append('"').append(", ");}
        }
        query.setLength(query.length()-2);

        query.append(" ) VALUES (");

        for(String ignored : valores){
            query.append("?, ");
        }

        query.setLength(query.length()-2);
        query.append(")");
        return query.toString();
    }

    protected static String deleteQuery(Tabla t){
        return "DELETE FROM " + t.nombre() + " WHERE ID = ?";
    }

    protected static String updateQuery(Tabla t){
        StringBuilder query = new StringBuilder("UPDATE ").append(t.nombre()).append(" SET ");
        for (Columna c : t.columnas()) {
            if (!c.clavePrimaria()) {
                query.append('"').append(c.nombre()).append('"').append(" = ?, ");
            }
        }
        query.setLength(query.length() - 2);

        query.append(" WHERE ID = ? ");
        return query.toString();
    }

    protected static String dropQuery(Tabla t){return "DELETE FROM " + t.nombre();}

    protected static String columnMetadataQuery(Tabla t){return "PRAGMA table_info(" + t.nombre() + ")";}
}
