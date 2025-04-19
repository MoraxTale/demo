package com.example.demo;

// 导入 JavaFX FXML 相关类
import javafx.fxml.FXML;
// 导入 JavaFX 对话框相关类
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
// 导入 JavaFX 布局相关类
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
// 导入 JavaFX 窗口相关类
import javafx.stage.Stage;
// 导入 Java 序列化相关类
import java.io.Serial;
import java.io.Serializable;
// 导入 Java 集合框架中的 LinkedHashMap 实现类和 Map 接口
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 炼丹控制器类，负责处理炼丹界面的逻辑和操作
 */
public class AlchemyController {
    // FXML 中定义的垂直布局容器，用于显示丹药按钮
    @FXML
    private GridPane gridPills;
    // 主控制器对象，用于与主游戏逻辑交互
    private Controller mainController;
    // 丹药数据映射，存储每种丹药的信息

    private Map<String, PillData> pills = new LinkedHashMap<>();
    private List<PillConfig> customPills = new ArrayList<>();

    /**
     * 丹药数据内部类，用于存储丹药的成本、成功率和购买数量
     */
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
        // 序列化版本号，确保序列化和反序列化的兼容性
        @Serial
        private static final long serialVersionUID = 1L;
        String pillId;
        String pillName;
        // 丹药成本
        int cost;
        // 丹药成功率
        double rate;
        // 已购买的丹药数量
        double successRateImpact;
        int count = 0;

        /**
         * 构造方法，初始化丹药数据对象
         * @param cost 丹药成本
         * @param rate 丹药成功率
         */
        public PillData(String pillId, String pillName, int cost, double rate, double successRateImpact) {
            this.pillId = pillId;
            this.pillName = pillName;
            this.cost = cost;
            this.rate = rate;
            this.successRateImpact = successRateImpact;
        }
    }

    /**
     * 设置主控制器的方法
     * @param controller 主控制器对象
     */
    public void setMainController(Controller controller) {
        this.mainController = controller;
        // 加载丹药数据
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
    /**
     * 加载丹药数据的方法
     */
    public void loadPillsOnClick() {
        // 如果主控制器有已保存的丹药数据，优先加载
        if (mainController != null && !mainController.getSavedPills().isEmpty()) {
            loadSavedPills();
        } else {
            generateNewPills();
        }// 更新丹药显示
        updatePillDisplay();
    }
    /**
     * 更新丹药显示的方法
     */
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

    private void updatePillDisplay() {
        // 更新5x5界面
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

    /**
     * 购买丹药的方法
     * @param pillId 丹药 ID
     * @param data 丹药数据对象
     */
    private void buyPill(String pillId, PillData data) {
        if (mainController.deductQi(data.cost)) {
            // 如果灵气足够，扣除灵气，增加已购买数量，增加灵气增长速度，并更新显示
            data.count++;
            mainController.increaseQiRate(data.rate);
            mainController.updateActualSuccessRate();
            updatePillDisplay();
        } else {
            // 如果灵气不足，显示警告对话框
            new Alert(Alert.AlertType.WARNING, "灵气不足！").showAndWait();
        }
    }




    /**
     * 关闭炼丹界面的方法
     */
    @FXML
    private void closeAlchemyPanel() {
        // 在关闭前将丹药数据传回主控制器
        if (mainController != null) {
            mainController.savePillsData(pills);
            System.out.println("[炼丹界面] 关闭并保存丹药数据");
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
