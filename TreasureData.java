package com.example.demo;

import java.io.Serializable;

public class TreasureData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String effect;
    private final int purchaseCost; // 新增购买价格字段
    private int level;
    private int nextUpgradeCost;

    public TreasureData(String name, String effect, int purchaseCost, int baseUpgradeCost) {
        this.name = name;
        this.effect = effect;
        this.purchaseCost = purchaseCost;
        this.level = 0;
        this.nextUpgradeCost = baseUpgradeCost;
    }

    public void upgrade() {
        level++;
        nextUpgradeCost *= 1.5;
    }

    // Getters
    public String getName() { return name; }
    public String getEffect() { return effect; }
    public int getPurchaseCost() { return purchaseCost; }
    public int getLevel() { return level; }
    public int getNextUpgradeCost() { return nextUpgradeCost; }
}
