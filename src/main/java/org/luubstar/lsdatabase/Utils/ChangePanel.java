package org.luubstar.lsdatabase.Utils;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.util.Duration;
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
            loader.setController(PANEL_NOMBRES.get(i).controller);
            paneles[i] = loader.load();
            controllers[i] = PANEL_NOMBRES.get(i).controller;
            mainPane.getChildren().add(paneles[i]);
            paneles[i].setVisible(false);
        }
    }

    public static void changeContent(Panel panel){
        int index = PANEL_NOMBRES.indexOf(panel);
        if(MainController.index == -1){paneles[index].setVisible(true); MainController.index = index;}
        if(index == MainController.index){return; }

        for (Parent p : paneles){
            if (p != paneles[MainController.index]){p.setVisible(false); p.setOpacity(0f);}
        }

        FadeTransition fadeOut = getFadeTransition(index);
        fadeOut.play();

        navigatorController.readNotificacion();

        try{controllers[index].start();}
        catch (Exception e){logger.debug("El controlador {} no está asignado",PANEL_NOMBRES.get(index).toString());}
    }

    private static FadeTransition getFadeTransition(int index) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(125), paneles[MainController.index]);
        fadeOut.setFromValue(1.0f);
        fadeOut.setToValue(0.0f);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(125), paneles[index]);
        fadeIn.setFromValue(0.0f);
        fadeIn.setToValue(1.0f);

        fadeOut.setOnFinished(event -> {
            paneles[MainController.index].setVisible(false);
            paneles[index].setVisible(true);
            fadeIn.play();
        });

        fadeIn.setOnFinished(actionEvent -> {
            paneles[index].setOpacity(1f);
            paneles[MainController.index].setOpacity(1f);
            MainController.index = index;
        });

        return fadeOut;
    }

    public static SidePanel getController(Panel panel){
        int index = PANEL_NOMBRES.indexOf(panel);
        return controllers[index];
    }

    public static MainController getNavigator(){return navigatorController;}
}
