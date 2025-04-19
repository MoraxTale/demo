package com.example.demo;

public class PillConfig {
    private final String name;
    private final int cost;
    private final double rate;
    private final double successRateImpact;

    public PillConfig(String id,String name, int cost, double rate, double successRateImpact) {
        this.name = name;
        this.cost = cost;
        this.rate = rate;
        this.successRateImpact = successRateImpact;
    }

    // Getters
    public String getName() { return name; }
    public int getCost() { return cost; }
    public double getRate() { return rate; }
    public double getSuccessRateImpact() { return successRateImpact; }
}