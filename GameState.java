package com.example.demo;

// 导入 Java 序列化相关类
import java.io.Serializable;
// 导入 Java 集合框架中的 Map 接口
import java.util.Map;

/**
 * 游戏状态类，用于保存游戏的当前状态，可序列化以便保存到文件
 */
public class GameState implements Serializable {
    // 序列化版本号，确保序列化和反序列化的兼容性
    private static final long serialVersionUID = 1;
    // 当前灵气值
    private int qi;
    // 当前灵气增长速度
    private double qiRate;
    // 炼丹数据，存储丹药信息
    private Map<String, AlchemyController.PillData> pills;
    // 当前境界等级
    private int stageLevel;

    /**
     * 构造方法，初始化游戏状态对象
     *
     * @param qi          当前灵气值
     * @param qiRate      当前灵气增长速度
     * @param pills       炼丹数据
     * @param stageLevel  当前境界等级
     * @param currentTime
     */
    public GameState(int qi, double qiRate, Map<String, AlchemyController.PillData> pills, int stageLevel, long currentTime) {
        this.qi = qi;
        this.qiRate = qiRate;
        this.pills = pills;
        this.stageLevel = stageLevel;
    }

    /**
     * 获取当前灵气值的方法
     * @return 当前灵气值
     */
    public int getQi() {
        return qi;
    }

    /**
     * 获取当前灵气增长速度的方法
     * @return 当前灵气增长速度
     */
    public double getQiRate() {
        return qiRate;
    }

    /**
     * 获取炼丹数据的方法
     * @return 炼丹数据
     */
    public Map<String, AlchemyController.PillData> getPills() {
        return pills;
    }

    /**
     * 获取当前境界等级的方法
     * @return 当前境界等级
     */
    public int getStageLevel() {
        return stageLevel;
    }
}
