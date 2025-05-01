package com.example.demo1;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Random;
import java.util.*;

public class RandomEventHandler {
    private static final double TRIGGER_CHANCE = 0.5; // 0.5%触发概率
    private final Random random = new Random();
    private final Controller mainController;
    public RandomEventHandler(Controller controller) {
        this.mainController = controller;
    }
    public void checkOnCultivateClick() {
        if (random.nextDouble() <= TRIGGER_CHANCE) {
            Platform.runLater(this::triggerRandomEvent); // 在JavaFX线程触发事件
        }
    }

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
        Platform.runLater(() -> {
            // 定义三种事件
            List<Event> events = new ArrayList<>();
            events.add(new Event("灵气波动", "灵气突然波动,运用功法好像能改变灵气的吸收程度", createQiOptions()));
            events.add(new Event("神秘商人", "神秘商人出现", createMerchantOptions()));
            events.add(new Event("秘境探险", "发现秘境入口", createAdventureOptions()));
            events.add(new Event("道友切磋", "一位道友邀请你切磋", createDuelOptions()));
            events.add(new Event("妖兽袭击", "一只妖兽突然袭击你", createMonsterAttackOptions()));
            events.add(new Event("秘境探索", "发现一处未知秘境", createSecretRealmOptions()));
            events.add(new Event("灵药园奇遇", "发现一片野生灵药园", createHerbGardenOptions()));
            events.add(new Event("古修士洞府", "发现一座古修士遗留的洞府", createAncientCaveOptions()));
            events.add(new Event("天劫余韵", "附近有修士渡劫失败，天劫余韵未散", createHeavenlyTribulationOptions()));
            events.add(new Event("心魔入侵", "修炼时突然遭遇心魔入侵", createInnerDemonOptions()));
            events.add(new Event("灵兽认主", "一只灵兽主动接近你", createSpiritBeastOptions()));
            events.add(new Event("丹道顿悟", "炼丹时突然有所感悟", createAlchemyEpiphanyOptions()));
            events.add(new Event("灵脉爆发", "地下灵脉突然爆发", createQiVeinEruptionOptions()));
            events.add(new Event("上古丹炉", "发现一座上古时期的炼丹炉", createAncientFurnaceOptions()));
            events.add(new Event("灵雨降临", "天降灵雨，蕴含丰富灵气", createSpiritualRainOptions()));
            events.add(new Event("时间秘境", "误入时间流速异常的区域", createTimeSecretRealmOptions()));
            events.add(new Event("丹毒发作", "长期服用丹药导致丹毒发作", createPillToxicityOptions()));
            events.add(new Event("丹道大会", "附近正在举办炼丹师交流大会", createAlchemyConferenceOptions()));

            // 随机选择一个事件
            Event selectedEvent = events.get(random.nextInt(events.size()));

            // 显示事件弹窗
            showEventAlert(selectedEvent);
        });
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
    private List<EventOption> createHerbGardenOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("采摘灵药", 0, 0)); // 可能获得丹药或灵气
        options.add(new EventOption("移植到洞府", 0, 0)); // 获得持续收益
        options.add(new EventOption("研究药性", 0, 0)); // 提升炼丹知识
        return options;
    }
    private List<EventOption> createAncientCaveOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("探索洞府", 0, 0)); // 高风险高回报
        options.add(new EventOption("谨慎探查", 0, 0)); // 中等风险
        options.add(new EventOption("设置禁制后离开", 0, 0)); // 低风险低回报
        return options;
    }
    private List<EventOption> createHeavenlyTribulationOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("吸收天劫之力", 0, 0)); // 可能提升境界或受伤
        options.add(new EventOption("炼制天劫丹", 0, 0)); // 获得特殊丹药
        options.add(new EventOption("避开余波", 0, 0)); // 安全选项
        return options;
    }
    private List<EventOption> createInnerDemonOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("正面对抗心魔", 0, 0)); // 高风险高回报
        options.add(new EventOption("服用清心丹", 0, 0)); // 消耗丹药但安全
        options.add(new EventOption("逃避现实", 0, 0)); // 负面效果
        return options;
    }
    private List<EventOption> createSpiritBeastOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("尝试驯服", 0, 0));
        options.add(new EventOption("喂食丹药", 0, 0));
        options.add(new EventOption("驱赶灵兽", 0, 0));
        return options;
    }
    private List<EventOption> createAlchemyEpiphanyOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("尝试创新丹方", 0, 0));
        options.add(new EventOption("改良现有丹药", 0, 0));
        options.add(new EventOption("记录心得", 0, 0));
        return options;
    }
    private List<EventOption> createQiVeinEruptionOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("全力吸收", 0, 0));
        options.add(new EventOption("引导灵气炼丹", 0, 0));
        options.add(new EventOption("稳固灵脉", 0, 0));
        return options;
    }
    private List<EventOption> createAncientFurnaceOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("尝试使用", 0, 0));
        options.add(new EventOption("研究丹炉符文", 0, 0));
        options.add(new EventOption("谨慎收藏", 0, 0));
        return options;
    }
    private List<EventOption> createSpiritualRainOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("沐浴灵雨", 0, 0));
        options.add(new EventOption("收集灵雨", 0, 0));
        options.add(new EventOption("避雨观察", 0, 0));
        return options;
    }
    private List<EventOption> createTimeSecretRealmOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("进入修炼", 0, 0));
        options.add(new EventOption("炼制时之丹", 0, 0));
        options.add(new EventOption("离开秘境", 0, 0));
        return options;
    }
    private List<EventOption> createPillToxicityOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("运功排毒", 0, 0));
        options.add(new EventOption("以毒攻毒", 0, 0));
        options.add(new EventOption("硬抗毒性", 0, 0));
        return options;
    }
    private List<EventOption> createAlchemyConferenceOptions() {
        List<EventOption> options = new ArrayList<>();
        options.add(new EventOption("参加比赛", 0, 0));
        options.add(new EventOption("交易丹药", 0, 0));
        options.add(new EventOption("观摩学习", 0, 0));
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
            switch (event.getName()) {
                case "道友切磋":
                    handleDuelResult(buttonType);
                    break;
                case "妖兽袭击":
                    handleMonsterAttackResult(buttonType);
                    break;
                case "秘境探索":
                    handleSecretRealmResult(buttonType);
                    break;
                case "灵药园奇遇":
                    handleHerbGardenResult(buttonType);
                    break;
                case "古修士洞府":
                    handleAncientCaveResult(buttonType);
                    break;
                case "天劫余韵":
                    handleHeavenlyTribulationResult(buttonType);
                    break;
                case "心魔入侵":
                    handleInnerDemonResult(buttonType);
                    break;
                case "灵兽认主":
                    handleSpiritBeastResult(buttonType);
                    break;
                case "丹道顿悟":
                    handleAlchemyEpiphanyResult(buttonType);
                    break;
                case "灵脉爆发":
                    handleQiVeinEruptionResult(buttonType);
                    break;
                case "上古丹炉":
                    handleAncientFurnaceResult(buttonType);
                    break;
                case "灵雨降临":
                    handleSpiritualRainResult(buttonType);
                    break;
                case "时间秘境":
                    handleTimeSecretRealmResult(buttonType);
                    break;
                case "丹毒发作":
                    handlePillToxicityResult(buttonType);
                    break;
                case "丹道大会":
                    handleAlchemyConferenceResult(buttonType);
                    break;
                default:
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
    private void handleHerbGardenResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("采摘灵药")) {
            if (random.nextDouble() < 0.7) {
                handlePillReward(1 + random.nextInt(3)); // 获得1-3颗随机丹药
            } else {
                showRewardAlert("采摘失败，灵药枯萎了");
            }
        } else if (choiceText.equals("移植到洞府")) {
            mainController.increaseQiRate(0.5); // 永久增加灵气增长速度
            showRewardAlert("成功移植灵药，灵气增长速度+0.5/s");
        } else {
            // 研究药性
            String pillId = getRandomPillId();
            if (pillId != null) {
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                pill.rate += 0.1; // 提升该丹药的效果
                showRewardAlert("深入研究" + pill.pillName + "的药性，其效果提升了10%");
            }
        }
    }
    private void handleAncientCaveResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("探索洞府")) {
            if (random.nextDouble() < 0.4) {
                int rarePillCount = 2 + random.nextInt(3);
                handlePillReward(rarePillCount); // 获得2-4颗稀有丹药
            } else {
                handlePillPenalty(2); // 失去2颗随机丹药
            }
        } else if (choiceText.equals("谨慎探查")) {
            int qiGain = getQiChangeRange()[1] * 3;
            mainController.updateQi(qiGain);
            showRewardAlert("获得 " + qiGain + " 点灵气");
        } else {
            // 设置禁制后离开
            showRewardAlert("你标记了洞府位置，以后可以再来探索");
        }
    }
    private void handleHeavenlyTribulationResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("吸收天劫之力")) {
            if (random.nextDouble() < 0.05) {
                // 小概率直接提升境界
                mainController.breakthrough();
            } else {
                handlePillPenalty(3); // 失去3颗随机丹药
            }
        } else if (choiceText.equals("炼制天劫丹")) {
            // 获得特殊丹药"天劫丹"
            AlchemyController.PillData tribulationPill = new AlchemyController.PillData(
                    "tribulation_pill", "天劫丹", 0, 2.0, 0.05, mainController.getStageLevel());
            mainController.getSavedPills().put(tribulationPill.pillId, tribulationPill);
            showRewardAlert("成功炼制天劫丹，获得强大的修炼加成");
        } else {
            showRewardAlert("你安全避开了天劫余波");
        }
    }
    private void handleInnerDemonResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("正面对抗心魔")) {if (random.nextDouble() < 0.5) {
                // 战胜心魔
                mainController.increaseQiRate(1.0);
                showRewardAlert("战胜心魔，道心更加稳固，修炼速度+1.0/s");
            } else {
                handlePillPenalty(4); // 失去4颗随机丹药
            }
        } else if (choiceText.equals("服用清心丹")) {
            String pillId = "clear_mind_pill"; // 假设清心丹ID
            if (mainController.getSavedPills().containsKey(pillId) ){
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                if (pill.count > 0) {
                    pill.count--;
                    showRewardAlert("使用清心丹成功驱散心魔");
                } else {
                    handleInnerDemonResult(new ButtonType("逃避现实")); // 没有丹药则按逃避处理
                }
            } else {
                handleInnerDemonResult(new ButtonType("逃避现实")); // 没有丹药则按逃避处理
            }
        } else {
            // 逃避现实
            mainController.deductQi(mainController.getQi() / 2); // 失去一半灵气
            showRewardAlert("心魔肆虐，你失去了大量灵气");
        }
    }
    private void handleSpiritBeastResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("尝试驯服")) {
            if (random.nextDouble() < 0.6) {
                // 成功驯服
                mainController.increaseQiRate(0.8);
                showRewardAlert("成功驯服灵兽，修炼速度+0.8/s");
            } else {
                handlePillPenalty(1); // 驯服失败失去1颗丹药
            }
        } else if (choiceText.equals("喂食丹药")) {
            String pillId = getRandomPillId();
            if (pillId != null && mainController.getSavedPills().get(pillId).count > 0) {
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                pill.count--;
                if (random.nextDouble() < 0.8) {
                    // 灵兽满意
                    handlePillReward(2); // 获得2颗随机丹药作为回报
                } else {
                    showRewardAlert("灵兽吃完丹药就跑掉了");
                }
            } else {
                showRewardAlert("没有丹药可喂食");
            }
        } else {
            showRewardAlert("你驱赶了灵兽，它离开时留下了一些灵气");
            mainController.updateQi(getQiChangeRange()[1]);
        }
    }
    private void handleAlchemyEpiphanyResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("尝试创新丹方")) {
            if (random.nextDouble() < 0.4) {
                // 创新成功，获得随机新丹药
                String newPillId = "new_pill_" + System.currentTimeMillis();
                AlchemyController.PillData newPill = new AlchemyController.PillData(
                        newPillId, "创新丹药", 0,
                        1.5 + random.nextDouble(),
                        0.03 + random.nextDouble() * 0.02,
                        mainController.getStageLevel());
                newPill.count = 1;
                mainController.getSavedPills().put(newPillId, newPill);
                showRewardAlert("创新成功！获得新型丹药：" + newPill.pillName);
            } else {
                handlePillPenalty(2); // 创新失败损失材料
            }
        } else if (choiceText.equals("改良现有丹药")) {
            String pillId = getRandomPillId();
            if (pillId != null) {
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                pill.rate *= 1.2; // 效果提升20%
                showRewardAlert(pill.pillName + "的效果提升了20%");
            }
        } else {
            // 记录心得
            mainController.increaseQiRate(0.3);
            showRewardAlert("记录心得使你对丹道理解更深，修炼速度+0.3/s");
        }
    }
    private void handleQiVeinEruptionResult(ButtonType choice) {
        String choiceText = choice.getText();
        int[] range = getQiChangeRange();
        if (choiceText.equals("全力吸收")) {
            int qiGain = range[1] * 5;
            mainController.updateQi(qiGain);
            showRewardAlert("吸收了大量灵气，获得 " + qiGain + " 点灵气");
        } else if (choiceText.equals("引导灵气炼丹")) {
            String pillId = getRandomPillId();
            if (pillId != null) {
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                int bonus = 3 + random.nextInt(3); // 3-5颗
                pill.count += bonus;
                showRewardAlert("利用灵脉灵气成功炼制出 " + bonus + " 颗" + pill.pillName);
            } else {
                showRewardAlert("没有可炼制的丹方");
            }
        } else {
            // 稳固灵脉
            mainController.increaseQiRate(0.7);
            showRewardAlert("稳固后的灵脉持续为你提供灵气，修炼速度+0.7/s");
        }
    }
    private void handleAncientFurnaceResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("尝试使用")) {
            if (random.nextDouble() < 0.5) {
                // 成功炼制
                int pillCount = 2 + random.nextInt(4); // 2-5颗
                handlePillReward(pillCount);
            } else {
                handlePillPenalty(2); // 炸炉损失材料
            }
        } else if (choiceText.equals("研究丹炉符文")) {
            // 永久提升所有丹药效果
            for (AlchemyController.PillData pill : mainController.getSavedPills().values()) {
                pill.rate *= 1.15;
                pill.successRateImpact *= 1.15;
            }
            showRewardAlert("研究符文使你对丹道理解更深，所有丹药效果提升15%");
        } else {
            showRewardAlert("你将丹炉小心收藏，以后可以慢慢研究");
        }
    }
    private void handleSpiritualRainResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("沐浴灵雨")) {
            // 随机效果
            double effect = random.nextDouble();
            if (effect < 0.3) {
                mainController.increaseQiRate(0.5);
                showRewardAlert("灵雨洗涤身心，修炼速度+0.5/s");
            } else if (effect < 0.7) {
                int qiGain = getQiChangeRange()[1] * 2;
                mainController.updateQi(qiGain);
                showRewardAlert("吸收灵雨精华，获得 " + qiGain + " 点灵气");
            } else {
                showRewardAlert("灵雨对你没有明显效果");
            }
        } else if (choiceText.equals("收集灵雨")) {
            // 获得特殊丹药"灵雨丹"
            AlchemyController.PillData rainPill = new AlchemyController.PillData(
                    "rain_pill", "灵雨丹", 0, 1.2, 0.03, mainController.getStageLevel());
            rainPill.count = 3 + random.nextInt(3); // 3-5颗
            mainController.getSavedPills().put(rainPill.pillId, rainPill);
            showRewardAlert("成功收集灵雨，炼制出 " + rainPill.count + " 颗灵雨丹");
        } else {
            showRewardAlert("你避开了灵雨，没有获得任何效果");
        }
    }
    private void handleTimeSecretRealmResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("进入修炼")) {
            // 获得相当于10分钟自动修炼的灵气
            int qiGain = (int)Math.round(mainController.getQiRate() * 600);
            mainController.updateQi(qiGain);
            showRewardAlert("在时间秘境中修炼良久，获得 " + qiGain + " 点灵气");
        } else if (choiceText.equals("炼制时之丹")) {
            // 获得特殊丹药"时之丹"
            AlchemyController.PillData timePill = new AlchemyController.PillData(
                    "time_pill", "时之丹", 0, 3.0, 0.1, mainController.getStageLevel());
            timePill.count = 1;
            mainController.getSavedPills().put(timePill.pillId, timePill);
            showRewardAlert("利用秘境时间之力炼制出1颗时之丹");
        } else {
            showRewardAlert("你离开了时间秘境");
        }
    }
    private void handlePillToxicityResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("运功排毒")) {
            int qiLoss = getQiChangeRange()[1] / 2;
            mainController.deductQi(qiLoss);
            showRewardAlert("消耗 " + qiLoss + " 点灵气排出丹毒");
        } else if (choiceText.equals("以毒攻毒")) {
            String pillId = getRandomPillId();
            if (pillId != null && mainController.getSavedPills().get(pillId).count > 0) {
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                pill.count--;
                if (random.nextDouble() < 0.6) {
                    showRewardAlert("以毒攻毒成功化解丹毒");
                } else {
                    handlePillPenalty(1); // 方法失败再失去1颗
                }
            } else {
                handlePillToxicityResult(new ButtonType("硬抗毒性")); // 没有丹药则按硬抗处理
            }
        } else {
            // 硬抗毒性
            mainController.increaseQiRate(-0.3); // 修炼速度降低
            showRewardAlert("丹毒影响修炼，修炼速度-0.3/s");
        }
    }
    private void handleAlchemyConferenceResult(ButtonType choice) {
        String choiceText = choice.getText();
        if (choiceText.equals("参加比赛")) {
            if (random.nextDouble() < 0.5) {
                // 比赛获胜
                String rarePillId = "rare_pill_" + System.currentTimeMillis();
                AlchemyController.PillData rarePill = new AlchemyController.PillData(
                        rarePillId, "大会奖励丹", 0,
                        2.0 + random.nextDouble(),
                        0.05 + random.nextDouble() * 0.03,
                        mainController.getStageLevel());
                rarePill.count = 1;
                mainController.getSavedPills().put(rarePillId, rarePill);
                showRewardAlert("比赛获胜！获得稀有丹药：" + rarePill.pillName);
            } else {
                handlePillPenalty(3); // 比赛失败损失材料
            }
        } else if (choiceText.equals("交易丹药")) {
            String pillId = getRandomPillId();
            if (pillId != null && mainController.getSavedPills().get(pillId).count > 1) {
                AlchemyController.PillData pill = mainController.getSavedPills().get(pillId);
                int tradeCount = Math.min(3, pill.count / 2);
                pill.count -= tradeCount;
                handlePillReward(tradeCount); // 获得等量其他丹药
            } else {
                showRewardAlert("没有足够丹药进行交易");
            }
        } else {
            // 观摩学习
            mainController.increaseQiRate(0.4);
            showRewardAlert("从其他炼丹师处学到技巧，修炼速度+0.4/s");
        }
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
        long currentQi = mainController.getQi();

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