package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.*;

public class TreasureShopController {
    private Controller mainController;
    private boolean initialized = false;
    @FXML
    private VBox vboxShop;

    public void setMainController(Controller mainController) {
        System.out.println("[DEBUG] 主控制器设置: " + (mainController != null));
        this.mainController = mainController;
        initializeShopItems(loadAllTreasures()); // 加载所有法宝数据

        // 延迟初始化商店数据
        if (!initialized) {
            loadShopTreasures();
            initialized = true;
        }
    }

    private List<TreasureData> loadAllTreasures() {
        // 返回所有法宝数据的列表（示例）
        return Arrays.asList();
    }


    @FXML
    private void initialize() {
        vboxShop.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        // 移除 loadShopTreasures() 的调用
    }

    // 初始化法宝商店时动态生成按钮
    public void initializeShopItems(List<TreasureData> treasures) {
        vboxShop.getChildren().clear();
        for (TreasureData treasure : treasures) {
            Button btn = new Button();
            btn.setText(treasure.getName());
            btn.setUserData(treasure);
            btn.getStyleClass().add("treasure-button");

            // 添加 Tooltip（无论是否已购买）
            Tooltip tooltip = new Tooltip();
            tooltip.setText(formatTooltipText(treasure));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(btn, tooltip); // 直接绑定 Tooltip

            // 已购买状态处理
            if (mainController.hasTreasure(treasure)) {
                if (treasure.isMaxLevel()) {
                    btn.setText(treasure.getName() + " (已满级)");
                    btn.setDisable(true);
                } else {
                    btn.setText(treasure.getName() + " (升级)");
                    btn.setStyle("-fx-background-color: #2196F3;");
                    btn.setOnAction(e -> {
                        if (mainController.deductQi(treasure.getUpgradeCost())) {
                            treasure.upgrade(mainController);
                            new Alert(Alert.AlertType.INFORMATION, "升级成功！").show();
                            loadShopTreasures(); // 重新加载商店界面
                            mainController.applyTreasureEffects(); // 更新效果
                        } else {
                            new Alert(Alert.AlertType.WARNING, "灵气不足，无法升级！").show();
                        }
                    });
                }
            } else {
                btn.setStyle("-fx-background-color: #FF5722;");
                btn.setOnAction(e -> showTreasureDetail(treasure));
            }

            vboxShop.getChildren().add(btn);
        }
    }

    // 生成提示文本
    private String formatTooltipText(TreasureData treasure) {
        String effectDesc;
        if ("AUTO_RATE".equals(treasure.getEffectType())) {
            effectDesc = String.format("修炼速度+%.1f%%", treasure.getEffectPercentage() * 100);
        } else {
            effectDesc = treasure.getEffectDescription();
        }

        return String.format(
                "【%s】Lv.%d/%d\n效果：%s\n描述：%s\n升级需要：%d灵气\n%s",
                treasure.getName(),
                treasure.getLevel(),
                treasure.getMaxLevel(),
                effectDesc,
                treasure.getDescription(),
                treasure.getUpgradeCost(),
                mainController.hasTreasure(treasure) ? "已拥有" : "未购买"
        );
    }


    public void loadShopTreasures() {
        if (mainController == null) {
            System.err.println("主控制器未初始化！");
            return;
        }

        vboxShop.getChildren().clear();
        List<TreasureData> shopTreasures = Arrays.asList(
                new TreasureData(
                        "WQX001",
                        "五禽戏秘籍",
                        "华佗所创养生功法，可强身健体",
                        "初始赠送",
                        500,
                        "CLICK_BONUS",
                        1000
                ),
                new TreasureData(
                        "JX002",
                        "聚灵阵图",
                        "可凝聚天地灵气的上古阵图",
                        "商店购买",
                        1000,
                        "AUTO_RATE",
                        0.10
                ),
                new TreasureData(
                        "SJ004",
                        "时空塔",
                        "可延长修炼时间的逆天法宝",
                        "商店购买",
                        2000,
                        "OFFLINE_TIME",
                        60 // 基础加60秒
                )
        );
        // 添加空值检查
        shopTreasures.forEach(treasure -> {
            // 获取实际存在的法宝对象（如果有）
            TreasureData actualTreasure = mainController.getTreasureController().getTreasures()
                    .getOrDefault(treasure.getId(), treasure);

            System.out.println("[DEBUG] 商店法宝 ID: " + treasure.getId());
            Button btn = new Button();
            btn.setUserData(actualTreasure); // 绑定实际对象
            btn.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: #e6d8a9; -fx-font-size: 14px;");
            btn.setText(buildButtonText(actualTreasure));

            // 添加 Tooltip
            Tooltip tooltip = new Tooltip();
            tooltip.setText(formatTooltipText(actualTreasure));
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(btn, tooltip);

            // 检查是否已经拥有该法宝
            if (mainController.hasTreasure(actualTreasure)) {
                if (treasure.isMaxLevel()) {
                    btn.setText(actualTreasure.getName() + " (已满级)");
                    btn.setDisable(true);
                } else {
                    btn.setText(actualTreasure.getName() + " (Lv." + actualTreasure.getLevel() + " 升级)");
                    btn.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: #e6d8a9; -fx-font-size: 14px;");
                    btn.setOnAction(e -> {
                        if (mainController.deductQi(actualTreasure.getUpgradeCost())) {
                            actualTreasure.upgrade(mainController); // 操作实际对象
                            loadShopTreasures();
                            new Alert(Alert.AlertType.INFORMATION,
                                    "升级成功！当前效果：" + actualTreasure.getEffectDescription()).show();
                        } else {
                            new Alert(Alert.AlertType.WARNING, "灵气不足！").show();
                        }
                    });
                }
            } else {
                btn.setOnAction(e -> showTreasureDetail(treasure));
            }

            btn.setMaxWidth(Double.MAX_VALUE);
            vboxShop.getChildren().add(btn);
        });
    }

    private String buildButtonText(TreasureData treasure) {
        return String.format(
                "【%s】Lv.%d\n%s\n获取方式：%s",
                treasure.getName(),
                treasure.getLevel(), // 显示当前等级
                treasure.getDescription(),
                treasure.getAcquisitionMethod()
        );
    }

    private void showTreasureDetail(TreasureData treasure) {
        if (mainController == null) {
            new Alert(Alert.AlertType.ERROR, "系统错误：控制器未初始化").show();
            return;
        }
        // 再次检查是否已经拥有该法宝
        if (mainController.hasTreasure(treasure)) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(treasure.getName());
        alert.setHeaderText("是否购买此法宝？");
        alert.setContentText(
                "效果：" + treasure.getEffectDescription() + "\n" +
                        "价格：" + treasure.getUpgradeCost() + "灵气"
        );

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (mainController.deductQi(treasure.getUpgradeCost())) {
                    // 创建新的法宝实例添加到背包
                    TreasureData newTreasure = new TreasureData(
                            treasure.getId(),
                            treasure.getName(),
                            treasure.getDescription(),
                            treasure.getAcquisitionMethod(),
                            treasure.getUpgradeCost(),
                            treasure.getEffectType(),
                            treasure.getEffectPercentage()
                    );
                    mainController.addTreasureToBackpack(newTreasure);
                    new Alert(Alert.AlertType.INFORMATION, "购买成功！").show();
                    // 购买成功后更新商店界面
                    loadShopTreasures();
                } else {
                    new Alert(Alert.AlertType.WARNING, "灵气不足！").show();
                }
            }
        });
    }

    @FXML
    private void closeShopPanel() {
        vboxShop.getScene().getWindow().hide();
    }
}