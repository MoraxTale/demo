package com.example.demo1;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Random;
import java.util.*;
import java.util.stream.Collectors;

public class RandomEventHandler {
    private static final double TRIGGER_CHANCE = 0.9; // 0.5%触发概率
    static final Random random = new Random();
    private Controller mainController;
    private enum NpcLevel {
        WEAKER,      // 比玩家弱
        EQUAL,       // 与玩家相当
        STRONGER     // 比玩家强
    }
    // **随机事件检查方法**
    public void checkRandomEvent() {
        if (random.nextDouble() < TRIGGER_CHANCE) {
            System.out.println("[随机事件] 触发随机事件");
            triggerRandomEvent();
        } else {
            System.out.println("[随机事件] 未触发");
        }
    }
    // 获取随机丹药ID
    private String getRandomPillId() {
        if (mainController.getSavedPills().isEmpty()) {
            return null;
        }
        List<String> pillIds = new ArrayList<>(mainController.getSavedPills().keySet());
        return pillIds.get(random.nextInt(pillIds.size()));
    }

    // 处理丹药奖励

    private enum RewardType {
        QI,          // 灵气
        PILL,        // 丹药
        TREASURE,    // 法宝
        RATE_BOOST   // 修炼速度
    }
    public RandomEventHandler(Controller controller) {
        this.mainController = controller;
    }


        private void handlePillReward(int count) {
            int playerLevel = mainController.getStageLevel();
            int maxRewardLevel = Math.min(25, playerLevel + 1);

            // 获取所有可奖励的丹药
            List<AlchemyController.PillData> rewardablePills = new ArrayList<>();
            Map<String, AlchemyController.PillData> savedPills = mainController.getSavedPills();

            for (AlchemyController.PillData pill : savedPills.values()) {
                AlchemyController.PillConfig config = mainController.getAlchemyController().getPillConfig(pill.pillId);
                if (config != null && config.getLevel() <= maxRewardLevel) {
                    rewardablePills.add(pill);
                }
            }

            if (!rewardablePills.isEmpty()) {
                AlchemyController.PillData pill = rewardablePills.get(random.nextInt(rewardablePills.size()));
                pill.count += count;
                String currentEffects = mainController.getPillStatus();
                showRewardAlert("✨ 获得 " + count + " 颗" + pill.pillName + "\n\n" + currentEffects);
            } else {
                int qiGain = getQiChangeRange()[1] * count;
                mainController.updateQi(qiGain);
                showRewardAlert("获得 " + qiGain + " 点灵气");
            }
        }

    private void handlePillPenalty(int count) {
        String pillId = getRandomPillId();
        if (pillId != null && mainController.getSavedPills().get(pillId).count > 0) {
            AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
            int actualLoss = Math.min(count, pill.count);
            double prevRate = pill.rate * pill.count;
            double prevSuccessRate = pill.successRateImpact * pill.count;
            pill.count -= actualLoss;
            mainController.applyPillEffects(); // 重新计算所有丹药效果
            double rateChange = prevRate - (pill.rate * pill.count);
            double successRateChange = prevSuccessRate - (pill.successRateImpact * pill.count);

            showRewardAlert(String.format("失去 %d 颗%s\n效果变化：修炼速度-%.1f，渡劫成功率-%.2f%%",
                    actualLoss, pill.pillName,
                    rateChange,
                    successRateChange * 100));
        } else {
            int qiLoss = getQiChangeRange()[1] * count / 2;
            if (mainController.deductQi(qiLoss)) {
                showRewardAlert("失去 " + qiLoss + " 点灵气");
            } else {
                showRewardAlert("惩罚减免(无丹药且灵气不足)");
            }
        }
    }
    // **随机事件检查方法**


    // **触发随机事件的方法**
    private void triggerRandomEvent() {
        // **定义三种事件**
        List<Event> events = new ArrayList<>();
        events.add(new Event("灵气波动", "灵气突然波动,运用功法好像能改变灵气的吸收程度", createQiOptions()));
        events.add(new Event("神秘商人", "神秘商人出现", createMerchantOptions()));
        events.add(new Event("秘境探险", "发现秘境入口", createAdventureOptions()));
        events.add(new Event("道友切磋", "一位道友邀请你切磋", createDuelOptions()));
        events.add(new Event("妖兽袭击", "一只妖兽突然袭击你", createMonsterAttackOptions()));
        events.add(new Event("秘境探索", "发现一处未知秘境", createSecretRealmOptions()));
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
    // 新增：创建道友切磋选项
    private List<EventOption> createDuelOptions() {
        NpcLevel npcLevel = getRandomNpcLevel();
        String npcDesc = getNpcDescription(npcLevel);

        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("接受切磋("+npcDesc+")", 0, 0)); // 结果由handleDuelResult处理
        options.add(new EventOption("婉拒邀请", 0, 0));
        options.add(new EventOption("走为上计", 0, 0));
        return options;
    }
    // 新增：创建妖兽袭击选项
    private List<EventOption> createMonsterAttackOptions() {
        NpcLevel monsterLevel = getRandomNpcLevel();
        String monsterDesc = getMonsterDescription(monsterLevel);

        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("正面迎战("+monsterDesc+")", 0, 0));
        options.add(new EventOption("使用法宝防御", 0, 0));
        options.add(new EventOption("逃跑", 0, 0));
        return options;
    }

    // 新增：创建秘境探索选项
    private List<EventOption> createSecretRealmOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("深入探索", 0, 0));
        options.add(new EventOption("谨慎探查", 0, 0));
        options.add(new EventOption("留下标记后离开", 0, 0));
        return options;
    }
    // **创建神秘商人事件的选项**
    private List<EventOption> createMerchantOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("购买丹药", 0, 0));  // 触发二级菜单
        options.add(new EventOption("出售一颗丹药", getQiChangeRange()[0] * 2, getQiChangeRange()[1] * 2));
        options.add(new EventOption("抢夺丹药", 0, 0));
        return options;
    }

    // **创建秘境探险事件的选项**
    private List<EventOption> createAdventureOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("遇见岔路，走左边", getQiChangeRange()[0], getQiChangeRange()[1]));
        options.add(new EventOption("遇见岔路，走右边", getQiChangeRange()[0] * -1, getQiChangeRange()[1] * -1)); // 负数表示失去
        options.add(new EventOption("探险途中误入密室", 0, 0)); // 无变化
        return options;
    }

    // **显示事件弹窗的方法**
    private void showEventAlert(Event event) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(event.getName());
        alert.setHeaderText(event.getDescription());
        // 只添加三个选项按钮
        ButtonType option1 = new ButtonType(event.getOptions().get(0).getDescription());
        ButtonType option2 = new ButtonType(event.getOptions().get(1).getDescription());
        ButtonType option3 = new ButtonType(event.getOptions().get(2).getDescription());

        alert.getButtonTypes().setAll(option1, option2, option3);

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            // 根据事件类型调用不同的处理方法
            if (event.getName().equals("道友切磋")) {
                handleDuelResult(buttonType);
            } else if (event.getName().equals("妖兽袭击")) {
                handleMonsterAttackResult(buttonType);
            } else if (event.getName().equals("秘境探索")) {
                handleSecretRealmResult(buttonType);
            } else {
                handleNormalEventResult(event, buttonType);
            }
        });
    }

    // 新增：处理切磋结果
    private void handleDuelResult(ButtonType choice) {
        NpcLevel npcLevel = getRandomNpcLevel();
        String choiceText = choice.getText();

        if (choiceText.startsWith("接受切磋")) {
            boolean playerWins = checkDuelOutcome(npcLevel);

            if (playerWins) {
                // 胜利奖励：获得1-3颗随机丹药
                int pillCount = 1 + random.nextInt(3);
                handlePillReward(pillCount);
            } else {
                // 失败惩罚
                handlePillPenalty(1);
            }
        } else if (choiceText.equals("婉拒邀请")) {
            showRewardAlert("你礼貌地拒绝了切磋邀请，对方表示理解");
        } else {
            showRewardAlert("你迅速离开了现场，避免了可能的冲突");
        }
    }

    // 新增：处理妖兽袭击结果
    private void handleMonsterAttackResult(ButtonType choice) {
        NpcLevel monsterLevel = getRandomNpcLevel();
        String choiceText = choice.getText();

        if (choiceText.startsWith("正面迎战")) {
            boolean playerWins = checkDuelOutcome(monsterLevel);

            if (playerWins) {
                // 胜利奖励：获得2-5颗随机丹药
                int pillCount = 2 + random.nextInt(4);
                handlePillReward(pillCount);
            } else {
                // 失败惩罚：失去2颗随机丹药或灵气
                handlePillPenalty(2);
            }
        } else if (choiceText.equals("使用法宝防御")) {
            showRewardAlert("成功使用法宝击退妖兽，法宝略有损伤");
        } else {
            boolean escapeSuccess = random.nextDouble() > 0.3;
            if (escapeSuccess) {
                showRewardAlert("你成功逃脱了妖兽的追击");
            } else {
                // 逃跑失败惩罚：失去1颗随机丹药或灵气
                handlePillPenalty(1);
            }
        }
    }

    // 新增：处理秘境探索结果
    private void handleSecretRealmResult(ButtonType choice) {
        String choiceText = choice.getText();

        if (choiceText.equals("深入探索")) {
            double successChance = 0.4;
            if (random.nextDouble() < successChance) {
                int qiGain = getQiChangeRange()[1] * 4;
                mainController.updateQi(qiGain);
                showRewardAlert("秘境深处发现珍稀宝物！获得" + qiGain + "灵气");
            } else {
                int qiLoss = getQiChangeRange()[1] / 2;
                mainController.deductQi(qiLoss);
                showRewardAlert("探索中遭遇陷阱，损失" + qiLoss + "灵气");
            }
        } else if (choiceText.equals("谨慎探查")) {
            int qiGain = getQiChangeRange()[1];
            mainController.updateQi(qiGain);
            showRewardAlert("谨慎探索获得" + qiGain + "灵气");
        } else {
            showRewardAlert("你离开了秘境，没有冒险探索");
        }
    }

    // 新增：判断切磋/战斗结果
    private boolean checkDuelOutcome(NpcLevel npcLevel) {
        int playerLevel = mainController.getStageLevel();
        int npcLevelValue = playerLevel;

        switch (npcLevel) {
            case WEAKER:
                npcLevelValue = Math.max(0, playerLevel - 1 - random.nextInt(2));
                break;
            case EQUAL:
                npcLevelValue = playerLevel;
                break;
            case STRONGER:
                npcLevelValue = Math.min(8, playerLevel + 1 + random.nextInt(2));
                break;
        }

        // 基础胜率50%，每高一级增加15%胜率，每低一级减少15%胜率
        double winChance = 0.5 + (playerLevel - npcLevelValue) * 0.15;
        return random.nextDouble() < winChance;
    }

    // 新增：获取随机NPC境界
    private NpcLevel getRandomNpcLevel() {
        double rand = random.nextDouble();
        if (rand < 0.4) return NpcLevel.WEAKER;    // 40%几率较弱
        else if (rand < 0.8) return NpcLevel.EQUAL;  // 40%几率相当
        else return NpcLevel.STRONGER;               // 20%几率更强
    }

    // 新增：获取NPC描述
    private String getNpcDescription(NpcLevel level) {
        switch (level) {
            case WEAKER: return "境界较低";
            case EQUAL: return "境界相当";
            case STRONGER: return "境界较高";
            default: return "";
        }
    }

    // 新增：获取妖兽描述
    private String getMonsterDescription(NpcLevel level) {
        switch (level) {
            case WEAKER: return "较弱妖兽";
            case EQUAL: return "普通妖兽";
            case STRONGER: return "强大妖兽";
            default: return "";
        }
    }
    //新增显示可购买丹药的弹窗方法
    private void showPillPurchaseDialog() {
        int currentQi = mainController.getQi();

        // 1. 检查炼丹控制器
        if (mainController.getAlchemyController() == null) {
            showRewardAlert("炼丹系统未准备好");
            return;
        }

        // 2. 创建弹窗
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("丹药商店");
        alert.setHeaderText(String.format("当前灵气: %d", currentQi));

        // 3. 创建5x5网格布局
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // 4. 填充可购买丹药
        Map<String, AlchemyController.PillData> allPills =
                mainController.getAlchemyController().getPills();
        List<AlchemyController.PillData> affordablePills = allPills.values().stream()
                .filter(pill -> pill.cost <= currentQi)
                .sorted(Comparator.comparingInt(p -> p.cost))
                .toList();

        int index = 0;
        for (AlchemyController.PillData pill : affordablePills) {
            if (index >= 50) break; // 最多显示25种

            Button pillButton = new Button();
            pillButton.setText(String.format("%s\n%d灵气", pill.pillName, pill.cost));
            pillButton.setStyle("-fx-font-size: 12; -fx-pref-width: 120; -fx-wrap-text: true;");
            pillButton.setOnAction(e -> {
                if (mainController.deductQi(pill.cost)) {
                    mainController.getSavedPills()
                            .computeIfAbsent(pill.pillId, k ->
                                    new AlchemyController.PillData(
                                            pill.pillId, pill.pillName,
                                            pill.cost, pill.rate, pill.successRateImpact,pill.level))
                            .count++;
                    mainController.applyPillEffects();
                    ((Stage) pillButton.getScene().getWindow()).close();
                    showRewardAlert("成功购买1颗" + pill.pillName);
                } else {
                    showRewardAlert("灵气不足！");
                }
            });

            int row = index / 5;
            int col = index % 5;
            grid.add(pillButton, col, row);
            index++;
        }

        // 5. 如果没有可购买的丹药
        if (affordablePills.isEmpty()) {
            Label emptyLabel = new Label("没有买得起的丹药");
            grid.add(emptyLabel, 0, 0, 5, 1);
        }

        // 6. 添加返回按钮
        ButtonType returnButton = new ButtonType("返回", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().add(returnButton);

        // 7. 设置弹窗内容
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(800, 600);

        // 8. 显示弹窗
        alert.showAndWait();
    }
    // **处理事件结果的方法**
    private void handleNormalEventResult(Event event, ButtonType choice) {
        for (EventOption option : event.getOptions()) {
            if (choice.getText().equals(option.getDescription())) {
                if (choice.getText().equals("购买丹药")) {
                    showPillPurchaseDialog(); // 显示丹药购买二级菜单
                }
                else if (choice.getText().equals("出售一颗丹药")) {
                    if (mainController.getSavedPills().isEmpty()) {
                        showRewardAlert("没有可出售的丹药");
                        return;
                    }
                    String pillId = getRandomPillId();
                    AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);

                    if (pill != null && pill.count > 0) {
                        // 获得双倍价格的灵气
                        int gain = pill.cost * 2;
                        // 失去丹药效果
                        pill.count--;
                        mainController.applyPillEffects();
                        // 增加灵气
                        mainController.updateQi(gain);

                        showRewardAlert(String.format(
                                "出售1颗%s\n获得%d灵气(双倍价格)",
                                pill.pillName, gain
                        ));
                    } else {
                        showRewardAlert("出售失败，没有可出售的丹药");
                    }
                }
                else if (choice.getText().equals("抢夺丹药")) {
                    double successChance = 0.6;
                    if (random.nextDouble() < successChance) {
                        handlePillReward(2);
                    } else {
                        handlePillPenalty(1);
                    }
                } else {
                    int change = calculateQiChange(option.getMinReward(), option.getMaxReward());

                    if (change > 0) {
                        mainController.updateQi(change);
                        showRewardAlert("恭喜你！获得了 " + change + " 点灵气。");
                    } else if (change < 0) {
                        if (mainController.deductQi(-change)) {
                            showRewardAlert("很遗憾！你失去了 " + (-change) + " 点灵气。");
                        } else {
                            showRewardAlert("灵气不足，无法扣除 " + (-change) + " 点灵气。");
                        }
                    } else {
                        showRewardAlert("恭喜你！获得了一颗特殊丹药。");
                    }
                }
                break;
            }
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
            case 0 -> new int[]{10, 50};       // 凡人
            case 1 -> new int[]{100, 500};     // 炼气
            case 2 -> new int[]{500, 1500};    // 筑基
            case 3 -> new int[]{1000, 3000};   // 金丹
            case 4 -> new int[]{2000, 5000};   // 元婴
            case 5 -> new int[]{3000, 8000};   // 化神
            case 6 -> new int[]{5000, 12000};  // 渡劫
            case 7 -> new int[]{8000, 20000};  // 大乘
            case 8 -> new int[]{10000, 30000}; // 大罗金仙
            case 9 -> new int[]{15000, 40000}; // 仙君
            case 10 -> new int[]{20000, 50000}; // 仙王
            case 11 -> new int[]{30000, 70000}; // 仙帝
            case 12 -> new int[]{50000, 100000}; // 仙尊
            case 13 -> new int[]{80000, 150000}; // 仙圣
            case 14 -> new int[]{120000, 200000}; // 仙祖
            case 15 -> new int[]{180000, 300000}; // 道君
            case 16 -> new int[]{250000, 400000}; // 道王
            case 17 -> new int[]{350000, 600000}; // 道帝
            case 18 -> new int[]{500000, 800000}; // 道尊
            case 19 -> new int[]{700000, 1200000}; // 道圣
            case 20 -> new int[]{1000000, 2000000}; // 道祖
            case 21 -> new int[]{1500000, 3000000}; // 混元大罗金仙
            case 22 -> new int[]{2500000, 5000000}; // 混元无极金仙
            case 23 -> new int[]{4000000, 8000000}; // 混沌天尊
            case 24 -> new int[]{6000000, 12000000}; // 鸿蒙至尊
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