package org.luubstar.lsdatabase.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import org.luubstar.lsdatabase.App;
import org.luubstar.lsdatabase.MainController;
import org.luubstar.lsdatabase.SidePanel;

import java.io.IOException;

public class ChangePanel {

    private static Parent[] paneles;
    private static SidePanel[] controllers;
    private static final Panels[] PANELS_NOMBRES = new Panels[]{Panels.DASHBOARD, Panels.BUSQUEDA, Panels.ANADIR,
            Panels.FACTURAR, Panels.RECORDATORIOS,};
    public static StackPane mainPane;
    public static MainController navigatorController;


    public static void init(StackPane mp,MainController c) throws IOException {
        navigatorController = c;
        mainPane = mp;
        paneles = new Pane[PANELS_NOMBRES.length];
        controllers = new SidePanel[PANELS_NOMBRES.length];
        for(int i = 0; i < PANELS_NOMBRES.length; i++){
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PANELS_NOMBRES[i].ruta + ".fxml"));
            paneles[i] = loader.load();
            controllers[i] = loader.getController();
            mainPane.getChildren().add(paneles[i]);
            paneles[i].setVisible(false);
        }
    }
    public static void changeContent(int index){
        if(MainController.index != index) {
           for(Parent p : paneles){p.setVisible(false);}
           paneles[index].setVisible(true);
           MainController.index = index;
           try{controllers[index].start();}
           catch (Exception e){System.out.println("Controlador no asignado " + e.getMessage());}
        }
    }

    public static SidePanel getController(int index){return controllers[index];}
    public static MainController getNavigator(){return navigatorController;}
}
