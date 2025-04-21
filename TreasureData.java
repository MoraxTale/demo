package com.example.demo1;

import java.io.*;
import java.util.Map;

import static com.example.demo1.GameState.BASE_MAX_OFFLINE_TIME_MS;

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
    public static final int MAX_LEVEL = 10; // 最大等级
    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }
    public String getEffectDescription() {
        switch (effectType) {
            case "AUTO_RATE":
                return String.format("修炼速度+%.1f%%", effectPercentage * 100);
            case "OFFLINE_TIME":
                return String.format("最大离线时间+%.0f秒", effectValue);
            case "CLICK_BONUS":
                return String.format("点击加成+%.0f", effectValue);
            default:
                return "特殊效果";
        }
    }
    public TreasureData(String id, String name, String description,
                        String acquisitionMethod, int upgradeCost,
                        String effectType, double effectValue)  {
        this.id = id;
        this.name = name;
        this.description = description;
        this.acquisitionMethod = acquisitionMethod;
        this.level = 1;
        this.upgradeCost = upgradeCost;
        this.effectType = effectType;
        this.effectPercentage = effectValue;
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
    public static long getMaxOfflineTimeMs(Map<String, TreasureData> treasures) {
        long baseTime = BASE_MAX_OFFLINE_TIME_MS; // 使用GameState的基础值
        if (treasures != null) {
            for (TreasureData treasure : treasures.values()) {
                if ("OFFLINE_TIME".equals(treasure.getEffectType())) {
                    baseTime += (long)(treasure.getEffectValue() * 1000); // 转换为毫秒
                }
            }
        }
        return baseTime;
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
            this.upgradeCost = (int)(upgradeCost * 1.5);
            // 根据不同类型更新效果值
            // 明确更新 effectValue（关键修正）
            if ("OFFLINE_TIME".equals(effectType)) {
                this.effectValue = 60 * level; // 每级增加60秒
                System.out.printf("[DEBUG] 时空塔升级至Lv.%d，效果值=%.0f秒\n", level, effectValue);
            }
            if (mainController != null) {
                mainController.applyTreasureEffects(); // 确保调用效果应用
                mainController.getTreasureController().updateTreasureDisplay(); // 更新显示
            switch (effectType) {
                case "AUTO_RATE":
                    // 百分比加成保持不变
                    break;
                case "CLICK_BONUS":
                    this.effectValue += 1000; // 点击加成固定值增加
                    break;
            }
            }
        }
    }
    // 添加getter方法
    public String getEffectType() {
        return effectType;
    }

    public double getEffectValue() {
        return switch (effectType) {
            case "OFFLINE_TIME" -> 60 * level; // 每级增加60秒
            case "AUTO_RATE" -> effectPercentage * level;
            case "CLICK_BONUS" -> effectPercentage * level;
            default -> power;
        };

    }

    public Object getMaxLevel() {
        return null;
    }
}