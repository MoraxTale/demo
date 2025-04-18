package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {
    private AlchemyController alchemyController;
    private static final double BASE_SUCCESS_RATE = 0.8;
    private static final double DECAY_FACTOR = 0.8;
    private static final int BREAKTHROUGH_COST = 10000;
    private final DoubleProperty actualSuccessRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);

    @FXML private Label lblStage;
    @FXML private Label lblQi;
    @FXML private Label lblQiRate;
    @FXML private Label lblSuccessRate;
    @FXML private Button btnCultivate;
    @FXML private Button btnAlchemy;
    @FXML private Button btnBreakthrough;

    private final IntegerProperty qi = new SimpleIntegerProperty(0);
    private final DoubleProperty qiRate = new SimpleDoubleProperty(1.0);
    private RandomEventHandler randomEventHandler;
    private Stage alchemyStage;
    private final StringProperty currentStage = new SimpleStringProperty("凡人");
    private final DoubleProperty successRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);
    private int stageLevel = 0;
    private final String[] STAGES = {"凡人", "炼气", "筑基", "金丹", "元婴", "化神", "渡劫", "大乘", "大罗金仙"};
    private Map<String, AlchemyController.PillData> savedPills = new LinkedHashMap<>();

    // ==== 初始化方法 ====
    @FXML
    private void initialize() {
        lblQi.textProperty().bind(qi.asString("灵气：%d"));
        lblQiRate.textProperty().bind(qiRate.asString("灵气增长速度：%.1f/s"));
        lblStage.textProperty().bind(currentStage);
        lblSuccessRate.textProperty().bind(Bindings.format("渡劫成功率：%.1f%%", actualSuccessRate.multiply(100)));

        startAutoQiGrowth();
        btnCultivate.setOnAction(event -> cultivate());
        btnAlchemy.setOnAction(event -> openAlchemyPanel());
        randomEventHandler = new RandomEventHandler(this);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AlchemyView.fxml"));
            Parent root = loader.load();
            alchemyController = loader.getController();
            alchemyController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("初始化炼丹控制器失败。");
        }
    }

    // ==== 自动灵气增长逻辑 ====
    private void startAutoQiGrowth() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000) { // 1秒间隔
                    qi.set(qi.get() + (int) qiRate.get());
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    // ==== 修炼按钮事件 ====
    @FXML
    private void cultivate() {
        qi.set(qi.get() + 1000);
        randomEventHandler.checkRandomEvent();
    }

    // ==== 打开炼丹界面 ====
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

    // ==== 更新实际成功率 ====
    public void updateActualSuccessRate() {
        double pillImpact = 0.0;
        if (alchemyController != null) {
            for (AlchemyController.PillData pill : alchemyController.getPills().values()) {
                pillImpact += pill.successRateImpact * pill.count;
            }
        }
        actualSuccessRate.set(Math.min(successRate.get() + pillImpact, 1.0));
    }

    // ==== 渡劫逻辑 ====
    @FXML
    private void breakthrough() {
        if (qi.get() < BREAKTHROUGH_COST) {
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

                if (alchemyController != null) {
                    alchemyController.getPills().values().forEach(pill -> {
                        pill.successRateImpact *= 0.1;
                        pill.count = 0;
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

    // ==== 数据持久化 ====
    public void saveGame(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            long currentTime = System.currentTimeMillis();
            GameState state = new GameState(
                    qi.get(),
                    qiRate.get(),
                    savedPills,
                    stageLevel,
                    currentTime
            );
            oos.writeObject(state);
            System.out.println("[保存] 存档时间已记录: " + currentTime);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("保存游戏时出现 IO 错误。");
        }
    }

    public void loadGame(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GameState state = (GameState) ois.readObject();
            qi.set(state.getQi());
            qiRate.set(state.getQiRate());
            stageLevel = state.getStageLevel();
            currentStage.set(STAGES[stageLevel]);
            successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));

            savedPills.clear();
            if (state.getPills() != null) {
                state.getPills().forEach((id, data) -> {
                    AlchemyController.PillData copiedData = new AlchemyController.PillData(
                            data.pillId,
                            data.pillName,
                            data.cost,
                            data.rate,
                            data.successRateImpact
                    );
                    copiedData.count = data.count;
                    savedPills.put(id, copiedData);
                });
            }

            if (alchemyController != null) {
                alchemyController.setPills(savedPills);
                updateActualSuccessRate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==== 辅助方法 ====
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

    public Map<String, AlchemyController.PillData> getSavedPills() {
        return savedPills;
    }

    public void savePillsData(Map<String, AlchemyController.PillData> pills) {
        savedPills.clear();
        pills.forEach((id, data) -> {
            AlchemyController.PillData copiedData = new AlchemyController.PillData(
                    data.pillId,
                    data.pillName,
                    data.cost,
                    data.rate,
                    data.successRateImpact
            );
            copiedData.count = data.count;
            savedPills.put(id, copiedData);
        });
        System.out.println("[主控制器] 已保存丹药数据: " + savedPills.size() + " 条");
    }

    public int getStageLevel() {
        return stageLevel;
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

    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存游戏");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("修仙存档", "*.xs"));
        File file = fileChooser.showSaveDialog(lblQi.getScene().getWindow());
        if (file != null) saveGame(file.getAbsolutePath());
    }

    @FXML
    private void handleLoad(javafx.event.ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("加载游戏");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("修仙存档", "*.xs"));
        File file = fileChooser.showOpenDialog(lblQi.getScene().getWindow());
        if (file != null) loadGame(file.getAbsolutePath());
    }
}
