module com.project.fflb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires junit;
    requires com.opencsv;

    opens com.project.fflb to javafx.fxml;
    exports com.project.fflb;
    exports com.project.fflb.controllers;
    opens com.project.fflb.controllers to javafx.fxml;
    exports com.project.fflb.controllers.customer;
    opens com.project.fflb.controllers.customer to javafx.fxml;
    exports com.project.fflb.controllers.car;
    opens com.project.fflb.controllers.car to javafx.fxml;
    exports com.project.fflb.controllers.salesman;
    opens com.project.fflb.controllers.salesman to javafx.fxml;
    exports com.project.fflb.controllers.paymentplan;
    opens com.project.fflb.controllers.paymentplan to javafx.fxml;
    exports com.project.fflb.enums;
    opens com.project.fflb.enums to javafx.fxml;
    exports com.project.fflb.threading.api;
    opens com.project.fflb.threading.api to javafx.fxml;
    exports com.project.fflb.threading.export;
    opens com.project.fflb.threading.export to javafx.fxml;

//test
    exports com.project.fflb.dbo;
    opens com.project.fflb.dbo to javafx.fxml;
}