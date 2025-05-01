package com.example.demo;

import java.io.Serial;
import java.io.Serializable;

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

    public boolean isMaxLevel() {
        return level >= maxLevel;
    }

    public TreasureData(String id, String name, String description,
                        String acquisitionMethod, int upgradeCost,
                        String effectType, double initialEffect)  {
        this.id = id;
        this.name = name;
        this.description = description;
        this.acquisitionMethod = acquisitionMethod;
        this.level = 1;
        this.upgradeCost = upgradeCost;
        this.effectType = effectType;
        this.effectValue = initialEffect;
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
    public String getEffectDescription() {
        switch (effectType) {
            case "CLICK_BONUS":
                return String.format("每次点击+%d灵气", (int)effectValue);
            case "AUTO_RATE":
                return String.format("灵气增速+%.1f/s", effectValue);
            case "SUCCESS_RATE":
                return String.format("成功率+%.1f%%", effectValue*100);
            default:
                return "未知效果";
        }
    }

    public void upgrade(Controller mainController) {
        if (mainController.deductQi(upgradeCost)) {
            level++; // 确保等级递增
            // 不同法宝的强化逻辑
            switch (effectType) {
                case "CLICK_BONUS":
                    effectValue *= 1.2;
                    break;
                case "AUTO_RATE":
                    effectValue *= 1.15;
                    break;
                case "SUCCESS_RATE":
                    effectValue *= 1.1;
                    break;
            }
            upgradeCost *= 1.5; // 升级费用增加50%
            System.out.println("[DEBUG] 升级后数据 - 等级:" + level + " 效果值:" + effectValue); // 调试输出
            mainController.applyTreasureEffects(); // 强制刷新效果
        }
    }

    // 添加getter方法
    public String getEffectType() {
        return effectType;
    }

    public double getEffectValue() {
        return effectValue;
    }
    public static final String EFFECT_ADVENTURE_ATTEMPTS = "ADVENTURE_ATTEMPTS";
}
