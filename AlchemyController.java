package com.example.demo;

// 导入 JavaFX FXML 相关类
import javafx.fxml.FXML;
// 导入 JavaFX 对话框相关类
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
// 导入 JavaFX 布局相关类
import javafx.scene.layout.VBox;
// 导入 JavaFX 窗口相关类
import javafx.stage.Stage;
// 导入 Java 序列化相关类
import java.io.Serial;
import java.io.Serializable;
// 导入 Java 集合框架中的 LinkedHashMap 实现类和 Map 接口
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 炼丹控制器类，负责处理炼丹界面的逻辑和操作
 */
public class AlchemyController {
    // FXML 中定义的垂直布局容器，用于显示丹药按钮
    @FXML
    private VBox vboxPills;
    // 主控制器对象，用于与主游戏逻辑交互
    private Controller mainController;
    // 丹药数据映射，存储每种丹药的信息
    private Map<String, PillData> pills = new LinkedHashMap<>();

    /**
     * 丹药数据内部类，用于存储丹药的成本、成功率和购买数量
     */
    public static class PillData implements Serializable {
        // 序列化版本号，确保序列化和反序列化的兼容性
        @Serial
        private static final long serialVersionUID = 1L;
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
        public PillData(int cost, double rate,double successRateImpact) {
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

    }

    /**
     * 加载丹药数据的方法
     */
    public void loadPillsOnClick() {
        // 如果主控制器有已保存的丹药数据，优先加载
        if (mainController != null && !mainController.getSavedPills().isEmpty()) {
            pills.clear();
            mainController.getSavedPills().forEach((id, data) -> {
                PillData copiedData = new PillData(data.cost, data.rate, data.successRateImpact);
                copiedData.count = data.count;
                pills.put(id, copiedData);
            });
            System.out.println("[炼丹界面] 从主控制器加载丹药数据: " + pills.size() + " 条");
        } else if (pills.isEmpty()) { // 无数据时初始化默认
            int currentStageLevel = mainController.getStageLevel();
            int[] costs = {1000, 500, 100};
            double[] rates = {2.0, 1.0, 0.5};
            double[] Impact = {0.01, 0.05, 0.10, 0.15};
            for (int i = 0; i < 4; i++) {
                int tier = (currentStageLevel * 3 + i) % costs.length;
                String pillId = String.format("丹药%02d - %d灵气 (+%.1f/s +%.0f%%)",
                        currentStageLevel * 4 + i + 1,
                        costs[tier],
                        rates[tier],
                        Impact[tier] * 100);
                pills.put(pillId, new PillData(costs[tier], rates[tier], Impact[tier]));
            }
            System.out.println("[炼丹界面] 初始化默认丹药数据");
        }
        // 更新丹药显示
        updatePillDisplay();
    }

    /**
     * 更新丹药显示的方法
     */
    private void updatePillDisplay() {
        // 清空垂直布局容器中的子节点
        vboxPills.getChildren().clear();

        // 遍历丹药数据映射
        pills.forEach((id, data) -> {
            // 创建按钮对象
            Button btn = new Button();
            // 设置按钮样式
            btn.setStyle("-fx-font-size: 14; -fx-pref-width: 300; -fx-max-width: infinity; -fx-background-color: lightblue; -fx-text-fill: black;");
            // 设置按钮文本，显示丹药信息和已购买数量
            btn.setText(String.format("%s 已购%d个 (当前影响+%.0f%%)", id, data.count, data.successRateImpact * 100 * data.count));
            // 为按钮添加点击事件处理逻辑
            btn.setOnAction(e -> buyPill(id, data));
            // 将按钮添加到垂直布局中，确保布局自动调整容纳按钮
            VBox.setVgrow(btn, javafx.scene.layout.Priority.ALWAYS);
            vboxPills.getChildren().add(btn);System.out.println("Added button: " + btn.getText()); // 日志输出
        });System.out.println("Number of buttons in VBox: " + vboxPills.getChildren().size()); // 日志输出
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
     * 获取丹药数据映射的方法
     * @return 丹药数据映射
     */
    public Map<String, PillData> getPills() {
        return pills;
    }

    /**
     * 设置丹药数据映射的方法
     * @param newPills 丹药数据映射
     */
    public void setPills(Map<String, PillData> newPills) {
        pills.clear(); // 清空现有数据
        if (newPills != null) {
            // 深拷贝每个 PillData 对象
            newPills.forEach((id, data) -> {
                PillData copiedData = new PillData(data.cost, data.rate, data.successRateImpact);
                copiedData.count = data.count; // 复制 count 值
                pills.put(id, copiedData);
                System.out.printf("[深拷贝] 加载丹药: %s, count=%d%n", id, copiedData.count);
            });
        }
        // 更新丹药显示
        updatePillDisplay();
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
        Stage stage = (Stage) vboxPills.getScene().getWindow();
        stage.close();
    }

}