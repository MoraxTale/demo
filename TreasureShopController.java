package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class TreasureShopController {
    @FXML
    private VBox vboxShop;

    private Controller mainController;
    private final Map<String, TreasureData> availableTreasures = new LinkedHashMap<>();

    public void setMainController(Controller controller) {
        this.mainController = controller;
        initializeShopItems();
    }

    private void initializeShopItems() {
        availableTreasures.clear();

        // 添加示例法宝
        availableTreasures.put("九转活水葫芦", new TreasureData("九转活水葫芦", "离线灵气能力", 1, 5000));
        availableTreasures.put("八方来财铜钱", new TreasureData("八方来财铜钱", "灵气+10%", 1, 8000));

        updateShopDisplay();
    }

    private void updateShopDisplay() {
        vboxShop.getChildren().clear();

        availableTreasures.forEach((name, data) -> {
            Button btn = new Button();
            btn.setStyle("-fx-font-size: 14; -fx-pref-width: 400; -fx-pref-height: 60;");
            btn.setText(String.format("%s\n效果：%s\n价格：%d灵气",
                    name, data.getEffect(), data.getNextUpgradeCost()));
            btn.setOnAction(e -> buyTreasure(name, data));
            vboxShop.getChildren().add(btn);
        });
    }

    private void buyTreasure(String name, TreasureData data) {
        if (mainController.deductQi(data.getNextUpgradeCost())) {
            mainController.addTreasureToBackpack(new TreasureData(data.getName(), data.getEffect(), 1, data.getNextUpgradeCost()));
            mainController.showInformation("成功购买", "您已成功购买：" + name);
        } else {
            mainController.showWarning("灵气不足", "无法购买：" + name);
        }
    }

    @FXML
    private void closeShopPanel() {
        ((Stage) vboxShop.getScene().getWindow()).close();
    }
}
