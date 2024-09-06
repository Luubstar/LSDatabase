package org.luubstar.lsdatabase.Utils;

import org.luubstar.lsdatabase.*;

public enum Panel {
    DASHBOARD("Dashboard", new DashboardController()), BUSQUEDA("Busqueda", new SearchController()),
    ANADIR("Añadir", new AddController()), FACTURAR("Facturar", new FacturarController()),
    RECORDATORIOS("Recordatorios", new RecordatoriosController());

    public final String ruta;
    public final SidePanel controller;

    Panel(String r, SidePanel c){
        this.ruta = r;
        this.controller = c;
    }
}
