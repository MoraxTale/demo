package com.example.demo1;

import java.util.HashMap;
import java.util.Map;

/**
 * 剧情独立状态管理类
 * 功能：管理剧情相关状态，与原系统完全隔离
 */
public class StoryState {
    private static StoryState instance;

    // 剧情独立状态
    private int qi = 10000;          // 灵气值（示例初始值）
    private int stageLevel = 2;      // 境界等级（示例初始值）
    private Map<String, Integer> buffs = new HashMap<>(); // Buff状态
    private Map<String, Integer> items = new HashMap<>(); // 物品库存

    // 私有构造器（防止外部实例化）
    private StoryState() {}

    /**
     * 获取单例实例
     */
    public static StoryState getInstance() {
        if (instance == null) {
            instance = new StoryState();
        }
        return instance;
    }

    //------------------------ 灵气操作 ------------------------
    /**
     * 扣除灵气
     * @param amount 扣除数量
     * @return 是否扣除成功
     */
    public boolean deductQi(int amount) {
        if (qi >= amount) {
            qi -= amount;
            return true;
        }
        return false;
    }

    /**
     * 增加灵气
     * @param amount 增加数量
     */
    public void addQi(int amount) {
        qi += amount;
    }

    //------------------------ 境界操作 ------------------------
    /**
     * 降低境界等级
     */
    public void decreaseStageLevel() {
        stageLevel = Math.max(0, stageLevel - 1);
    }

    //------------------------ Buff管理 ------------------------
    /**
     * 添加Buff
     * @param name Buff名称
     * @param duration 持续次数
     */
    public void addBuff(String name, int duration) {
        buffs.put(name, duration);
    }

    //------------------------ 物品管理 ------------------------
    /**
     * 添加物品
     * @param item 物品名称
     */
    public void addItem(String item) {
        items.put(item, items.getOrDefault(item, 0) + 1);
    }

    /**
     * 获取物品数量
     * @param item 物品名称
     */
    public int getItemCount(String item) {
        return items.getOrDefault(item, 0);
    }
}
