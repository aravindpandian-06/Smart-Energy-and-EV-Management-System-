package com.smartenergy.controller;

import com.smartenergy.model.Appliance;
import com.smartenergy.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class ApplianceController {

    private VBox root;
    private ComboBox<String> nameCombo;
    private TextField customNameField;
    private TextField wattsField;
    private TextField hoursField;
    private TextField ageField;
    private TextField rateField;

    private VBox resultSection;
    private FlowPane metricsPane;
    private VBox alertBox;
    private VBox recsBox;
    private ProgressBar effBar;
    private Label effLabel;

    private VBox applianceListBox;
    private Label countLabel;
    private List<Appliance> appliances = new ArrayList<>();

    private static final String[] APPLIANCE_NAMES = {
        "Refrigerator", "Air Conditioner", "Washing Machine",
        "Water Heater", "Television", "Ceiling Fan",
        "Microwave Oven", "Dishwasher", "Air Cooler", "Other..."
    };

    public ApplianceController() {
        root = new VBox(20);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");
        buildUI();
    }

    private void buildUI() {
        root.getChildren().add(UIHelper.sectionHeader(
            "Appliance Efficiency Monitor",
            "Evaluate aging appliances and get replacement recommendations"
        ));

        // ── Input card ────────────────────────────────────────────────────────
        VBox inputCard = UIHelper.card("Appliance Details");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(33.33);
        grid.getColumnConstraints().addAll(c, c, c);

        nameCombo = new ComboBox<>(FXCollections.observableArrayList(APPLIANCE_NAMES));
        nameCombo.getSelectionModel().select(0);
        nameCombo.setPrefWidth(Double.MAX_VALUE);
        UIHelper.styleControl(nameCombo);

        customNameField = new TextField();
        customNameField.setPromptText("e.g. Air Cooler");
        customNameField.setDisable(true);
        nameCombo.setOnAction(e -> customNameField.setDisable(!nameCombo.getValue().equals("Other...")));

        wattsField = new TextField();
        wattsField.setPromptText("e.g. 1500");
        hoursField = new TextField();
        hoursField.setPromptText("e.g. 8");
        ageField = new TextField();
        ageField.setPromptText("e.g. 5");
        rateField = new TextField("5.0");
        rateField.setPromptText("e.g. 5.0");

        grid.add(UIHelper.formField("Appliance Name", nameCombo), 0, 0);
        grid.add(UIHelper.formField("Custom Name (if Other)", customNameField), 1, 0);
        grid.add(UIHelper.formField("Rated Wattage (W)", wattsField), 2, 0);
        grid.add(UIHelper.formField("Daily Usage (hours)", hoursField), 0, 1);
        grid.add(UIHelper.formField("Appliance Age (years)", ageField), 1, 1);
        grid.add(UIHelper.formField("Electricity Rate (₹/unit)", rateField), 2, 1);

        Button analyzeBtn = UIHelper.primaryButton("Analyze Efficiency");
        Button clearBtn = UIHelper.secondaryButton("Clear All");
        analyzeBtn.setOnAction(e -> analyzeAppliance());
        clearBtn.setOnAction(e -> clearAll());

        HBox btnRow = new HBox(10, analyzeBtn, clearBtn);
        inputCard.getChildren().addAll(grid, btnRow);

        // ── Result card ───────────────────────────────────────────────────────
        resultSection = new VBox(14);
        resultSection.setVisible(false);
        resultSection.setManaged(false);

        VBox resultCard = UIHelper.card("Efficiency Analysis");
        metricsPane = new FlowPane(12, 12);

        // Efficiency progress
        Label effTitle = new Label("Efficiency Remaining");
        effTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        effTitle.setTextFill(Color.web(UIHelper.GRAY_600));
        effLabel = new Label("0%");
        effLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        HBox effHeader = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        effHeader.getChildren().addAll(effTitle, spacer, effLabel);
        effBar = new ProgressBar(0);
        effBar.setPrefWidth(Double.MAX_VALUE);
        effBar.setPrefHeight(12);
        VBox effSection = new VBox(6, effHeader, effBar);

        alertBox = new VBox();
        recsBox = new VBox(8);

        Label recsTitle = new Label("Recommendations");
        recsTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        recsTitle.setTextFill(Color.web(UIHelper.GRAY_600));

        resultCard.getChildren().addAll(metricsPane, effSection, alertBox, recsTitle, recsBox);
        resultSection.getChildren().add(resultCard);

        // ── Appliance list card ───────────────────────────────────────────────
        VBox listCard = UIHelper.card("Analyzed Appliances");
        countLabel = new Label("0 appliances");
        countLabel.setFont(Font.font("System", 12));
        countLabel.setTextFill(Color.web(UIHelper.GRAY_400));
        applianceListBox = new VBox(8);
        Label emptyLbl = new Label("No appliances analyzed yet. Add one above.");
        emptyLbl.setTextFill(Color.web(UIHelper.GRAY_400));
        emptyLbl.setFont(Font.font("System", 13));
        applianceListBox.getChildren().add(emptyLbl);
        listCard.getChildren().addAll(countLabel, applianceListBox);

        root.getChildren().addAll(inputCard, resultSection, listCard);
    }

    private void analyzeAppliance() {
        // Validation
        if (!UIHelper.isValidDouble(wattsField)) {
            UIHelper.markError(wattsField);
            UIHelper.showAlert("Validation Error", "Please enter a valid wattage value.");
            return;
        }
        if (!UIHelper.isValidDouble(hoursField)) {
            UIHelper.markError(hoursField);
            UIHelper.showAlert("Validation Error", "Please enter valid daily usage hours.");
            return;
        }
        if (!UIHelper.isValidPositiveInt(ageField)) {
            UIHelper.markError(ageField);
            UIHelper.showAlert("Validation Error", "Please enter a valid appliance age.");
            return;
        }

        String name = nameCombo.getValue().equals("Other...")
            ? (customNameField.getText().trim().isEmpty() ? "Unknown" : customNameField.getText().trim())
            : nameCombo.getValue();

        double watts = UIHelper.getDouble(wattsField, 0);
        double hours = UIHelper.getDouble(hoursField, 0);
        int age = UIHelper.getInt(ageField, 0);
        double rate = UIHelper.getDouble(rateField, 5.0);

        Appliance app = new Appliance(name, watts, hours, age, rate);

        // Metrics
        metricsPane.getChildren().clear();
        String effColor = app.getEfficiencyPercent() < 70 ? UIHelper.RED_PRIMARY
            : app.getEfficiencyPercent() < 85 ? UIHelper.AMBER_PRIMARY
            : UIHelper.GREEN_PRIMARY;

        metricsPane.getChildren().addAll(
            UIHelper.metricCard("Rated Wattage", UIHelper.formatDouble(watts, 0) + " W", "original", UIHelper.GRAY_900),
            UIHelper.metricCard("Actual Draw", UIHelper.formatDouble(app.getActualWattage(), 0) + " W", "after aging", UIHelper.AMBER_PRIMARY),
            UIHelper.metricCard("Base Monthly", UIHelper.formatDouble(app.getBaseMonthlyKWh(), 2), "kWh", UIHelper.GRAY_900),
            UIHelper.metricCard("Actual Monthly", UIHelper.formatDouble(app.getActualMonthlyKWh(), 2), "kWh", UIHelper.AMBER_PRIMARY),
            UIHelper.metricCard("Efficiency", UIHelper.formatPercent(app.getEfficiencyPercent()), "", effColor),
            UIHelper.metricCard("Extra Cost", UIHelper.formatCurrency(app.getExtraMonthlyCost()), "/month", UIHelper.RED_PRIMARY),
            UIHelper.metricCard("Annual Waste", UIHelper.formatCurrency(app.getAnnualWasteCost()), "/year", UIHelper.RED_PRIMARY),
            UIHelper.metricCard("Savings if Replaced", UIHelper.formatCurrency(app.getAnnualSavingsIfReplaced()), "/year", UIHelper.GREEN_PRIMARY)
        );

        // Efficiency bar
        effBar.setProgress(app.getEfficiencyPercent() / 100.0);
        effBar.setStyle("-fx-accent: " + effColor + ";");
        effLabel.setText(UIHelper.formatPercent(app.getEfficiencyPercent()));
        effLabel.setTextFill(Color.web(effColor));

        // Alert
        alertBox.getChildren().clear();
        HBox banner;
        if (app.getEfficiencyPercent() < 70) {
            banner = UIHelper.alertBanner("danger",
                name + " has lost " + UIHelper.formatDouble(app.getAgingLossPercent(), 0) +
                "% efficiency over " + age + " years. Immediate replacement is strongly recommended.");
        } else if (app.getEfficiencyPercent() < 85) {
            banner = UIHelper.alertBanner("warning",
                name + " efficiency has dropped to " + UIHelper.formatPercent(app.getEfficiencyPercent()) +
                ". Consider replacement soon to reduce ₹" + UIHelper.formatDouble(app.getAnnualWasteCost(), 0) + "/year wasted.");
        } else {
            banner = UIHelper.alertBanner("success",
                name + " is performing well at " + UIHelper.formatPercent(app.getEfficiencyPercent()) + " efficiency.");
        }
        alertBox.getChildren().add(banner);

        // Recommendations
        recsBox.getChildren().clear();
        for (String rec : buildRecs(app)) {
            recsBox.getChildren().add(recItem(rec, app.getEfficiencyPercent()));
        }

        resultSection.setVisible(true);
        resultSection.setManaged(true);

        // Add to list
        appliances.removeIf(a -> a.getName().equals(app.getName()));
        appliances.add(0, app);
        refreshList();
    }

    private List<String> buildRecs(Appliance app) {
        List<String> recs = new ArrayList<>();
        if (app.getEfficiencyPercent() < 70) {
            recs.add("Replace immediately — annual energy waste: " + UIHelper.formatCurrency(app.getAnnualWasteCost()));
            recs.add("A BEE 5-star rated model could save " + UIHelper.formatCurrency(app.getAnnualSavingsIfReplaced()) + " per year.");
            recs.add("Check government subsidies for energy-efficient appliance upgrades.");
        } else if (app.getEfficiencyPercent() < 85) {
            recs.add("Plan replacement within 1–2 years for optimal energy savings.");
            recs.add("Schedule professional servicing to recover some efficiency losses.");
        } else {
            recs.add("Appliance is in good condition. Regular cleaning and servicing recommended.");
        }
        return recs;
    }

    private HBox recItem(String text, double eff) {
        String color = eff < 70 ? UIHelper.RED_PRIMARY : eff < 85 ? UIHelper.AMBER_PRIMARY : UIHelper.GREEN_PRIMARY;
        String bg = eff < 70 ? "#FEF2F2" : eff < 85 ? "#FFFBEB" : "#F0FDF4";
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

    private void refreshList() {
        applianceListBox.getChildren().clear();
        countLabel.setText(appliances.size() + " appliance" + (appliances.size() != 1 ? "s" : ""));
        for (int i = 0; i < appliances.size(); i++) {
            Appliance a = appliances.get(i);
            applianceListBox.getChildren().add(buildApplianceRow(a, i));
        }
    }

    private HBox buildApplianceRow(Appliance a, int idx) {
        String effColor = a.getEfficiencyPercent() < 70 ? UIHelper.RED_PRIMARY
            : a.getEfficiencyPercent() < 85 ? UIHelper.AMBER_PRIMARY
            : UIHelper.GREEN_PRIMARY;

        Label name = new Label(a.getName());
        name.setFont(Font.font("System", FontWeight.BOLD, 14));
        name.setTextFill(Color.web(UIHelper.GRAY_900));

        Label details = new Label(
            (int)a.getWattage() + "W · " + a.getDailyHours() + "h/day · " +
            a.getAgeYears() + " yrs · " + UIHelper.formatDouble(a.getActualMonthlyKWh(), 1) + " kWh/mo"
        );
        details.setFont(Font.font("System", 12));
        details.setTextFill(Color.web(UIHelper.GRAY_400));

        VBox left = new VBox(3, name, details);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label effVal = new Label(UIHelper.formatPercent(a.getEfficiencyPercent()));
        effVal.setFont(Font.font("System", FontWeight.BOLD, 18));
        effVal.setTextFill(Color.web(effColor));

        Label effLbl = new Label("efficiency");
        effLbl.setFont(Font.font("System", 11));
        effLbl.setTextFill(Color.web(UIHelper.GRAY_400));

        VBox right = new VBox(2, effVal, effLbl);
        right.setAlignment(Pos.CENTER);

        Button del = new Button("✕");
        del.setStyle("-fx-background-color: transparent; -fx-border-color: " + UIHelper.GRAY_200 +
                     "; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 12;");
        del.setTextFill(Color.web(UIHelper.GRAY_400));
        del.setOnAction(e -> { appliances.remove(idx); refreshList(); });

        HBox row = new HBox(14, left, right, del);
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-background-color: " + UIHelper.WHITE + ";" +
                     "-fx-border-color: " + UIHelper.GRAY_200 + ";" +
                     "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        return row;
    }

    private void clearAll() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Clear all appliance records?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) { appliances.clear(); refreshList(); }
        });
    }

    public VBox getView() { return root; }
}
