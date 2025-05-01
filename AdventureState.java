// AdventureState.java
package com.example.demo1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AdventureState implements Serializable {
    private long startTime; // 冒险开始时间戳
    private int currentArea; // 当前冒险区域
    private Map<Integer, Integer> dailyCounts = new HashMap<>(); // 各区域当日次数
    private int totalCompleted; // 总完成次数（用于剧情触发）

    // Getters and Setters
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public int getCurrentArea() { return currentArea; }
    public void setCurrentArea(int currentArea) { this.currentArea = currentArea; }
    public Map<Integer, Integer> getDailyCounts() { return dailyCounts; }
    public int getTotalCompleted() { return totalCompleted; }
    public void incrementTotalCompleted() { totalCompleted++; }
}