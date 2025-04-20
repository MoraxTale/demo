package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreasureController {

    @FXML
    private ListView<TreasureData> treasureListView; // 必须指定泛型类型
    private Controller mainController;
    private final Map<String, TreasureData> treasures = new LinkedHashMap<>();

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
        updateTreasureDisplay();
    }

    public Map<String, TreasureData> getTreasures() {
        return treasures;
    }

    public void setTreasures(Map<String, TreasureData> newTreasures) {
        treasures.clear();
        treasures.putAll(newTreasures);
        updateTreasureDisplay();
    }

    @FXML
    public void initialize() {
        // 自定义Cell工厂
        treasureListView.setCellFactory(lv -> new ListCell<TreasureData>() {
            @Override
            protected void updateItem(TreasureData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(5);
                    // 显示名称、等级和效果
                    Label nameLabel = new Label(String.format("【%s】Lv.%d", item.getName(), item.getLevel()));
                    Label effectLabel = new Label("效果：" + item.getEffectDescription());
                    nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gold;");
                    effectLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e6d8a9;");

                    // 动态按钮状态
                    Button upgradeButton = new Button();
                    if (item.isMaxLevel()) {
                        upgradeButton.setText("已满级");
                        upgradeButton.setDisable(true);
                        upgradeButton.setStyle("-fx-opacity: 0.7;");
                    } else {
                        // 修改按钮文本，添加当前法宝等级
                        upgradeButton.setText(String.format("升级 (Lv.%d, 消耗 %d 灵气)", item.getLevel(), item.getUpgradeCost()));
                        // 修改升级按钮样式为与购买按钮相同
                        upgradeButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
                        upgradeButton.setOnAction(e -> {
                            item.upgrade(mainController);
                            updateTreasureDisplay(); // 刷新列表
                            mainController.updateActualSuccessRate();
                        });
                    }

                    vbox.getChildren().addAll(nameLabel, effectLabel, upgradeButton);
                    setGraphic(vbox);
                }
            }
        });
    }


    @FXML
    private void handleUpgrade() {
        TreasureData selected = treasureListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (mainController.deductQi(selected.getUpgradeCost())) {
                selected.upgrade(mainController);
                updateTreasureDisplay();
                mainController.applyTreasureEffects(); // 确保应用最新效果
                mainController.updateActualSuccessRate();
            } else {
                new Alert(Alert.AlertType.WARNING, "灵气不足，无法升级！").show();
            }
        }
    }

    // 更新法宝显示
    public void updateTreasureDisplay() {
        ObservableList<TreasureData> obtainedTreasures = FXCollections.observableArrayList();
        treasures.values().stream()
                .filter(t -> t.getLevel() > 0)
                .forEach(t -> {
                    System.out.println("[DEBUG] 刷新列表项：" + t.getId() + " Lv." + t.getLevel()); // 调试输出
                    obtainedTreasures.add(t);
                });
        treasureListView.setItems(obtainedTreasures);
        treasureListView.refresh(); // 强制刷新ListView
    }
}