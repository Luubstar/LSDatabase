package org.luubstar.lsdatabase.Utils.Validation;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Validator {
    List<ValidationItem> nodes = new ArrayList<>();
    List<ValidationWrap> wraps = new ArrayList<>();
    Map<Node, List<ValidationMessage>> messages = new HashMap<>();
    public Validator(){}

    public Validator add(Node n, Consumer<Validator> v){
        nodes.add(new ValidationItem(n, v));
        n.addEventHandler(EventType.ROOT, this::handleEvent);
        n.setFocusTraversable(true);
        return this;
    }

    public ValidationWrap wrap(Node n, String m){
        ValidationWrap wrap = new ValidationWrap(n, m).start();
        nodes.add(new ValidationItem(n, v -> {}));
        wraps.add(wrap);
        return wrap;
    }

    public void logError(Node n, String e){
        addValue(messages, n, new ValidationMessage("❌ " + e, ValidationType.ERROR));
    }

    public void logAviso(Node n, String e){
        addValue(messages, n, new ValidationMessage("⚠ " + e, ValidationType.WARNING));
    }

    private void handleEvent(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED ||
                event.getEventType() == KeyEvent.KEY_PRESSED ||
                event.getEventType() == KeyEvent.KEY_RELEASED) {
            check();
        }
    }

    public void check(){
        messages.clear();
        for(ValidationItem i : nodes){
            i.v.accept(this);
        }
        clearNodes();
        for(Node k : messages.keySet()){
            addTooltip(k, messages.get(k));
        }
        checkWraps();
    }

    private <K, V> void addValue(Map<K, List<V>> map, K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    private void addTooltip(Node n,List<ValidationMessage> m){
        Collections.sort(m);
        String concatenatedMessages = m.stream()
            .map(ValidationMessage::getMessage)
            .collect(Collectors.joining("\n"));

        ValidationType type = m.getFirst().getType();
        ValidationTooltip tooltip = new ValidationTooltip(concatenatedMessages);
        Tooltip t = tooltip.getTip();

        if( n.getProperties().get("TOOLTIP") == null) {
            if (type.equals(ValidationType.ERROR)) {
                n.getStyleClass().add("error");
                n.getStyleClass().remove("warning");
                t.getStyleClass().add("error-tooltip");
            } else if (type.equals(ValidationType.WARNING)) {
                n.getStyleClass().add("warning");
                t.getStyleClass().add("warning-tooltip");
            }
            n.getProperties().put("TOOLTIP", t);
            Tooltip.install(n, t);
        }
    }

    private void clearNodes(){
        for(ValidationItem item : nodes){
            if(item.getN().getProperties().get("TOOLTIP") != null){
                item.getN().getStyleClass().remove("error");
                item.getN().getStyleClass().remove("warning");
                Tooltip.uninstall(item.getN(),(Tooltip) item.getN().getProperties().get("TOOLTIP"));
                ((Tooltip) item.getN().getProperties().get("TOOLTIP")).hide();
                item.getN().getProperties().put("TOOLTIP", null);
            }
        }
    }

    private void checkWraps(){
        List<ValidationMessage> mensajes = new ArrayList<>();
        boolean hayErrores = false;
        boolean hayAvisos = false;

        for(Node k : messages.keySet()){
            List<ValidationMessage> m = messages.get(k);
            for(ValidationMessage men : m){
                mensajes.add(men);
                if(men.getType().equals(ValidationType.ERROR)){hayErrores = true;}
                else if(men.getType().equals(ValidationType.WARNING)){hayAvisos = true;}
            }
        }

        if(hayAvisos || hayErrores) {
            Collections.sort(mensajes);
            String concatenatedMessages = mensajes.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining("\n"));
            for (ValidationWrap w : wraps) {
                w.createTooltip(mensajes.getFirst().getType(), concatenatedMessages);
            }
        }
    }

}
