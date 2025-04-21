package com.example.demo1;

// 导入 Java 序列化相关类
import java.io.Serializable;
// 导入 Java 集合框架中的 Map 接口
import java.util.Map;

/**
 * 游戏状态类，用于保存游戏的当前状态，可序列化以便保存到文件
 */
public class GameState implements Serializable {
    // 序列化版本号，确保序列化和反序列化的兼容性
    private static final long serialVersionUID = 1L;
    public static final long BASE_MAX_OFFLINE_TIME_MS = 60 * 1000; // 基础60秒离线时间
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
    private Map<String, TreasureData> treasures; // 新增：法宝数据字段
    // 基础最大离线时间（毫秒），设为可配置的静态变量

    public static long getMaxOfflineTimeMs(Map<String, TreasureData> treasures) {
        long maxTime = BASE_MAX_OFFLINE_TIME_MS;
        if (treasures != null) {
            for (TreasureData treasure : treasures.values()) {
                if ("OFFLINE_TIME".equals(treasure.getEffectType())) {
                    maxTime += (long)(treasure.getEffectValue() * 1000);
                }
            }
        }
        return maxTime;
    }
    // 新增方法获取基础值（用于调试）
    public static long getBaseMaxOfflineTime() {
        return BASE_MAX_OFFLINE_TIME_MS;
    }

    // 添加设置方法

    // 修改获取最大离线时间的方法
    /**
     * 构造方法，初始化游戏状态对象
     * @param qi 当前灵气值
     * @param qiRate 当前灵气增长速度
     * @param pills 炼丹数据
     * @param stageLevel 当前境界等级
     */
    public GameState(int qi, double qiRate, Map<String, AlchemyController.PillData> pills, Map<String, TreasureData> treasures,int stageLevel,long lastSaveTime) {
        this.qi = qi;
        this.qiRate = qiRate;
        this.pills = pills;
        this.stageLevel = stageLevel;
        this.treasures = treasures;
        this.lastSaveTime = lastSaveTime; // 记录保存时间
    }

    public static Object getInstance() {
        return null;
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

    // 新增：法宝数据Getter
    public Map<String, TreasureData> getTreasures() {
        return treasures;
    }


    public long getLastSaveTime() {
        return lastSaveTime;
    }
    /**
     * 获取当前境界等级的方法
     * @return 当前境界等级
     */
    public int getStageLevel() {
        return stageLevel;
    }
    @Override
    public String toString() {
        return String.format(
                "GameState{qi=%d, qiRate=%.1f, pills=%s, stageLevel=%d}",
                qi, qiRate, (pills != null ? pills.size() + " items" : "null"), stageLevel
        );
    }
}