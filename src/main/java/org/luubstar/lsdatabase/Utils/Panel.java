package org.luubstar.lsdatabase.Utils;

public enum Panel {
    DASHBOARD("Dashboard"), BUSQUEDA("Busqueda"), ANADIR("Añadir"),
    FACTURAR("Facturar"), RECORDATORIOS("Recordatorios");

    final String ruta;
    Panel(String r){
        this.ruta = r;
    }
}
