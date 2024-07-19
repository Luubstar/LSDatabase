package org.luubstar.lsdatabase.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import org.luubstar.lsdatabase.App;
import org.luubstar.lsdatabase.MainController;
import org.luubstar.lsdatabase.SidePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ChangePanel {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static Parent[] paneles;
    private static SidePanel[] controllers;
    private static final List<Panel> PANEL_NOMBRES = Arrays.asList(Panel.DASHBOARD, Panel.BUSQUEDA, Panel.ANADIR,
                                                     Panel.FACTURAR, Panel.RECORDATORIOS);
    public static StackPane mainPane;
    public static MainController navigatorController;

    public static void init(StackPane mp, MainController c) throws IOException {
        navigatorController = c;
        mainPane = mp;
        paneles = new Pane[PANEL_NOMBRES.size()];
        controllers = new SidePanel[PANEL_NOMBRES.size()];
        for(int i = 0; i < PANEL_NOMBRES.size(); i++){
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PANEL_NOMBRES.get(i).ruta + ".fxml"));
            paneles[i] = loader.load();
            controllers[i] = loader.getController();
            mainPane.getChildren().add(paneles[i]);
            paneles[i].setVisible(false);
        }
    }

    public static void changeContent(Panel panel){
        int index = PANEL_NOMBRES.indexOf(panel);
        if(MainController.index != index) {
           for(Parent p : paneles){p.setVisible(false);}
           paneles[index].setVisible(true);
           MainController.index = index;
           try{controllers[index].start();}
           catch (Exception e){logger.debug("El controlador {} no está asignado",PANEL_NOMBRES.get(index).toString());}
        }
    }

    public static SidePanel getController(Panel panel){
        int index = PANEL_NOMBRES.indexOf(panel);
        return controllers[index];
    }

    public static MainController getNavigator(){return navigatorController;}
}
