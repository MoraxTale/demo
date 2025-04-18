package com.example.demo1;

import java.io.Serializable;
import java.util.Map;
public class GameState implements Serializable {
    public static long getMaxOfflineTimeMs() {
        return MAX_OFFLINE_TIME_MS;
    }
    // 序列化版本号，确保序列化和反序列化的兼容性
    private static final long serialVersionUID = 1L;
    private static final long MAX_OFFLINE_TIME_MS = 1 * 60 * 1000;
    // 当前灵气值
    private int qi;
    // 当前灵气增长速度
    private double qiRate;
    // 炼丹数据，存储丹药信息
    private Map<String, AlchemyController.PillData> pills;
    // 当前境界等级
    private int stageLevel;
    // 新增：保存时的系统时间（毫秒）
    private long lastSaveTime;


    /**
     * 构造方法，初始化游戏状态对象
     * @param qi 当前灵气值
     * @param qiRate 当前灵气增长速度
     * @param pills 炼丹数据
     * @param stageLevel 当前境界等级
     * @param lastSaveTime 保存时的系统时间（毫秒）
     */
    public GameState(int qi, double qiRate, Map<String, AlchemyController.PillData> pills, int stageLevel, long lastSaveTime) {
        this.qi = qi;
        this.qiRate = qiRate;
        this.pills = pills;
        this.stageLevel = stageLevel;
        this.lastSaveTime = lastSaveTime; // 记录保存时间
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

    /**
     * 获取保存时的系统时间的方法
     * @return 保存时的系统时间（毫秒）
     */
    public long getLastSaveTime() {
        return lastSaveTime;
    }

    @Override
    public String toString() {
        return String.format(
                "GameState{qi=%d, qiRate=%.1f, pills=%s, stageLevel=%d, lastSaveTime=%d}",
                qi, qiRate, (pills != null ? pills.size() + " items" : "null"), stageLevel, lastSaveTime
        );
    }
}
