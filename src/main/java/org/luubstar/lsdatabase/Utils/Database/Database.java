package org.luubstar.lsdatabase.Utils.Database;

import org.luubstar.lsdatabase.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static String databaseURL;
    private static final String tableName = "Clientes";
    public static List<Tabla> tablas = new ArrayList<>();

    private static Connection connectToDb() throws SQLException{
        try{return DriverManager.getConnection(databaseURL);}
        catch (SQLException e){logger.error("Error contectanse con la base de datos {}", databaseURL, e); throw e;}
    }

    public static void start() throws InstantiationException {
        if(databaseURL == null){throw new InstantiationException("Database can't be null");}
        //TODO: Ahora mismo solo lee la primera tabla
        try(Connection conn = connectToDb()) {
            Tabla t = Tabla.createEmpty(tableName);
            t.generateTable(conn);
            tablas.add(t);
        }
        catch (SQLException e){logger.error("Error en la inicialización", e); throw new InstantiationException();}
    }

    public static void loadFile(String s) throws InvalidParameterException{
        File f = new File(s);
        if(f.exists() && f.isFile() && f.canRead()){
            databaseURL = "jdbc:sqlite:" + s;}
        else{throw new InvalidParameterException("Not a file " + f.getAbsolutePath());}
    }

    public static Tabla searchLike(Tabla original, String s) {
        try (Connection conn = connectToDb()) {
            if (s.isEmpty() || s.equals("*")) {
                return Tabla.createEmpty(original.nombre()).generateTable(conn);
            }

            try (Statement stmt = conn.createStatement()) {
                String query = Queries.searchQuery(original, s);
                try (ResultSet rs = stmt.executeQuery(query)) {
                    return (Tabla.createEmpty(original.nombre())).generateTable(conn, rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando {} en la tabla {}.\nQuery -> {}", s, original.nombre(), Queries.searchQuery(original, s), e);
            return Tabla.createEmpty(original.nombre());
        }
    }

    public static void add(Tabla tabla, List<String> valores) {
        try (Connection conn = connectToDb()) {
            String query = Queries.addQuery(tabla, valores);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                for (int i = 0; i < valores.size(); i++) {
                    stmt.setObject(i + 1, valores.get(i));
                }
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error añadiendo {} en la tabla {}.\nQuery -> {}", valores, tabla.nombre(), Queries.addQuery(tabla, valores), e);}
    }

    public static void delete(Tabla tabla, String ID) {
        try (Connection conn = connectToDb()) {
            String query = Queries.deleteQuery(tabla);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, ID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error eliminando el objeto de ID {} de la tabla {}", ID, tabla.nombre(), e);}
    }

    public static void update(Tabla tabla, List<String> valores, String ID) {
        try (Connection conn = connectToDb()) {
            String query = Queries.updateQuery(tabla);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                for (int i = 0; i < valores.size(); i++) {
                    stmt.setObject(i + 1, valores.get(i));
                }
                stmt.setObject(valores.size() + 1, ID);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error actualizando la tabla {} en el ID {}.\nQuery -> {}", tabla.nombre(),
                    ID, Queries.updateQuery(tabla), e);
        }
    }
}
