package com.example.demo;

import java.io.Serializable;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private int qi;
    private double qiRate;
    private Map<String, AlchemyController.PillData> pills;
    private Map<String, TreasureData> treasures; // 新增：法宝数据字段
    private int stageLevel;

    // 修改后的构造方法（新增 treasures 参数）
    public GameState(int qi,
                     double qiRate,
                     Map<String, AlchemyController.PillData> pills,
                     Map<String, TreasureData> treasures, // 新增参数
                     int stageLevel) {
        this.qi = qi;
        this.qiRate = qiRate;
        this.pills = pills;
        this.treasures = treasures; // 新增：初始化法宝数据
        this.stageLevel = stageLevel;
    }

    // 新增：法宝数据Getter
    public Map<String, TreasureData> getTreasures() {
        return treasures;
    }

    // 原有Getter保持不变
    public int getQi() {
        return qi;
    }

    public double getQiRate() {
        return qiRate;
    }

    public Map<String, AlchemyController.PillData> getPills() {
        return pills;
    }

    public int getStageLevel() {
        return stageLevel;
    }
}
