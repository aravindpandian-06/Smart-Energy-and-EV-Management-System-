module com.smartenergy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens com.smartenergy to javafx.fxml;
    opens com.smartenergy.controller to javafx.fxml;
    opens com.smartenergy.model to javafx.base;
    opens com.smartenergy.view to javafx.fxml;

    exports com.smartenergy;
    exports com.smartenergy.controller;
    exports com.smartenergy.model;
    exports com.smartenergy.view;
    exports com.smartenergy.util;
}
