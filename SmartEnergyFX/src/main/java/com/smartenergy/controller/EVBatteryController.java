package com.smartenergy.controller;

import com.smartenergy.model.EVBattery;
import com.smartenergy.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EVBatteryController {

    private VBox root;
    private TextField modelField;
    private TextField origRangeField;
    private TextField currRangeField;
    private TextField ageField;
    private TextField kwhField;
    private TextField distField;
    private TextField rateField;
    private TextField effField;

    private VBox resultSection;
    private FlowPane metricsPane;
    private ProgressBar healthBar;
    private Label healthBarLabel;
    private VBox alertBox;
    private VBox recsBox;

    private TableView<EVRecord> historyTable;
    private ObservableList<EVRecord> historyData = FXCollections.observableArrayList();

    public EVBatteryController() {
        root = new VBox(20);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");
        buildUI();
    }

    private void buildUI() {
        root.getChildren().add(UIHelper.sectionHeader(
            "EV Battery & Charging Analyzer",
            "Track battery health degradation, estimate charging costs, plan renewal"
        ));

        // ── Input card ────────────────────────────────────────────────────────
        VBox inputCard = UIHelper.card("EV Details");
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(25);
        grid.getColumnConstraints().addAll(c, c, c, c);

        modelField = new TextField();
        modelField.setPromptText("e.g. Tata Nexon EV");
        origRangeField = new TextField();
        origRangeField.setPromptText("e.g. 400");
        currRangeField = new TextField();
        currRangeField.setPromptText("e.g. 320");
        ageField = new TextField();
        ageField.setPromptText("e.g. 3");
        kwhField = new TextField();
        kwhField.setPromptText("e.g. 40");
        distField = new TextField();
        distField.setPromptText("e.g. 1200");
        rateField = new TextField("7.0");
        rateField.setPromptText("e.g. 7.0");
        effField = new TextField("90");
        effField.setPromptText("90");

        grid.add(UIHelper.formField("Vehicle Model / Name", modelField), 0, 0);
        grid.add(UIHelper.formField("Original Range (km)", origRangeField), 1, 0);
        grid.add(UIHelper.formField("Current Range (km)", currRangeField), 2, 0);
        grid.add(UIHelper.formField("Vehicle Age (years)", ageField), 3, 0);
        grid.add(UIHelper.formField("Battery Capacity (kWh)", kwhField), 0, 1);
        grid.add(UIHelper.formField("Monthly Distance (km)", distField), 1, 1);
        grid.add(UIHelper.formField("Electricity Rate (₹/unit)", rateField), 2, 1);
        grid.add(UIHelper.formField("Charger Efficiency (%)", effField), 3, 1);

        Button analyzeBtn = UIHelper.primaryButton("Analyze Battery Health");
        Button clearBtn = UIHelper.secondaryButton("Clear History");
        analyzeBtn.setOnAction(e -> analyzeEV());
        clearBtn.setOnAction(e -> clearHistory());

        HBox btnRow = new HBox(10, analyzeBtn, clearBtn);
        inputCard.getChildren().addAll(grid, btnRow);

        // ── Result card ───────────────────────────────────────────────────────
        resultSection = new VBox(14);
        resultSection.setVisible(false);
        resultSection.setManaged(false);

        VBox resultCard = UIHelper.card("Battery Health Report");
        metricsPane = new FlowPane(12, 12);

        Label healthTitle = new Label("Battery Health");
        healthTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        healthTitle.setTextFill(Color.web(UIHelper.GRAY_600));
        healthBarLabel = new Label("0%");
        healthBarLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox healthHeader = new HBox();
        healthHeader.getChildren().addAll(healthTitle, sp, healthBarLabel);
        healthBar = new ProgressBar(0);
        healthBar.setPrefWidth(Double.MAX_VALUE);
        healthBar.setPrefHeight(12);
        VBox healthSection = new VBox(6, healthHeader, healthBar);

        alertBox = new VBox();
        recsBox = new VBox(8);
        Label recsTitle = new Label("Recommendations");
        recsTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        recsTitle.setTextFill(Color.web(UIHelper.GRAY_600));

        resultCard.getChildren().addAll(metricsPane, healthSection, alertBox, recsTitle, recsBox);
        resultSection.getChildren().add(resultCard);

        // ── History table card ────────────────────────────────────────────────
        VBox histCard = UIHelper.card("EV Analysis History");
        historyTable = buildHistoryTable();
        histCard.getChildren().add(historyTable);

        root.getChildren().addAll(inputCard, resultSection, histCard);
    }

    private void analyzeEV() {
        // Validation
        String model = modelField.getText().trim().isEmpty() ? "Your EV" : modelField.getText().trim();

        double orig, curr, age, kwh, dist, rate, eff;
        try {
            orig = Double.parseDouble(origRangeField.getText().trim());
            curr = Double.parseDouble(currRangeField.getText().trim());
            age = Double.parseDouble(ageField.getText().trim());
            kwh = Double.parseDouble(kwhField.getText().trim());
            dist = Double.parseDouble(distField.getText().trim());
            rate = Double.parseDouble(rateField.getText().trim());
            eff = Double.parseDouble(effField.getText().trim());
            if (orig <= 0 || curr <= 0 || age < 0 || kwh <= 0 || dist <= 0 || rate <= 0 || eff <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            UIHelper.showAlert("Validation Error", "Please fill in all EV details with valid positive numbers.");
            return;
        }

        if (curr > orig) {
            UIHelper.showAlert("Validation Error", "Current range cannot exceed the original range.");
            return;
        }

        EVBattery ev = new EVBattery(model, orig, curr, age, kwh, dist, rate, eff);

        String healthColor = ev.getHealthPercent() < 70 ? UIHelper.RED_PRIMARY
            : ev.getHealthPercent() < 80 ? UIHelper.AMBER_PRIMARY
            : UIHelper.GREEN_PRIMARY;

        // Metrics
        metricsPane.getChildren().clear();
        String renewalStr = ev.getYearsToRenewal() > 0
            ? UIHelper.formatDouble(ev.getYearsToRenewal(), 1) + " yrs" : "Now";

        metricsPane.getChildren().addAll(
            UIHelper.metricCard("Battery Health", UIHelper.formatPercent(ev.getHealthPercent()), "", healthColor),
            UIHelper.metricCard("Degradation", UIHelper.formatPercent(ev.getDegradationPercent()), "over " + (int)age + " years", UIHelper.AMBER_PRIMARY),
            UIHelper.metricCard("Deg. Rate", UIHelper.formatDouble(ev.getDegradationPerYear(), 2) + "%", "per year", UIHelper.GRAY_400),
            UIHelper.metricCard("Consumption", UIHelper.formatDouble(ev.getKWhPer100kmActual(), 2), "kWh/100km", UIHelper.GRAY_900),
            UIHelper.metricCard("Monthly kWh", UIHelper.formatDouble(ev.getMonthlyKWh(), 1), "charging units", UIHelper.BLUE_PRIMARY),
            UIHelper.metricCard("Monthly Cost", UIHelper.formatCurrency(ev.getMonthlyCost()), "at ₹" + rate + "/unit", "#7C3AED"),
            UIHelper.metricCard("Annual Cost", UIHelper.formatCurrency(ev.getAnnualCost()), "/year", UIHelper.AMBER_PRIMARY),
            UIHelper.metricCard("Renewal In", renewalStr, "estimated", UIHelper.GRAY_400)
        );

        // Health bar
        healthBar.setProgress(Math.min(ev.getHealthPercent() / 100.0, 1.0));
        healthBar.setStyle("-fx-accent: " + healthColor + ";");
        healthBarLabel.setText(UIHelper.formatPercent(ev.getHealthPercent()));
        healthBarLabel.setTextFill(Color.web(healthColor));

        // Alert
        alertBox.getChildren().clear();
        String alertType = ev.getHealthPercent() < 70 ? "danger"
            : ev.getHealthPercent() < 80 ? "warning" : "success";
        alertBox.getChildren().add(UIHelper.alertBanner(alertType, ev.getStatusMessage()));

        // Recommendations
        recsBox.getChildren().clear();
        for (String rec : buildRecs(ev)) {
            recsBox.getChildren().add(recItem(rec, ev.getHealthPercent()));
        }

        resultSection.setVisible(true);
        resultSection.setManaged(true);

        // Save history
        String status = ev.getStatus();
        historyData.add(0, new EVRecord(
            model,
            UIHelper.formatDouble(orig, 0) + " km",
            UIHelper.formatDouble(curr, 0) + " km",
            UIHelper.formatPercent(ev.getHealthPercent()),
            UIHelper.formatDouble(age, 1),
            UIHelper.formatCurrency(ev.getMonthlyCost()),
            status
        ));
        if (historyData.size() > 20) historyData.remove(historyData.size() - 1);
    }

    private java.util.List<String> buildRecs(EVBattery ev) {
        java.util.List<String> recs = new java.util.ArrayList<>();
        if (ev.getHealthPercent() < 70) {
            recs.add("Battery health is critically low. Contact your EV dealer for battery health certification.");
            recs.add("Charging costs have increased by ~" +
                UIHelper.formatDouble((ev.getKWhPer100kmActual() / ev.getKWhPer100kmBase() - 1) * 100, 0) +
                "% due to degradation.");
            recs.add("Consider battery replacement or vehicle renewal for optimal performance.");
        } else if (ev.getHealthPercent() < 80) {
            recs.add("Avoid frequent DC fast charging to slow further battery degradation.");
            recs.add("Keep charge level between 20%–80% for daily use to extend battery life.");
            recs.add("Plan for battery replacement or upgrade in ~" +
                UIHelper.formatDouble(ev.getYearsToRenewal(), 1) + " years.");
        } else {
            recs.add("Battery is in good health. Regular charging habits will maintain performance.");
            recs.add("Avoid deep discharges below 10% and extreme heat/cold exposure.");
            recs.add("Schedule annual battery diagnostics with your EV service center.");
        }
        return recs;
    }

    private HBox recItem(String text, double health) {
        String color = health < 70 ? UIHelper.RED_PRIMARY : health < 80 ? UIHelper.AMBER_PRIMARY : UIHelper.GREEN_PRIMARY;
        String bg = health < 70 ? "#FEF2F2" : health < 80 ? "#FFFBEB" : "#F0FDF4";
        Label dot = new Label("•");
        dot.setFont(Font.font("System", FontWeight.BOLD, 16));
        dot.setTextFill(Color.web(color));
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", 13));
        lbl.setTextFill(Color.web(UIHelper.GRAY_600));
        lbl.setWrapText(true);
        HBox.setHgrow(lbl, Priority.ALWAYS);
        HBox box = new HBox(10, dot, lbl);
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + color +
                     "; -fx-border-width: 0 0 0 3; -fx-background-radius: 0 6 6 0;");
        return box;
    }

    private TableView<EVRecord> buildHistoryTable() {
        TableView<EVRecord> table = new TableView<>(historyData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        table.setPlaceholder(new Label("No EV records yet. Analyze an EV to add records."));

        String[] titles = {"Vehicle", "Orig. Range", "Curr. Range", "Health", "Age (yrs)", "Monthly Cost", "Status"};
        String[] props = {"vehicleModel", "origRange", "currRange", "health", "age", "monthlyCost", "status"};
        for (int i = 0; i < titles.length; i++) {
            TableColumn<EVRecord, String> col = new TableColumn<>(titles[i]);
            col.setCellValueFactory(new PropertyValueFactory<>(props[i]));
            table.getColumns().add(col);
        }
        return table;
    }

    private void clearHistory() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Clear EV analysis history?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) historyData.clear();
        });
    }

    public VBox getView() { return root; }

    // ── Inner DTO for TableView ───────────────────────────────────────────────
    public static class EVRecord {
        private final javafx.beans.property.SimpleStringProperty vehicleModel;
        private final javafx.beans.property.SimpleStringProperty origRange;
        private final javafx.beans.property.SimpleStringProperty currRange;
        private final javafx.beans.property.SimpleStringProperty health;
        private final javafx.beans.property.SimpleStringProperty age;
        private final javafx.beans.property.SimpleStringProperty monthlyCost;
        private final javafx.beans.property.SimpleStringProperty status;

        public EVRecord(String vehicleModel, String origRange, String currRange, String health,
                        String age, String monthlyCost, String status) {
            this.vehicleModel = new javafx.beans.property.SimpleStringProperty(vehicleModel);
            this.origRange = new javafx.beans.property.SimpleStringProperty(origRange);
            this.currRange = new javafx.beans.property.SimpleStringProperty(currRange);
            this.health = new javafx.beans.property.SimpleStringProperty(health);
            this.age = new javafx.beans.property.SimpleStringProperty(age);
            this.monthlyCost = new javafx.beans.property.SimpleStringProperty(monthlyCost);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public String getVehicleModel() { return vehicleModel.get(); }
        public String getOrigRange() { return origRange.get(); }
        public String getCurrRange() { return currRange.get(); }
        public String getHealth() { return health.get(); }
        public String getAge() { return age.get(); }
        public String getMonthlyCost() { return monthlyCost.get(); }
        public String getStatus() { return status.get(); }
    }
}
