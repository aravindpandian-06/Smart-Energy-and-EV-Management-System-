package com.smartenergy.view;

import com.smartenergy.controller.*;
import com.smartenergy.util.UIHelper;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainWindow {

    private Stage stage;
    private BorderPane mainLayout;
    private StackPane contentArea;

    // Controllers (lazy initialized)
    private BillAnalyzerController billController;
    private ApplianceController applianceController;
    private UPSController upsController;
    private EVBatteryController evController;

    private VBox activeNavBtn = null;

    public MainWindow(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");

        mainLayout.setTop(buildHeader());
        mainLayout.setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + "; -fx-background: " + UIHelper.GRAY_50 + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainLayout.setCenter(scroll);

        // Load dashboard by default
        showDashboard();

        Scene scene = new Scene(mainLayout, 1100, 750);
        stage.setScene(scene);
        stage.setTitle("Smart Household Energy & EV Management System");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private HBox buildHeader() {
        // Logo
        Label logoIcon = new Label("⚡");
        logoIcon.setFont(Font.font("System", FontWeight.BOLD, 18));
        logoIcon.setTextFill(Color.WHITE);
        StackPane logoBox = new StackPane(logoIcon);
        logoBox.setPrefSize(36, 36);
        logoBox.setStyle("-fx-background-color: " + UIHelper.BLUE_PRIMARY + "; -fx-background-radius: 8;");

        Label appTitle = new Label("SmartEnergy MS");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        appTitle.setTextFill(Color.web(UIHelper.GRAY_900));

        Label appSub = new Label("Household Energy & EV Management");
        appSub.setFont(Font.font("System", 12));
        appSub.setTextFill(Color.web(UIHelper.GRAY_400));

        VBox titleBox = new VBox(2, appTitle, appSub);

        HBox logo = new HBox(12, logoBox, titleBox);
        logo.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label("v1.0 — Clean Light Professional");
        versionLabel.setFont(Font.font("System", 12));
        versionLabel.setTextFill(Color.web(UIHelper.GRAY_400));

        HBox header = new HBox(0, logo, spacer, versionLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(14, 28, 14, 24));
        header.setStyle(
            "-fx-background-color: " + UIHelper.WHITE + ";" +
            "-fx-border-color: " + UIHelper.GRAY_200 + ";" +
            "-fx-border-width: 0 0 1 0;"
        );
        return header;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle(
            "-fx-background-color: " + UIHelper.WHITE + ";" +
            "-fx-border-color: " + UIHelper.GRAY_200 + ";" +
            "-fx-border-width: 0 1 0 0;"
        );

        Label navTitle = new Label("MODULES");
        navTitle.setFont(Font.font("System", FontWeight.BOLD, 10));
        navTitle.setTextFill(Color.web(UIHelper.GRAY_400));
        navTitle.setPadding(new Insets(0, 0, 8, 8));

        VBox dashBtn = navButton("Dashboard", "⊞", UIHelper.BLUE_PRIMARY, true);
        VBox billBtn = navButton("Bill Analyzer", "💡", UIHelper.BLUE_PRIMARY, false);
        VBox appBtn  = navButton("Appliance Monitor", "🔌", UIHelper.GREEN_PRIMARY, false);
        VBox upsBtn  = navButton("UPS Stability", "🔋", UIHelper.AMBER_PRIMARY, false);
        VBox evBtn   = navButton("EV Battery", "🚗", "#9333EA", false);

        dashBtn.setOnMouseClicked(e -> { setActive(dashBtn); showDashboard(); });
        billBtn.setOnMouseClicked(e -> { setActive(billBtn); showBill(); });
        appBtn.setOnMouseClicked(e  -> { setActive(appBtn);  showAppliance(); });
        upsBtn.setOnMouseClicked(e  -> { setActive(upsBtn);  showUPS(); });
        evBtn.setOnMouseClicked(e   -> { setActive(evBtn);   showEV(); });

        activeNavBtn = dashBtn;
        sidebar.getChildren().addAll(navTitle, dashBtn, billBtn, appBtn, upsBtn, evBtn);

        // Footer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label footer = new Label("Smart Energy System\n© 2024 — JavaFX");
        footer.setFont(Font.font("System", 11));
        footer.setTextFill(Color.web(UIHelper.GRAY_400));
        footer.setPadding(new Insets(8, 0, 0, 8));

        sidebar.getChildren().addAll(spacer, footer);
        return sidebar;
    }

    private VBox navButton(String text, String icon, String accentColor, boolean active) {
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("System", 15));

        Label textLbl = new Label(text);
        textLbl.setFont(Font.font("System", active ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        textLbl.setTextFill(Color.web(active ? accentColor : UIHelper.GRAY_600));

        HBox inner = new HBox(10, iconLbl, textLbl);
        inner.setAlignment(Pos.CENTER_LEFT);

        VBox btn = new VBox(inner);
        btn.setPadding(new Insets(10, 14, 10, 14));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setUserData(new String[]{accentColor, text});

        if (active) {
            btn.setStyle(
                "-fx-background-color: " + UIHelper.BLUE_LIGHT + ";" +
                "-fx-border-color: " + accentColor + ";" +
                "-fx-border-width: 0 0 0 3;" +
                "-fx-background-radius: 0 8 8 0;"
            );
            textLbl.setTextFill(Color.web(accentColor));
            textLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;");
        }

        btn.setOnMouseEntered(e -> {
            if (btn != activeNavBtn) {
                btn.setStyle("-fx-background-color: " + UIHelper.GRAY_100 + "; -fx-background-radius: 8;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeNavBtn) {
                btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;");
            }
        });
        return btn;
    }

    private void setActive(VBox btn) {
        // Reset previous
        if (activeNavBtn != null) {
            String[] data = (String[]) activeNavBtn.getUserData();
            activeNavBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8;");
            HBox inner = (HBox) activeNavBtn.getChildren().get(0);
            Label textLbl = (Label) inner.getChildren().get(1);
            textLbl.setTextFill(Color.web(UIHelper.GRAY_600));
            textLbl.setFont(Font.font("System", FontWeight.NORMAL, 13));
        }
        // Activate new
        String[] data = (String[]) btn.getUserData();
        String accentColor = data[0];
        String bgColor = accentColor.equals(UIHelper.GREEN_PRIMARY) ? UIHelper.GREEN_LIGHT
            : accentColor.equals(UIHelper.AMBER_PRIMARY) ? UIHelper.AMBER_LIGHT
            : accentColor.equals("#9333EA") ? "#FAF5FF"
            : UIHelper.BLUE_LIGHT;

        btn.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 0 0 0 3;" +
            "-fx-background-radius: 0 8 8 0;"
        );
        HBox inner = (HBox) btn.getChildren().get(0);
        Label textLbl = (Label) inner.getChildren().get(1);
        textLbl.setTextFill(Color.web(accentColor));
        textLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        activeNavBtn = btn;
    }

    private void switchContent(Region newContent) {
        FadeTransition fade = new FadeTransition(Duration.millis(180), newContent);
        fade.setFromValue(0);
        fade.setToValue(1);
        contentArea.getChildren().setAll(newContent);
        fade.play();
    }

    private void showDashboard() {
        switchContent(buildDashboard());
    }

    private void showBill() {
        if (billController == null) billController = new BillAnalyzerController();
        switchContent(billController.getView());
    }

    private void showAppliance() {
        if (applianceController == null) applianceController = new ApplianceController();
        switchContent(applianceController.getView());
    }

    private void showUPS() {
        if (upsController == null) upsController = new UPSController();
        switchContent(upsController.getView());
    }

    private void showEV() {
        if (evController == null) evController = new EVBatteryController();
        switchContent(evController.getView());
    }

    private VBox buildDashboard() {
        VBox dash = new VBox(24);
        dash.setPadding(new Insets(32));
        dash.setStyle("-fx-background-color: " + UIHelper.GRAY_50 + ";");

        // Hero
        Label welcome = new Label("Smart Household Energy & EV System");
        welcome.setFont(Font.font("System", FontWeight.BOLD, 26));
        welcome.setTextFill(Color.web(UIHelper.GRAY_900));

        Label sub = new Label("Monitor, analyze, and optimize your electricity usage and electric vehicle energy consumption");
        sub.setFont(Font.font("System", 14));
        sub.setTextFill(Color.web(UIHelper.GRAY_400));
        sub.setWrapText(true);

        VBox hero = new VBox(8, welcome, sub);
        hero.setPadding(new Insets(0, 0, 8, 0));

        // Module cards grid
        Label modulesTitle = new Label("Available Modules");
        modulesTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        modulesTitle.setTextFill(Color.web(UIHelper.GRAY_600));

        GridPane moduleGrid = new GridPane();
        moduleGrid.setHgap(16);
        moduleGrid.setVgap(16);
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(50);
        moduleGrid.getColumnConstraints().addAll(c, c);

        moduleGrid.add(dashModuleCard("💡", "Electricity Bill Analyzer",
            "Calculate monthly bills, compare tariffs, track usage history with visual graphs.",
            UIHelper.BLUE_PRIMARY, UIHelper.BLUE_LIGHT, "Bill Analyzer"), 0, 0);
        moduleGrid.add(dashModuleCard("🔌", "Appliance Efficiency Monitor",
            "Evaluate aging appliances, estimate energy loss, get replacement recommendations.",
            UIHelper.GREEN_PRIMARY, UIHelper.GREEN_LIGHT, "Appliance Monitor"), 1, 0);
        moduleGrid.add(dashModuleCard("🔋", "UPS Load Stability Analyzer",
            "Check overload risks, predict backup duration, detect low-voltage conditions.",
            UIHelper.AMBER_PRIMARY, UIHelper.AMBER_LIGHT, "UPS Stability"), 0, 1);
        moduleGrid.add(dashModuleCard("🚗", "EV Battery & Charging Analyzer",
            "Assess battery health degradation, estimate charging costs, renewal alerts.",
            "#9333EA", "#FAF5FF", "EV Battery"), 1, 1);

        // Workflow card
        VBox workflowCard = UIHelper.card("System Workflow");
        HBox workflow = new HBox(6);
        workflow.setAlignment(Pos.CENTER_LEFT);
        workflow.setWrapLength(800);

        String[] steps = {"Login", "Dashboard", "Select Module", "Enter Inputs", "Validate", "Calculate", "Analyze", "Alerts & Reports"};
        String[] colors = {UIHelper.GRAY_400, UIHelper.GRAY_400, UIHelper.GRAY_400, UIHelper.GRAY_400,
                           UIHelper.GRAY_400, UIHelper.BLUE_PRIMARY, UIHelper.BLUE_PRIMARY, UIHelper.GREEN_PRIMARY};
        String[] bgs = {UIHelper.GRAY_100, UIHelper.GRAY_100, UIHelper.GRAY_100, UIHelper.GRAY_100,
                        UIHelper.GRAY_100, UIHelper.BLUE_LIGHT, UIHelper.BLUE_LIGHT, UIHelper.GREEN_LIGHT};

        for (int i = 0; i < steps.length; i++) {
            Label step = new Label(steps[i]);
            step.setFont(Font.font("System", FontWeight.BOLD, 12));
            step.setTextFill(Color.web(colors[i]));
            step.setPadding(new Insets(6, 12, 6, 12));
            step.setStyle("-fx-background-color: " + bgs[i] + "; -fx-background-radius: 6;");
            workflow.getChildren().add(step);
            if (i < steps.length - 1) {
                Label arrow = new Label("→");
                arrow.setTextFill(Color.web(UIHelper.GRAY_400));
                arrow.setFont(Font.font("System", 13));
                workflow.getChildren().add(arrow);
            }
        }
        workflowCard.getChildren().add(workflow);

        dash.getChildren().addAll(hero, modulesTitle, moduleGrid, workflowCard);
        return dash;
    }

    private VBox dashModuleCard(String icon, String title, String desc, String accent, String bg, String navTarget) {
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("System", 26));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLbl.setTextFill(Color.web(UIHelper.GRAY_900));

        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("System", 13));
        descLbl.setTextFill(Color.web(UIHelper.GRAY_400));
        descLbl.setWrapText(true);

        Label openLbl = new Label("Open Module →");
        openLbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        openLbl.setTextFill(Color.web(accent));

        VBox card = new VBox(10, iconLbl, titleLbl, descLbl, openLbl);
        card.setPadding(new Insets(20));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(
            "-fx-background-color: " + UIHelper.WHITE + ";" +
            "-fx-border-color: " + UIHelper.GRAY_200 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + UIHelper.WHITE + ";" +
            "-fx-border-color: " + UIHelper.GRAY_200 + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        ));

        // Navigate on click
        card.setOnMouseClicked(e -> {
            VBox sidebar = (VBox) mainLayout.getLeft();
            sidebar.getChildren().stream()
                .filter(n -> n instanceof VBox && n.getUserData() instanceof String[])
                .map(n -> (VBox) n)
                .filter(btn -> ((String[])btn.getUserData())[1].equals(navTarget))
                .findFirst()
                .ifPresent(btn -> {
                    setActive(btn);
                    switch (navTarget) {
                        case "Bill Analyzer":    showBill(); break;
                        case "Appliance Monitor":showAppliance(); break;
                        case "UPS Stability":    showUPS(); break;
                        case "EV Battery":       showEV(); break;
                    }
                });
        });

        return card;
    }
}
