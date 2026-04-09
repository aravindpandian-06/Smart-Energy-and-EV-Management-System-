package com.smartenergy.model;

public class EVBattery {
    private String vehicleModel;
    private double originalRangeKm;
    private double currentRangeKm;
    private double ageYears;
    private double batteryCapacityKWh;
    private double monthlyDistanceKm;
    private double electricityRate;
    private double chargerEfficiencyPercent;

    // Results
    private double healthPercent;
    private double degradationPercent;
    private double degradationPerYear;
    private double kWhPer100kmBase;
    private double kWhPer100kmActual;
    private double monthlyKWh;
    private double monthlyCost;
    private double annualCost;
    private double yearsToRenewal;
    private String status;
    private String statusMessage;

    public EVBattery(String vehicleModel, double originalRangeKm, double currentRangeKm,
                     double ageYears, double batteryCapacityKWh, double monthlyDistanceKm,
                     double electricityRate, double chargerEfficiencyPercent) {
        this.vehicleModel = vehicleModel;
        this.originalRangeKm = originalRangeKm;
        this.currentRangeKm = currentRangeKm;
        this.ageYears = ageYears;
        this.batteryCapacityKWh = batteryCapacityKWh;
        this.monthlyDistanceKm = monthlyDistanceKm;
        this.electricityRate = electricityRate;
        this.chargerEfficiencyPercent = chargerEfficiencyPercent;
        calculate();
    }

    private void calculate() {
        healthPercent = (currentRangeKm / originalRangeKm) * 100.0;
        degradationPercent = 100.0 - healthPercent;
        degradationPerYear = ageYears > 0 ? degradationPercent / ageYears : 0;

        kWhPer100kmBase = (batteryCapacityKWh / originalRangeKm) * 100.0;
        kWhPer100kmActual = kWhPer100kmBase * (100.0 / healthPercent);

        double effFactor = chargerEfficiencyPercent / 100.0;
        monthlyKWh = (monthlyDistanceKm / 100.0) * kWhPer100kmActual / effFactor;
        monthlyCost = monthlyKWh * electricityRate;
        annualCost = monthlyCost * 12;

        double renewalThreshold = 70.0;
        if (healthPercent > renewalThreshold && degradationPerYear > 0) {
            yearsToRenewal = (healthPercent - renewalThreshold) / degradationPerYear;
        } else {
            yearsToRenewal = 0;
        }

        if (healthPercent < 70) {
            status = "CRITICAL";
            statusMessage = "Battery health critically low. Immediate battery replacement or vehicle renewal is strongly recommended.";
        } else if (healthPercent < 80) {
            status = "WARNING";
            statusMessage = "Battery showing moderate degradation. Plan replacement in ~" + String.format("%.1f", yearsToRenewal) + " years.";
        } else {
            status = "GOOD";
            statusMessage = "Battery health is good. Continue current charging habits to maintain performance.";
        }
    }

    public String getVehicleModel() { return vehicleModel; }
    public double getOriginalRangeKm() { return originalRangeKm; }
    public double getCurrentRangeKm() { return currentRangeKm; }
    public double getAgeYears() { return ageYears; }
    public double getBatteryCapacityKWh() { return batteryCapacityKWh; }
    public double getMonthlyDistanceKm() { return monthlyDistanceKm; }
    public double getElectricityRate() { return electricityRate; }
    public double getChargerEfficiencyPercent() { return chargerEfficiencyPercent; }
    public double getHealthPercent() { return healthPercent; }
    public double getDegradationPercent() { return degradationPercent; }
    public double getDegradationPerYear() { return degradationPerYear; }
    public double getKWhPer100kmBase() { return kWhPer100kmBase; }
    public double getKWhPer100kmActual() { return kWhPer100kmActual; }
    public double getMonthlyKWh() { return monthlyKWh; }
    public double getMonthlyCost() { return monthlyCost; }
    public double getAnnualCost() { return annualCost; }
    public double getYearsToRenewal() { return yearsToRenewal; }
    public String getStatus() { return status; }
    public String getStatusMessage() { return statusMessage; }
}
