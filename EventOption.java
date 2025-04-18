package com.example.demo;

/**
 * 事件选项类，用于表示随机事件中的选项
 */
public record EventOption(String description, int reward) {
    /**
     * 获取选项描述的方法
     * @return 选项描述
     */
    public String getDescription() {
        return "";
    }

    /**
     * 获取选项奖励值的方法
     * @return 选项奖励值
     */
    public int getReward() {
        return 0;
    }
}