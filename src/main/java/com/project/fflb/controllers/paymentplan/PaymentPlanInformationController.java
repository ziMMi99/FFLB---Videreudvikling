package com.project.fflb.controllers.paymentplan;

import com.project.fflb.controllers.ExportSceneController;
import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CarData;
import com.project.fflb.data.dboData.PaymentPlanData;
import com.project.fflb.dbo.PaymentPlan;
import com.project.fflb.enums.ThreadResult;
import com.project.fflb.threading.export.ExportHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Victor
 */
public class PaymentPlanInformationController extends SceneController implements ExportSceneController, Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    Label IDLabel, RentLabel, CustomerIDLabel,
            CustomerFirstNameLabel, CustomerLastNameLabel,
            SalesmanIDLabel, SalesmanFirstNameLabel,
            SalesmanLastNameLabel, CarIDLabel,
            CarNameLabel, CarPriceLabel, MonthlyPaymentLabel;
    @FXML
    TextField PlanLengthTextField, DownPaymentTextField;
    @FXML
    DatePicker StartDatePicker;
    @FXML
    Button ConfirmChangesButton;

    // =============
    //   VARIABLES
    // =============

    /**
     * Handler used for exporting CSV files
     */
    ExportHandler exportHandler = new ExportHandler(this);

    /**
     * Currently selected payment plan
     */
    private PaymentPlan selectedPaymentplan = PaymentPlanTableController.getSelectedPaymentPlan();

    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - switch back to the payment plan table
     * @param event {@link ActionEvent} from button press - used for scene switch
     */
    public void switchToPaymentPlanTable(ActionEvent event) {
        //Clear selection before returning to table view
        PaymentPlanTableController.setSelectedPaymentPlan(null);
        //Uses the SceneController method to switch the scene
        switchToScene(event, "PaymentPlanTable");
    }

    /**
     * Construct new PaymentPlan object with the newly updated information and apply changes to database.
     * @param event {@link ActionEvent} from button press - used for scene switch
     */
    public void confirmUpdate(ActionEvent event){
        //Fetch data from the text fields
        int updatedPlanLength;
        double downPayment;
        Date updatedDate;

        //Check that the plan length has been formatted correctly
        try {
            updatedPlanLength = Integer.parseInt(PlanLengthTextField.getText());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered plan length is invalid");
            ConfirmChangesButton.setDisable(false);
            return;
        }

        //Check that the down payment is formatted correctly
        try {
            //Use replace method to replace comma with dot, since we use String.format to format down payment to a 2 decimal number.
            //This uses the local decimal separator which is comma in this example. But Double.parseDouble can only parse a double if it's a dot.
            //So you replace the comma with a dot before parsing the string to a double.
            downPayment = Double.parseDouble(DownPaymentTextField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered down payment is invalid");
            ConfirmChangesButton.setDisable(false);
            return;
        }

        //Check that the date is formatted correctly
        try {
            updatedDate = Date.valueOf(StartDatePicker.getValue());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered start date is invalid");
            ConfirmChangesButton.setDisable(false);
            return;
        }

        //Construct payment plan using the information
        PaymentPlan paymentPlan = new PaymentPlan(
                selectedPaymentplan.getId(),
                downPayment,
                updatedPlanLength,
                selectedPaymentplan.getMonthlyRent(),
                selectedPaymentplan.getCustomer(),
                selectedPaymentplan.getSalesman(),
                selectedPaymentplan.getCar(),
                updatedDate,
                selectedPaymentplan.getFixedCarPrice()
        );

        //Insert into database
        PaymentPlanData.update(paymentPlan);

        //Make sure the selected payment plan matches the newly updated data
        selectedPaymentplan = new PaymentPlan(paymentPlan);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Success");
        alert.setContentText("Updated payment plan in the Database");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            System.out.println("Alert closed");
        }

        //Clear selection before switching back to table view
        PaymentPlanTableController.setSelectedPaymentPlan(null);
        //Switches back to the customer table
        switchToScene(event, "PaymentPlanTable");
    }

    /**
     * Delete the selected payment plan from the database
     * @param event {@link ActionEvent} from button press - used for scene switch
     */
    public void deletePaymentPlan(ActionEvent event) {
        //Create confirmation pop-up to make sure the user wants to delete it
        Optional<ButtonType> result = createConfirmationPopup("System alert",
                "Are you sure you want to delete PaymentPlan: " + "ID: " + selectedPaymentplan.getId() +
                        " Customer: " + selectedPaymentplan.getCustomer().getFirstName() + " From the database?");

        //Delete if user pressed OK
        if (result.isEmpty()) {
            System.out.println("Alert closed");
            return;
        } else if (result.get() == ButtonType.OK) {
            //Deletes the car from the database if the finished payment plan is deleted
            if (selectedPaymentplan.isFinished()){
                CarData.delete(selectedPaymentplan.getCar().getCarID());
            } else { //If the payment plan isn't finished then the car is set to no longer be in a payment plan
                selectedPaymentplan.getCar().setHasPaymentPlan(false);
            }

            //Now, delete the plan itself
            PaymentPlanData.delete(selectedPaymentplan.getId());
        } else if (result.get() == ButtonType.CANCEL) {
            System.out.println("Deletion cancelled");
            return;
        }

        //Clear the selected payment plan before returning to table view
        PaymentPlanTableController.setSelectedPaymentPlan(null);
        switchToPaymentPlanTable(event);
    }

    @Override
    public void IOUpdate(ThreadResult threadResult) {
        //Create a pop-up informing the user of the result
        if (threadResult == ThreadResult.SUCCESS) {
            Platform.runLater(() -> createInformationPopup("Export successful", "Exported payment plan to CSV."));
        } else {
            Platform.runLater(() -> createErrorPopup("Error during export", "Could not export payment plan to CSV."));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set up text fields
        IDLabel.setText("Payment plan ID: " + selectedPaymentplan.getId());
        DownPaymentTextField.setText(String.format("%.2f", selectedPaymentplan.getDownPayment()));
        RentLabel.setText(String.format("Rent: %.2f%%", selectedPaymentplan.getMonthlyRent()));

        //Set monthly payment label by calculating it
        MonthlyPaymentLabel.setText(String.format("Monthly Payment: %.2f DKK",
                PaymentPlan.calcMonthlyPayment(
                    selectedPaymentplan.getFixedCarPrice(),
                    selectedPaymentplan.getDownPayment(),
                    selectedPaymentplan.getMonthlyRent(),
                    selectedPaymentplan.getPlanLength()
                )
        ));

        //Set car labels
        CarIDLabel.setText("ID: " + selectedPaymentplan.getCar().getCarID());
        CarNameLabel.setText(selectedPaymentplan.getCar().getModelName());
        CarPriceLabel.setText(String.format("%.2f", selectedPaymentplan.getFixedCarPrice()));

        //Set salesman labels
        SalesmanIDLabel.setText("ID: " + selectedPaymentplan.getSalesman().getPersonID());
        SalesmanFirstNameLabel.setText(selectedPaymentplan.getSalesman().getFirstName());
        SalesmanLastNameLabel.setText(selectedPaymentplan.getSalesman().getLastName());

        //Set customer labels
        CustomerIDLabel.setText("ID: " + selectedPaymentplan.getCustomer().getPersonID());
        CustomerFirstNameLabel.setText(selectedPaymentplan.getCustomer().getFirstName());
        CustomerLastNameLabel.setText(selectedPaymentplan.getCustomer().getLastName());

        //Set payment plan labels
        PlanLengthTextField.setText(String.valueOf(selectedPaymentplan.getPlanLength()));
        StartDatePicker.setValue(selectedPaymentplan.getStartDate().toLocalDate());

        //Checks if the payment plan is started, by comparing the current date with the payment plan date.
        //You cannot make changes after the plan has started.
        Date date = selectedPaymentplan.getStartDate();
        if (date.toLocalDate().isBefore(LocalDate.now())) {
            PlanLengthTextField.setDisable(true);
            StartDatePicker.setDisable(true);
            DownPaymentTextField.setDisable(true);
            ConfirmChangesButton.setDisable(true);
        }
    }

    /**
     * Export the selected plan to CSV.
     * @param event {@link ActionEvent} from button press
     */
    public void exportToCSV(ActionEvent event) {
        //File dialog pop-up
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV-file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("paymentplan_"+selectedPaymentplan.getId()+".csv");

        //Get stage using the event and show the file dialog on said stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(stage);

        //If user has not selected a file
        if (selectedFile == null) { return; }

        //Make call to the handler to export the selected payment plan
        exportHandler.exportPlanToCSV(selectedPaymentplan, selectedFile);
    }
}
