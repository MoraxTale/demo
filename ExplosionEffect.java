package com.example.demo1;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ExplosionEffect extends Circle {
    public ExplosionEffect(double centerX, double centerY) {
        super(centerX, centerY, 0);
        setFill(javafx.scene.paint.Color.rgb(255, 100, 100, 0.5));
        setEffect(new Glow(0.8));
    }

    public void playAnimation(Runnable onFinished) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(radiusProperty(), 0),
                        new KeyValue(opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(radiusProperty(), 150),
                        new KeyValue(opacityProperty(), 0.9)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(radiusProperty(), 0),
                        new KeyValue(opacityProperty(), 0)
                )
        );
        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
    }
}