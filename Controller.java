// 声明包名
package com.example.demo;
// 导入 JavaFX 属性相关类，用于创建和管理可观察的属性
import javafx.application.Platform;
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
import javafx.event.ActionEvent;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Modality;
// 导入 Java 输入输出相关类
import java.io.*;
// 导入 JavaFX 文件选择器类
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
// 导入 Java 集合框架中的 Map 接口
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javafx.geometry.Bounds;
import java.util.Random; // 添加这一行
// 新增导入
import javafx.scene.effect.Glow;
import javafx.scene.effect.BoxBlur;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    private static final int BASE_BREAKTHROUGH_COST = 1000;
    private final DoubleProperty actualSuccessRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);

    private long lastUpdate; // 声明为类成员变量
    // 以下是 FXML 中定义的界面元素，通过 @FXML 注解注入
    @FXML
    private Label lblStage; // 显示当前境界的标签
    @FXML
    private Label lblQi; // 显示当前灵气值的标签
    @FXML
    private Label lblQiRate; // 显示灵气增长速度的标签
    @FXML
    private Label lblSuccessRate; // 显示渡劫成功率的标签
    @FXML
    private Button btnCultivate; // 修炼按钮，点击可增加灵气
    @FXML
    private Button btnAlchemy; // 炼丹按钮，点击打开炼丹界面
    @FXML
    private Button btnBreakthrough; // 渡劫按钮，点击尝试渡劫

    // 灵气值属性，使用 JavaFX 的 IntegerProperty 实现可观察
    private final IntegerProperty qi = new SimpleIntegerProperty(0);
    // 灵气增长速度属性，使用 JavaFX 的 DoubleProperty 实现可观察
    private final DoubleProperty qiRate = new SimpleDoubleProperty(1.0);
    // 随机事件处理器对象，用于处理随机触发的事件
    private RandomEventHandler randomEventHandler;
    // 炼丹界面的窗口对象
    private Stage alchemyStage;
    // 基础点击加成
    private int clickBonus = 1;
    // 当前境界属性，使用 JavaFX 的 StringProperty 实现可观察
    private final StringProperty currentStage = new SimpleStringProperty("凡人");
    // 渡劫成功率属性，使用 JavaFX 的 DoubleProperty 实现可观察
    private final DoubleProperty successRate = new SimpleDoubleProperty(BASE_SUCCESS_RATE);
    // 境界等级，凡人对应 0，炼气对应 1，以此类推
    private int stageLevel = 0;
    // 境界名称数组，存储所有可能的境界名称
    private final String[] STAGES = {"凡人", "炼气", "筑基", "金丹", "元婴",
            "化神", "渡劫", "大乘", "大罗金仙", "仙君",
            "仙王", "仙帝", "仙尊", "仙圣", "仙祖",
            "道君", "道王", "道帝", "道尊", "道圣",
            "道祖", "混元大罗金仙", "混元无极金仙", "混沌天尊", "鸿蒙至尊"};
    private final Map<String, AlchemyController.PillData> savedPills = new LinkedHashMap<>();
    // 控制器和窗口对象
    private TreasureController treasureController;
    private TreasureShopController treasureShopController;
    private Stage treasureStage, treasureShopStage;
    private AdventureState adventureState;
    private AnimationTimer adventureCheckTimer;
    @FXML
    private Pane effectLayer; // 仙气粒子层容器（与FXML中的fx:id对应）
    @FXML
    private ImageView character; // 主角图片容器（与FXML中的fx:id对应）
    private final Timeline characterAnimation = new Timeline(); // 角色动画时间线
    private final Random random = new Random(); // 用于生成随机数



    private void initializeCharacterAnimation() {
        try {
            Image charImage = new Image("file:E://JAVA%20FX//demo//pic//character.png");
            character.setImage(charImage);
        } catch (Exception e) {
            System.err.println("主角图片加载失败: " + e.getMessage());
        }

        // 保留浮动动画，但移除任何效果
        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(2), character);
        floatAnim.setFromY(-10);
        floatAnim.setToY(10);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.play();

        // 移除呼吸光效的透明度变化（如果需要完全无效果）
        FadeTransition glowAnim = new FadeTransition(Duration.seconds(1.5), character);
        glowAnim.setFromValue(1.0); // 保持完全不透明
        glowAnim.setToValue(1.0);   // 取消淡入淡出
        glowAnim.play();
    }
    private void startParticleEffect() {
        // ================== 第一层：主仙气粒子 ==================
        Timeline mainParticleEmitter = new Timeline(
                new KeyFrame(Duration.seconds(0.25), event -> { // 修正：移除多余的逗号，使用正确参数格式
                    // 1. 创建扇形粒子（模拟烟雾效果）
                    Arc particle = new Arc(0, 0, 12, 6, 0, 180);
                    particle.setType(ArcType.ROUND);
                    particle.setFill(Color.rgb(255, 240, 200, 0.6));

                    // 2. 添加发光特效
                    Glow glow = new Glow(0.7);
                    glow.setInput(new BoxBlur(8, 8, 2));
                    particle.setEffect(glow);

                    // 3. 设置初始位置（围绕角色随机偏移）
                    Bounds charBounds = character.getBoundsInParent();
                    double startX = charBounds.getCenterX() + random.nextInt(40) - 20;
                    double startY = charBounds.getCenterY() + random.nextInt(40) - 20;
                    particle.setTranslateX(startX);
                    particle.setTranslateY(startY);
                    effectLayer.getChildren().add(particle);

                    // 4. 创建运动路径（柔和曲线）
                    Path path = new Path();
                    path.getElements().addAll(
                            new MoveTo(startX, startY),
                            new CubicCurveTo(
                                    startX + random.nextInt(30) - 15,
                                    startY - 80 + random.nextInt(20),
                                    startX + random.nextInt(20) - 10,
                                    startY - 160 + random.nextInt(40),
                                    startX + random.nextInt(10) - 5,
                                    startY - 220
                            )
                    );

                    // 5. 组合动画（路径移动 + 淡出 + 缩放）
                    PathTransition pathTransition = new PathTransition(
                            Duration.seconds(8), path, particle
                    );
                    pathTransition.setInterpolator(Interpolator.EASE_OUT);

                    FadeTransition fade = new FadeTransition(Duration.seconds(6), particle);
                    fade.setFromValue(0.8);
                    fade.setToValue(0);

                    ScaleTransition scale = new ScaleTransition(Duration.seconds(7), particle);
                    scale.setToX(0.4);
                    scale.setToY(0.4);

                    ParallelTransition combo = new ParallelTransition(
                            pathTransition, fade, scale
                    );

                    // 6. 动画完成后移除粒子
                    combo.setOnFinished(evt -> effectLayer.getChildren().remove(particle));
                    combo.play();
                })
        );
        mainParticleEmitter.setCycleCount(Animation.INDEFINITE);
        mainParticleEmitter.play();

        // ================== 第二层：背景雾气 ==================
        Timeline fogEmitter = new Timeline(
                new KeyFrame(Duration.seconds(1.5), event -> { // 修正：参数格式
                    Circle fog = new Circle(30, Color.rgb(220, 230, 255, 0.1));
                    fog.setEffect(new BoxBlur(25, 25, 3));

                    Bounds charBounds = character.getBoundsInParent();
                    fog.setTranslateX(charBounds.getCenterX());
                    fog.setTranslateY(charBounds.getCenterY());
                    effectLayer.getChildren().add(fog);

                    ScaleTransition scale = new ScaleTransition(Duration.seconds(10), fog);
                    scale.setToX(4.0);
                    scale.setToY(4.0);

                    FadeTransition fade = new FadeTransition(Duration.seconds(9), fog);
                    fade.setToValue(0);

                    ParallelTransition fogAnim = new ParallelTransition(scale, fade);
                    fogAnim.setOnFinished(evt -> effectLayer.getChildren().remove(fog));
                    fogAnim.play();
                })
        );
        fogEmitter.setCycleCount(Animation.INDEFINITE);
        fogEmitter.play();

        // ================== 第三层：符文光点 ==================
        Timeline runeEmitter = new Timeline(
                new KeyFrame(Duration.seconds(0.8), event -> { // 修正：参数格式
                    Text rune = new Text(".");
                    rune.setFont(Font.font("楷体", FontWeight.BOLD, 24));
                    rune.setFill(Color.rgb(255, 215, 0, 0.7));

                    Bounds charBounds = character.getBoundsInParent();
                    rune.setTranslateX(charBounds.getCenterX() + random.nextInt(80) - 40);
                    rune.setTranslateY(charBounds.getCenterY() + random.nextInt(80) - 40);
                    effectLayer.getChildren().add(rune);

                    TranslateTransition rise = new TranslateTransition(Duration.seconds(4), rune);
                    rise.setByY(-120);
                    rise.setByX(random.nextInt(20) - 10);

                    RotateTransition rotate = new RotateTransition(Duration.seconds(6), rune);
                    rotate.setByAngle(360);

                    FadeTransition fade = new FadeTransition(Duration.seconds(3), rune);
                    fade.setToValue(0);

                    ParallelTransition runeAnim = new ParallelTransition(rise, rotate, fade);
                    runeAnim.setOnFinished(evt -> effectLayer.getChildren().remove(rune));
                    runeAnim.play();
                })
        );
        runeEmitter.setCycleCount(Animation.INDEFINITE);
        runeEmitter.play();
    }

    @FXML
    private void handleAdventureButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdventureView.fxml"));
            Parent root = loader.load();
            AdventureController controller = loader.getController();
            controller.setMainController(this);
            controller.loadAdventureState(this.adventureState);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // 添加窗口关闭监听
            stage.setOnHidden(event -> {
                // 将最新状态同步回主控制器
                this.adventureState = controller.getAdventureState();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startAdventureCheckTimer() {
        adventureCheckTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (adventureState != null && adventureState.getStartTime() > 0) {
                    long elapsed = System.currentTimeMillis() - adventureState.getStartTime();
                    long remaining = calculateAdventureTime(adventureState.getCurrentArea()) - elapsed;

                    if (remaining <= 0) {
                        completeAdventureInBackground();
                    }
                }
            }
        };
        adventureCheckTimer.start();
    }
    private long calculateAdventureTime(int area) {
        return (long) (3600000 * Math.pow(2, area - 1));
    }

    private void completeAdventureInBackground() {
        int area = adventureState.getCurrentArea();
        int baseReward = 1000 * area;
        double speedBonus = getQiRate() * 3600;
        updateQi((int) (baseReward + speedBonus));

        adventureState.getDailyCounts().put(area,
                adventureState.getDailyCounts().getOrDefault(area, 0) + 1);
        adventureState.incrementTotalCompleted();
        adventureState.setStartTime(0); // 重置开始时间
    }
    public long calculateMaxOfflineTime() {
        long baseTime = GameState.BASE_MAX_OFFLINE_TIME_MS; // 60秒基础

        if (treasureController != null) {
            for (TreasureData treasure : treasureController.getTreasures().values()) {
                if ("OFFLINE_TIME".equals(treasure.getEffectType())) {
                    baseTime += (long)(treasure.getEffectValue() * 1000); // 转换为毫秒
                    System.out.printf("[DEBUG] 应用时空塔效果: %s Lv.%d -> +%.0f秒\n",
                            treasure.getName(),
                            treasure.getLevel(),
                            treasure.getEffectValue());
                }
            }
        }

        System.out.printf("[DEBUG] 最终最大离线时间: %d秒\n", baseTime/1000);
        return baseTime;
    }

    public int getStageLevel() {
        return stageLevel;
    }

    public TreasureController getTreasureController() {
        return treasureController;
    }
    private void startRandomEventCheck() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
            }
        }.start();
    }
    static final long BASE_MAX_OFFLINE_TIME_MS = 1*60 * 1000; // 60秒基础离线时间
    /**
     * 初始化方法，在 FXML 加载完成后自动调用，用于初始化界面元素和事件处理
     */
    @FXML
    private void initialize() {
        // 初始化随机事件处理器
        randomEventHandler = new RandomEventHandler(this);
        // 启动随机事件检查的定时器
        startRandomEventCheck();
        // 将境界标签的文本属性绑定到当前境界属性，并添加前缀 "境界: "
        lblStage.textProperty().bind(
                Bindings.createStringBinding(() ->
                                "境界: " + currentStage.get(),
                        currentStage
                )
        );
        // 将灵气标签的文本属性绑定到灵气属性，实现自动更新显示
        lblQi.textProperty().bind(
                Bindings.createStringBinding(() ->
                                "灵气：" + NumberFormatter.formatNumber(qi.get()),
                        qi
                )
        );
        // 将灵气增长速度标签的文本属性绑定到灵气增长速度属性，实现自动更新显示
        lblQiRate.textProperty().bind(
                Bindings.createStringBinding(() ->
                                "增速：" + NumberFormatter.formatNumber((long) qiRate.get()) + "/s",
                        qiRate
                )
        );

        // 启动灵气自动增长的定时器
        startAutoQiGrowth();

        // 将渡劫成功率标签的文本属性绑定到渡劫成功率属性，实现自动更新显示
        lblSuccessRate.textProperty().bind(Bindings.format("渡劫成功率：%.1f%%", actualSuccessRate.multiply(100)));

        // 为修炼按钮添加点击事件处理逻辑
        startAutoQiGrowth();
        // 为炼丹按钮添加点击事件处理逻辑
        btnAlchemy.setOnAction(event -> openAlchemyPanel());
        // 为修炼按钮添加点击事件处理逻辑
        btnCultivate.setOnAction(event -> updataQi(1000));
        // 初始化随机事件处理器
        randomEventHandler = new RandomEventHandler(this);
        // 再次启动灵气自动增长的定时器（可能是代码冗余，可考虑优化）
        startAutoQiGrowth();
        initTreasurePanel();
        initTreasureShop();
        initStarterTreasure();

        try {
            // 创建 FXMLLoader 对象，用于加载炼丹界面的 FXML 文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AlchemyView.fxml"));
            // 加载 FXML 文件并获取根节点
            Parent root = loader.load();
            this.alchemyController = loader.getController();
            this.alchemyController.setMainController(this);
            // 初始化丹药数据
            this.alchemyController.initializeCustomPills();
        } catch (IOException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            // 输出初始化炼丹控制器失败的信息
            System.out.println("初始化炼丹控制器失败。");
        }
        // 在 initialize() 方法末尾添加：
        showStoryDialog();
        startAdventureCheckTimer();
        initializeCharacterAnimation(); // 初始化角色动画
        startParticleEffect();         // 启动粒子特效
    }
    private void showStoryDialog() {
        try {
            // 自定义剧情文本（可自由修改）
            List<String> storyPages = List.of(
                    "【混沌初开】\n" +
                            "你在一片混沌中缓缓苏醒，四周灵气缭绕如雾。\n" +
                            "耳边传来缥缈的声音：「此乃太虚之境，汝既入此界，当寻仙问道...」\n",

                    "【灵器认主】\n" +
                            "腰间突然传来一阵温热，你低头发现一枚古朴玉佩正泛着微光。\n" +
                            "玉佩上浮现文字：「触摸灵玉，可窥仙途」\n",

                    "【仙缘指引】\n" +
                            "指尖触及玉佩的刹那，浩瀚信息涌入神识：\n" +
                            "「修仙四要：炼丹聚灵，炼器护道，渡劫破境，因果轮回...」\n",

                    "【界面初现】\n" +
                            "玉佩化作流光没入眉心，眼前浮现玄奥符文构成的界面。\n" +
                            "冥冥中有所明悟——此乃汝的修仙命盘！"
            );

            // 加载剧情弹窗
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StoryDialog.fxml"));
            Parent root = loader.load();
            Stage storyStage = new Stage();
            storyStage.initModality(Modality.APPLICATION_MODAL);
            storyStage.setTitle("仙途启程");
            storyStage.initStyle(StageStyle.UNDECORATED); // 设置窗口样式为无装饰
            storyStage.setScene(new Scene(root));

            // 修改最后一页按钮文本为「进入修仙界面」
            StoryDialogController controller = loader.getController();
            controller.initStory(storyPages);
            controller.setCustomButtonText("进入修仙界面", storyPages.size() - 1); // 新增方法
            storyStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 添加以下方法到Controller类
    public String getPillStatus() {
        StringBuilder sb = new StringBuilder("当前丹药效果：\n");
        for (AlchemyController.PillData pill : savedPills.values()) {
            if (pill.count > 0) {
                sb.append(String.format("%s x%d: 速度+%.1f, 成功率+%.2f%%\n",
                        pill.pillName, pill.count,
                        pill.rate * pill.count,
                        pill.successRateImpact * pill.count * 100));
            }
        }
        return sb.toString();
    }

    public void applyPillEffects() {
        // 重置基础属性
        double baseQiRate = 1.0 + (stageLevel * 0.5); // 基础修炼速度随境界提升
        double baseSuccessRate = BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel);

        // 计算所有丹药的累计效果
        double totalRateBoost = 0;
        double totalSuccessBoost = 0;

        for (AlchemyController.PillData pill : savedPills.values()) {
            totalRateBoost += pill.rate * pill.count;
            totalSuccessBoost += pill.successRateImpact * pill.count;
        }

        // 应用效果
        qiRate.set(baseQiRate + totalRateBoost);
        successRate.set(Math.max(0, Math.min(1, baseSuccessRate + totalSuccessBoost)));
        updateActualSuccessRate();
    }
    /**
     * 保存游戏方法，将当前游戏状态保存到指定文件
     *
     * @param filePath 保存文件的路径
     */
    public void saveGame(String filePath) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // 创建父目录
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                long currentTime = System.currentTimeMillis();
                if (alchemyController == null) {
                    GameState state = new GameState(
                            qi.get(),
                            qiRate.get(),
                            savedPills,
                            treasureController != null ? treasureController.getTreasures() : null,
                            stageLevel,
                            System.currentTimeMillis(),
                            adventureState

                    );
                    oos.writeObject(state);
                } else {
                    GameState state = new GameState(
                            qi.get(),
                            qiRate.get(),
                            savedPills,
                            treasureController.getTreasures(), // 确保保存法宝数据
                            stageLevel,
                            System.currentTimeMillis(),
                            adventureState
                    );
                    oos.writeObject(state);
                    System.out.println("[保存] 存档时间已记录: " + currentTime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initTreasurePanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureView.fxml")); // 正确加载背包界面
            Parent root = loader.load();
            treasureController = loader.getController(); // 正确获取 TreasureController
            treasureController.setMainController(this);System.out.println("[DEBUG] TreasureController 初始化完成");
        } catch (IOException e) {
            e.printStackTrace();
            // 输出保存游戏时出现 IO 错误的信息
            System.err.println("保存游戏时出现 IO 错误。");
        }
    }
    // 初始化法宝商店界面
    private void initTreasureShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureShopView.fxml"));
            Parent root = loader.load();
            treasureShopController = loader.getController();
            treasureShopController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 检查是否已经拥有某个法宝
    public boolean hasTreasure(TreasureData treasure) {
        if (treasureController != null) {
            return treasureController.getTreasures().containsKey(treasure.getId());
        }
        return false;
    }
    // 打开法宝商店
    @FXML
    private void openTreasureShop() {
        try {
            // 每次创建新的加载器和窗口
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TreasureShopView.fxml"));
            Parent root = loader.load();

            // 关键步骤：获取控制器并设置主控制器
            TreasureShopController shopController = loader.getController();
            shopController.setMainController(this); // 传递当前 Controller 实例

            Stage shopStage = new Stage();
            shopStage.setTitle("法宝商店");
            shopStage.initModality(Modality.APPLICATION_MODAL);
            shopStage.initOwner(lblQi.getScene().getWindow()); // 设置父窗口
            shopStage.setScene(new Scene(root));


            shopStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "无法打开法宝商店！").show();
        }
    }
    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d小时%d分%d秒", hours, minutes, seconds);
    }
    public void updateActualSuccessRate() {
        double pillImpact = 0.0;
        if (alchemyController != null) {
            for (AlchemyController.PillData pill : alchemyController.getPills().values()) {
                pillImpact = pill.successRateImpact * pill.count;
            }
        }
        actualSuccessRate.set(Math.min(successRate.get() + pillImpact, 1.0));
    }
    public String getMaxOfflineTimeDisplay() {
        long maxSeconds = calculateMaxOfflineTime() / 1000;
        return formatTime(maxSeconds);
    }
    /**
     * 加载游戏方法，从指定文件加载游戏状态
     *
     * @param filePath 加载文件的路径
     */
    public void loadGame(String filePath) {
        try {
            File file = new File(filePath);
            if (alchemyStage != null && alchemyStage.isShowing()) {
                alchemyStage.requestFocus();
            }
            if (file.length() == 0) {
                System.out.println("存档文件为空，无法加载。");
                return;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                GameState state = (GameState) ois.readObject();
                System.out.println("[加载] 读取存档，丹药数量: " + (state.getPills() != null ? state.getPills().size() : 0));

                // 1. 先加载基础数据
                qi.set(state.getQi());
                qiRate.set(state.getQiRate());
                stageLevel = state.getStageLevel();
                currentStage.set(STAGES[stageLevel]);
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));

                // 2. 加载法宝数据（必须在计算离线时间前完成）
                if (state.getTreasures() != null) {
                    treasureController.setTreasures(state.getTreasures());
                    System.out.printf("[加载] 已加载 %d 件法宝\n", state.getTreasures().size());
                    applyTreasureEffects(); // 立即应用法宝效果
                }

                // 3. 加载丹药数据（不再弹出窗口）
                savedPills.clear();
                if (state.getPills() != null) {
                    state.getPills().forEach((id, data) -> {
                        AlchemyController.PillData copiedData = new AlchemyController.PillData(
                                data.pillId, data.pillName, data.cost,
                                data.rate, data.successRateImpact, data.level);
                        copiedData.count = data.count;
                        savedPills.put(id, copiedData);
                    });
                    applyPillEffects();
                }

                // 4. 计算离线奖励（此时法宝效果已应用）
                long savedTime = state.getLastSaveTime();
                long currentTime = System.currentTimeMillis();
                long totalOfflineTimeMs = currentTime - savedTime;

                if (totalOfflineTimeMs > 0) {
                    long maxAllowedOfflineTimeMs = calculateMaxOfflineTime();
                    System.out.printf("[离线时间] 实际离线: %d 秒, 最大允许: %d 秒\n",
                            totalOfflineTimeMs / 1000, maxAllowedOfflineTimeMs / 1000);

                    long effectiveOfflineTimeMs = Math.min(totalOfflineTimeMs, maxAllowedOfflineTimeMs);
                    int gainedQi = (int)((effectiveOfflineTimeMs / 1000) * qiRate.get());

                    qi.set(qi.get() + gainedQi);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("离线奖励");
                        alert.setHeaderText("修真无岁月，洞中已千年");
                        alert.setContentText(String.format(
                                "你在离开的 %s 时间内(最大允许: %s)\n通过修炼获得了 %d 灵气！",
                                formatTime(totalOfflineTimeMs / 1000),
                                formatTime(maxAllowedOfflineTimeMs / 1000),
                                gainedQi
                        ));
                        alert.showAndWait();
                    });
                }

                // 5. 初始化炼丹控制器但不显示窗口
                if (alchemyController == null) {
                    initializeAlchemyController();
                }
                if (alchemyController != null && state.getPills() != null) {
                    alchemyController.setPills(state.getPills());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "加载存档失败: " + e.getMessage()).show();
            });
        }

        System.out.printf("[加载完成] 当前最大离线时间: %d秒\n", calculateMaxOfflineTime()/1000);
    }


    /**
     * 处理保存游戏按钮点击事件的方法
     *
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
     *
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
     * 渡劫逻辑方法
     */
    @FXML
    void breakthrough() {
        // 获取当前境界对应的消耗量（修改点：动态计算）
        int currentCost = getCurrentBreakthroughCost();
        // 灵气不足判断（修改点：使用动态消耗量）
        if (qi.get() < currentCost) {
            new Alert(Alert.AlertType.WARNING,
                    "灵气不足！需要 " + currentCost + " 灵气（当前境界：" + STAGES[stageLevel] + "）") // 新增境界显示
                    .showAndWait();
            return;
        }

        updateActualSuccessRate();
        double currentActualRate = actualSuccessRate.get();
        if (Math.random() < currentActualRate) {
            if (stageLevel < STAGES.length - 1) {
                stageLevel++;
                currentStage.set(STAGES[stageLevel]);
                successRate.set(BASE_SUCCESS_RATE * Math.pow(DECAY_FACTOR, stageLevel));
                qi.set(qi.get() - BASE_BREAKTHROUGH_COST);
// 新增：删除所有丹药
                if (alchemyController != null) {
                    // 清空主控制器保存的丹药数据
                    savedPills.clear();
                    // 清空炼丹界面的丹药数据
                    alchemyController.loadPillsOnClick();
                    // 重新生成丹药（可选，根据需求）
                    alchemyController.updateAvailablePills();
                    // 更新显示
                    alchemyController.updatePillDisplay();
                    // 应用效果变化
                    applyPillEffects();
                }

                updateActualSuccessRate();
                new Alert(Alert.AlertType.INFORMATION, "渡劫成功！当前境界：" + STAGES[stageLevel]).showAndWait();
                showBreakthroughDialog(); // <--- 新增此行
                showStageStoryDialog(stageLevel);
            }
        } else {
            successRate.set(Math.min(successRate.get() + 0.1, 1.0));
            qi.set(qi.get() - BASE_BREAKTHROUGH_COST);
            updateActualSuccessRate();
            new Alert(Alert.AlertType.ERROR, "渡劫失败！下次成功率：" + String.format("%.1f%%", actualSuccessRate.get() * 100)).showAndWait();
        }
    }
    // 在 Controller.java 中添加方法
    private void showBreakthroughDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BreakthroughDialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("破境机缘");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 在 Controller.java 中添加
    private void showStageStoryDialog(int newStageLevel) {
        List<String> story = StageStoryConfig.STAGE_STORIES.get(newStageLevel);
        if (story == null || story.isEmpty()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StoryDialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));

            StoryDialogController controller = loader.getController();
            controller.initStory(story);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     *
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
    // 法宝管理方法
    // 修改应用法宝效果的方法
    public void applyTreasureEffects() {
        // 先重置基础值
        double baseQiRate = 1.0 + (stageLevel * 0.5);
        qiRate.set(baseQiRate);
        clickBonus = 1;
        // 应用所有法宝效果
        if (treasureController != null) {
            for (TreasureData treasure : treasureController.getTreasures().values()) {
                switch (treasure.getEffectType()) {
                    case "CLICK_BONUS":
                        clickBonus += (int) treasure.getEffectValue();
                        break;
                    case "AUTO_RATE":
                        qiRate.set(qiRate.get() + (qiRate.get() * treasure.getEffectPercentage() / 100));
                        break;
                    case "AUTO_RATE_BASE":  // 新增处理
                        qiRate.set(qiRate.get() + treasure.getEffectValue());
                        break;
                    case "OFFLINE_TIME":
                        // 离线时间效果在计算离线时间时处理
                        break;
                }
            }
        }
        // 打印调试信息
        System.out.printf("[效果应用] 修炼速度: %.1f, 最大离线: %d秒\n",
                qiRate.get(), calculateMaxOfflineTime()/1000);
    }
    // 新增方法：提供给其他类获取丹药数据

    /**
     * 增加灵气增长速度的方法
     *
     * @param rate 要增加的灵气增长速度
     */
    public void increaseQiRate(double rate) {
        // 增加灵气增长速度
        qiRate.set(qiRate.get() + rate);
    }
    // 法宝管理方法



    public void addTreasureToBackpack(TreasureData treasure) {
        if (treasureController != null) {
            treasureController.getTreasures().put(treasure.getId(), treasure);
            treasureController.updateTreasureDisplay();
            System.out.println("[DEBUG] 添加法宝: " + treasure.getId());
            applyTreasureEffects();
        }
    }


    /**
     * 更新灵气值的方法
     *
     * @param amount 要增加或减少的灵气数量
     */
    public void updateQi(int amount) {
        // 更新灵气值
        qi.set(qi.get() + amount);
    }


    // 新增方法：提供给其他类获取丹药数据
    public Map<String, AlchemyController.PillData> getSavedPills() {
        return savedPills;
    }


    // 新增方法：接收炼丹界面关闭时传递的数据
    public void savePillsData(Map<String, AlchemyController.PillData> pills) {
        savedPills.clear();
        pills.forEach((id, data) -> {
            AlchemyController.PillData copiedData = new AlchemyController.PillData(
                    data.pillId,
                    data.pillName,
                    data.cost,
                    data.rate,
                    data.successRateImpact,
                    data.level
            );
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
    private void initStarterTreasure() {
        TreasureData starterTreasure = new TreasureData(
                "WQX001",
                "五禽戏秘籍",
                "华佗所创养生功法，可强身健体",
                "初始赠送",
                1000,
                "CLICK_BONUS",
                5
        );
        starterTreasure.setLevel(1); // 初始等级设为1
        treasureController.getTreasures().put(starterTreasure.getId(), starterTreasure);
        treasureController.updateTreasureDisplay();
        applyTreasureEffects();
    }
    public void updataQi(int baseBonus) {
        int totalBonus = baseBonus;
        // 遍历所有法宝，累加点击加成
        if (treasureController != null) {
            for (TreasureData treasure : treasureController.getTreasures().values()) {
                if ("CLICK_BONUS".equals(treasure.getEffectType())) {
                    totalBonus += (int) treasure.getEffectValue();
                }
            }
        }
        qi.set(qi.get() + totalBonus);
        randomEventHandler.checkOnCultivateClick();
    }
    public Map<String, Double> getAllTreasureEffects() {
        Map<String, Double> effects = new HashMap<>();
        effects.put("AUTO_RATE_BASE", 0.0);
        effects.put("AUTO_RATE_PERCENT", 0.0);
        effects.put("CLICK_BONUS", 0.0);
        effects.put("OFFLINE_TIME", 0.0);

        if (treasureController != null) {
            for (TreasureData treasure : treasureController.getTreasures().values()) {
                switch (treasure.getEffectType()) {
                    case "AUTO_RATE":
                        // 聚灵阵等百分比加成法宝
                        effects.put("AUTO_RATE_PERCENT", effects.get("AUTO_RATE_PERCENT") + treasure.getEffectPercentage());
                        break;
                    case "CLICK_BONUS":
                        effects.put("CLICK_BONUS", effects.get("CLICK_BONUS") + treasure.getEffectValue());
                        break;
                    case "OFFLINE_TIME":
                        effects.put("OFFLINE_TIME", effects.get("OFFLINE_TIME") + treasure.getEffectValue());
                        break;
                    case "AUTO_RATE_BASE":
                        // 其他直接增加固定值的修炼速度法宝
                        effects.put("AUTO_RATE_BASE", effects.get("AUTO_RATE_BASE") + treasure.getEffectValue());
                        break;
                }
            }
        }
        return effects;
    }
    // 修改自动增长逻辑
    private void startAutoQiGrowth() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                long delta = now - lastUpdate;
                if (delta >= 1_000_000_000) { // 每秒更新一次
                    double totalAutoRateBonus = 0;
                    // 遍历所有法宝，累加自动增速加成
                    if (treasureController != null) {
                        for (TreasureData treasure : treasureController.getTreasures().values()) {
                            if ("AUTO_RATE".equals(treasure.getEffectType())) {
                                totalAutoRateBonus += treasure.getEffectValue();
                            }
                        }
                    }
                    // 应用基础灵气增长速度和法宝加成
                    qi.set(qi.get() + (int) (qiRate.get() + totalAutoRateBonus));
                    lastUpdate = now;
                }
            }
        }.start();
    }

    private int getCurrentBreakthroughCost() {
        // 基础消耗为10000，每次境界提升后翻倍
        return BASE_BREAKTHROUGH_COST * (int) Math.pow(4, stageLevel);
    }

    public AlchemyController getAlchemyController() {
        return this.alchemyController;
    }


    public int getQi() { return qi.get();
    }
    /**
     * 显示弹幕
     * @param text 弹幕内容
     */
    private void showDanmaku(String text) {
        Region root = (Region) lblQi.getScene().getRoot();

        if (root == null) return;

        // 获取按钮在场景中的坐标（修正后的逻辑）
        javafx.geometry.Point2D sceneCoords = btnCultivate.localToScene(0, 0);
        double x = sceneCoords.getX() + btnCultivate.getScene().getWindow().getX();
        double y = sceneCoords.getY() + btnCultivate.getScene().getWindow().getY();

        System.out.println("根布局宽度: " + root.getWidth() + ", 根布局高度: " + root.getHeight());
// 输出根布局的属性，用于调试
        System.out.println("根布局属性: clip = " + root.getClip() + ", effect = " + root.getEffect());

        // 创建弹幕并直接添加到根布局
        Danmaku danmaku = new Danmaku(text, x, y);
        if (root instanceof Pane) {
            ((Pane) root).getChildren().add(danmaku);

            System.out.println("根布局子节点数量: " + ((Pane) root).getChildren().size());

        } else {
            new Alert(Alert.AlertType.ERROR, "弹幕系统初始化失败").show();
        }
    }

    public double getQiRate() {
        return qiRate.get(); // 假设 qiRate 是 DoubleProperty
    }
}
