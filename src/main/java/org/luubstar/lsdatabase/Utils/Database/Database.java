package org.luubstar.lsdatabase.Utils.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Generated;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static String databaseURL;
    private static final String tableName = "Clientes";
    public static Tabla actual;
    public static final String DEFAULT = "base.db";
    static File actualFile;
    static final String PLANTILLA = "src/main/resources/org/luubstar/lsdatabase/Utils/defaultBase.db";

    @Generated("Constructor privado")
    private Database(){}

    public static void loadFile(String s) {
        actualFile = new File(s);
        if(actualFile.exists() && actualFile.isFile() && actualFile.canRead()){
            databaseURL = "jdbc:sqlite:" + s;}
        else{
            File f = new File(PLANTILLA);
            try{Files.copy(f.toPath(),Path.of("./" + s));}
            catch (Exception e){logger.error("Error copiando el fichero ",e);}
        }
    }

    static void disconect(){
        actualFile = null;
        actual = null;
        databaseURL = null;
    }

    public static void updateTables(){
        try(Connection conn = connectToDb()) {
            Tabla t = Tabla.createEmpty(tableName);
            t.generateTable(conn);
            actual = t;
        }
        catch (Exception e){logger.error("Error en la inicialización", e);}
    }

    static Connection connectToDb() throws SQLException{
        try{return DriverManager.getConnection(databaseURL);}
        catch (SQLException e){logger.error("Error contectanse con la base de datos {}", databaseURL, e); throw e;}
    }

    public static void start() throws InstantiationException {
        if(databaseURL == null){throw new InstantiationException("Database can't be null");}
        updateTables();
        try{Backup.makeBackup();}
        catch (Exception e){logger.error("Error creando el backup ", e);}
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

    public static void clear(Tabla tabla){
        try (Connection conn = connectToDb()) {
            String query = Queries.dropQuery(tabla);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.executeUpdate();
            }

            query = "DELETE FROM sqlite_sequence WHERE name='" + tabla.nombre()+"'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {logger.error("Error dropeando la tabla ", e);}
    }

    public static int entries(Tabla tab){
        try {
            if (tab.columnas().isEmpty()) {
                return 0;
            }
            return tab.columnas().getFirst().valores().size();
        }
        catch (Exception e){logger.error("Error en el calculo de entradas ", e);}

        return -1;
    }
}
