package com.example.demo1;

public class EventOption {
    private String description;
    private int minReward;
    private int maxReward;

    public EventOption(String description, int minReward, int maxReward) {
        this.description = description;
        this.minReward = minReward;
        this.maxReward = maxReward;
    }

    public String getDescription() {
        return description;
    }

    public int getMinReward() {
        return minReward;
    }

    public int getMaxReward() {
        return maxReward;
    }
}
