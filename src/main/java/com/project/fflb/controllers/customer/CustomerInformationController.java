package com.project.fflb.controllers.customer;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CustomerData;
import com.project.fflb.data.dboData.PaymentPlanData;
import com.project.fflb.dbo.Customer;
import com.project.fflb.dbo.PaymentPlan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller responsible for CustomerInformation.fxml
 *
 * @author Ebbe
 */
public class CustomerInformationController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    Button ConfirmChangesButton;
    @FXML
    TextField FirstNameTextField, LastNameTextField, EmailTextField, AddressTextField, PhoneNumberTextField, CPRTextField, PostcodeTextField;
    @FXML
    Label IDLabel, CreditScoreLabel;

    // =============
    //   VARIABLES
    // =============

    /**
     * The customer which will be handled by this menu
     */
    private Customer selectedCustomer;

    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - Switch back to the customer view.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCustomerTable(ActionEvent event){
        CustomerTableController.setSelectedCustomer(null);
        //Uses the sceneController method to switch the scene
        switchToScene(event, "CustomerTable");
    }

    /**
     * "Confirm Changes" button - Updates the customers information, by constructing a new
     * customer with the newly updated information and apply the changes to database.
     *
     * @param event {@link ActionEvent} from button press - used for scene switch
     */
    public void confirmUpdate(ActionEvent event) {
        //Assign text from the fields to a variable
        String updatedFirstName = FirstNameTextField.getText();
        String updatedLastName = LastNameTextField.getText();
        String updatedEmail = EmailTextField.getText();
        String updatedAddress = AddressTextField.getText();

        int updatedPhoneNumber;
        int updatedPostcode;

        //Make sure integer fields are valid
        try {
            updatedPhoneNumber = Integer.parseInt(PhoneNumberTextField.getText());
            updatedPostcode = Integer.parseInt(PostcodeTextField.getText());
        } catch (NumberFormatException e) {
            //Error alert
            createErrorPopup("System alert", "Phone nr. or postcode is invalid! They must only contain numbers.");
            return;
        }

        //Stops the updating, if any of the fields are empty
        if (updatedFirstName.isEmpty() ||
                updatedLastName.isEmpty() ||
                updatedEmail.isEmpty() ||
                updatedAddress.isEmpty() ||
                PhoneNumberTextField.getText().isEmpty() ||
                PostcodeTextField.getText().isEmpty()) {
            //Error alert
            createErrorPopup("System alert", "One or more fields are empty");
            return;
        }

        //Makes the updated customer
        Customer customer = new Customer(
                selectedCustomer.getPersonID(),
                updatedFirstName,
                updatedLastName,
                updatedEmail,
                updatedPhoneNumber,
                updatedAddress,
                updatedPostcode,
                selectedCustomer.getCPR(),
                selectedCustomer.getCreditScore()
        );

        //Update customer in database
        CustomerData.update(customer);

        //Make sure the selected customer matches the newly updated data
        selectedCustomer = customer;

        //Feedback alert
        createInformationPopup("Success", "Updated customer in the database");
        CustomerTableController.setSelectedCustomer(null);
        //Switches back to the customer table
        switchToScene(event, "CustomerTable");
    }

    /**
     * "Delete Customer" button - Delete the selected customer from the database.
     * @param event ActionEvent from the button press. It is used for switching the scene.
     */
    public void deleteCustomer(ActionEvent event) {
        //Get all payment plans and construct list of plans containing the customer
        ArrayList<PaymentPlan> paymentPlans = PaymentPlanData.getAll(); //All payment plans
        ArrayList<PaymentPlan> plansWithCustomer = new ArrayList<>(); //Plans involving the customer

        //Whether the customer is in a payment plan
        boolean customerInPlan = false;

        //Loop through all plans and add whichever plans the customer is part of
        for (PaymentPlan p : paymentPlans) {
            if (p.getCustomer().getPersonID() == selectedCustomer.getPersonID()) {
                customerInPlan = true;
                plansWithCustomer.add(p);
            }
        }

        StringBuilder alertMsg = new StringBuilder();

        alertMsg.append("Are you sure you want to delete " + selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName() + " from the database?");
        //If plans are found which involve the customer, show those in the alert message.
        if (customerInPlan) {
            alertMsg.append("\nThis will also delete the following payment plans:");
            for (PaymentPlan p : plansWithCustomer) {
                alertMsg.append("\n"+p);
            }
        }

        //Display alert
        Optional<ButtonType> result = createConfirmationPopup("Confirm", alertMsg.toString());

        //Only delete if the user pressed "OK"
        if (result.isEmpty()) {
            System.out.println("Alert closed");
            return;
        } else if (result.get() == ButtonType.OK) {
            CustomerData.delete(selectedCustomer.getPersonID());
        } else if (result.get() == ButtonType.CANCEL) {
            System.out.println("Deletion Cancelled");
            return;
        }

        //Notify the customer was deleted
        createInformationPopup("System alert", selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName() + " was deleted from the database");

        //Make sure selected customer reflects the updated info
        CustomerTableController.setSelectedCustomer(selectedCustomer);

        //Switch back to table view
        switchToCustomerTable(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set the currently selected customer to the one selected in the table view.
        selectedCustomer = CustomerTableController.getSelectedCustomer();

        //Set up all text fields to the correct values
        CreditScoreLabel.setText("Credit Score: " + selectedCustomer.getCreditScore());
        IDLabel.setText("ID: " + selectedCustomer.getPersonID());
        FirstNameTextField.setText(selectedCustomer.getFirstName());
        LastNameTextField.setText(selectedCustomer.getLastName());
        EmailTextField.setText(selectedCustomer.getEmail());
        AddressTextField.setText(selectedCustomer.getAddress());
        PhoneNumberTextField.setText(String.valueOf(selectedCustomer.getPhoneNumber()));
        CPRTextField.setText(selectedCustomer.getCPR());
        PostcodeTextField.setText(String.valueOf(selectedCustomer.getPostCode()));

        //CPR cannot be changed
        CPRTextField.setDisable(true);
    }
}
