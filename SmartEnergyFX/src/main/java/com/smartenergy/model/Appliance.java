package com.smartenergy.model;

public class Appliance {
    private String name;
    private double wattage;
    private double dailyHours;
    private int ageYears;
    private double rate;

    // Calculated fields
    private double agingLossPercent;
    private double actualWattage;
    private double baseMonthlyKWh;
    private double actualMonthlyKWh;
    private double extraMonthlyCost;
    private double annualWasteCost;
    private double efficiencyPercent;
    private double annualSavingsIfReplaced;
    private String recommendation;

    public Appliance(String name, double wattage, double dailyHours, int ageYears, double rate) {
        this.name = name;
        this.wattage = wattage;
        this.dailyHours = dailyHours;
        this.ageYears = ageYears;
        this.rate = rate;
        calculate();
    }

    private void calculate() {
        double lossPerYear = ageYears <= 5 ? 1.0 : 1.5;
        agingLossPercent = Math.min(ageYears * lossPerYear, 40.0);
        actualWattage = wattage * (1 + agingLossPercent / 100.0);
        baseMonthlyKWh = (wattage * dailyHours * 30) / 1000.0;
        actualMonthlyKWh = (actualWattage * dailyHours * 30) / 1000.0;
        double extraUnits = actualMonthlyKWh - baseMonthlyKWh;
        extraMonthlyCost = extraUnits * rate;
        annualWasteCost = extraMonthlyCost * 12;
        efficiencyPercent = 100.0 - agingLossPercent;

        double modernWatts = wattage * 0.90;
        double modernMonthly = (modernWatts * dailyHours * 30) / 1000.0;
        annualSavingsIfReplaced = (actualMonthlyKWh - modernMonthly) * rate * 12;

        if (efficiencyPercent < 70) {
            recommendation = "REPLACE IMMEDIATELY — Efficiency critically low";
        } else if (efficiencyPercent < 85) {
            recommendation = "Plan replacement within 1–2 years";
        } else {
            recommendation = "Good condition — Regular servicing recommended";
        }
    }

    public String getName() { return name; }
    public double getWattage() { return wattage; }
    public double getDailyHours() { return dailyHours; }
    public int getAgeYears() { return ageYears; }
    public double getRate() { return rate; }
    public double getAgingLossPercent() { return agingLossPercent; }
    public double getActualWattage() { return actualWattage; }
    public double getBaseMonthlyKWh() { return baseMonthlyKWh; }
    public double getActualMonthlyKWh() { return actualMonthlyKWh; }
    public double getExtraMonthlyCost() { return extraMonthlyCost; }
    public double getAnnualWasteCost() { return annualWasteCost; }
    public double getEfficiencyPercent() { return efficiencyPercent; }
    public double getAnnualSavingsIfReplaced() { return annualSavingsIfReplaced; }
    public String getRecommendation() { return recommendation; }
}
