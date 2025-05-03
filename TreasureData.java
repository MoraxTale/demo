package com.example.demo1;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import static com.example.demo1.Controller.BASE_MAX_OFFLINE_TIME_MS;
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
        return level >= maxLevel;
    }
    private boolean obtained;
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
        this.effectValue = effectValue;
        this.effectPercentage = effectValue;
        this.effectValue = effectType.equals("ADVENTURE_ATTEMPTS") ? 1 : effectValue;
        this.obtained = false;
    }
    public boolean isObtained() {
        return obtained;
    }

    public void setObtained(boolean obtained) {
        this.obtained = obtained;
    }
    // 修改效果描述方法
    public String getEffectDescription() {
        return switch (effectType) {
            case "CLICK_BONUS" -> String.format("点击加成+%.0f", effectValue * level);
            case "AUTO_RATE" -> String.format("修炼速度+%.1f%%", effectPercentage * level);
            case "AUTO_RATE_BASE" -> String.format("每秒修炼速度+%.1f", effectValue ); // 新增
            case "OFFLINE_TIME" -> String.format("离线时间+%.0f秒", effectValue * level);
            case "ADVENTURE_ATTEMPTS" -> String.format("每日冒险次数+%.0f次", effectValue );
            default -> "特殊效果";
        };
    }
    public String getEffectType() {
        return effectType;
    }

    public double getEffectPercentage() {
        return effectPercentage;
    }
    public double getEffectValue() {
        return effectValue;
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
            this.upgradeCost = (int)(upgradeCost * 10);

            // 根据不同类型更新效果值
            switch (effectType) {
                case "CLICK_BONUS":
                    this.effectValue *= 5; // 每级增加1000点击加成
                    break;
                case "AUTO_RATE":
                    this.effectPercentage += 1; // 每级增加0.5%修炼速度
                    break;
                case "AUTO_RATE_BASE":
                    this.effectValue *= 2; // 每级增加5.0基础修炼速度
                    break;
                case "OFFLINE_TIME":
                    this.effectValue += 1800; // 每级增加60秒离线时间
                    break;
                case "ADVENTURE_ATTEMPTS":  // 新增：冒险罗盘升级逻辑
                    this.effectValue += 1;  // 每次升级增加1次冒险次数
                    break;
            }

            if (mainController != null) {
                mainController.applyTreasureEffects();
            }
        }
    }

    // 添加getter方法
    public Object getMaxLevel() {
        return maxLevel;
    }
}