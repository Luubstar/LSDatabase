package org.luubstar.lsdatabase.Utils;

import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class AnimateButton {

    public static void animateButton(Button button){
        ScaleTransition hoverAnimation = new ScaleTransition(Duration.millis(200), button);
        hoverAnimation.setToX(1.1);
        hoverAnimation.setToY(1.1);

        ScaleTransition hoverBackAnimation = new ScaleTransition(Duration.millis(200), button);
        hoverBackAnimation.setToX(1.0);
        hoverBackAnimation.setToY(1.0);

        ScaleTransition clickAnimation = new ScaleTransition(Duration.millis(100), button);
        clickAnimation.setToX(0.9);
        clickAnimation.setToY(0.9);

        ScaleTransition clickBackAnimation = new ScaleTransition(Duration.millis(100), button);
        clickBackAnimation.setToX(1.0);
        clickBackAnimation.setToY(1.0);


        EventHandler<MouseEvent> originalMousePressed = (EventHandler<MouseEvent>) button.getOnMousePressed();
        button.setOnMousePressed(event -> {
            clickAnimation.playFromStart();
            if (originalMousePressed != null) {
                originalMousePressed.handle(event);
            }
        });

        EventHandler<MouseEvent> originalMouseReleased = (EventHandler<MouseEvent>) button.getOnMouseReleased();
        button.setOnMouseReleased(event -> {
            clickBackAnimation.playFromStart();
            clickBackAnimation.setOnFinished(e -> {
                if (button.isHover()) {
                    hoverAnimation.playFromStart();
                }
            });
            if (originalMouseReleased != null) {
                originalMouseReleased.handle(event);
            }
        });

        EventHandler<MouseEvent> originalMouseEntered = (EventHandler<MouseEvent>) button.getOnMouseEntered();
        button.setOnMouseEntered(event -> {
            hoverAnimation.playFromStart();
            if (originalMouseEntered != null) {
                originalMouseEntered.handle(event);
            }
        });

        EventHandler<MouseEvent> originalMouseExited = (EventHandler<MouseEvent>) button.getOnMouseExited();
        button.setOnMouseExited(event -> {
            hoverBackAnimation.playFromStart();
            if (originalMouseExited != null) {
                originalMouseExited.handle(event);
            }
        });
    }

}
