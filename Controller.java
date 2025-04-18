// 声明包名
package com.example.demo;

// 导入 JavaFX 属性相关类，用于创建和管理可观察的属性
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
// 导入 JavaFX 动画计时器类，用于实现定时任务
import javafx.animation.AnimationTimer;
// 导入 JavaFX 属性相关类
import javafx.beans.property.*;
// 导入 JavaFX FXML 相关类，用于加载 FXML 文件和处理 FXML 元素
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
// 导入 JavaFX 场景图相关类
import javafx.scene.Parent;
import javafx.scene.Scene;
// 导入 JavaFX 对话框相关类
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
// 导入 JavaFX 窗口相关类
import javafx.stage.Stage;
import javafx.stage.Modality;
// 导入 Java 输入输出相关类
import java.io.*;
// 导入 JavaFX 文件选择器类
import javafx.stage.FileChooser;
// 导入 Java 集合框架中的 Map 接口
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主控制器类，负责处理游戏的主要逻辑和界面交互
 */
public class Controller {
    // 炼丹控制器对象，用于管理炼丹界面和操作
    private AlchemyController alchemyController;
    // 基础渡劫成功率，固定值
    private static final double BASE_SUCCESS_RATE = 0.8;
    // 成功率衰减因子，每次渡劫成功后成功率会按此因子衰减
    private static final double DECAY_FACTOR = 0.8;
    // 渡劫所需的灵气消耗，固定值
    private static final int BREAKTHROUGH_COST = 10000;
    private final DoubleProperty actualSuccessRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);

    // 以下是 FXML 中定义的界面元素，通过 @FXML 注解注入
    @FXML private Label lblStage; // 显示当前境界的标签
    @FXML private Label lblQi; // 显示当前灵气值的标签
    @FXML private Label lblQiRate; // 显示灵气增长速度的标签
    @FXML private Label lblSuccessRate; // 显示渡劫成功率的标签
    @FXML private Button btnCultivate; // 修炼按钮，点击可增加灵气
    @FXML private Button btnAlchemy; // 炼丹按钮，点击打开炼丹界面
    @FXML private Button btnBreakthrough; // 渡劫按钮，点击尝试渡劫

    // 灵气值属性，使用 JavaFX 的 IntegerProperty 实现可观察
    private final IntegerProperty qi = new SimpleIntegerProperty(0);
    // 灵气增长速度属性，使用 JavaFX 的 DoubleProperty 实现可观察
    private final DoubleProperty qiRate = new SimpleDoubleProperty(1.0);
    // 随机事件处理器对象，用于处理随机触发的事件
    private RandomEventHandler randomEventHandler;
    // 炼丹界面的窗口对象
    private Stage alchemyStage;

    // 当前境界属性，使用 JavaFX 的 StringProperty 实现可观察
    private final StringProperty currentStage = new SimpleStringProperty("凡人");
    // 渡劫成功率属性，使用 JavaFX 的 DoubleProperty 实现可观察
    private final DoubleProperty successRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);
    // 境界等级，凡人对应 0，炼气对应 1，以此类推
    private int stageLevel = 0;
    // 境界名称数组，存储所有可能的境界名称
    private final String[] STAGES = {"凡人", "炼气", "筑基", "金丹", "元婴", "化神", "渡劫", "大乘", "大罗金仙"};
    public int getStageLevel() {
        return stageLevel;
    }
    /**
     * 初始化方法，在 FXML 加载完成后自动调用，用于初始化界面元素和事件处理
     */
    @FXML
    private void initialize() {
        // 将灵气标签的文本属性绑定到灵气属性，实现自动更新显示
        lblQi.textProperty().bind(qi.asString("灵气：%d"));
        // 将灵气增长速度标签的文本属性绑定到灵气增长速度属性，实现自动更新显示
        lblQiRate.textProperty().bind(qiRate.asString("灵气增长速度：%.1f/s"));
        // 启动灵气自动增长的定时器
        startAutoQiGrowth();
        // 将境界标签的文本属性绑定到当前境界属性，实现自动更新显示
        lblStage.textProperty().bind(currentStage);
        // 将渡劫成功率标签的文本属性绑定到渡劫成功率属性，实现自动更新显示
        lblSuccessRate.textProperty().bind(Bindings.format("渡劫成功率：%.1f%%", actualSuccessRate.multiply(100)));

        // 为修炼按钮添加点击事件处理逻辑
        btnCultivate.setOnAction(event -> updataQi(1000));
        // 为炼丹按钮添加点击事件处理逻辑
        btnAlchemy.setOnAction(event -> openAlchemyPanel());
        // 初始化随机事件处理器
        randomEventHandler = new RandomEventHandler(this);
        // 再次启动灵气自动增长的定时器（可能是代码冗余，可考虑优化）
        startAutoQiGrowth();

        try {
            // 创建 FXMLLoader 对象，用于加载炼丹界面的 FXML 文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AlchemyView.fxml"));
            // 加载 FXML 文件并获取根节点
            Parent root = loader.load();
            // 获取炼丹控制器对象
            alchemyController = loader.getController();
            // 设置炼丹控制器的主控制器为当前控制器
            alchemyController.setMainController(this);
        } catch (IOException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 输出初始化炼丹控制器失败的信息
            System.out.println("初始化炼丹控制器失败。");
        }
    }

    /**
     * 保存游戏方法，将当前游戏状态保存到指定文件
     * @param filePath 保存文件的路径
     */
    public void saveGame(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            long currentTime = System.currentTimeMillis(); // 获取当前时间
            if (alchemyController == null) {
                // 如果炼丹控制器未初始化，创建不包含炼丹数据的游戏状态对象
                GameState state = new GameState(
                        qi.get(),
                        qiRate.get(),
                        null,
                        stageLevel,
                        currentTime
                );
                // 将游戏状态对象写入文件
                oos.writeObject(state);
            } else {
                // 创建包含炼丹数据的游戏状态对象
                GameState state = new GameState(
                        qi.get(),
                        qiRate.get(),
                        savedPills, // 确保获取最新数据
                        stageLevel,
                        currentTime
                );
                // 将游戏状态对象写入文件
                oos.writeObject(state);
                System.out.println("[保存] 存档时间已记录: " + currentTime);
            }
        }
        catch (IOException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 输出保存游戏时出现 IO 错误的信息
            System.err.println("保存游戏时出现 IO 错误。");
        }
    }

    public void updateActualSuccessRate() {
        double pillImpact = 0.0;
        if (alchemyController != null) {
            for (AlchemyController.PillData pill : alchemyController.getPills().values()) {
                pillImpact += pill.successRateImpact * pill.count;
            }
        }
        actualSuccessRate.set(Math.min(successRate.get() + pillImpact, 1.0));
    }

    /**
     * 加载游戏方法，从指定文件加载游戏状态
     * @param filePath 加载文件的路径
     */
    public void loadGame(String filePath) {
        try {
            // 创建文件对象
            java.io.File file = new java.io.File(filePath);
            if (file.length() == 0) {
                // 如果文件为空，输出提示信息并返回
                System.out.println("存档文件为空，无法加载。");
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                // 从文件中读取游戏状态对象
                GameState state = (GameState) ois.readObject();
                System.out.println("[加载] 读取存档，丹药数量: " + (state.getPills() != null ? state.getPills().size() : 0));// 计算时间差（单位：秒）
                long savedTime = state.getLastSaveTime();System.out.println("[DEBUG] 存档时间: " + savedTime);
                long currentTime = System.currentTimeMillis();System.out.println("[DEBUG] 当前时间: " + currentTime);
                long deltaTimeSeconds = (currentTime - savedTime) / 1000;System.out.println("[DEBUG] 时间差（秒）: " + deltaTimeSeconds);


                // 声明需要显示弹窗的变量
                boolean showDialog = false;
                String timeMessage = "";
                int gainedQi = 0;

                if (savedTime == 0L) {
                    // 旧存档处理
                    qi.set(state.getQi());
                    System.out.println("[加载] 旧存档，无离线时间补偿");
                } else if (deltaTimeSeconds > 0) {
                    // 计算离线灵气
                    gainedQi = (int) (deltaTimeSeconds * state.getQiRate());
                    qi.set(state.getQi() + gainedQi);

                    // 转换时间为可读格式
                    long hours = deltaTimeSeconds / 3600;
                    long minutes = (deltaTimeSeconds % 3600) / 60;
                    long seconds = deltaTimeSeconds % 60;
                    timeMessage = String.format("%d小时%d分%d秒", hours, minutes, seconds);

                    showDialog = true;
                    System.out.printf("[加载] 离线补偿: %s → %d灵气%n", timeMessage, gainedQi);
                } else {
                    // 时间差无效
                    qi.set(state.getQi());
                    System.out.println("[加载] 无效时间差，不补偿灵气");
                }

                // 设置灵气值
                qi.set(state.getQi() + gainedQi);
                // 设置灵气增长速度
                qiRate.set(state.getQiRate());
                // 加载境界等级
                stageLevel = state.getStageLevel();
                // 更新当前境界显示
                currentStage.set(STAGES[stageLevel]);
                // 更新渡劫成功率
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));
                // 显示离线奖励弹窗
                if (showDialog && gainedQi > 0) {
                    String finalTimeMessage = timeMessage;
                    int finalGainedQi = gainedQi;
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("离线奖励");
                        alert.setHeaderText("修真无岁月，洞中已千年");
                        alert.setContentText(String.format(
                                "你在离开的 %s 时间内\n通过修炼获得了 %d 灵气！",
                                finalTimeMessage, finalGainedQi
                        ));
                        alert.showAndWait();
                    });
                }
                // 新增：加载丹药后更新成功率
                if (alchemyController != null) {
                    alchemyController.setPills(state.getPills());
                    updateActualSuccessRate(); // 关键：触发计算
                }
                // 2. 更新丹药数据到主控制器的 savedPills
                savedPills.clear();
                if (state.getPills() != null) {
                    state.getPills().forEach((id, data) -> {
                        AlchemyController.PillData copiedData = new AlchemyController.PillData(data.cost, data.rate,data.successRateImpact);
                        copiedData.count = data.count;
                        savedPills.put(id, copiedData);
                    });
                }
                if (alchemyController != null) {
                    // 如果炼丹控制器已初始化，设置炼丹数据
                    alchemyController.setPills(state.getPills());
                    // 输出游戏加载成功的信息
                    System.out.println("游戏加载成功，包含炼丹数据和境界信息。");
                } else {
                    initializeAlchemyController(); // 新增方法
                    // 输出炼丹控制器未初始化，无法加载炼丹数据的信息
                    System.err.println("炼丹控制器未初始化，无法加载炼丹数据。");
                }
            }
        } catch (java.io.EOFException e) {
            // 输出存档文件损坏，无法加载的信息
            System.err.println("存档文件损坏，无法加载。");
            // 打印异常堆栈信息
            e.printStackTrace();
        } catch (java.io.IOException | ClassNotFoundException e) {
            // 输出加载游戏时出现错误的信息
            System.err.println("加载游戏时出现错误：" + e.getMessage());
            // 打印异常堆栈信息
            e.printStackTrace();
        }
    }

    /**
     * 处理保存游戏按钮点击事件的方法
     * @param event 按钮点击事件对象
     */
    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        // 创建文件选择器对象
        FileChooser fileChooser = new FileChooser();
        // 设置文件选择器的标题
        fileChooser.setTitle("保存游戏");
        // 添加文件扩展名过滤器，只显示 .xs 扩展名的文件
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("修仙存档", "*.xs")
        );
        // 显示文件保存对话框，获取用户选择的文件
        File file = fileChooser.showSaveDialog(lblQi.getScene().getWindow());
        if (file != null) {
            // 如果用户选择了文件，调用保存游戏方法
            saveGame(file.getAbsolutePath());
        }
    }

    /**
     * 处理加载游戏按钮点击事件的方法
     * @param event 按钮点击事件对象
     */
    @FXML
    private void handleLoad(javafx.event.ActionEvent event) {
        // 创建文件选择器对象
        FileChooser fileChooser = new FileChooser();
        // 设置文件选择器的标题
        fileChooser.setTitle("加载游戏");
        // 添加文件扩展名过滤器，只显示 .xs 扩展名的文件
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("修仙存档", "*.xs")
        );
        // 显示文件打开对话框，获取用户选择的文件
        File file = fileChooser.showOpenDialog(lblQi.getScene().getWindow());
        if (file != null) {
            // 如果用户选择了文件，调用加载游戏方法
            loadGame(file.getAbsolutePath());
        }
    }

    /**
     * 启动灵气自动增长的定时器方法
     */
    private void startAutoQiGrowth() {
        // 创建动画计时器对象
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000) {
                    // 如果距离上次更新超过 1 秒，增加灵气值
                    qi.set(qi.get() + (int) qiRate.get());
                    // 更新上次更新时间
                    lastUpdate = now;
                }
            }
        };
        // 启动定时器
        timer.start();
    }

    /**
     * 渡劫逻辑方法
     */
    @FXML
    private void breakthrough() {
        if (qi.get() < BREAKTHROUGH_COST) {
            // 如果灵气不足，显示警告对话框
            new Alert(Alert.AlertType.WARNING, "灵气不足！需要" + BREAKTHROUGH_COST + "灵气").showAndWait();
            return;
        }
        updateActualSuccessRate();
        double currentActualRate = actualSuccessRate.get();
        if (Math.random() < currentActualRate) {
            if (stageLevel < STAGES.length - 1) {
                stageLevel++;
                currentStage.set(STAGES[stageLevel]);
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));
                qi.set(qi.get() - BREAKTHROUGH_COST);

                // 衰减丹药影响力并重新加载界面
                if (alchemyController != null) {
                    alchemyController.getPills().values().forEach(pill -> {
                        pill.successRateImpact *= 0.1;
                    });
                    alchemyController.loadPillsOnClick();
                }
                updateActualSuccessRate();
                new Alert(Alert.AlertType.INFORMATION, "渡劫成功！当前境界：" + STAGES[stageLevel]).showAndWait();
            }
        } else {
            successRate.set(Math.min(successRate.get() + 0.1, 1.0));
            qi.set(qi.get() - BREAKTHROUGH_COST);
            updateActualSuccessRate();
            new Alert(Alert.AlertType.ERROR, "渡劫失败！下次成功率：" + String.format("%.1f%%", actualSuccessRate.get() * 100)).showAndWait();
        }
    }
    /**
     * 修炼方法，增加灵气值并检查随机事件
     */
    @FXML
    private void updataQi(int amount) {
        // 增加灵气值
        qi.set(qi.get() + amount);
        // 检查是否触发随机事件
        randomEventHandler.checkRandomEvent();
    }

    /**
     * 打开炼丹界面的方法
     */
    @FXML
    private void openAlchemyPanel() {
        try {
            if (alchemyStage != null && alchemyStage.isShowing()) {
                // 如果炼丹界面已打开，将其置于前台
                alchemyStage.requestFocus();
                return;
            }

            if (alchemyStage == null) {
                // 如果炼丹界面未创建，创建并初始化
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AlchemyView.fxml"));
                Parent root = loader.load();

                alchemyStage = new Stage();
                alchemyStage.setTitle("炼丹界面");
                alchemyStage.initModality(Modality.APPLICATION_MODAL);
                alchemyStage.initOwner(lblQi.getScene().getWindow());
                alchemyStage.setOnHidden(event -> alchemyStage = null);

                alchemyStage.setScene(new Scene(root));
                alchemyController = loader.getController();
                alchemyController.setMainController(this);

            }

            // 在打开炼丹界面时加载丹药
            alchemyController.loadPillsOnClick();
            // 显示炼丹界面并等待其关闭
            alchemyStage.showAndWait();
        } catch (IOException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
        }
    }

    /**
     * 扣除灵气的方法
     * @param amount 要扣除的灵气数量
     * @return 如果扣除成功返回 true，否则返回 false
     */
    public boolean deductQi(int amount) {
        if (amount < 0) {
            // 防止负数输入
            return false;
        }
        if (qi.get() >= amount) {
            // 如果灵气足够，扣除灵气并返回 true
            qi.set(qi.get() - amount);
            return true;
        }
        return false;
    }

    /**
     * 增加灵气增长速度的方法
     * @param rate 要增加的灵气增长速度
     */
    public void increaseQiRate(double rate) {
        // 增加灵气增长速度
        qiRate.set(qiRate.get() + rate);
    }

    /**
     * 更新灵气值的方法
     * @param amount 要增加或减少的灵气数量
     */
    public void updateQi(int amount) {
        // 更新灵气值
        qi.set(qi.get() + amount);
    }
    private Map<String, AlchemyController.PillData> savedPills = new LinkedHashMap<>(); // 新增字段存储丹药数据
    // 新增方法：提供给其他类获取丹药数据
    public Map<String, AlchemyController.PillData> getSavedPills() {
        return savedPills;
    }


    // 新增方法：接收炼丹界面关闭时传递的数据
    public void savePillsData(Map<String, AlchemyController.PillData> pills) {
        savedPills.clear();
        pills.forEach((id, data) -> {
            // 深拷贝每个丹药对象，避免引用问题
            AlchemyController.PillData copiedData = new AlchemyController.PillData(data.cost, data.rate,data.successRateImpact);
            copiedData.count = data.count;
            savedPills.put(id, copiedData);
        });
        System.out.println("[主控制器] 已保存丹药数据: " + savedPills.size() + " 条");
    }
    private void initializeAlchemyController() {
        try {
            if (alchemyController == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AlchemyView.fxml"));
                Parent root = loader.load();
                alchemyController = loader.getController();
                alchemyController.setMainController(this);
                System.out.println("[初始化] 炼丹控制器已加载");
            }
        } catch (IOException e) {
            System.err.println("[错误] 初始化炼丹控制器失败:");
            e.printStackTrace();
        }
    }
}
