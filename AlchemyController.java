package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class AlchemyController {
    @FXML
    private GridPane gridPills;
    private Controller mainController;
    private Map<String, PillData> pills = new LinkedHashMap<>();
    private List<PillConfig> customPills = new ArrayList<>();

    // 丹药配置类（包含唯一ID）
    public static class PillConfig {
        private final String pillId;
        private final String name;
        private final int cost;
        private final double rate;
        private final double successRateImpact;

        public PillConfig(String pillId, String name, int cost, double rate, double successRateImpact) {
            this.pillId = pillId;
            this.name = name;
            this.cost = cost;
            this.rate = rate;
            this.successRateImpact = successRateImpact;
        }

        public String getPillId() { return pillId; }
        public String getName() { return name; }
        public int getCost() { return cost; }
        public double getRate() { return rate; }
        public double getSuccessRateImpact() { return successRateImpact; }
    }

    // 丹药数据类（包含唯一ID和名称）
    public static class PillData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        String pillId;
        String pillName;
        int cost;
        double rate;
        double successRateImpact;
        int count = 0;

        public PillData(String pillId, String pillName, int cost, double rate, double successRateImpact) {
            this.pillId = pillId;
            this.pillName = pillName;
            this.cost = cost;
            this.rate = rate;
            this.successRateImpact = successRateImpact;
        }
    }

    public void setMainController(Controller controller) {
        this.mainController = controller;
        initializeCustomPills();
    }

    // 初始化自定义丹药（示例数据）
    private void initializeCustomPills() {
        customPills.clear();
        customPills.add(new PillConfig(
                "pill_001", "九转金丹", 1500, 3.0, 0.02
        ));
        customPills.add(new PillConfig(
                "pill_002", "太乙神丹", 800, 1.5, 0.08
        ));
    }

    // 加载丹药逻辑
    public void loadPillsOnClick() {
        if (mainController != null && !mainController.getSavedPills().isEmpty()) {
            loadSavedPills();
        } else {
            generateNewPills();
        }
        updatePillDisplay();
    }

    private void loadSavedPills() {
        pills.clear();
        mainController.getSavedPills().forEach((id, data) -> {
            PillData copiedData = new PillData(
                    data.pillId, data.pillName, data.cost, data.rate, data.successRateImpact
            );
            copiedData.count = data.count;
            pills.put(id, copiedData);
        });
    }

    private void generateNewPills() {
        pills.clear();
        int index = 0;
        for (PillConfig config : customPills) {
            if (index >= 25) break;
            pills.put(config.getPillId(), new PillData(
                    config.getPillId(),
                    config.getName(),
                    config.getCost(),
                    config.getRate(),
                    config.getSuccessRateImpact()
            ));
            index++;
        }
    }

    // 更新5x5界面
    private void updatePillDisplay() {
        gridPills.getChildren().clear();
        int index = 0;
        for (Map.Entry<String, PillData> entry : pills.entrySet()) {
            if (index >= 25) break;
            int row = index / 5;
            int col = index % 5;
            PillData data = entry.getValue();

            Button btn = new Button();
            btn.setStyle("-fx-font-size: 12; -fx-pref-width: 150; -fx-background-color: lightblue;");
            btn.setText(String.format("%s\n已购%d个", data.pillName, data.count));
            btn.setOnAction(e -> buyPill(entry.getKey(), data));

            gridPills.add(btn, col, row);
            index++;
        }
    }

    // 购买逻辑
    private void buyPill(String pillId, PillData data) {
        if (mainController.deductQi(data.cost)) {
            data.count++;
            mainController.increaseQiRate(data.rate);
            mainController.updateActualSuccessRate();
            updatePillDisplay();
        } else {
            new Alert(Alert.AlertType.WARNING, "灵气不足！").showAndWait();
        }
    }

    @FXML
    private void closeAlchemyPanel() {
        if (mainController != null) {
            mainController.savePillsData(pills);
        }
        Stage stage = (Stage) gridPills.getScene().getWindow();
        stage.close();
    }

    public Map<String, PillData> getPills() { return pills; }

    public void setPills(Map<String, PillData> newPills) {
        pills.clear();
        newPills.forEach((id, data) -> {
            PillData copiedData = new PillData(
                    data.pillId, data.pillName, data.cost, data.rate, data.successRateImpact
            );
            copiedData.count = data.count;
            pills.put(id, copiedData);
        });
        updatePillDisplay();
    }
}
