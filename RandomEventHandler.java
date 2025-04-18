package com.example.demo;

// 导入 JavaFX 对话框相关类
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
// 导入 Java 集合框架中的 List 接口和 ArrayList 实现类
import java.util.ArrayList;
import java.util.List;
// 导入 Java 随机数生成类
import java.util.Random;

/**
 * 随机事件处理器类，负责处理游戏中的随机事件
 */
public class RandomEventHandler {
    // 随机事件触发概率，固定值
    private static final double TRIGGER_CHANCE = 0.05; // **0.5%触发概率**
    // 随机数生成器对象
    private static final Random random = new Random();
    // 主控制器对象，用于与主游戏逻辑交互
    private Controller mainController;

    /**
     * 构造方法，初始化随机事件处理器
     * @param controller 主控制器对象
     */
    public RandomEventHandler(Controller controller) {
        this.mainController = controller;
    }

    /**
     * 随机事件检查方法，检查是否触发随机事件
     */
    public void checkRandomEvent() {
        if (random.nextDouble() < TRIGGER_CHANCE) {
            // 如果随机数小于触发概率，触发随机事件
            triggerRandomEvent();
        }
    }

    /**
     * 触发随机事件的方法
     */
    private void triggerRandomEvent() {
        // 定义随机事件的选项列表
        List<EventOption> options = new ArrayList<>();
        // 添加获得 1000 灵气的选项
        options.add(new EventOption("获得1000灵气", 1000));
        // 添加失去 500 灵气的选项
        options.add(new EventOption("失去500灵气", -500));
        // 添加获得特殊丹药的选项
        options.add(new EventOption("获得特殊丹药", 0));

        // 显示事件弹窗，让用户选择选项
        showEventAlert(options);
    }

    /**
     * 显示事件弹窗的方法
     * @param options 随机事件的选项列表
     */
    private void showEventAlert(List<EventOption> options) {
        // 创建确认对话框对象
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // 设置对话框标题
        alert.setTitle("随机事件");
        // 设置对话框头部文本
        alert.setHeaderText("你触发了一个随机事件！");
        // 设置对话框内容文本
        alert.setContentText("请选择一个选项：");

        // 创建选项按钮
        ButtonType buttonType1 = new ButtonType(options.get(0).getDescription());
        ButtonType buttonType2 = new ButtonType(options.get(1).getDescription());
        ButtonType buttonType3 = new ButtonType(options.get(2).getDescription());
        // 将选项按钮添加到对话框中
        alert.getButtonTypes().setAll(buttonType1, buttonType2, buttonType3);

        // 显示对话框并等待用户选择
        ButtonType result = alert.showAndWait().orElse(null);

        if (result != null) {
            // 根据用户选择执行相应操作
            if (result.equals(buttonType1)) {
                handleEventResult(options.get(0).getReward());
            } else if (result.equals(buttonType2)) {
                handleEventResult(options.get(1).getReward());
            } else if (result.equals(buttonType3)) {
                handleEventResult(options.get(2).getReward());
            }
        }
    }

    /**
     * 处理事件结果的方法
     * @param reward 事件的奖励或惩罚值
     */
    private void handleEventResult(int reward) {
        if (reward > 0) {
            // 如果奖励值大于 0，增加灵气并显示奖励信息对话框
            mainController.updateQi(reward);
            showRewardAlert("恭喜你！获得了 " + reward + " 点灵气。");
        } else if (reward < 0) {
            // 如果奖励值小于 0，减少灵气并显示惩罚信息对话框
            mainController.updateQi(reward);
            showRewardAlert("很遗憾！你失去了 " + (-reward) + " 点灵气。");
        } else {
            // 如果奖励值等于 0，显示获得特殊丹药的信息对话框
            showRewardAlert("恭喜你！获得了一颗特殊丹药。");
        }
    }

    /**
     * 显示奖励弹窗的方法
     * @param message 奖励信息文本
     */
    private void showRewardAlert(String message) {
        // 创建信息对话框对象
        Alert rewardAlert = new Alert(Alert.AlertType.INFORMATION);
        // 设置对话框标题
        rewardAlert.setTitle("奖励");
        // 设置对话框头部文本
        rewardAlert.setHeaderText("事件奖励");
        // 设置对话框内容文本
        rewardAlert.setContentText(message);
        // 显示对话框并等待用户关闭
        rewardAlert.showAndWait();
    }
}
