package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.util.*;

public class TreasureShopController {
    private Controller mainController;
    private boolean initialized = false;
    @FXML
    private VBox vboxShop;

    public void setMainController(Controller mainController) {
        System.out.println("[DEBUG] 主控制器设置: " + (mainController != null));
        this.mainController = mainController;

        // 延迟初始化商店数据
        if (!initialized) {
            loadShopTreasures();
            initialized = true;
        }
    }

    @FXML
    private void initialize() {
        vboxShop.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        // 移除 loadShopTreasures() 的调用
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
                        5.0
                )
        );
        // 添加空值检查

        shopTreasures.forEach(treasure -> {
            System.out.println("[DEBUG] 商店法宝 ID: " + treasure.getId());
            Button btn = new Button();
            btn.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: #e6d8a9; -fx-font-size: 14px;");
            btn.setText(buildButtonText(treasure));

            // 检查是否已经拥有该法宝
            if (mainController.hasTreasure(treasure)) {
                btn.setText(treasure.getName() + " 已拥有");
                btn.setDisable(true);
            } else {
                btn.setOnAction(e -> showTreasureDetail(treasure));
            }

            btn.setMaxWidth(Double.MAX_VALUE);
            vboxShop.getChildren().add(btn);
        });
    }
    private String buildButtonText(TreasureData treasure) {
        return String.format(
                "【%s】\n%s\n获取方式：%s",
                treasure.getName(),
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
                    mainController.addTreasureToBackpack(treasure);
                    new Alert(Alert.AlertType.INFORMATION, "购买成功！").show();
                    // 购买成功后更新商店界面
                    loadShopTreasures();
                } else {
                    new Alert(Alert.AlertType.WARNING, "灵气不足！").show();
                }
            }
        });
    }
    private String convertEffectType(String type) {
        switch (type) {
            case "CLICK_BONUS": return "点击加成";
            case "AUTO_RATE": return "自动增速";
            case "SUCCESS_RATE": return "渡劫成功率";
            default: return "未知类型";
        }
    }

    @FXML
    private void closeShopPanel() {
        vboxShop.getScene().getWindow().hide();
    }
}
