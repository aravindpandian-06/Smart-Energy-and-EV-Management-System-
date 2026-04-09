# Smart Household Energy & EV Efficiency Management System
### JavaFX Desktop Application — Clean Light Professional Theme

---

## Project Structure

```
SmartEnergyFX/
├── pom.xml
└── src/main/java/
    ├── module-info.java
    └── com/smartenergy/
        ├── MainApp.java                        ← Application entry point
        ├── model/
        │   ├── BillRecord.java                 ← Bill data model
        │   ├── Appliance.java                  ← Appliance + efficiency calc
        │   ├── UPSLoad.java                    ← UPS load model
        │   └── EVBattery.java                  ← EV battery model
        ├── util/
        │   └── UIHelper.java                   ← UI factory & formatting utils
        ├── controller/
        │   ├── BillAnalyzerController.java     ← Module 1 UI + logic
        │   ├── ApplianceController.java        ← Module 2 UI + logic
        │   ├── UPSController.java              ← Module 3 UI + logic
        │   └── EVBatteryController.java        ← Module 4 UI + logic
        └── view/
            └── MainWindow.java                 ← Stage, header, sidebar, nav
```

---

## Requirements

- Java JDK 17 or later
- JavaFX SDK 21 (bundled via Maven)
- Maven 3.8+

---

## How to Run

### Option A — Maven (Recommended)

```bash
cd SmartEnergyFX
mvn javafx:run
```

### Option B — IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA
2. Let IntelliJ auto-import the Maven project
3. Right-click `MainApp.java` → Run 'MainApp.main()'

### Option C — VS Code

1. Install the "Extension Pack for Java" from the VS Code marketplace
2. Open the project folder
3. Press F5 or click Run on `MainApp.java`

### Option D — Eclipse

1. File → Import → Existing Maven Projects
2. Select the SmartEnergyFX folder
3. Right-click project → Run As → Java Application → select `MainApp`

---

## Modules

### 1. Electricity Bill Analyzer
- Enter monthly consumption in kWh
- Select tariff rate (₹3.50 to ₹8.00, or custom)
- Calculates: energy charge, 5% tax, subsidy (up to 10%), fixed charges
- Shows total bill, avg daily usage, CO₂ footprint
- Bar chart shows usage across tariff brackets
- Stores up to 24 months of history in a table

### 2. Appliance Efficiency Monitor
- Enter appliance name, wattage, daily hours, age, and tariff rate
- Aging model: 1%/year for first 5 years, 1.5%/year after (max 40%)
- Shows actual draw vs rated wattage
- Calculates extra monthly cost, annual waste, annual savings if replaced
- Color-coded efficiency bar (green/amber/red)
- Stores all analyzed appliances in a list with delete option

### 3. UPS Load Stability Analyzer
- Enter UPS capacity (VA), battery Ah, voltage, power factor
- Select from 10 pre-loaded appliances OR add custom loads
- Calculates: total load watts, load %, estimated backup time
- Overload detection → danger/warning/safe color alerts
- Backup time = (Ah × V) / totalWatts × 0.85 efficiency factor

### 4. EV Battery & Charging Analyzer
- Enter vehicle model, original/current range, age, battery kWh
- Calculates battery health % = (currentRange / originalRange) × 100
- Degradation rate per year, actual kWh/100km consumption
- Monthly charging cost, annual cost, estimated years to renewal (at 70% threshold)
- Stores analysis history in a table

---

## Key Design Decisions

- **No external database** — data is stored in Java ObservableLists during the session
- **MVC pattern** — Models handle calculations, Controllers handle UI, MainWindow handles navigation
- **Lazy initialization** — module controllers are created only when first navigated to
- **UIHelper factory** — all styling is centralized, no CSS files needed

---

## Calculations Reference

| Metric | Formula |
|--------|---------|
| Energy charge | units × rate |
| Tax | energyCharge × 0.05 |
| Subsidy | energyCharge × (10% if ≤100 units, 5% if ≤200) |
| Total bill | energyCharge − subsidy + tax + fixedCharge |
| Aging loss | min(age × 1.0 or 1.5, 40)% |
| Actual wattage | ratedWatts × (1 + agingLoss/100) |
| UPS backup hrs | (Ah × V / totalWatts) × 0.85 |
| Battery health | (currentRange / originalRange) × 100 |
| Monthly EV kWh | (distance/100) × kWhPer100km / chargerEff |
