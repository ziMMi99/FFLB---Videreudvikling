package com.project.fflb.controllers.paymentplan;

import api.com.ferrari.finances.dk.rki.Rating;
import com.project.fflb.controllers.APISceneController;
import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CarData;
import com.project.fflb.data.dboData.CustomerData;
import com.project.fflb.data.dboData.PaymentPlanData;
import com.project.fflb.data.dboData.SalesmanData;
import com.project.fflb.dbo.Car;
import com.project.fflb.dbo.Customer;
import com.project.fflb.dbo.PaymentPlan;
import com.project.fflb.dbo.Salesman;
import com.project.fflb.enums.ThreadResult;
import com.project.fflb.threading.api.APIData;
import com.project.fflb.threading.api.APIHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author Ebbe
 */
public class PaymentPlanCreationController extends SceneController implements APISceneController, Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    SearchableComboBox<Customer> CustomerComboBox = new SearchableComboBox<>();
    @FXML
    SearchableComboBox<Salesman> SalesmanComboBox = new SearchableComboBox<>();
    @FXML
    SearchableComboBox<Car> CarComboBox = new SearchableComboBox<>();
    @FXML
    TextField PlanLengthTextField, DownpaymentTextField;
    @FXML
    DatePicker StartDatePicker;
    @FXML
    Label RateLabel, MonthlyRentLabel, CalculateRentMessage;
    @FXML
    Button CreatePaymentPlanButton, CalculateRentButton;

    // =============
    //   VARIABLES
    // =============

    /**
     * Whether this controller is looking for the daily rent to return from the bank call.
     */
    private boolean isLookingForDailyRent = false;
    /**
     * Currently fetched interest rate from the bank.
     */
    private double interestRate;


    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - Switch back to the table view
     * @param event {@link ActionEvent} from button press
     */
    public void switchToPaymentPlanTable(ActionEvent event){
        //Uses the sceneController method to switch the scene
        switchToScene(event, "PaymentPlanTable");
    }

    /**
     * Verify all inputted information and then add payment plan to database.
     * @param event {@link ActionEvent} from button press
     */
    public void createNewPaymentPlan(ActionEvent event) {
        //Get values from drop-down fields
        Customer customer = CustomerComboBox.getValue();
        Salesman salesman = SalesmanComboBox.getValue();
        Car car = CarComboBox.getValue();

        //If any fields are somehow null, abort
        if ( (customer == null) || (salesman == null) || (car == null) ) {
            return;
        }

        //Get plan length, check for correct formatting
        int planLength;
        try {
            planLength = Integer.parseInt(PlanLengthTextField.getText());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered plan length is invalid.");
            return;
        }

        //Get value from datepicker and convert to SQL-compatible date format
        Date startDate = Date.valueOf(StartDatePicker.getValue());

        //Check down payment is a valid number
        double downpayment;
        try {
            downpayment = Double.parseDouble(DownpaymentTextField.getText());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered down payment is invalid.");
            return;
        }

        double fixedCarPrice = car.getPrice();

        if (fixedCarPrice - downpayment > salesman.getSalesmanLoanLimit()) {
            createErrorPopup("System alert", "Salesman loan limit exceeded. Contact Sales manager");
            return;
        }

        //Construct PaymentPlan object using this information
        PaymentPlan paymentPlan = new PaymentPlan(
                downpayment,
                planLength,
                //Use static method to calculate the monthly rent
                PaymentPlan.calcMonthlyRent(interestRate, customer, car, downpayment, planLength),
                customer,
                salesman,
                car,
                startDate,
                fixedCarPrice
        );

        //Insert into database
        PaymentPlanData.create(paymentPlan);

        //Update car in database to be attached to a payment plan
        car.setHasPaymentPlan(true);
        CarData.update(car);

        createInformationPopup("Success", "Added Payment Plan to the Database");

        switchToPaymentPlanTable(event);
    }

    /**
     * Button to calculate rent and monthly payment. Executes a call to the bank API.
     */
    public void calculateRent() {
        //We're now waiting for the call to the bank API to return with the rate.
        isLookingForDailyRent = true;

        //Execute call
        APIHandler apiHandler = new APIHandler(this);
        apiHandler.executeAPICallToBank();

        //Make the appropriate fields visible
        CalculateRentButton.setDisable(true);
        CalculateRentMessage.setVisible(true);
    }

    //Reset all buttons and fields to their initial states
    public void resetButtons() {
        CalculateRentButton.setVisible(true);
        CalculateRentButton.setDisable(false);
        CreatePaymentPlanButton.setDisable(true);
        RateLabel.setText("Rent: ");
        MonthlyRentLabel.setText("Monthly payment:");
        CalculateRentButton.setStyle("-fx-background-color: limegreen");
        CreatePaymentPlanButton.setStyle("-fx-background-color: grey");
    }

    @Override
    public void APIUpdate(ThreadResult result, APIData data) {
        //Something's gone wrong if we've received data without requesting it
        if (!isLookingForDailyRent) {
            System.out.println("Ignoring received API data, none requested.");
            return;
        }

        //Update the appropriate fields if the API call was a success
        if (result == ThreadResult.SUCCESS) {
            isLookingForDailyRent = false;
            interestRate = data.getInterest();

            //runLater for JavaFX thread
            Platform.runLater(() -> {
                Customer customer = CustomerComboBox.getValue();
                Salesman salesman = SalesmanComboBox.getValue();
                Car car = CarComboBox.getValue();

                //If any fields are somehow null, abort
                if ((customer == null) || (salesman == null) || (car == null)) {
                    createErrorPopup("System alert", "One or more of the necessary fields are empty");

                    CalculateRentMessage.setVisible(false);
                    CalculateRentButton.setDisable(false);
                    return;
                }

                //Get plan length and make sure it's formatted correctly
                int planLength;
                try {
                    planLength = Integer.parseInt(PlanLengthTextField.getText());
                } catch (NumberFormatException e) {
                    createErrorPopup("System alert", "The entered plan length is invalid.");

                    CalculateRentMessage.setVisible(false);
                    CalculateRentButton.setDisable(false);
                    return;
                }

                double downpayment = 0;
                boolean downPaymentValid = true;

                //Check if a valid number is inputted
                try {
                    downpayment = Double.parseDouble(DownpaymentTextField.getText());
                } catch (NumberFormatException e) {
                    downPaymentValid = false;
                }

                //Check if the down payment is less than the car's price
                if (downPaymentValid) {
                    if (downpayment > car.getPrice()) {
                        downPaymentValid = false;
                    }
                }

                //If it's invalid, spit out an error
                if (!downPaymentValid) {
                    createErrorPopup("System alert", "The entered down payment is invalid.");

                    CalculateRentMessage.setVisible(false);
                    CalculateRentButton.setDisable(false);
                    return;
                }

                try {
                    //Get value from datepicker and convert to SQL-compatible date format
                    Date.valueOf(StartDatePicker.getValue());
                } catch (NullPointerException e) {
                    createErrorPopup("System alert", "No date specified.");

                    CalculateRentMessage.setVisible(false);
                    CalculateRentButton.setDisable(false);
                    return;
                }

                //Calculate the monthly rent and monthly payment
                double newInterestRate = PaymentPlan.calcMonthlyRent(interestRate, customer, car, downpayment, planLength);
                double monthlyPayment = PaymentPlan.calcMonthlyPayment(car.getPrice(), downpayment, newInterestRate, planLength);

                //Set the text, format numbers to have 2 decimal numbers
                MonthlyRentLabel.setText(String.format("Monthly payment: %.2f", monthlyPayment));
                RateLabel.setText(String.format("Rate: %.2f%%", newInterestRate));

                //Set buttons and labels to their correct states
                CreatePaymentPlanButton.setDisable(false);
                CalculateRentMessage.setVisible(false);
                CalculateRentButton.setStyle("-fx-background-color: grey");
                CreatePaymentPlanButton.setStyle("-fx-background-color: limegreen");
            });
        } else { //An error happened
            Platform.runLater(() -> {
                CalculateRentMessage.setVisible(false);
                CalculateRentButton.setDisable(false);
                CalculateRentButton.setStyle("-fx-background-color: limegreen");

                createErrorPopup("System Error", "Error fetching rent from bank API.");
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateCarDropDown();
        updateCustomerDropDown();
        updateSalesmanDropDown();
        CreatePaymentPlanButton.setDisable(true);
    }

    //Helper methods

    /**
     * Get all customers and add to the dropdown, exclude those with rating D
     */
    private void updateCustomerDropDown() {
        //Get all customers from database

        ArrayList<Customer> list = CustomerData.getAll();

        if (list == null) { return; }

        //Sort customers based on their credit score
        Customer.sortCustomerListByRating(list);

        //Remove any customers with credit score 'D'
        list.removeIf(customer -> customer.getCreditScore() == Rating.D);

        CustomerComboBox.getItems().addAll(list);

    }

    /**
     * Get all salesmen and add to the dropdown, exclude those who are inactive
     */
    private void updateSalesmanDropDown() {
        //Get all salesmen from database
        ArrayList<Salesman> list = SalesmanData.getAll();

        if (list == null) { return; }

        //Do not display if salesman isn't active
        list.removeIf(salesman -> !salesman.getIsActive());

        //Add to dropdown
        SalesmanComboBox.getItems().addAll(list);
    }

    /**
     * Get all cars and add to the dropdown, exclude those that are part of a payment plan already
     */
    private void updateCarDropDown() {
        //Get all cars from database
        ArrayList<Car> list = CarData.getAll();

        if (list == null) { return; }

        //Loop through all cars and remove any that are part of a payment plan already.
        //We pass a method reference to the list to remove any elements for which that method returns true.
        list.removeIf(Car::hasPaymentPlan);

        CarComboBox.getItems().addAll(list);
    }

    //If any information is changed, the below methods make sure the buttons are reset properly.

    public void customerChanged(ActionEvent event) {
        resetButtons();
    }

    public void planLengthChanged(KeyEvent event) {
        resetButtons();
    }

    public void downPaymentChanged(KeyEvent event) {
        resetButtons();
    }

    public void carChanged(ActionEvent event) {
        resetButtons();
    }


}
