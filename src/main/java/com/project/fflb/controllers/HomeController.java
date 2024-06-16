package com.project.fflb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller class for the HomePage.fxml
 *
 * @author Sebastian
 */
public class HomeController extends SceneController {
    // ========
    //   FXML
    // ========

    @FXML
    Button HomePageCustomerButton, HomePagePaymentPlanButton, HomePageSalesmenButton, HomePageCarsButton;

    // ===========
    //   METHODS
    // ===========

    //Switch to the appropriate scenes depending on the button pressed

    public void switchToCustomerTable(ActionEvent event) {
        switchToScene(event, "CustomerTable");
    }
    public void switchToPaymentPlanTable(ActionEvent event) {
        switchToScene(event, "PaymentPlanTable");
    }
    public void switchToSalesmanTable(ActionEvent event) {
        switchToScene(event, "SalesmanTable");
    }
    public void switchToCarTable(ActionEvent event) {
        switchToScene(event, "CarTable");
    }

}
