package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class TreasureShopController {
    private Controller mainController;

    @FXML
    private VBox vboxShop;

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        loadShopTreasures();
    }

    public void loadShopTreasures() {
        // 示例法宝数据，可以替换为动态加载数据
        String[] treasures = {"五禽戏秘籍", "法宝 B", "法宝 C"};
        // 法宝简介
        Map<String, String> treasureDescriptions = new HashMap<>();
        treasureDescriptions.put("五禽戏秘籍", "升级可提高每次点击的灵力数量");
        treasureDescriptions.put("法宝 B", "此法宝蕴含着古老的力量，可提升使用者的防御。");
        treasureDescriptions.put("法宝 C", "该法宝具有独特的属性，能帮助使用者洞察先机。");

        vboxShop.getChildren().clear();
        for (String treasure : treasures) {
            Button button = new Button(treasure);
            button.setStyle("-fx-font-size: 14; -fx-padding: 5;");
            button.setOnAction(e -> showTreasureDescription(treasure, treasureDescriptions.get(treasure)));
            vboxShop.getChildren().add(button);
        }
    }

    private void showTreasureDescription(String treasureName, String description) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("法宝简介");
        alert.setHeaderText(treasureName + " 简介");
        alert.setContentText(description);
        alert.showAndWait();
    }

    @FXML
    private void closeShopPanel() {
        vboxShop.getScene().getWindow().hide();
    }
}