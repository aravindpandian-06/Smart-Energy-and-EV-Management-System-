package com.smartenergy.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BillRecord {
    private String month;
    private double units;
    private double rate;
    private double energyCharge;
    private double fixedCharge;
    private double tax;
    private double subsidy;
    private double totalBill;
    private LocalDate date;

    public BillRecord(String month, double units, double rate, double energyCharge,
                      double fixedCharge, double tax, double subsidy, double totalBill) {
        this.month = month;
        this.units = units;
        this.rate = rate;
        this.energyCharge = energyCharge;
        this.fixedCharge = fixedCharge;
        this.tax = tax;
        this.subsidy = subsidy;
        this.totalBill = totalBill;
        this.date = LocalDate.now();
    }

    public String getMonth() { return month; }
    public double getUnits() { return units; }
    public double getRate() { return rate; }
    public double getEnergyCharge() { return energyCharge; }
    public double getFixedCharge() { return fixedCharge; }
    public double getTax() { return tax; }
    public double getSubsidy() { return subsidy; }
    public double getTotalBill() { return totalBill; }
    public LocalDate getDate() { return date; }
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
}
