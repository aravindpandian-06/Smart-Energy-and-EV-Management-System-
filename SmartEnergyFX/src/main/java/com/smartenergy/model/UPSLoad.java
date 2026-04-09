package com.smartenergy.model;

import java.util.List;
import java.util.ArrayList;

public class UPSLoad {
    private double upsCapacityVA;
    private double batteryAh;
    private double batteryVoltage;
    private double powerFactor;
    private List<UPSAppliance> connectedAppliances;

    // Results
    private double totalWatts;
    private double maxWatts;
    private double loadPercent;
    private double batteryWh;
    private double backupHours;
    private String status;
    private String statusMessage;

    public UPSLoad(double upsCapacityVA, double batteryAh, double batteryVoltage, double powerFactor) {
        this.upsCapacityVA = upsCapacityVA;
        this.batteryAh = batteryAh;
        this.batteryVoltage = batteryVoltage;
        this.powerFactor = powerFactor;
        this.connectedAppliances = new ArrayList<>();
    }

    public void addAppliance(UPSAppliance appliance) {
        connectedAppliances.add(appliance);
    }

    public void clearAppliances() {
        connectedAppliances.clear();
    }

    public void calculate() {
        totalWatts = connectedAppliances.stream().mapToDouble(UPSAppliance::getWatts).sum();
        maxWatts = upsCapacityVA * powerFactor;
        loadPercent = (totalWatts / maxWatts) * 100.0;
        batteryWh = batteryAh * batteryVoltage;
        backupHours = totalWatts > 0 ? (batteryWh / totalWatts) * 0.85 : 0;

        if (loadPercent > 100) {
            status = "OVERLOAD";
            statusMessage = "CRITICAL OVERLOAD! Total load exceeds UPS capacity. Disconnect appliances immediately to prevent shutdown.";
        } else if (loadPercent > 90) {
            status = "CRITICAL";
            statusMessage = "Critical load level. Risk of low-voltage signal and UPS instability. Remove non-essential loads.";
        } else if (loadPercent > 70) {
            status = "WARNING";
            statusMessage = "High load detected. UPS will function but backup time is reduced. Possible low-voltage fluctuation.";
        } else {
            status = "SAFE";
            statusMessage = "Load is within safe operating range. UPS running stably.";
        }
    }

    public double getUpsCapacityVA() { return upsCapacityVA; }
    public double getBatteryAh() { return batteryAh; }
    public double getBatteryVoltage() { return batteryVoltage; }
    public double getPowerFactor() { return powerFactor; }
    public List<UPSAppliance> getConnectedAppliances() { return connectedAppliances; }
    public double getTotalWatts() { return totalWatts; }
    public double getMaxWatts() { return maxWatts; }
    public double getLoadPercent() { return loadPercent; }
    public double getBatteryWh() { return batteryWh; }
    public double getBackupHours() { return backupHours; }
    public String getStatus() { return status; }
    public String getStatusMessage() { return statusMessage; }

    public static class UPSAppliance {
        private String name;
        private double watts;
        private boolean selected;

        public UPSAppliance(String name, double watts) {
            this.name = name;
            this.watts = watts;
            this.selected = false;
        }

        public String getName() { return name; }
        public double getWatts() { return watts; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }

        @Override
        public String toString() { return name + " (" + (int) watts + "W)"; }
    }
}
