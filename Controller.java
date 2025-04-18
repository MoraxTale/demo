package com.example.demo;

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
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.Map;

public class Controller {
    // === 常量定义 ===
    public static final double BASE_SUCCESS_RATE = 0.8;
    public static final double DECAY_FACTOR = 0.8;
    public static final int BREAKTHROUGH_COST = 10000;

    // 界面元素绑定
    @FXML private Label lblStage, lblQi, lblQiRate, lblSuccessRate;
    @FXML private Button btnCultivate, btnAlchemy, btnBreakthrough;

    // 核心属性
    private final IntegerProperty qi = new SimpleIntegerProperty(0);
    private final DoubleProperty qiRate = new SimpleDoubleProperty(1.0);
    private final StringProperty currentStage = new SimpleStringProperty("凡人");
    private final DoubleProperty successRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);
    private int stageLevel = 0;
    private final String[] STAGES = {"凡人", "炼气", "筑基", "金丹", "元婴", "化神", "渡劫", "大乘", "大罗金仙"};

    // 控制器和窗口对象
    private AlchemyController alchemyController;
    private TreasureController treasureController;
    private TreasureShopController treasureShopController;
    private Stage alchemyStage, treasureStage, treasureShopStage;
    private RandomEventHandler randomEventHandler;

    // === 初始化方法 ===
    @FXML
    private void initialize() {
        // 属性绑定
        lblQi.textProperty().bind(qi.asString("灵气：%d"));
        lblQiRate.textProperty().bind(qiRate.asString("增速：%.1f/s"));
        lblStage.textProperty().bind(currentStage);
        lblSuccessRate.textProperty().bind(successRate.asString("成功率：%.1f%%"));

        // 初始化处理器和界面
        randomEventHandler = new RandomEventHandler(this);
        initTreasurePanel();
        initTreasureShop();
        startAutoQiGrowth(); // 优化：仅保留一次调用
    }

    // === 核心功能方法 ===

    // 初始化炼丹界面
    private void initAlchemyPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AlchemyView.fxml"));
            Parent root = loader.load();
            alchemyController = loader.getController();
            alchemyController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 初始化法宝界面
    private void initTreasurePanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureView.fxml")); // 正确加载背包界面
            Parent root = loader.load();
            treasureController = loader.getController(); // 正确获取 TreasureController
            treasureController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 初始化法宝商店界面
    private void initTreasureShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureShopView.fxml"));
            Parent root = loader.load();
            // 修改：确保获取正确的控制器类型
            treasureShopController = loader.getController();
            treasureShopController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === 界面操作方法 ===

    // 打开法宝界面（修复：添加@FXML和public修饰符）
    @FXML
    public void openTreasurePanel() {
        try {
            if (treasureStage != null && treasureStage.isShowing()) {
                treasureStage.requestFocus();
                return;
            }

            if (treasureStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureView.fxml"));
                Parent root = loader.load();
                treasureStage = new Stage();
                treasureStage.setTitle("我的法宝");
                treasureStage.initModality(Modality.APPLICATION_MODAL);
                treasureStage.initOwner(lblQi.getScene().getWindow());
                treasureStage.setOnHidden(event -> treasureStage = null);
                treasureStage.setScene(new Scene(root));
            }
            treasureStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 打开法宝商店（修复：添加@FXML和public修饰符）
    @FXML
    public void openTreasureShop() {
        try {
            if (treasureShopStage != null && treasureShopStage.isShowing()) {
                treasureShopStage.requestFocus();
                return;
            }

            if (treasureShopStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureShopView.fxml"));
                Parent root = loader.load();
                treasureShopStage = new Stage();
                treasureShopStage.setTitle("法宝商店");
                treasureShopStage.initModality(Modality.APPLICATION_MODAL);
                treasureShopStage.initOwner(lblQi.getScene().getWindow());
                treasureShopStage.setOnHidden(event -> treasureShopStage = null);
                treasureShopStage.setScene(new Scene(root));
            }
            treasureShopStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === 法宝管理方法 ===
    public void addTreasureToBackpack(TreasureData treasure) {
        if (treasureController != null) {
            treasureController.getTreasures().put(treasure.getName(), treasure);
            treasureController.updateTreasureDisplay();
        }
    }

    // === 原有功能方法 ===

    // 保存游戏（集成法宝数据）
    public void saveGame(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            GameState state = new GameState(
                    qi.get(),
                    qiRate.get(),
                    alchemyController != null ? alchemyController.getPills() : null,
                    treasureController != null ? treasureController.getTreasures() : null,
                    stageLevel
            );
            oos.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载游戏（集成法宝数据）
    public void loadGame(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GameState state = (GameState) ois.readObject();
            qi.set(state.getQi());
            qiRate.set(state.getQiRate());
            stageLevel = state.getStageLevel();
            currentStage.set(STAGES[stageLevel]);
            successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));

            if (alchemyController != null) {
                alchemyController.setPills(state.getPills());
            }

            if (treasureController != null) {
                treasureController.setTreasures(state.getTreasures());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 渡劫逻辑
    @FXML
    private void breakthrough() {
        if (qi.get() < BREAKTHROUGH_COST) {
            new Alert(Alert.AlertType.WARNING, "灵气不足！需要" + BREAKTHROUGH_COST + "灵气").showAndWait();
            return;
        }

        if (Math.random() < successRate.get()) {
            if (stageLevel < STAGES.length - 1) {
                stageLevel++;
                currentStage.set(STAGES[stageLevel]);
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));
                qi.set(qi.get() - BREAKTHROUGH_COST);
                new Alert(Alert.AlertType.INFORMATION, "渡劫成功！当前境界：" + STAGES[stageLevel]).showAndWait();
            }
        } else {
            successRate.set(Math.min(successRate.get() + 0.1, 1.0));
            qi.set(qi.get() - BREAKTHROUGH_COST);
            new Alert(Alert.AlertType.ERROR, "渡劫失败！下次成功率：" + String.format("%.1f%%", successRate.get() * 100)).showAndWait();
        }
    }

    // 修炼方法
    @FXML
    private void cultivate() {
        qi.set(qi.get() + 1000);
        randomEventHandler.checkRandomEvent();
    }

    // 打开炼丹界面
    @FXML
    private void openAlchemyPanel() {
        try {
            if (alchemyStage != null && alchemyStage.isShowing()) {
                alchemyStage.requestFocus();
                return;
            }

            if (alchemyStage == null) {
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
            alchemyController.loadPillsOnClick();
            alchemyStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === 工具方法 ===
    public boolean deductQi(int amount) {
        if (qi.get() >= amount) {
            qi.set(qi.get() - amount);
            return true;
        }
        return false;
    }

    public void increaseQiRate(double rate) {
        qiRate.set(qiRate.get() + rate);
    }

    public void updateQi(int amount) {
        qi.set(qi.get() + amount);
    }

    // 文件操作
    @FXML
    private void handleSave(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存游戏");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("修仙存档", "*.xs"));
        File file = fileChooser.showSaveDialog(lblQi.getScene().getWindow());
        if (file != null) saveGame(file.getAbsolutePath());
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("加载游戏");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("修仙存档", "*.xs"));
        File file = fileChooser.showOpenDialog(lblQi.getScene().getWindow());
        if (file != null) loadGame(file.getAbsolutePath());
    }

    // 自动灵气增长
    private void startAutoQiGrowth() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000) {
                    qi.set(qi.get() + (int) qiRate.get());
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }
}
