package com.smartenergy.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIHelper {

    // ── Color constants ──────────────────────────────────────────────────────
    public static final String BLUE_PRIMARY   = "#2563EB";
    public static final String BLUE_LIGHT     = "#EFF6FF";
    public static final String BLUE_BORDER    = "#BFDBFE";
    public static final String GREEN_PRIMARY  = "#16A34A";
    public static final String GREEN_LIGHT    = "#F0FDF4";
    public static final String AMBER_PRIMARY  = "#D97706";
    public static final String AMBER_LIGHT    = "#FFFBEB";
    public static final String RED_PRIMARY    = "#DC2626";
    public static final String RED_LIGHT      = "#FEF2F2";
    public static final String GRAY_50        = "#F9FAFB";
    public static final String GRAY_100       = "#F3F4F6";
    public static final String GRAY_200       = "#E5E7EB";
    public static final String GRAY_400       = "#9CA3AF";
    public static final String GRAY_600       = "#4B5563";
    public static final String GRAY_900       = "#111827";
    public static final String WHITE          = "#FFFFFF";

    // ── Formatting helpers ───────────────────────────────────────────────────
    public static String formatCurrency(double value) {
        return String.format("\u20B9%.2f", value);
    }

    public static String formatDouble(double value, int decimals) {
        return String.format("%." + decimals + "f", value);
    }

    public static String formatPercent(double value) {
        return String.format("%.1f%%", value);
    }

    // ── Section header ───────────────────────────────────────────────────────
    public static VBox sectionHeader(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web(GRAY_900));

        Label subLabel = new Label(subtitle);
        subLabel.setFont(Font.font("System", 13));
        subLabel.setTextFill(Color.web(GRAY_400));

        VBox box = new VBox(4, titleLabel, subLabel);
        box.setPadding(new Insets(0, 0, 16, 0));
        return box;
    }

    // ── White card container ─────────────────────────────────────────────────
    public static VBox card(String title) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );

        if (title != null && !title.isEmpty()) {
            Label lbl = new Label(title);
            lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
            lbl.setTextFill(Color.web(GRAY_600));
            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: " + GRAY_200 + ";");
            card.getChildren().addAll(lbl, sep);
        }
        return card;
    }

    // ── Metric card (result display) ─────────────────────────────────────────
    public static VBox metricCard(String label, String value, String unit, String valueColor) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(14));
        box.setAlignment(Pos.CENTER);
        box.setStyle(
            "-fx-background-color: " + GRAY_50 + ";" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        box.setPrefWidth(160);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", 11));
        lbl.setTextFill(Color.web(GRAY_400));
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);

        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 20));
        val.setTextFill(Color.web(valueColor != null ? valueColor : GRAY_900));

        box.getChildren().addAll(lbl, val);

        if (unit != null && !unit.isEmpty()) {
            Label unitLbl = new Label(unit);
            unitLbl.setFont(Font.font("System", 11));
            unitLbl.setTextFill(Color.web(GRAY_400));
            box.getChildren().add(unitLbl);
        }
        return box;
    }

    // ── Labeled form field ───────────────────────────────────────────────────
    public static VBox formField(String label, Control control) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(GRAY_600));

        styleControl(control);

        VBox box = new VBox(5, lbl, control);
        return box;
    }

    public static void styleControl(Control control) {
        control.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + GRAY_200 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 13;" +
            "-fx-padding: 7 10 7 10;"
        );

        control.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                control.setStyle(
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-border-color: " + BLUE_PRIMARY + ";" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-font-size: 13;" +
                    "-fx-padding: 7 10 7 10;"
                );
            } else {
                control.setStyle(
                    "-fx-background-color: " + WHITE + ";" +
                    "-fx-border-color: " + GRAY_200 + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-font-size: 13;" +
                    "-fx-padding: 7 10 7 10;"
                );
            }
        });
    }

    // ── Primary button ───────────────────────────────────────────────────────
    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setTextFill(Color.WHITE);
        String base = "-fx-background-color: " + BLUE_PRIMARY + ";" +
                      "-fx-background-radius: 7;" +
                      "-fx-padding: 9 22 9 22;" +
                      "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base + "-fx-background-color: #1D4ED8;"));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    // ── Secondary button ─────────────────────────────────────────────────────
    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", 13));
        btn.setTextFill(Color.web(GRAY_600));
        String base = "-fx-background-color: " + WHITE + ";" +
                      "-fx-border-color: " + GRAY_200 + ";" +
                      "-fx-border-width: 1;" +
                      "-fx-background-radius: 7;" +
                      "-fx-border-radius: 7;" +
                      "-fx-padding: 9 22 9 22;" +
                      "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base.replace(WHITE, GRAY_100)));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    // ── Alert banner ─────────────────────────────────────────────────────────
    public static HBox alertBanner(String type, String message) {
        String bg, border, textColor, icon;
        switch (type) {
            case "danger":
                bg = RED_LIGHT; border = "#FECACA"; textColor = "#991B1B"; icon = "⚠"; break;
            case "warning":
                bg = AMBER_LIGHT; border = "#FDE68A"; textColor = "#92400E"; icon = "⚡"; break;
            case "success":
                bg = GREEN_LIGHT; border = "#BBF7D0"; textColor = "#14532D"; icon = "✓"; break;
            default:
                bg = BLUE_LIGHT; border = BLUE_BORDER; textColor = "#1E40AF"; icon = "ℹ"; break;
        }

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        iconLbl.setTextFill(Color.web(textColor));

        Label msgLbl = new Label(message);
        msgLbl.setFont(Font.font("System", 13));
        msgLbl.setTextFill(Color.web(textColor));
        msgLbl.setWrapText(true);
        HBox.setHgrow(msgLbl, Priority.ALWAYS);

        HBox box = new HBox(10, iconLbl, msgLbl);
        box.setPadding(new Insets(12, 16, 12, 16));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        return box;
    }

    // ── Progress bar ─────────────────────────────────────────────────────────
    public static VBox progressBar(String label, double percent, String color) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(GRAY_600));

        Label pctLbl = new Label(formatPercent(percent));
        pctLbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        pctLbl.setTextFill(Color.web(color));

        HBox header = new HBox();
        header.getChildren().addAll(lbl, pctLbl);
        HBox.setHgrow(lbl, Priority.ALWAYS);

        ProgressBar bar = new ProgressBar(Math.min(percent / 100.0, 1.0));
        bar.setPrefWidth(Double.MAX_VALUE);
        bar.setPrefHeight(10);
        bar.setStyle("-fx-accent: " + color + ";");

        VBox box = new VBox(6, header, bar);
        return box;
    }

    // ── Validation ───────────────────────────────────────────────────────────
    public static boolean isValidDouble(TextField tf) {
        try {
            double v = Double.parseDouble(tf.getText().trim());
            return v > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPositiveInt(TextField tf) {
        try {
            int v = Integer.parseInt(tf.getText().trim());
            return v >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double getDouble(TextField tf, double defaultVal) {
        try {
            return Double.parseDouble(tf.getText().trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static int getInt(TextField tf, int defaultVal) {
        try {
            return Integer.parseInt(tf.getText().trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static void markError(TextField tf) {
        tf.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + RED_PRIMARY + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 13;" +
            "-fx-padding: 7 10 7 10;"
        );
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
