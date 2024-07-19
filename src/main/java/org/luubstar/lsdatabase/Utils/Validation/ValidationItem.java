package org.luubstar.lsdatabase.Utils.Validation;

import javafx.scene.Node;

import java.util.function.Consumer;

public class ValidationItem {
    protected Node n;
    protected Consumer<Validator> v;

    protected ValidationItem(Node nodo, Consumer<Validator> func){
        n = nodo;
        v = func;
    }

    public Node getN() {
        return n;
    }

    public Consumer<Validator> getV() {
        return v;
    }
}
