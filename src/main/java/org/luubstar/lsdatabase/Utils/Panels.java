package org.luubstar.lsdatabase.Utils;

public enum Panels {
    DASHBOARD("Dashboard"), BUSQUEDA("Busqueda"), ANADIR("Añadir"),
    FACTURAR("Facturar"), RECORDATORIOS("Recordatorios");

    final String ruta;
    Panels(String r){
        this.ruta = r;
    }
}
