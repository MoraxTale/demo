package com.example.demo1;
// 导入 JavaFX FXML 相关类
import javafx.application.Platform;
import javafx.fxml.FXML;
// 导入 JavaFX 对话框相关类
import javafx.geometry.Pos;
import javafx.scene.control.*;
// 导入 JavaFX 布局相关类
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
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
    @FXML
    private ScrollPane scrollPane; // 新增绑定
    // FXML 中定义的垂直布局容器，用于显示丹药按钮
    @FXML
    private GridPane gridPills;
    // 保存滚动位置
    private double lastScrollPosition = 0.0;

    // 主控制器对象，用于与主游戏逻辑交互
    private Controller mainController;
    // 丹药数据映射，存储每种丹药的信息

    private Map<String, PillData> pills = new LinkedHashMap<>();
    private List<PillConfig> availablePills = new ArrayList<>();
    private final Map<String, PillConfig> pillConfigMap = new HashMap<>();
    private final List<PillConfig> customPills = new ArrayList<>();
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


    /// 丹药数据类（包含唯一ID和名称）

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
        this.mainController = controller;
        // 加载丹药数据
        initializeCustomPills();
    }
    // 初始化自定义丹药（示例数据）
    void initializeCustomPills() {
        customPills.clear();
        pillConfigMap.clear();

        // 第1级丹药 (凡人可用的基础丹药)
        addPillConfig(1, "pill_001", "聚气丹", 1, 0.1, 0.005);
        addPillConfig(1, "pill_002", "养元丹", 1, 0.15, 0.01);

        // 第2级丹药 (炼气期)
        addPillConfig(2, "pill_003", "凝神丹", 3, 0.2, 0.005);
        addPillConfig(2, "pill_004", "固本丹", 3, 1.25, 0.01);

        // 第3级丹药 (筑基期)
        addPillConfig(3, "pill_005", "洗髓丹", 10, 0.3, 0.005);
        addPillConfig(3, "pill_006", "通脉丹", 10, 0.35, 0.02);

        // 第4级丹药 (金丹期)
        addPillConfig(4, "pill_007", "金丹丸", 30, 0.4, 0.005);
        addPillConfig(4, "pill_008", "玉液丹", 30, 0.45, 0.01);

        // 第5级丹药 (元婴期)
        addPillConfig(5, "pill_009", "元婴造化丹", 90, 0.5, 0.005);
        addPillConfig(5, "pill_010", "九转还魂丹", 90, 0.55, 0.01);

        // 第6级丹药 (化神期)
        addPillConfig(6, "pill_011", "化神丹", 270, 0.6, 0.005);
        addPillConfig(6, "pill_012", "天元神丹", 270, 0.65, 0.01);

        // 第7级丹药 (渡劫期)
        addPillConfig(7, "pill_013", "渡劫丹", 810, 0.7, 0.005);
        addPillConfig(7, "pill_014", "避劫丹", 810, 0.75, 0.01);

        // 第8级丹药 (大乘期)
        addPillConfig(8, "pill_015", "大乘玄丹", 2430, 0.8, 0.005);
        addPillConfig(8, "pill_016", "乾坤一气丹", 2430, 0.85, 0.01);

        // 第9级丹药 (大罗金仙)
        addPillConfig(9, "pill_017", "大罗金丹", 7290, 0.9, 0.005);
        addPillConfig(9, "pill_018", "混元丹", 7290, 0.95, 0.01);

        // 第10级丹药 (仙君)
        addPillConfig(10, "pill_019", "仙君玉丹", 21000, 1.0, 0.005);
        addPillConfig(10, "pill_020", "太乙丹", 21000, 1.5, 0.01);

        // 第11级丹药 (仙王)
        addPillConfig(11, "pill_021", "仙王丹", 65000, 2.0, 0.005);
        addPillConfig(11, "pill_022", "玄天丹", 65000, 2.5, 0.01);

        // 第12级丹药 (仙帝)
        addPillConfig(12, "pill_023", "仙帝丹", 190000, 3.0, 0.005);
        addPillConfig(12, "pill_024", "紫霄丹", 190000, 3.5, 0.01);

        // 第13级丹药 (仙尊)
        addPillConfig(13, "pill_025", "仙尊丹", 590000, 4.0, 0.005);
        addPillConfig(13, "pill_026", "九转金丹", 590000, 4.5, 0.01);

        // 第14级丹药 (仙圣)
        addPillConfig(14, "pill_027", "仙圣丹", 1770000, 5.0, 0.005);
        addPillConfig(14, "pill_028", "混沌丹", 1770000, 5.5, 0.01);

        // 第15级丹药 (仙祖)
        addPillConfig(15, "pill_029", "仙祖丹", 5000000, 6.0, 0.005);
        addPillConfig(15, "pill_030", "鸿蒙丹", 5300000, 6.5, 0.01);

        // 第16级丹药 (道君)
        addPillConfig(16, "pill_031", "道君丹", 15900000, 7.0, 0.005);
        addPillConfig(16, "pill_032", "太初丹", 15900000, 7.5, 0.01);

        // 第17级丹药 (道王)
        addPillConfig(17, "pill_033", "道王丹", 47800000, 8.0, 0.005);
        addPillConfig(17, "pill_034", "玄黄丹", 47800000, 8.5, 0.01);

        // 第18级丹药 (道帝)
        addPillConfig(18, "pill_035", "道帝丹", 143000000, 9.0, 0.005);
        addPillConfig(18, "pill_036", "造化丹", 143000000, 9.5, 0.01);

        // 第19级丹药 (道尊)
        addPillConfig(19, "pill_037", "道尊丹", 430000000, 10.0, 0.005);
        addPillConfig(19, "pill_038", "天命丹", 430000000, 10.5, 0.01);

        // 第20级丹药 (道圣)
        addPillConfig(20, "pill_039", "道圣丹", 1290000000, 11.0, 0.005);
        addPillConfig(20, "pill_040", "永恒丹", 1290000000, 11.5, 0.01);

        // 第21级丹药 (道祖)
        addPillConfig(21, "pill_041", "道祖丹", 2000000000, 12.0, 0.005);
        addPillConfig(21, "pill_042", "创世丹", 2000000000, 12.5, 0.01);

        // 第22级丹药 (混元大罗金仙)
        addPillConfig(22, "pill_043", "混元丹", 2000000000, 13.0, 0.005);
        addPillConfig(22, "pill_044", "无极丹", 2000000000, 13.5, 0.01);

        // 第23级丹药 (混元无极金仙)
        addPillConfig(23, "pill_045", "无极金丹", 2000000000, 14.0, 0.005);
        addPillConfig(23, "pill_046", "太虚丹", 2000000000, 14.5, 0.01);

        // 第24级丹药 (混沌天尊)
        addPillConfig(24, "pill_047", "混沌丹", 2000000000, 15.0, 0.005);
        addPillConfig(24, "pill_048", "鸿蒙丹", 2000000000, 15.5, 0.01);

        // 第25级丹药 (鸿蒙至尊)
        addPillConfig(25, "pill_049", "鸿蒙至尊丹", 2000000000, 16.0, 0.005);
        addPillConfig(25, "pill_050", "大道丹", 2000000000, 16.5, 0.01);
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
        } else {
            generateNewPills();
        }// 更新丹药显示
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
        // 1. 保存当前滚动位置（必须在清空内容前获取）
        lastScrollPosition = scrollPane.getVvalue();

        // 2. 清空旧内容
        gridPills.getChildren().clear();
        gridPills.getColumnConstraints().clear();
        gridPills.getRowConstraints().clear();

        // 3. 重建两列布局
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPills.getColumnConstraints().addAll(col1, col2);

        // 4. 动态生成按钮
        int index = 0;
        for (Map.Entry<String, PillData> entry : pills.entrySet()) {
            int row = index / 2;
            int col = index % 2;


            PillData data = entry.getValue();

            // 使用 VBox 包装多行文本
            VBox container = new VBox(3);
            container.setAlignment(Pos.CENTER);

            Label nameLabel = new Label(data.pillName);
            nameLabel.getStyleClass().add("pill-name");

            Label countLabel = new Label("数量: " + data.count);
            countLabel.getStyleClass().add("pill-detail");

            Label effectLabel = new Label(
                    String.format("灵气增速 +%.1f/s\n成功率 +%.1f%%", data.rate, data.successRateImpact * 100)
            );
            effectLabel.getStyleClass().add("pill-detail-small"); // 小号字体

            Button btn = new Button();
            btn.getStyleClass().add("pill-btn");
            btn.setGraphic(container); // 将文本内容设置为按钮的图形
            btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btn.setOnAction(e -> buyPill(entry.getKey(), data));

            container.getChildren().addAll(nameLabel, countLabel, effectLabel);

            // 动态添加行约束
            if (row >= gridPills.getRowConstraints().size()) {
                RowConstraints rc = new RowConstraints();
                rc.setPrefHeight(90);
                gridPills.getRowConstraints().add(rc);
            }

            gridPills.add(btn, col, row);
            index++;
        }

        // 5. 精准恢复滚动位置
        Platform.runLater(() -> {
            // 强制布局计算
            scrollPane.layout();
            gridPills.layout();

            // 计算有效位置
            double validPosition = Math.min(lastScrollPosition, 1.0);
            validPosition = Math.max(validPosition, 0.0);

            // 分步设置确保生效
            scrollPane.setVvalue(0.0); // 先重置到顶部
            scrollPane.setVvalue(validPosition); // 再设置目标位置
            double finalValidPosition = validPosition;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        scrollPane.setVvalue(finalValidPosition);
                    });
                }
            }, 50); // 50ms延迟
        });

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