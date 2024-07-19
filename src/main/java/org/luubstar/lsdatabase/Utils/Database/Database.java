package org.luubstar.lsdatabase.Utils.Database;

import java.io.File;
import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static String db;
    private static final String tableName = "Clientes";
    public static List<Tabla> tablas = new ArrayList<>();

    public static void start() throws InstantiationException {
        if(db == null){throw new InstantiationException("Database can't be null");}

        try (Connection conn = DriverManager.getConnection(db)) {
            if (conn != null) {
                //TODO: Ahora mismo solo lee la primera tabla
                Tabla t = Tabla.createEmpty(tableName);
                t.generateTable(conn);
                tablas.add(t);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadFile(String s) throws InvalidParameterException{
        File f = new File(s);
        if(f.exists() && f.isFile() && f.canRead()){db = "jdbc:sqlite:" + s;}
        else{throw new InvalidParameterException("Not a file " + f.getAbsolutePath());}
    }

    public static Tabla searchLike(Tabla original, String s) {
        try (Connection conn = DriverManager.getConnection(db)) {
            Statement stmt = conn.createStatement();

            if(s.isEmpty() || s.equals("*")){return Tabla.createEmpty(original.nombre()).generateTable(conn);}

            StringBuilder query = new StringBuilder("SELECT * FROM " + original.nombre() + " WHERE ");
            for(int i = 0; i < original.columnas().size(); i++){
                if(!original.columnas().get(i).clavePrimaria()) {
                    query.append('"').append(original.columnas().get(i).nombre()).append('"').append(" LIKE '%").append(s).append("%' OR ");
                }
            }
            query.setLength(query.length() - 4);

            ResultSet rs = stmt.executeQuery(query.toString());
            return (Tabla.createEmpty(original.nombre())).generateTable(conn, rs);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Tabla.createEmpty(original.nombre());
        }
    }

    public static void add(Tabla t, List<String> valores) {
        try (Connection conn = DriverManager.getConnection(db)) {
            StringBuilder query = new StringBuilder("INSERT INTO ").append(t.nombre()).append(" (");

            for(Columna c : t.columnas()){
                if(!c.clavePrimaria()){query.append('"').append(c.nombre()).append('"').append(", ");}
            }
            query.setLength(query.length()-2);

            query.append(" ) VALUES (");

            for(String v : valores){
                query.append("?, ");
            }

            query.setLength(query.length()-2);
            query.append(")");


            PreparedStatement stmt = conn.prepareStatement(query.toString());

            for(int i = 0; i < valores.size(); i++){
                stmt.setObject(i+1, valores.get(i));
            }

            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delete(Tabla t, String ID) {
        try (Connection conn = DriverManager.getConnection(db)) {

            String query = "DELETE FROM " + t.nombre() + " WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, ID);

            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void update(Tabla t, List<String> valores, String ID) {
        try (Connection conn = DriverManager.getConnection(db)) {

            StringBuilder query = new StringBuilder("UPDATE ").append(t.nombre()).append(" SET ");
            for(Columna c : t.columnas()){
                if(!c.clavePrimaria()){query.append('"').append(c.nombre()).append('"').append(" = ?, ");}
            }
            query.setLength(query.length()-2);

            query.append(" WHERE ID = ? ");

            PreparedStatement stmt = conn.prepareStatement(query.toString());
            for(int i = 0; i < valores.size(); i++){
                    stmt.setObject(i+1, valores.get(i));
            }

            stmt.setObject(valores.size()+1, ID);

            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
