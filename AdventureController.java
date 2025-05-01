package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;

public class AdventureController {
    @FXML private Label lblResult;
    @FXML private Label lblTimer;
    private Controller mainController;
    private static final int BASE_COST = 300;
    private AdventureState adventureState = new AdventureState();
    private AnimationTimer timer;

    public void initialize() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateAdventureTimer();
            }
        };
    }
    private void startAdventure(int areaNumber) {
        if (!checkUnlocked(areaNumber)) {
            showAlert("境界不足！需达到" + (areaNumber*2) + "重境界");
            return;
        }
        if (adventureState == null) {
            adventureState = new AdventureState();
        }

        if (adventureState.getDailyCounts().getOrDefault(areaNumber, 0) >= getMaxAttempts()) {
            showAlert("今日次数已用尽！");
            return;
        }

        adventureState.setCurrentArea(areaNumber);
        adventureState.setStartTime(System.currentTimeMillis());
        timer.start();

        // 禁用按钮
        getAreaButton(areaNumber).setDisable(true);
    }

    private void updateAdventureTimer() {
        if (adventureState.getStartTime() == 0) return;

        long elapsed = System.currentTimeMillis() - adventureState.getStartTime();
        long remaining = getAreaTime(adventureState.getCurrentArea()) - elapsed;

        if (remaining <= 0) {
            completeAdventure();
            return;
        }

        lblTimer.setText(formatTime(remaining));
    }

    private void completeAdventure() {
        timer.stop();
        int area = adventureState.getCurrentArea();

        // 计算奖励
        int baseReward = 1000 * area;
        double speedBonus = mainController.getQiRate() * 3600; // 每小时收益
        mainController.updateQi((int) (baseReward + speedBonus));

        // 更新次数
        adventureState.getDailyCounts().put(area,
                adventureState.getDailyCounts().getOrDefault(area, 0) + 1);
        adventureState.incrementTotalCompleted();

        // 重置状态
        adventureState.setStartTime(0);
        getAreaButton(area).setDisable(false);
    }

    private String formatTime(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        return String.format("%02d:%02d", hours, minutes);
    }

    private long getAreaTime(int area) {
        return (long) (3600000 * Math.pow(2, area-1)); // 区域时间递增
    }

    private boolean checkUnlocked(int area) {
        return mainController.getStageLevel() >= area * 2;
    }

    private int getMaxAttempts() {
        int base = 1;
        // 法宝增加次数
        for (TreasureData t : mainController.getTreasureController().getTreasures().values()) {
            if ("ADVENTURE_ATTEMPTS".equals(t.getEffectType())) {
                base += t.getEffectValue();
            }
        }
        return base;
    }

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    private void handleAdventure(int areaNumber) {
        if (!mainController.deductQi(BASE_COST * areaNumber)) {
            showAlert("灵气不足！需要 " + (BASE_COST * areaNumber) + " 灵气");
            return;
        }

        double successRate = 0.7 - (areaNumber * 0.1);
        boolean success = Math.random() < successRate;

        if (success) {
            int reward = 1000 * areaNumber;
            mainController.updateQi(reward);
            lblResult.setText(String.format("区域%d探索成功！\n获得%d灵气", areaNumber, reward));
        } else {
            lblResult.setText(String.format("区域%d遭遇危机！\n损失%d灵气", areaNumber, BASE_COST * areaNumber));
        }
    }

    // 修改后的区域处理方法（补充"同理"部分）
    @FXML
    private void handleArea1() { startAdventure(1); }

    @FXML
    private void handleArea2() { startAdventure(2); }

    @FXML
    private void handleArea3() { startAdventure(3); }

    @FXML
    private void handleArea4() { startAdventure(4); }

    @FXML
    private void handleArea5() { startAdventure(5); }

    // 获取对应区域的按钮
    private Button getAreaButton(int areaNumber) {
        return switch (areaNumber) {
            case 1 -> (Button) lblResult.getParent().lookup("#area1Btn");
            case 2 -> (Button) lblResult.getParent().lookup("#area2Btn");
            case 3 -> (Button) lblResult.getParent().lookup("#area3Btn");
            case 4 -> (Button) lblResult.getParent().lookup("#area4Btn");
            case 5 -> (Button) lblResult.getParent().lookup("#area5Btn");
            default -> throw new IllegalArgumentException("无效区域编号");
        };
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
    // 在AdventureController.java中添加状态加载方法
    public void loadAdventureState(AdventureState state) {
        this.adventureState = (state != null) ? state : new AdventureState();
        if (adventureState.getStartTime() > 0) {
            int currentArea = adventureState.getCurrentArea();
            getAreaButton(currentArea).setDisable(true);
            // 更新计时显示
            long remaining = getAreaTime(currentArea) - (System.currentTimeMillis() - adventureState.getStartTime());
            lblTimer.setText(formatTime(remaining));
            timer.start();
        }

        // 初始化界面状态
        for (int i=1; i<=5; i++) {
            Button btn = getAreaButton(i);
            if (btn != null) {
                btn.setDisable(!checkUnlocked(i));
            }
        }
    }
    public AdventureState getAdventureState() {
        return this.adventureState;
    }
}
