package com.smartenergy.controller;

import com.smartenergy.model.UPSLoad;
import com.smartenergy.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class UPSController {

    private VBox root;
    private TextField capsField;
    private TextField ahField;
    private ComboBox<String> voltCombo;
    private TextField pfField;
    private TextField customNameField;
    private TextField customWattsField;

    private VBox resultSection;
    private FlowPane metricsPane;
    private ProgressBar loadBar;
    private Label loadBarLabel;
    private VBox alertBox;
    private VBox recsBox;

    private FlowPane checkboxPane;
    private List<UPSLoad.UPSAppliance> allAppliances = new ArrayList<>();

    private static final String[][] DEFAULT_APPLIANCES = {
        {"Desktop PC", "300"}, {"Monitor", "80"}, {"Laptop", "65"},
        {"Ceiling Fan", "75"}, {"LED Tube Light", "20"}, {"Router/Modem", "30"},
        {"Refrigerator", "200"}, {"Television", "150"}, {"Air Cooler", "200"}, {"Printer", "400"}
    };

    public UPSController() {
        root = new VBox(20);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");

        for (String[] pair : DEFAULT_APPLIANCES) {
            allAppliances.add(new UPSLoad.UPSAppliance(pair[0], Double.parseDouble(pair[1])));
        }
        buildUI();
    }

    private void buildUI() {
        root.getChildren().add(UIHelper.sectionHeader(
            "UPS Load Stability Analyzer",
            "Prevent overload damage and predict backup duration for your UPS"
        ));

        // ── UPS Config card ───────────────────────────────────────────────────
        VBox configCard = UIHelper.card("UPS Configuration");
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(25);
        grid.getColumnConstraints().addAll(c, c, c, c);

        capsField = new TextField("1500");
        capsField.setPromptText("e.g. 1500");
        ahField = new TextField("12");
        ahField.setPromptText("e.g. 12");

        voltCombo = new ComboBox<>();
        voltCombo.getItems().addAll("12V", "24V", "36V", "48V");
        voltCombo.getSelectionModel().select(0);
        voltCombo.setPrefWidth(Double.MAX_VALUE);
        UIHelper.styleControl(voltCombo);

        pfField = new TextField("0.8");
        pfField.setPromptText("0.8");

        grid.add(UIHelper.formField("UPS Capacity (VA)", capsField), 0, 0);
        grid.add(UIHelper.formField("Battery Capacity (Ah)", ahField), 1, 0);
        grid.add(UIHelper.formField("Battery Voltage", voltCombo), 2, 0);
        grid.add(UIHelper.formField("Power Factor", pfField), 3, 0);
        configCard.getChildren().add(grid);

        // ── Appliance selection card ──────────────────────────────────────────
        VBox appCard = UIHelper.card("Connected Appliances — Select Active Loads");
        checkboxPane = new FlowPane(10, 10);
        refreshCheckboxes();

        // Custom load row
        Label customTitle = new Label("Add Custom Load");
        customTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        customTitle.setTextFill(Color.web(UIHelper.GRAY_600));
        Separator sep = new Separator();

        customNameField = new TextField();
        customNameField.setPromptText("e.g. Router");
        customWattsField = new TextField();
        customWattsField.setPromptText("e.g. 30");

        Button addCustomBtn = UIHelper.secondaryButton("+ Add");
        addCustomBtn.setOnAction(e -> addCustomLoad());

        GridPane customGrid = new GridPane();
        customGrid.setHgap(12);
        ColumnConstraints cc1 = new ColumnConstraints(); cc1.setPercentWidth(40);
        ColumnConstraints cc2 = new ColumnConstraints(); cc2.setPercentWidth(30);
        ColumnConstraints cc3 = new ColumnConstraints(); cc3.setPercentWidth(30);
        customGrid.getColumnConstraints().addAll(cc1, cc2, cc3);
        customGrid.add(UIHelper.formField("Appliance Name", customNameField), 0, 0);
        customGrid.add(UIHelper.formField("Wattage (W)", customWattsField), 1, 0);
        customGrid.add(new VBox(20, new Label(""), addCustomBtn), 2, 0);

        Button analyzeBtn = UIHelper.primaryButton("Analyze UPS Load");
        analyzeBtn.setOnAction(e -> analyzeUPS());

        appCard.getChildren().addAll(checkboxPane, sep, customTitle, customGrid, analyzeBtn);

        // ── Result card ───────────────────────────────────────────────────────
        resultSection = new VBox(14);
        resultSection.setVisible(false);
        resultSection.setManaged(false);

        VBox resultCard = UIHelper.card("UPS Load Analysis");
        metricsPane = new FlowPane(12, 12);

        // Load progress
        Label loadTitle = new Label("Load Percentage");
        loadTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        loadTitle.setTextFill(Color.web(UIHelper.GRAY_600));
        loadBarLabel = new Label("0%");
        loadBarLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox loadHeader = new HBox();
        loadHeader.getChildren().addAll(loadTitle, sp, loadBarLabel);
        loadBar = new ProgressBar(0);
        loadBar.setPrefWidth(Double.MAX_VALUE);
        loadBar.setPrefHeight(12);
        VBox loadSection = new VBox(6, loadHeader, loadBar);

        alertBox = new VBox();
        recsBox = new VBox(8);
        Label recsTitle = new Label("Recommendations");
        recsTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        recsTitle.setTextFill(Color.web(UIHelper.GRAY_600));

        resultCard.getChildren().addAll(metricsPane, loadSection, alertBox, recsTitle, recsBox);
        resultSection.getChildren().add(resultCard);

        root.getChildren().addAll(configCard, appCard, resultSection);
    }

    private void refreshCheckboxes() {
        checkboxPane.getChildren().clear();
        for (UPSLoad.UPSAppliance app : allAppliances) {
            CheckBox cb = new CheckBox(app.getName() + " (" + (int)app.getWatts() + "W)");
            cb.setFont(Font.font("System", 13));
            cb.setTextFill(Color.web(UIHelper.GRAY_900));
            cb.setSelected(app.isSelected());
            cb.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: " + UIHelper.WHITE +
                        "; -fx-border-color: " + UIHelper.GRAY_200 + "; -fx-border-radius: 6; -fx-background-radius: 6;");
            cb.selectedProperty().addListener((obs, o, n) -> {
                app.setSelected(n);
                cb.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: " +
                    (n ? UIHelper.BLUE_LIGHT : UIHelper.WHITE) +
                    "; -fx-border-color: " + (n ? UIHelper.BLUE_PRIMARY : UIHelper.GRAY_200) +
                    "; -fx-border-radius: 6; -fx-background-radius: 6;");
            });
            checkboxPane.getChildren().add(cb);
        }
    }

    private void addCustomLoad() {
        String name = customNameField.getText().trim();
        String wattsStr = customWattsField.getText().trim();
        if (name.isEmpty() || wattsStr.isEmpty()) {
            UIHelper.showAlert("Validation Error", "Please enter both a name and wattage for the custom load.");
            return;
        }
        try {
            double watts = Double.parseDouble(wattsStr);
            if (watts <= 0) throw new NumberFormatException();
            allAppliances.add(new UPSLoad.UPSAppliance(name, watts));
            customNameField.clear();
            customWattsField.clear();
            refreshCheckboxes();
        } catch (NumberFormatException ex) {
            UIHelper.showAlert("Validation Error", "Please enter a valid wattage value.");
        }
    }

    private void analyzeUPS() {
        double caps, ah, pf;
        double volt = Double.parseDouble(voltCombo.getValue().replace("V", ""));

        try {
            caps = Double.parseDouble(capsField.getText().trim());
            ah = Double.parseDouble(ahField.getText().trim());
            pf = Double.parseDouble(pfField.getText().trim());
            if (caps <= 0 || ah <= 0 || pf <= 0 || pf > 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            UIHelper.showAlert("Validation Error", "Please enter valid UPS configuration values.");
            return;
        }

        List<UPSLoad.UPSAppliance> selected = new ArrayList<>();
        for (UPSLoad.UPSAppliance app : allAppliances) {
            if (app.isSelected()) selected.add(app);
        }

        if (selected.isEmpty()) {
            UIHelper.showAlert("No Selection", "Please select at least one connected appliance.");
            return;
        }

        UPSLoad ups = new UPSLoad(caps, ah, volt, pf);
        for (UPSLoad.UPSAppliance app : selected) ups.addAppliance(app);
        ups.calculate();

        String barColor = ups.getLoadPercent() > 90 ? UIHelper.RED_PRIMARY
            : ups.getLoadPercent() > 70 ? UIHelper.AMBER_PRIMARY
            : UIHelper.GREEN_PRIMARY;

        // Metrics
        metricsPane.getChildren().clear();
        double backupMins = ups.getBackupHours() * 60;
        String backupStr = backupMins >= 60
            ? UIHelper.formatDouble(ups.getBackupHours(), 2) + " hr"
            : UIHelper.formatDouble(backupMins, 0) + " min";

        metricsPane.getChildren().addAll(
            UIHelper.metricCard("Total Load", UIHelper.formatDouble(ups.getTotalWatts(), 0) + " W", selected.size() + " appliances", UIHelper.GRAY_900),
            UIHelper.metricCard("UPS Capacity", UIHelper.formatDouble(ups.getMaxWatts(), 0) + " W", "rated power", UIHelper.GRAY_900),
            UIHelper.metricCard("Load %", UIHelper.formatPercent(ups.getLoadPercent()), "of UPS", barColor),
            UIHelper.metricCard("Battery Energy", UIHelper.formatDouble(ups.getBatteryWh(), 0) + " Wh", "total", UIHelper.BLUE_PRIMARY),
            UIHelper.metricCard("Backup Time", backupStr, "estimated", "#7C3AED"),
            UIHelper.metricCard("Status", ups.getStatus(), "", barColor)
        );

        // Progress bar
        loadBar.setProgress(Math.min(ups.getLoadPercent() / 100.0, 1.0));
        loadBar.setStyle("-fx-accent: " + barColor + ";");
        loadBarLabel.setText(UIHelper.formatPercent(ups.getLoadPercent()));
        loadBarLabel.setTextFill(Color.web(barColor));

        // Alert
        alertBox.getChildren().clear();
        String alertType = ups.getLoadPercent() > 90 ? "danger"
            : ups.getLoadPercent() > 70 ? "warning" : "success";
        alertBox.getChildren().add(UIHelper.alertBanner(alertType, ups.getStatusMessage()));

        // Recommendations
        recsBox.getChildren().clear();
        List<String> recs = buildRecs(ups);
        for (String rec : recs) {
            recsBox.getChildren().add(recItem(rec, ups.getLoadPercent()));
        }

        resultSection.setVisible(true);
        resultSection.setManaged(true);
    }

    private List<String> buildRecs(UPSLoad ups) {
        List<String> recs = new ArrayList<>();
        if (ups.getLoadPercent() > 100) {
            recs.add("CRITICAL: Remove at least " +
                UIHelper.formatDouble(ups.getTotalWatts() - ups.getMaxWatts() * 0.85, 0) +
                "W of load immediately to prevent UPS shutdown.");
            recs.add("Consider upgrading to a higher-capacity UPS.");
        } else if (ups.getLoadPercent() > 90) {
            recs.add("Disconnect non-essential appliances to bring load below 80%.");
            recs.add("High load severely reduces battery backup duration.");
        } else if (ups.getLoadPercent() > 70) {
            double excess = ups.getTotalWatts() - ups.getMaxWatts() * 0.65;
            recs.add("Ideal UPS load is 50–70%. Consider removing ~" +
                UIHelper.formatDouble(excess, 0) + "W for better backup duration.");
        } else {
            recs.add("UPS load is within safe range. Battery health check every 6 months recommended.");
            recs.add("Keep batteries charged and tested regularly for reliable backup.");
        }

        // List connected loads
        StringBuilder sb = new StringBuilder("Active loads: ");
        List<UPSLoad.UPSAppliance> connected = ups.getConnectedAppliances();
        for (int i = 0; i < connected.size(); i++) {
            sb.append(connected.get(i).getName())
              .append(" (").append((int)connected.get(i).getWatts()).append("W)");
            if (i < connected.size() - 1) sb.append(", ");
        }
        recs.add(sb.toString());
        return recs;
    }

    private HBox recItem(String text, double loadPct) {
        String color = loadPct > 90 ? UIHelper.RED_PRIMARY : loadPct > 70 ? UIHelper.AMBER_PRIMARY : UIHelper.GREEN_PRIMARY;
        String bg = loadPct > 90 ? "#FEF2F2" : loadPct > 70 ? "#FFFBEB" : "#F0FDF4";
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

    public VBox getView() { return root; }
}
