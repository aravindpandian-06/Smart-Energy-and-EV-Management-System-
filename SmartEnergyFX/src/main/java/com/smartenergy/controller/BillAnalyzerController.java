package com.smartenergy.controller;

import com.smartenergy.model.BillRecord;
import com.smartenergy.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class BillAnalyzerController {

    private VBox root;
    private TextField unitsField;
    private ComboBox<String> tariffCombo;
    private TextField customRateField;
    private TextField fixedChargeField;
    private ComboBox<String> monthCombo;

    private VBox resultSection;
    private FlowPane metricsPane;
    private VBox alertBox;
    private Canvas barCanvas;

    private TableView<BillRecord> historyTable;
    private ObservableList<BillRecord> historyData = FXCollections.observableArrayList();

    private static final String[] MONTHS = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    private static final String[] TARIFF_LABELS = {
        "₹3.50/unit — Domestic (0–100 units)",
        "₹5.00/unit — Domestic (101–300 units)",
        "₹6.50/unit — Domestic (301–500 units)",
        "₹8.00/unit — Domestic (500+ units)",
        "₹7.50/unit — Commercial",
        "Custom Rate..."
    };

    private static final double[] TARIFF_VALUES = {3.5, 5.0, 6.5, 8.0, 7.5, 0};

    public BillAnalyzerController() {
        root = new VBox(20);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");
        buildUI();
    }

    private void buildUI() {
        root.getChildren().add(UIHelper.sectionHeader(
            "Electricity Bill Analyzer",
            "Calculate your monthly electricity bill and track usage history"
        ));

        // ── Input card ────────────────────────────────────────────────────────
        VBox inputCard = UIHelper.card("Bill Details");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        grid.setColumnConstraints(buildTwoColConstraints());

        unitsField = new TextField();
        unitsField.setPromptText("e.g. 250");
        tariffCombo = new ComboBox<>(FXCollections.observableArrayList(TARIFF_LABELS));
        tariffCombo.getSelectionModel().select(1);
        tariffCombo.setPrefWidth(Double.MAX_VALUE);
        UIHelper.styleControl(tariffCombo);

        customRateField = new TextField();
        customRateField.setPromptText("Enter custom rate");
        customRateField.setDisable(true);

        tariffCombo.setOnAction(e -> {
            boolean custom = tariffCombo.getSelectionModel().getSelectedIndex() == 5;
            customRateField.setDisable(!custom);
        });

        fixedChargeField = new TextField("100");
        monthCombo = new ComboBox<>(FXCollections.observableArrayList(MONTHS));
        monthCombo.getSelectionModel().select(11);
        monthCombo.setPrefWidth(Double.MAX_VALUE);
        UIHelper.styleControl(monthCombo);

        grid.add(UIHelper.formField("Monthly Consumption (kWh)", unitsField), 0, 0);
        grid.add(UIHelper.formField("Tariff Rate", tariffCombo), 1, 0);
        grid.add(UIHelper.formField("Custom Rate (₹/unit)", customRateField), 0, 1);
        grid.add(UIHelper.formField("Fixed Charges (₹/month)", fixedChargeField), 1, 1);
        grid.add(UIHelper.formField("Billing Month", monthCombo), 0, 2);

        Button calcBtn = UIHelper.primaryButton("Calculate Bill");
        Button clearBtn = UIHelper.secondaryButton("Clear History");
        calcBtn.setOnAction(e -> calculateBill());
        clearBtn.setOnAction(e -> clearHistory());

        HBox btnRow = new HBox(10, calcBtn, clearBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        inputCard.getChildren().addAll(grid, btnRow);

        // ── Result card ───────────────────────────────────────────────────────
        resultSection = new VBox(16);
        resultSection.setVisible(false);
        resultSection.setManaged(false);

        VBox resultCard = UIHelper.card("Bill Summary");
        metricsPane = new FlowPane(12, 12);
        alertBox = new VBox();

        // Bar chart
        Label chartLabel = new Label("Usage by Tariff Bracket (units)");
        chartLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        chartLabel.setTextFill(Color.web(UIHelper.GRAY_600));
        barCanvas = new Canvas(560, 130);

        resultCard.getChildren().addAll(metricsPane, alertBox, new Separator(), chartLabel, barCanvas);
        resultSection.getChildren().add(resultCard);

        // ── History card ──────────────────────────────────────────────────────
        VBox histCard = UIHelper.card("Usage History");
        historyTable = buildHistoryTable();
        histCard.getChildren().add(historyTable);

        root.getChildren().addAll(inputCard, resultSection, histCard);
    }

    private void calculateBill() {
        // Validate
        if (unitsField.getText().trim().isEmpty()) {
            UIHelper.markError(unitsField);
            UIHelper.showAlert("Validation Error", "Please enter monthly consumption in units.");
            return;
        }

        double units;
        try {
            units = Double.parseDouble(unitsField.getText().trim());
            if (units <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            UIHelper.markError(unitsField);
            UIHelper.showAlert("Validation Error", "Please enter a valid positive number for units.");
            return;
        }

        int idx = tariffCombo.getSelectionModel().getSelectedIndex();
        double rate;
        if (idx == 5) {
            try {
                rate = Double.parseDouble(customRateField.getText().trim());
                if (rate <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                UIHelper.showAlert("Validation Error", "Please enter a valid custom tariff rate.");
                return;
            }
        } else {
            rate = TARIFF_VALUES[idx];
        }

        double fixed = UIHelper.getDouble(fixedChargeField, 100);
        String month = monthCombo.getSelectionModel().getSelectedItem();

        double energyCharge = units * rate;
        double subsidyPct = units <= 100 ? 10 : units <= 200 ? 5 : 0;
        double subsidy = (energyCharge * subsidyPct) / 100.0;
        double tax = energyCharge * 0.05;
        double total = energyCharge - subsidy + tax + fixed;
        double avgDaily = units / 30.0;
        double co2 = units * 0.82;

        // Metrics
        metricsPane.getChildren().clear();
        metricsPane.getChildren().addAll(
            UIHelper.metricCard("Energy Charge", UIHelper.formatCurrency(energyCharge), "before adjustments", UIHelper.GRAY_900),
            UIHelper.metricCard("Subsidy", "– " + UIHelper.formatCurrency(subsidy), subsidyPct + "% applied", UIHelper.GREEN_PRIMARY),
            UIHelper.metricCard("Taxes (5%)", UIHelper.formatCurrency(tax), "on energy charge", UIHelper.AMBER_PRIMARY),
            UIHelper.metricCard("Fixed Charges", UIHelper.formatCurrency(fixed), "per month", UIHelper.GRAY_400),
            UIHelper.metricCard("TOTAL BILL", UIHelper.formatCurrency(total), month, UIHelper.BLUE_PRIMARY),
            UIHelper.metricCard("Daily Usage", UIHelper.formatDouble(avgDaily, 1), "kWh/day", UIHelper.GRAY_900),
            UIHelper.metricCard("Carbon Footprint", UIHelper.formatDouble(co2, 1), "kg CO₂/month", UIHelper.AMBER_PRIMARY)
        );

        // Alert
        alertBox.getChildren().clear();
        HBox banner;
        if (total > 3000) {
            banner = UIHelper.alertBanner("danger",
                "High bill of " + UIHelper.formatCurrency(total) + "! Consider energy-efficient appliances or solar panels to reduce costs.");
        } else if (total > 1500) {
            banner = UIHelper.alertBanner("warning",
                "Moderate bill of " + UIHelper.formatCurrency(total) + ". Review high-consumption appliances to optimize usage.");
        } else {
            banner = UIHelper.alertBanner("success",
                "Great! Your bill of " + UIHelper.formatCurrency(total) + " is within a reasonable range. Keep conserving energy.");
        }
        alertBox.getChildren().add(banner);

        // Draw bar chart
        drawBarChart(units);

        resultSection.setVisible(true);
        resultSection.setManaged(true);

        // Save to history
        BillRecord record = new BillRecord(month, units, rate, energyCharge, fixed, tax, subsidy, total);
        historyData.add(0, record);
        if (historyData.size() > 24) historyData.remove(historyData.size() - 1);
    }

    private void drawBarChart(double units) {
        GraphicsContext gc = barCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, barCanvas.getWidth(), barCanvas.getHeight());

        double[] brackets = {
            Math.min(units, 100),
            Math.max(0, Math.min(units - 100, 200)),
            Math.max(0, Math.min(units - 300, 200)),
            Math.max(0, units - 500)
        };
        String[] labels = {"0–100", "101–300", "301–500", "500+"};
        String[] colors = {"#2563EB", "#7C3AED", "#D97706", "#DC2626"};

        double maxVal = 0;
        for (double b : brackets) maxVal = Math.max(maxVal, b);
        if (maxVal == 0) return;

        double chartH = 90;
        double barW = 80;
        double gap = 20;
        double startX = 30;

        for (int i = 0; i < 4; i++) {
            double h = brackets[i] > 0 ? (brackets[i] / maxVal) * chartH : 4;
            double x = startX + i * (barW + gap);
            double y = chartH - h + 10;

            gc.setFill(Color.web(colors[i], brackets[i] > 0 ? 0.85 : 0.2));
            gc.fillRoundRect(x, y, barW, h, 6, 6);

            gc.setFill(Color.web(UIHelper.GRAY_600));
            gc.setFont(Font.font("System", 11));
            gc.fillText(labels[i], x + barW / 2 - 15, chartH + 22);

            gc.setFill(Color.web(colors[i]));
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.fillText(String.format("%.0f", brackets[i]), x + barW / 2 - 10, y - 4);
        }
    }

    private TableView<BillRecord> buildHistoryTable() {
        TableView<BillRecord> table = new TableView<>(historyData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(220);
        table.setPlaceholder(new Label("No history yet. Calculate a bill to add records."));
        table.setStyle("-fx-background-color: " + UIHelper.WHITE + ";");

        addCol(table, "Month", "month", 80);
        addCol(table, "Units (kWh)", "units", 80);
        addCol(table, "Rate (₹)", "rate", 70);
        addCol(table, "Energy Charge", "energyCharge", 110);
        addCol(table, "Fixed (₹)", "fixedCharge", 80);
        addCol(table, "Total Bill", "totalBill", 100);
        addCol(table, "Date", "formattedDate", 100);
        return table;
    }

    private <T> void addCol(TableView<T> table, String title, String prop, double width) {
        TableColumn<T, ?> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setMinWidth(width);
        table.getColumns().add(col);
    }

    private void clearHistory() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Clear all bill history?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) historyData.clear();
        });
    }

    private List<ColumnConstraints> buildTwoColConstraints() {
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        return List.of(c1, c2);
    }

    public VBox getView() { return root; }
}
