package com.example.demo;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomEventHandler {
    private static final double TRIGGER_CHANCE = 0.5; // 0.5%触发概率
    private static final Random random = new Random();

    private Controller mainController;

    public RandomEventHandler(Controller controller) {
        this.mainController = controller;
    }

    // **随机事件检查方法**
    public void checkRandomEvent() {
        if (random.nextDouble() < TRIGGER_CHANCE) {
            triggerRandomEvent();
        }
    }

    // **触发随机事件的方法**
    private void triggerRandomEvent() {
        // **定义三种事件**
        List<Event> events = new ArrayList<>();
        events.add(new Event("灵气波动", "灵气突然波动,运用功法好像能改变灵气的吸收程度", createQiOptions()));
        events.add(new Event("神秘商人", "神秘商人出现", createMerchantOptions()));
        events.add(new Event("秘境探险", "发现秘境入口", createAdventureOptions()));

        // **随机选择一个事件**
        Event selectedEvent = events.get(random.nextInt(events.size()));

        // **显示事件弹窗**
        showEventAlert(selectedEvent);
    }

    // **创建灵气波动事件的选项**
    private List<EventOption> createQiOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("强化筋脉,稳固身形", getQiChangeRange()[0], getQiChangeRange()[1]));
        options.add(new EventOption("放松穴位,任其吸入", getQiChangeRange()[0] * -1, getQiChangeRange()[1] * -1)); // 负数表示失去
        options.add(new EventOption("集中精神,进行炼化", 0, 0)); // 无变化
        return options;
    }

    // **创建神秘商人事件的选项**
    private List<EventOption> createMerchantOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("购买丹药，花费灵气", getQiChangeRange()[0] * -1, getQiChangeRange()[1] * -1)); // 负数表示失去
        options.add(new EventOption("出售灵气，获得灵气", getQiChangeRange()[0], getQiChangeRange()[1])); // 正数表示获得
        options.add(new EventOption("交易失败，无任何变化", 0, 0)); // 无变化
        return options;
    }

    // **创建秘境探险事件的选项**
    private List<EventOption> createAdventureOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("探险成功，获得灵气", getQiChangeRange()[0], getQiChangeRange()[1]));
        options.add(new EventOption("探险失败，失去灵气", getQiChangeRange()[0] * -1, getQiChangeRange()[1] * -1)); // 负数表示失去
        options.add(new EventOption("探险途中获得特殊丹药", 0, 0)); // 无变化
        return options;
    }

    // **显示事件弹窗的方法**
    private void showEventAlert(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(event.getName());
        alert.setHeaderText(event.getDescription());
        alert.setContentText("请选择一个选项：");

        // **添加选项按钮**
        for (EventOption option : event.getOptions()) {
            ButtonType buttonType = new ButtonType(option.getDescription());
            alert.getButtonTypes().add(buttonType);
        }

        // **显示弹窗并等待用户选择**
        ButtonType result = alert.showAndWait().orElse(null);

        if (result != null) {
            // **根据用户选择执行相应操作**
            for (EventOption option : event.getOptions()) {
                if (result.getText().equals(option.getDescription())) {
                    handleEventResult(option);
                    break;
                }
            }
        }
    }

    // **处理事件结果的方法**
    private void handleEventResult(EventOption option) {
        // **计算灵气变化值**
        int change = calculateQiChange(option.getMinReward(), option.getMaxReward());

        // **处理奖励或惩罚**
        if (change > 0) {
            // **增加灵气**
            mainController.updateQi(change);
            showRewardAlert("恭喜你！获得了 " + change + " 点灵气。");
        } else if (change < 0) {
            // **尝试扣除灵气**
            if (mainController.deductQi(-change)) {
                showRewardAlert("很遗憾！你失去了 " + (-change) + " 点灵气。");
            } else {
                // **灵气不足，提示用户**
                showRewardAlert("灵气不足，无法扣除 " + (-change) + " 点灵气。");
            }
        } else {
            // **获得特殊丹药**
            showRewardAlert("恭喜你！获得了一颗特殊丹药。");
        }
    }

    // **计算灵气变化值的方法**
    private int calculateQiChange(int min, int max) {
        if (min == 0 && max == 0) {
            return 0;
        }
        if (min > max) {
            // 交换 min 和 max
            int temp = min;
            min = max;
            max = temp;
        }

        // 确保范围有效
        if (max - min + 1 <= 0) {
            // 范围无效，返回 0
            return 0;
        }
        // **随机选择一个值**
        return random.nextInt(max - min + 1) + min;
    }

    // **根据当前境界等级获取灵气变化范围的方法**
    private int[] getQiChangeRange() {
        int level = mainController.getStageLevel();
        return switch (level) {
            case 0 -> // 凡人
                    new int[]{10, 50};
            case 1 -> // 炼气
                    new int[]{100, 500};
            case 2 -> // 筑基
                    new int[]{500, 1500};
            case 3 -> // 金丹
                    new int[]{1000, 3000};
            case 4 -> // 元婴
                    new int[]{2000, 5000};
            case 5 -> // 化神
                    new int[]{3000, 8000};
            case 6 -> // 渡劫
                    new int[]{5000, 12000};
            case 7 -> // 大乘
                    new int[]{8000, 20000};
            case 8 -> // 大罗金仙
                    new int[]{10000, 30000};
            default -> new int[]{10, 50};
        };
    }

    // **显示奖励弹窗的方法**
    private void showRewardAlert(String message) {
        Alert rewardAlert = new Alert(Alert.AlertType.INFORMATION);
        rewardAlert.setTitle("奖励");
        rewardAlert.setHeaderText("事件奖励");
        rewardAlert.setContentText(message);
        rewardAlert.showAndWait();
    }
}
