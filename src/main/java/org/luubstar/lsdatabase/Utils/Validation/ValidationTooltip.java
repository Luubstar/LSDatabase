package org.luubstar.lsdatabase.Utils.Validation;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class ValidationTooltip extends Tooltip {

    Tooltip tip;
    public ValidationTooltip(String message){
        Tooltip t = new Tooltip(message);
        t.setShowDelay(Duration.millis(600));
        t.setShowDuration(Duration.millis(10000));
        t.setHideDelay(Duration.millis(200));
        tip = t;
    }

    public Tooltip getTip(){return tip;}
}
