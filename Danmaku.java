package com.example.demo;

import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Danmaku extends Label {
    public Danmaku(String text, double x, double y) {
        super(text);
        setStyle("-fx-text-fill: #FF0000; -fx-font-size: 20px; -fx-font-weight: bold;");

        // 设置弹幕的初始位置
        setLayoutX(x);
        setLayoutY(y);

        // 创建弹幕向上移动的动画
        TranslateTransition transition = new TranslateTransition(Duration.seconds(5), this);
        transition.setToY(-200); // 向上移动200像素，持续5秒
        transition.setAutoReverse(false);
        transition.setCycleCount(1);

        // 动画结束后移除弹幕
        transition.setOnFinished(event -> {
            if (getParent() != null) {
                ((Pane) getParent()).getChildren().remove(this);
            }
        });
        transition.play();
    }
}