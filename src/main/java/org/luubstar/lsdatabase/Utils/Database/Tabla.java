package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public record Tabla(List<Columna> columnas, String nombre) {
    private static final Logger logger = LoggerFactory.getLogger(Tabla.class);

    public void createColumns(Connection conn) throws SQLException {
        try{
            if(conn == null){return;}
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(Queries.columnMetadataQuery(this));
            while (rs.next()) {columnas.add(Columna.newFromResultSet(rs));}
        }
        catch (SQLException e){logger.error("Error generando las columnas de la tabla {}", nombre, e); throw e;}
    }

    public Tabla generateTable(Connection conn){
        try {
            createColumns(conn);

            try(Statement stmt = conn.createStatement()){
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + nombre);
                while (rs.next()) {
                    for (Columna c : columnas) {
                        c.valores().add(rs.getString(c.nombre()));
                    }
                }
                return this;
            }
        }
        catch (Exception e){logger.error("Error generando la tabla {}", this.nombre, e); }
        return null;
    }

    public Tabla generateTable(Connection conn, ResultSet rs) throws SQLException {
        createColumns(conn);
        while(rs.next()){
            for(Columna c: columnas){c.valores().add(rs.getString(c.nombre()));}
        }
        return this;
    }

    public static Tabla createEmpty(String name){return new Tabla(new ArrayList<>(), name);}
}
