package com.project.fflb.controllers.customer;

import api.com.ferrari.finances.dk.rki.Rating;
import com.project.fflb.controllers.APISceneController;
import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CustomerData;
import com.project.fflb.dbo.Customer;
import com.project.fflb.enums.CPRErrorType;
import com.project.fflb.enums.ThreadResult;
import com.project.fflb.threading.api.APIData;
import com.project.fflb.threading.api.APIHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controller responsible for CustomerCreation.fxml
 *
 * @author Simon
 */
public class CustomerCreationController extends SceneController implements APISceneController {
    // ========
    //   FXML
    // ========

    @FXML
    TextField FirstNameTextField, LastNameTextField, EmailTextField, AddressTextField, PhoneNumberTextField, CPRTextField, PostcodeTextField;
    @FXML
    Label FetchingLabel, CouldNotFetchLabel, CreditScoreLabel;
    @FXML
    Button CreateCustomerButton, CreditScoreButton;

    // =============
    //   VARIABLES
    // =============

    /**
     * Whether this controller is currently waiting for a credit score from an API call.
     */
    private boolean isLookingForCreditScore = false;
    /**
     * The credit score of the currently created customer.
     */
    private Rating creditScore = null;
    /**
     * The handler for the API used to fetch the credit score.
     */
    private APIHandler handler = new APIHandler(this);


    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - Switch back to the customer table.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCustomerTable(ActionEvent event){
        switchToScene(event, "CustomerTable");
    }

    /**
     * "Create customer" button - creates a new customer when the button is pressed, if the information is valid.
     * @param event {@link ActionEvent} from button press - used for scene switch
     */
    public void createNewCustomer(ActionEvent event) {
        //If the system doesn't have the credit score yet, then it won't create a new customer
        if (isLookingForCreditScore || (creditScore == null)) {
            //Feedback alert
            createInformationPopup("System alert", "Wait for system to retrieve credit score");
            return;
        }

        //Retrieves information from the scene
        String firstName = FirstNameTextField.getText();
        String lastName = LastNameTextField.getText();
        String email = EmailTextField.getText();
        String address = AddressTextField.getText();
        String cpr = CPRTextField.getText();

        //If any required fields are empty, do not allow creation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || address.isEmpty() || cpr.isEmpty()) {
            //Error alert
            createErrorPopup("System alert", "One or more fields are empty");
            return;
        }

        int phoneNumber;
        //Checks if the phone number is a valid input and spits out an error if not
        try {
            phoneNumber = Integer.parseInt(PhoneNumberTextField.getText());
        } catch (NumberFormatException e) {
            //Error alert
            createErrorPopup("System alert" , "The entered phone number is invalid");
            return;
        }

        int postCode;
        //Checks if the post code is a valid input and spits out an error if not
        try {
            postCode = Integer.parseInt(PostcodeTextField.getText());
        } catch (NumberFormatException e) {
            //Error alert
            createErrorPopup("System alert", "The entered Postcode is invalid");
            return;
        }

        //Makes a new customer from the information retrieved
        Customer customer = new Customer(
                firstName,
                lastName,
                email,
                phoneNumber,
                address,
                postCode,
                cpr,
                creditScore
        );

        //Connects to the database and adds the new customer
        CustomerData.create(customer);

        //Feedback alert
        createInformationPopup("Success", "Added customer to the database");
        //Switches back to the customer table
        switchToScene(event, "CustomerTable");
    }

    /**
     * "Get Credit Score" button - Verifies the typed in CPR and makes a call to RKI through {@link APIHandler} to fetch the credit score.
     */
    public void getCreditScore() {
        //Check the validity of the CPR number
        String CPR = CPRTextField.getText();
        CPRErrorType eType = isValidCPR(CPR);

        //Perform different actions depending on the result of validating the CPR
        switch (eType) {
            //The CPR was the wrong length
            case INVALID_LENGTH -> createErrorPopup("System alert", "The entered CPR is the wrong length - all CPR numbers are 10 digits long.");
            //The CPR had an invalid date
            case INVALID_DATE -> createErrorPopup("System alert", "The entered CPR is an invalid date.");
            //The CPR is valid
            case SUCCESS -> {
                //Disables the credit score button
                CreditScoreButton.setDisable(true);
                //Feedback label
                FetchingLabel.setVisible(true);
                CouldNotFetchLabel.setVisible(false);
                //disables the create customer button
                CreateCustomerButton.setDisable(true);

                //We're now looking for the credit score
                isLookingForCreditScore = true;
                //Makes call to RKI
                handler.executeAPICallToRKI(CPRTextField.getText());
            }
        }
    }

    /**
     * Called upon the CPR field being edited.
     * The user is not allowed to create a customer without having the current CPR validated and the credit score fetched.
     */
    public void onCPRChanged() {
        //Enables the "get credit score" button and changes its color
        CreditScoreButton.setDisable(false);
        CreditScoreButton.setStyle("-fx-background-color: limegreen");
        //Disables the "create customer" button and changes its color
        CreateCustomerButton.setDisable(true);
        CreateCustomerButton.setStyle("-fx-background-color: grey");
    }

    @Override
    public void APIUpdate(ThreadResult result, APIData data) {
        //If we're getting a return call from the API, but we didn't request it, something's gone wrong.
        if (!isLookingForCreditScore) {
            return;
        }

        //JavaFX elements can only be edited in JavaFX threads. For any changes to them, we wrap the code
        //in Platform.runlater such that it gets performed on a JavaFX thread.

        if (result == ThreadResult.SUCCESS) { //Request succeeded
            //No longer need to look for credit score
            isLookingForCreditScore = false;
            //Sets credit score
            creditScore = data.getCreditScore();

            Platform.runLater(() -> {
                //Feedback
                FetchingLabel.setVisible(false);
                CreditScoreLabel.setText("Credit score: " + creditScore);
                //Enables the create customer button
                CreateCustomerButton.setDisable(false);
                CreditScoreButton.setStyle("-fx-background-color: grey");
                CreateCustomerButton.setStyle("-fx-background-color: limegreen");
            });
        } else { //An error happened
            Platform.runLater(() -> {
                //Feedback
                FetchingLabel.setVisible(false);
                CouldNotFetchLabel.setVisible(true);
                //Re-enables the credit score button
                CreditScoreButton.setVisible(true);
                CreditScoreButton.setDisable(false);

                createErrorPopup("System Error", "An error occurred when calling RKI API.");
            });
        }
    }

    /**
     * A method that checks the validity of a CPR number.
     * It does this by checking if the date of birth in the cpr number is in the future.
     * If so, the cpr is invalid, since you cannot be born in the future.
     * The {@link CPRErrorType} Enum is used to differentiate between what error happened
     * for easier debugging when a user experiences an error.
     *
     * @param cpr The cpr number that is to be validated
     * @return Whether the cpr is valid
     */
    public CPRErrorType isValidCPR(String cpr) {
        //Return an error if the CPR is the wrong length
        if (cpr == null || cpr.length() != 10) {
            return CPRErrorType.INVALID_LENGTH;
        }

        //Extract The day, month and year from the CPR Number, by Creating substrings of the original string.
        //substring() takes a start index and an end index, and creates a new string from another string.
        //Using these 3 separate strings, it allows you to use the LocalDate class to check if they are an actual date.
        String day = cpr.substring(0, 2);
        String month = cpr.substring(2, 4);
        String year = cpr.substring(4, 6);

        //Create a DateTimeFormatter with the correct date format: "ddMMyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

        try {
            //Take the three strings and convert them to a LocalDate with the format "ddMMyy"
            //If it's not parsable to a LocalDate, it will throw a "DateTimeParseException".
            LocalDate.parse(day + month + year, formatter);

            //Check if the day is past the 31st and the month is past the 12th. If so, the date is invalid.
            if ((Integer.parseInt(day) > 31 || Integer.parseInt(month) > 12)) {
                return CPRErrorType.INVALID_DATE;
            }

            //Return a success if the entered CPR number is a valid date that is not in the future
            return CPRErrorType.SUCCESS;
        } catch (DateTimeParseException e) { //catch the thrown DateTimeParseException and return an error code
            return CPRErrorType.INVALID_DATE;
        }
    }

}
