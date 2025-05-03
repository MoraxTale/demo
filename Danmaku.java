package com.example.demo1;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class Danmaku extends Label {
    private static final Font DANMAKU_FONT = Font.font("Microsoft YaHei", 16);
    private static final Color BASE_COLOR = Color.rgb(0, 255, 0);
    private static final Color CRIT_COLOR = Color.rgb(255, 215, 0);

    public Danmaku(String text, double x, double y, boolean isCritical) {
        super(text);
        setFont(DANMAKU_FONT);
        setTextFill(isCritical ? CRIT_COLOR : BASE_COLOR);
        setStyle("-fx-font-weight: bold; " +
                (isCritical ? "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 10, 0, 0, 0);" :
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,255,0,0.8), 5, 0, 0, 0);"));
        setLayoutX(x);
        setLayoutY(y);

        if (isCritical) {
            setScaleX(1.5);
            setScaleY(1.5);
        }

        setupAnimations();
    }

    private void setupAnimations() {
        // 向上移动动画
        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1.5), this);
        moveUp.setByY(-100);

        // 淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), this);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // 缩放动画（如果是暴击）
        if (getScaleX() > 1) {
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.5), this);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        }

        // 动画完成时移除自己
        ParallelTransition combo = new ParallelTransition(moveUp, fadeOut);
        combo.setOnFinished(e -> {
            if (getParent() != null) {
                ((javafx.scene.layout.Pane) getParent()).getChildren().remove(this);
            }
        });
        combo.play();
    }
}