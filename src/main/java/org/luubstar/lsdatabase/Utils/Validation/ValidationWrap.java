package org.luubstar.lsdatabase.Utils.Validation;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;


public class ValidationWrap {
    protected final Node node;
    protected final String message;
    Consumer<ValidationWrap> action;
    protected ValidationWrap(Node node, String message) {
        this.node = node;
        this.message = message;
    }

    protected ValidationWrap start(){
        node.addEventHandler(EventType.ROOT, this::handleEvent);
        return this;
    }

    private void handleEvent(Event event) {
        Tooltip tooltip = (Tooltip) node.getProperties().get("TOOLTIP");
        if(tooltip != null) {
            if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                activateTooltip();
            } else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
                hideTooltip();
            }
        }
        else{
            if(event.getEventType().equals(MouseEvent.MOUSE_RELEASED )){action.accept(this);}
        }
    }

    public void setAction(Consumer<ValidationWrap> action) {
        this.action = action;
    }

    private void activateTooltip(){
        Tooltip tooltip = (Tooltip) node.getProperties().get("TOOLTIP");
        Tooltip.install(node, tooltip);
        tooltip.show(node.getScene().getWindow());
    }
    private void hideTooltip(){
        Tooltip tooltip = (Tooltip) node.getProperties().get("TOOLTIP");
        Tooltip.uninstall(node, tooltip);
        tooltip.hide();
    }

    public void createTooltip(ValidationType type, String mensaje){
        ValidationTooltip tooltip = new ValidationTooltip(mensaje);
        Tooltip t = tooltip.getTip();

        if( node.getProperties().get("TOOLTIP") == null) {
            if (type.equals(ValidationType.ERROR)) {
                node.getStyleClass().add("wrap-error");
                t.getStyleClass().add("error-tooltip");
            } else if (type.equals(ValidationType.WARNING)) {
                node.getStyleClass().add("warning");
                t.getStyleClass().add("warning-tooltip");
            }
            node.getProperties().put("TOOLTIP", t);
        }
    }


}
