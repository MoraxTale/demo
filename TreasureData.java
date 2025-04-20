package com.example.demo1;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

// 法宝数据类
public class TreasureData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;
    private String acquisitionMethod;
    private int level;
    private int upgradeCost;
    private double power;
    private String effectType; // 效果类型 CLICK_BONUS/AUTO_RATE/SUCCESS_RATE
    private double effectValue; // 效果数值
    private final int maxLevel = 10; // 最大等级
    private double effectPercentage; // 百分比加成
    public boolean isMaxLevel() {
        return level >= maxLevel;
    }
    public static long getMaxOfflineTimeMs(Map<String, TreasureData> treasures) {
        long baseTime = 1 * 60 * 1000; // 基础60秒
        if (treasures != null) {
            for (TreasureData treasure : treasures.values()) {
                if ("OFFLINE_TIME".equals(treasure.getEffectType())) {
                    baseTime += (long)(treasure.getEffectValue() * 1000); // 转换为毫秒
                }
            }
        }
        return baseTime;
    }
    public TreasureData(String id, String name, String description,
                        String acquisitionMethod, int upgradeCost,
                        String effectType, double effectPercentage)  {
        this.id = id;
        this.name = name;
        this.description = description;
        this.acquisitionMethod = acquisitionMethod;
        this.level = 1;
        this.upgradeCost = upgradeCost;
        this.effectType = effectType;
        this.effectPercentage = effectPercentage;
    }
    // 新增获取百分比效果的方法
    public double getEffectPercentage() {
        return effectPercentage * level; // 每级增加固定百分比
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }

    public int getLevel() {
        return level;
    }
    // 添加 setLevel 方法
    public void setLevel(int level) {
        if (level >= 1 && level <= maxLevel) {
            this.level = level;
        } else {
            throw new IllegalArgumentException("法宝等级必须在 1 到 " + maxLevel + " 之间");
        }
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public double getPower() {
        return power;
    }

    // 新增获取效果的方法
    public void upgrade(Controller mainController) {
        if (this.level < maxLevel) {
            this.level++;
            this.upgradeCost = (int)(upgradeCost * 1.5);// 升级成本增加50%
            if ("OFFLINE_TIME".equals(effectType)) {
                this.effectValue = 60 * level; // 确保每级增加60秒
            }
            // 根据不同类型更新效果值
            switch (effectType) {
                case "AUTO_RATE":
                    // 百分比加成保持不变
                    break;
                case "OFFLINE_TIME":
                    this.effectValue = 60 * level; // 每级增加60秒
                    break;
                case "CLICK_BONUS":
                    this.effectValue += 500; // 点击加成固定值增加
                    break;
            }
            if (mainController != null) {
                mainController.applyTreasureEffects(); // 立即应用新效果
            }
        }
    }
    public String getEffectDescription() {
        switch (effectType) {
            case "AUTO_RATE":
                return String.format("修炼速度+%.1f%%", effectValue * 100);
            case "OFFLINE_TIME":
                return String.format("最大离线时间+%.0f秒", effectValue);
            default:
                return String.format("效果值: %.1f", effectValue);
        }
    }
    // 添加getter方法
    public String getEffectType() {
        return effectType;
    }

    public double getEffectValue() {
        // 对于百分比加成类型，返回当前等级对应的加成值
        if ("AUTO_RATE".equals(effectType)) {
            return effectPercentage * level;
        }
        // 对于固定值类型，返回固定值
        return power;
    }

    public Object getMaxLevel() {
        return null;
    }
}