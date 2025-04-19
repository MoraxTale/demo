package com.example.demo1;

// 导入 JavaFX FXML 相关类
import javafx.fxml.FXML;
// 导入 JavaFX 对话框相关类
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
// 导入 JavaFX 布局相关类
import javafx.scene.layout.GridPane;
// 导入 JavaFX 窗口相关类
import javafx.stage.Stage;
// 导入 Java 序列化相关类
import java.io.Serial;
import java.io.Serializable;
// 导入 Java 集合框架中的 LinkedHashMap 实现类和 Map 接口
import java.util.*;
        import java.util.stream.Collectors;

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

    private List<PillConfig> availablePills = new ArrayList<>();
    private final Map<String, PillConfig> pillConfigMap = new HashMap<>();
    private final List<PillConfig> customPills = new ArrayList<>();
    private final Map<String, PillData> pills = new LinkedHashMap<>();

    // 添加方法获取丹药配置
    public PillConfig getPillConfig(String pillId) {
        return pillConfigMap.get(pillId);
    }

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
        private final int level;
        public PillConfig(String pillId, String name, int cost, double rate, double successRateImpact, int level) {
            this.pillId = pillId;
            this.name = name;
            this.cost = cost;
            this.rate = rate;
            this.successRateImpact = successRateImpact;
            this.level = level;
        }
        public String getPillId() { return pillId; }
        public String getName() { return name; }
        public int getCost() { return cost; }
        public double getRate() { return rate; }
        public double getSuccessRateImpact() { return successRateImpact; }
        public int getLevel() { return level; } // 新增getter方法

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
        int level;
        /**
         * 构造方法，初始化丹药数据对象
         * @param cost 丹药成本
         * @param rate 丹药成功率
         */
        public PillData(String pillId, String pillName, int cost, double rate, double successRateImpact,int level) {
            this.pillId = pillId;
            this.pillName = pillName;
            this.cost = cost;
            this.rate = rate;
            this.successRateImpact = successRateImpact;
            this.level =level;
        }
    }


    /**
     * 设置主控制器的方法
     * @param controller 主控制器对象
     */
    public void setMainController(Controller controller) {
        // 加载丹药数据
        this.mainController = controller;
        initializeCustomPills();
    }
    // 初始化自定义丹药（示例数据）
    void initializeCustomPills() {
        customPills.clear();
        pillConfigMap.clear();

        // 25级丹药，每级2种，共50种
        addPillConfig(1, "pill_001", "聚气丹", 500, 0.5, 0.01);
        addPillConfig(1, "pill_002", "养元丹", 600, 0.6, 0.012);

        addPillConfig(2, "pill_003", "凝神丹", 800, 0.8, 0.015);
        addPillConfig(2, "pill_004", "固本丹", 1000, 1.0, 0.018);

        addPillConfig(3, "pill_005", "牛逼丹", 1200,1.1,0.1);
        addPillConfig(3,"pill_006","自爆丹",1300,1.2,0.12);
        // ... 添加更多丹药配置 ...

        updateAvailablePills();
    }


    // 更新可用丹药列表
    public void updateAvailablePills() {
        int playerLevel = mainController != null ? mainController.getStageLevel() : 0;
        int maxAvailableLevel = Math.min(25, playerLevel + 2); // 当前境界+2级

        availablePills.clear();
        for (PillConfig config : customPills) {
            if (config.getLevel() <= maxAvailableLevel) {
                availablePills.add(config);
            }
        }

        // 确保至少显示3种最低级丹药
        if (availablePills.size() < 3) {
            availablePills = customPills.stream()
                    .filter(c -> c.getLevel() <= 2)
                    .limit(3)
                    .collect(Collectors.toList());
        }
        Map<String, PillData> oldPills = new HashMap<>(pills);
        pills.clear();
        for (PillConfig config : availablePills) {
            String pillId = config.getPillId();
            PillData existingPill = oldPills.get(pillId);

            if (existingPill != null) {
                // 保留已购买的丹药数量
                pills.put(pillId, existingPill);
            } else {
                // 创建新的丹药数据
                pills.put(pillId, new PillData(
                        pillId,
                        config.getName(),
                        config.getCost(),
                        config.getRate(),
                        config.getSuccessRateImpact(),
                        config.getLevel()
                ));
            }
        }
    }

    // 修改getPills方法只返回可用丹药
    public Map<String, PillData> getPills() {
        Map<String, PillData> result = new LinkedHashMap<>();
        for (PillConfig config : availablePills) {
            String pillId = config.getPillId();
            if (!pills.containsKey(pillId)) {
                pills.put(pillId, new PillData(
                        pillId,
                        config.getName(),
                        config.getCost(),
                        config.getRate(),
                        config.getSuccessRateImpact(),
                        config.getLevel()
                ));
            }
            result.put(pillId, pills.get(pillId));
        }
        return result;
    }
    private void addPillConfig(int level, String id, String name, int cost, double rate, double successRate) {
        customPills.add(new PillConfig(id, name, cost, rate, successRate, level));
    }
    /**
     * 加载丹药数据的方法
     */

    public void loadPillsOnClick() {
        // 如果主控制器有已保存的丹药数据，优先加载
        if (mainController != null && !mainController.getSavedPills().isEmpty()) {
            loadSavedPills();
        }
        updateAvailablePills();
        updatePillDisplay();
    }
    /**
     * 更新丹药显示的方法
     */
    private void loadSavedPills() {
        // 先清空现有数据但保留配置
        Map<String, PillData> oldPills = new HashMap<>(pills);
        pills.clear();

        // 加载保存的数据
        mainController.getSavedPills().forEach((id, data) -> {
            // 保留原来的丹药对象以避免UI刷新问题
            PillData existingPill = oldPills.get(id);
            if (existingPill != null) {
                existingPill.count = data.count;
                pills.put(id, existingPill);
            } else {
                PillData copiedData = new PillData(
                        data.pillId, data.pillName, data.cost,
                        data.rate, data.successRateImpact, data.level
                );
                copiedData.count = data.count;
                pills.put(id, copiedData);
            }
        });

        // 确保所有可用丹药都有数据
        for (PillConfig config : availablePills) {
            pills.putIfAbsent(config.getPillId(), new PillData(
                    config.getPillId(),
                    config.getName(),
                    config.getCost(),
                    config.getRate(),
                    config.getSuccessRateImpact(),
                    config.getLevel()
            ));
        }
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
                    config.getSuccessRateImpact(),
                    config.getLevel()
            ));
            index++;
        }
    }

    void updatePillDisplay() {
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
            btn.setText(String.format("%s\n已购%d个\n灵气速度+%.1f\n成功率+%.1f%%\n所需灵气%d",
                    data.pillName, data.count, data.rate, data.successRateImpact * 100,data.cost));
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
        if (mainController == null) {
            new Alert(Alert.AlertType.ERROR, "炼丹系统未正确初始化！").showAndWait();
            return;
        }

        if (mainController.deductQi(data.cost)) {
            data.count++;

            // 更新主控制器的保存数据
            mainController.getSavedPills().merge(pillId, data, (oldVal, newVal) -> {
                oldVal.count = newVal.count;
                return oldVal;
            });

            mainController.applyPillEffects();
            updatePillDisplay();

            System.out.printf("[购买丹药] %s 数量: %d, 总效果: 速度+%.1f, 成功率+%.2f%%\n",
                    data.pillName, data.count,
                    data.rate * data.count,
                    data.successRateImpact * data.count * 100);
        } else {
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
            // 确保所有丹药数据都保存
            pills.forEach((id, data) -> {
                mainController.getSavedPills().merge(id, data, (oldVal, newVal) -> {
                    oldVal.count = newVal.count;
                    return oldVal;
                });
            });

            mainController.savePillsData(pills);
            mainController.applyPillEffects(); // 重新计算效果
            System.out.println("[炼丹界面] 关闭并保存丹药数据");
        }
        Stage stage = (Stage) gridPills.getScene().getWindow();
        stage.close();
    }


    public void setPills(Map<String, PillData> newPills) {
        pills.clear();
        newPills.forEach((id, data) -> {
            PillData copiedData = new PillData(
                    data.pillId, data.pillName, data.cost, data.rate, data.successRateImpact,data.level
            );
            copiedData.count = data.count;
            pills.put(id, copiedData);
        });
        updatePillDisplay();
    }
}
