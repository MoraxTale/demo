package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.LinkedHashMap;
import java.util.Map;

public class TreasureShopController {
    @FXML
    private VBox vboxShop; // 必须与 FXML 中的 fx:id="vboxShop" 一致

    private Controller mainController;
    private final Map<String, TreasureData> availableTreasures = new LinkedHashMap<>();

    // === 初始化方法 ===
    public void setMainController(Controller controller) {
        this.mainController = controller;
        initializeShopItems(); // 确保初始化数据
    }

    // === 核心修复：正确加载数据并更新界面 ===
    private void initializeShopItems() {
        // 清空旧数据避免重复
        availableTreasures.clear();

        // 添加示例法宝（确保数据存在）
        availableTreasures.put("九转活水葫芦",
                new TreasureData("九转活水葫芦", "获得离线灵气能力", 5000, 20000));
        availableTreasures.put("八方来财铜钱",
                new TreasureData("八方来财铜钱", "灵气获取+10%", 8000, 15000));

        // 强制更新界面
        updateShopDisplay();
    }

    // === 关键修复：正确生成按钮并添加到界面 ===
    private void updateShopDisplay() {
        vboxShop.getChildren().clear(); // 清空旧内容

        availableTreasures.forEach((name, data) -> {
            Button btn = new Button();
            // 设置按钮样式（确保可见）
            btn.setStyle("-fx-font-size: 14; -fx-pref-width: 400; -fx-pref-height: 60;");
            // 设置多行文本（必须用 \n 换行）
            btn.setText(String.format("%s\n效果：%s\n价格：%d灵气",
                    name, data.getEffect(), data.getPurchaseCost()));
            // 绑定点击事件
            btn.setOnAction(e -> buyTreasure(name, data));
            // 添加到布局
            vboxShop.getChildren().add(btn);
        });
    }

    // 购买逻辑（保持不变）
    private void buyTreasure(String name, TreasureData data) {
        if (mainController.deductQi(data.getPurchaseCost())) {
            mainController.addTreasureToBackpack(
                    new TreasureData(data.getName(), data.getEffect(), 0, data.getNextUpgradeCost())
            );
            new Alert(Alert.AlertType.INFORMATION, "成功购买 " + name).showAndWait();
        } else {
            new Alert(Alert.AlertType.WARNING, "灵气不足！").showAndWait();
        }
    }

    // 关闭界面
    @FXML
    private void closeShopPanel() {
        ((Stage) vboxShop.getScene().getWindow()).close();
    }
}
