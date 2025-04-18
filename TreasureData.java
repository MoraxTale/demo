package com.example.demo;

public class TreasureData {
    private String name;
    private String effect; // 法宝的效果描述
    private int level; // 当前法宝等级
    private int nextUpgradeCost; // 升级所需灵气

    // 修改后的构造器，包含完整的参数
    public TreasureData(String name, String effect, int level, int nextUpgradeCost) {
        this.name = name;
        this.effect = effect;
        this.level = level;
        this.nextUpgradeCost = nextUpgradeCost;
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public String getEffect() {
        return effect;
    }

    public int getLevel() {
        return level;
    }

    public int getNextUpgradeCost() {
        return nextUpgradeCost;
    }

    public void upgrade() {
        this.level++; // 等级提升
        this.nextUpgradeCost += 5000; // 假设每次升级费用增加5000灵气
    }
}
