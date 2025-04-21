package com.example.demo1;

public class PillConfig {
    private final String name;
    private final int cost;
    private final double rate;
    private final double successRateImpact;
    private final int level;
    private final String pillId;
    public PillConfig(String pillId,String name, int cost, double rate, double successRateImpact,int level) {
        this.pillId = pillId;
        this.name = name;
        this.cost = cost;
        this.rate = rate;
        this.successRateImpact = successRateImpact;
        this.level=level;
    }

    // Getters
    public int getLevel(){return level;}
    public String getName() { return name; }
    public int getCost() { return cost; }
    public double getRate() { return rate; }
    public double getSuccessRateImpact() { return successRateImpact; }
}