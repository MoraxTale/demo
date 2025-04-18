// 声明包名
package com.example.demo1;

// 导入 JavaFX 属性相关类，用于创建和管理可观察的属性

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;

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
        lblSuccessRate.textProperty().bind(successRate.asString("渡劫成功率：%.1f"));

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
            if (alchemyController == null) {
                // 如果炼丹控制器未初始化，创建不包含炼丹数据的游戏状态对象
                GameState state = new GameState(
                        qi.get(),
                        qiRate.get(),
                        null,
                        stageLevel
                );
                // 将游戏状态对象写入文件
                oos.writeObject(state);
            } else {
                // 创建包含炼丹数据的游戏状态对象
                GameState state = new GameState(
                        qi.get(),
                        qiRate.get(),
                        alchemyController.getPills(),
                        stageLevel
                );
                // 将游戏状态对象写入文件
                oos.writeObject(state);
            }
        } catch (IOException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 输出保存游戏时出现 IO 错误的信息
            System.err.println("保存游戏时出现 IO 错误。");
        }
    }

    /**
     * 加载游戏方法，从指定文件加载游戏状态
     * @param filePath 加载文件的路径
     */
    public void loadGame(String filePath) {
        try {
            // 创建文件对象
            File file = new File(filePath);
            if (file.length() == 0) {
                // 如果文件为空，输出提示信息并返回
                System.out.println("存档文件为空，无法加载。");
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                // 从文件中读取游戏状态对象
                GameState state = (GameState) ois.readObject();
                // 设置灵气值
                qi.set(state.getQi());
                // 设置灵气增长速度
                qiRate.set(state.getQiRate());
                // 加载境界等级
                stageLevel = state.getStageLevel();
                // 更新当前境界显示
                currentStage.set(STAGES[stageLevel]);
                // 更新渡劫成功率
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));
                if (alchemyController != null) {
                    // 如果炼丹控制器已初始化，设置炼丹数据
                    alchemyController.setPills(state.getPills());
                    // 输出游戏加载成功的信息
                    System.out.println("游戏加载成功，包含炼丹数据和境界信息。");
                } else {
                    // 输出炼丹控制器未初始化，无法加载炼丹数据的信息
                    System.err.println("炼丹控制器未初始化，无法加载炼丹数据。");
                }
            }
        } catch (EOFException e) {
            // 输出存档文件损坏，无法加载的信息
            System.err.println("存档文件损坏，无法加载。");
            // 打印异常堆栈信息
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
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
    private void handleSave(ActionEvent event) {
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
    private void handleLoad(ActionEvent event) {
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

        if (Math.random() < successRate.get()) {
            // 如果随机数小于渡劫成功率，渡劫成功
            if (stageLevel < STAGES.length - 1) {
                // 如果不是最高境界，提升境界等级
                stageLevel++;
                // 更新当前境界显示
                currentStage.set(STAGES[stageLevel]);
                // 更新渡劫成功率
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));
                // 扣除渡劫所需的灵气
                qi.set(qi.get() - BREAKTHROUGH_COST);
                // 显示渡劫成功的信息对话框
                new Alert(Alert.AlertType.INFORMATION,
                        "渡劫成功！当前境界：" + STAGES[stageLevel]).showAndWait();
            }
        } else {
            // 渡劫失败，增加下次渡劫成功率，但不超过 100%
            successRate.set(Math.min(successRate.get() + 0.1, 1.0));
            // 扣除渡劫所需的灵气
            qi.set(qi.get() - BREAKTHROUGH_COST);
            // 显示渡劫失败的信息对话框
            new Alert(Alert.AlertType.ERROR,
                    "渡劫失败！下次成功率：" + String.format("%.1f%%", successRate.get() * 100)).showAndWait();
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
            }
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
}
