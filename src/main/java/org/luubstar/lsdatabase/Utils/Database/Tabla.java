package org.luubstar.lsdatabase.Utils.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Tabla(List<Columna> columnas, String nombre) {

    public void createColumns(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + nombre + ")");
        while (rs.next()) {columnas.add(Columna.newFromResultSet(rs));}
    }

    public Tabla generateTable(Connection conn) throws SQLException {
        createColumns(conn);

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + nombre);
        while(rs.next()){
            for(Columna c: columnas){c.valores().add(rs.getString(c.nombre()));}
        }
        Collections.sort(columnas);
        return this;
    }

    public Tabla generateTable(Connection conn, ResultSet rs) throws SQLException {
        createColumns(conn);
        while(rs.next()){
            for(Columna c: columnas){c.valores().add(rs.getString(c.nombre()));}
        }
        return this;
    }

    public static Tabla createEmpty(String name){return new Tabla(new ArrayList<>(), name);}

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Cantidad de columas: ").append(columnas.size()).append("\nValores:\n");
        for (Columna c : columnas) {
            res.append(c).append("\n");
        }
        return res.toString();
    }
}
