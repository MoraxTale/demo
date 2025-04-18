package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.LinkedHashMap;
import java.util.Map;

public class TreasureController {
    @FXML private VBox vboxTreasures;
    private Controller mainController;
    private Map<String, TreasureData> treasures = new LinkedHashMap<>();

    // === 核心方法 ===

    public void setMainController(Controller controller) {
        this.mainController = controller;
        initializeTreasures();
    }

    // 初始化法宝列表
    private void initializeTreasures() {
        if (treasures.isEmpty()) {
            treasures.put("九转活水葫芦", new TreasureData("九转活水葫芦", "离线灵气能力", 5000, 20000));
            treasures.put("八方来财铜钱", new TreasureData("八方来财铜钱", "灵气+10%", 8000, 15000));
        }
        updateTreasureDisplay();
    }

    // 更新界面显示（修改为 public）
    public void updateTreasureDisplay() {
        vboxTreasures.getChildren().clear();
        treasures.forEach((name, data) -> {
            Button btn = new Button();
            btn.setStyle("-fx-font-size: 14; -fx-pref-width: 400;");
            btn.setText(String.format("%s Lv.%d\n效果：%s\n升级需要：%d灵气",
                    name, data.getLevel(), data.getEffect(), data.getNextUpgradeCost()));
            btn.setOnAction(e -> upgradeTreasure(name, data));
            vboxTreasures.getChildren().add(btn);
        });
    }

    // 法宝升级逻辑
    private void upgradeTreasure(String name, TreasureData data) {
        if (mainController.deductQi(data.getNextUpgradeCost())) {
            data.upgrade();
            applyTreasureEffect(name);
            updateTreasureDisplay();
        } else {
            new Alert(Alert.AlertType.WARNING, "灵气不足！").showAndWait();
        }
    }

    // 应用法宝效果
    private void applyTreasureEffect(String name) {
        switch (name) {
            case "八方来财铜钱":
                mainController.increaseQiRate(0.1 * treasures.get(name).getLevel());
                break;
        }
    }

    // === 数据访问方法 ===
    public Map<String, TreasureData> getTreasures() {
        return treasures;
    }

    public void setTreasures(Map<String, TreasureData> treasures) {
        this.treasures = treasures;
        updateTreasureDisplay();
    }

    // 关闭界面
    @FXML
    private void closeTreasurePanel() {
        ((Stage) vboxTreasures.getScene().getWindow()).close();
    }
}
