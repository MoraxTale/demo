package com.example.demo1;

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
        private static final long serialVersionUID = 1;
        // 丹药成本
        int cost;
        // 丹药成功率
        double rate;
        // 已购买的丹药数量
        int count = 0;

        /**
         * 构造方法，初始化丹药数据对象
         * @param cost 丹药成本
         * @param rate 丹药成功率
         */
        public PillData(int cost, double rate) {
            this.cost = cost;
            this.rate = rate;
        }
    }

    /**
     * 设置主控制器的方法
     * @param controller 主控制器对象
     */
    public void setMainController(Controller controller) {
        this.mainController = controller;
        // 加载丹药数据
        loadPills();
    }

    /**
     * 加载丹药数据的方法
     */
    private void loadPills() {
        // 定义不同等级丹药的成本
        int[] costs = {1000, 500, 100};
        // 定义不同等级丹药的成功率
        double[] rates = {2.0, 1.0, 0.5};

        for (int i = 0; i < 25; i++) {
            // 计算丹药等级
            int tier = i % 3;
            // 生成丹药 ID
            String pillId = String.format("丹药%02d - %d灵气 (+%.1f/s)", i + 1, costs[tier], rates[tier]);
            // 创建丹药数据对象并添加到映射中
            pills.put(pillId, new PillData(costs[tier], rates[tier]));
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
            btn.setStyle("-fx-font-size: 14; -fx-pref-width: 300;");
            // 设置按钮文本，显示丹药信息和已购买数量
            btn.setText(String.format("%s 已购%d个", id, data.count));
            // 为按钮添加点击事件处理逻辑
            btn.setOnAction(e -> buyPill(id, data));
            // 将按钮添加到垂直布局容器中
            vboxPills.getChildren().add(btn);
        });
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
     * @param pills 丹药数据映射
     */
    public void setPills(Map<String, PillData> pills) {
        this.pills = pills;
        // 更新丹药显示
        updatePillDisplay();
    }

    /**
     * 关闭炼丹界面的方法
     */
    @FXML
    private void closeAlchemyPanel() {
        // 获取当前窗口对象
        Stage stage = (Stage) vboxPills.getScene().getWindow();
        // 关闭窗口
        stage.close();
    }
}
